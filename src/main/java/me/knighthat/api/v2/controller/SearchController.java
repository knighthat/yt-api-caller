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
import me.knighthat.api.utils.Concurrency;
import me.knighthat.api.utils.SystemInfo;
import me.knighthat.api.v2.YoutubeAPI;
import me.knighthat.api.v2.error.YoutubeAPIErrorTemplate;
import me.knighthat.api.v2.instance.InfoContainer;
import me.knighthat.api.v2.instance.preview.ChannelPreviewCard;
import me.knighthat.api.v2.instance.preview.VideoPreviewCard;
import me.knighthat.api.v2.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController( "search-v2" )
@RequestMapping( "/v2" )
public class SearchController {

    private @NotNull List<SearchResult> searchByKeyword( long max, @Nullable String region, @NotNull String keyword ) throws IOException {
        if ( max < 0 )
            /*
            Set number of results return by YouTubeAPI to 'ids' size
            if provided value is a sub-zero number.
            */
            max = 50;
        if ( max == 0 )
            /* No need to waste quota on 0 result query */
            return Collections.emptyList();

        if ( region == null )
            region = SystemInfo.countryCode();
        /* Invalid country code */
        if ( region.length() != 2 )
            throw new IllegalArgumentException( "\"region\" can only be a 2 characters string!" );

        return YoutubeAPI.getService()
                         .search()
                         .list( "snippet" )
                         .setKey( YoutubeAPI.API_KEY )
                         .setQ( keyword )
                         .setRegionCode( region )
                         .setMaxResults( max )
                         .execute()
                         .getItems();
    }

    @GetMapping( "/search" )
    @CrossOrigin
    public @NotNull ResponseEntity<?> search(
            @RequestParam( required = false, defaultValue = "50" ) int max,
            @RequestParam( required = false ) String region,
            @RequestParam String key
    ) {
        try {

            Set<InfoContainer> containers = new CopyOnWriteArraySet<>();
            Set<String> videoIds = new CopyOnWriteArraySet<>();
            Concurrency.voidAsync(
                    this.searchByKeyword( max, region, key ),
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
                    YoutubeAPI.videos( videoIds.size(), null, videoIds.toArray( String[]::new ) ),
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

        } catch ( IOException e ) {

            YoutubeAPIErrorTemplate errorTemplate = new YoutubeAPIErrorTemplate( e );

            Logger.severe( "YouTubeAPI returns error" );
            Logger.severe( "Reason: " + errorTemplate.getReason() );

            return errorTemplate.makeResponse();
            
        }
    }
}
