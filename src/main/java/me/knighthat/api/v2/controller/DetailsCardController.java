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

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import lombok.SneakyThrows;
import me.knighthat.api.v2.YoutubeAPI;
import me.knighthat.api.v2.instance.detail.ChannelDetails;
import me.knighthat.api.v2.instance.detail.VideoDetails;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping( "/v2/details" )
public class DetailsCardController {

    @GetMapping( "/video" )
    @CrossOrigin
    @SneakyThrows( IOException.class )
    public @NotNull ResponseEntity<?> videoDetails( @RequestParam String id ) {
        Video video = YoutubeAPI.videos( 1, null, id ).get( 0 );
        return ResponseEntity.ok( new VideoDetails( video ) );
    }

    @GetMapping( "/channel" )
    @CrossOrigin
    @SneakyThrows( IOException.class )
    public @NotNull ResponseEntity<?> channelDetails( @RequestParam String id ) {
        Channel channel = YoutubeAPI.channels( 1, id ).get( 0 );
        return ResponseEntity.ok( new ChannelDetails( channel ) );
    }
}
