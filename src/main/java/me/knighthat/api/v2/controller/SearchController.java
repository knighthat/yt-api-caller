/*
 * Copyright (c) 2024 Knight Hat. All Rights Reserved.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.knighthat.api.v2.controller;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.VideoSnippet;
import lombok.SneakyThrows;
import me.knighthat.api.utils.Concurrency;
import me.knighthat.api.utils.Sanitizer;
import me.knighthat.api.v2.YoutubeAPI;
import me.knighthat.api.v2.error.RawErrorTemplate;
import me.knighthat.api.v2.instance.InfoContainer;
import me.knighthat.api.v2.instance.preview.ChannelPreviewCard;
import me.knighthat.api.v2.instance.preview.VideoPreviewCard;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController( "search-v2" )
@RequestMapping( "/v2" )
public class SearchController {

    @NotNull
    private final YoutubeAPI service;

    @Autowired
    public SearchController( @NotNull YoutubeAPI service ) {
        this.service = service;
    }

    @GetMapping( "/search" )
    @CrossOrigin
    @SneakyThrows( IOException.class )
    public @NotNull ResponseEntity<?> search(
            @RequestParam( required = false, defaultValue = "50" ) long max,
            @RequestParam( required = false ) String region,
            @RequestParam( required = false ) String channelId,
            @RequestParam( required = false ) String key
    ) {
        if ( channelId == null && key == null )
            return RawErrorTemplate.body( HttpStatus.NOT_FOUND, "Please provide at least 1 argument of \"key\" or \"channelId\"" );
        if ( channelId != null && key != null )
            return RawErrorTemplate.body( HttpStatus.CONFLICT, "Cannot process with both \"key\" and \"channelId\" provided!" );

        if ( max < 0 )
            throw new IllegalArgumentException( "\"max\" must be a positive number!" );
        if ( max == 0 )
            return ResponseEntity.ok( Collections.emptyList() );

        Set<InfoContainer> containers = new CopyOnWriteArraySet<>();
        Set<String> videoIds = new CopyOnWriteArraySet<>();

        List<SearchResult> results = null;
        if ( channelId != null )
            results = service.search().setChannelId( channelId ).setMaxResults( max ).execute().getItems();
        if ( key != null ) {
            region = Sanitizer.countryCode( region );
            results = service.search().setQ( key ).setRegionCode( region ).setMaxResults( max ).execute().getItems();
        }

        Concurrency.voidAsync(
                results,
                result -> {
                    switch (result.getId().getKind()) {
                        case "youtube#channel" -> {
                            SearchResultSnippet snippet = result.getSnippet();

                            containers.add(
                                    new ChannelPreviewCard(
                                            result.getId().getChannelId(),
                                            snippet.getPublishedAt(),
                                            snippet.getTitle(),
                                            "",
                                            snippet.getThumbnails().getMedium().getUrl()
                                    )
                            );
                        }

                            /*
                            SearchResult of video doesn't have enough fields
                            to instantiate a VideoPreviewCard.

                            Therefore, add its id to the set and make another
                            call which only cost 1 quota unit for all videos
                            instead of 1 quota for each video.
                             */
                        case "youtube#video" -> videoIds.add( result.getId().getVideoId() );
                    }
                }
        );

        Concurrency.voidAsync(
                service.videos().setId( new ArrayList<>( videoIds ) ).execute().getItems(),
                video -> {
                    VideoPreviewCard card;
                    if ( video.getStatistics().getLikeCount() == null ) {
                        VideoSnippet snippet = video.getSnippet();

                        card = new VideoPreviewCard(
                                video.getId(),
                                snippet.getPublishedAt(),
                                VideoPreviewCard.VideoDuration.fromString( video.getContentDetails().getDuration() ),
                                snippet.getTitle(),
                                BigInteger.ZERO,
                                video.getStatistics().getViewCount(),
                                snippet.getChannelId()
                        );
                    } else
                        card = new VideoPreviewCard( video );

                    containers.add( card );
                }
        );

        return ResponseEntity.ok( containers );
    }
}
