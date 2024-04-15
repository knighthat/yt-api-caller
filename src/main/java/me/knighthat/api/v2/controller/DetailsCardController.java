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

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import lombok.SneakyThrows;
import me.knighthat.api.utils.Sanitizer;
import me.knighthat.api.v2.YoutubeAPI;
import me.knighthat.api.v2.instance.detail.ChannelDetails;
import me.knighthat.api.v2.instance.detail.VideoDetails;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping( "/v2/details" )
public class DetailsCardController {

    @NotNull
    private final YoutubeAPI service;

    @Autowired
    public DetailsCardController( @NotNull YoutubeAPI service ) {
        this.service = service;
    }

    @GetMapping( "/video" )
    @CrossOrigin
    @SneakyThrows( IOException.class )
    public @NotNull ResponseEntity<?> videoDetails( @RequestParam List<String> id ) {
        List<Video> videos = service.videos()
                                    .setId( id )
                                    .setMaxResults( 1L )
                                    .execute()
                                    .getItems();
        if ( videos.isEmpty() )
            return ResponseEntity.ok( Collections.emptyList() );

        VideoDetails vDetails = new VideoDetails( videos.get( 0 ) );
        return ResponseEntity.ok( vDetails );
    }

    @GetMapping( "/channel" )
    @CrossOrigin
    @SneakyThrows( IOException.class )
    public @NotNull ResponseEntity<?> channelDetails(
            @RequestParam( required = false ) List<String> id,
            @RequestParam( required = false ) String handle
    ) {
        Sanitizer.atLeastOneNotNull(
            new Object[] {id, handle},
            new String[] {"id", "handle"}
        );

        YouTube.Channels.List channelList = service.channels().setMaxResults( 1L );

        if ( id != null )
            channelList = channelList.setId( id );
        if ( handle != null )
            channelList = channelList.setForHandle( handle );

        List<Channel> channels = channelList.execute().getItems();

        if ( channels.isEmpty() )
            return ResponseEntity.ok( Collections.emptyList() );

        ChannelDetails details = new ChannelDetails( channels.get( 0 ) );
        return ResponseEntity.ok( details );
    }
}
