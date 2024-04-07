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

import me.knighthat.api.utils.Concurrency;
import me.knighthat.api.v2.YoutubeAPI;
import me.knighthat.api.v2.error.YoutubeAPIErrorTemplate;
import me.knighthat.api.v2.instance.preview.ChannelPreviewCard;
import me.knighthat.api.v2.instance.preview.VideoPreviewCard;
import me.knighthat.api.v2.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
@RequestMapping( "/v2/preview" )
public class PreviewCardController {

    @GetMapping( "/videos" )
    @CrossOrigin
    public @NotNull ResponseEntity<?> videoCards( @RequestParam String... id ) {
        try {

            Set<VideoPreviewCard> cards = new CopyOnWriteArraySet<>();
            Concurrency.voidAsync(
                    YoutubeAPI.videos( id.length, null, id ),
                    video -> cards.add( new VideoPreviewCard( video ) )
            );

            return ResponseEntity.ok( cards );

        } catch ( IOException e ) {

            YoutubeAPIErrorTemplate errorTemplate = new YoutubeAPIErrorTemplate( e );

            Logger.severe( "YouTubeAPI returns error" );
            Logger.severe( "Reason: " + errorTemplate.getReason() );

            return errorTemplate.makeResponse();

        }
    }

    @GetMapping( "/channels" )
    @CrossOrigin
    public @NotNull ResponseEntity<?> channelCards( @RequestParam String... id ) {
        try {

            Set<ChannelPreviewCard> cards = new CopyOnWriteArraySet<>();
            Concurrency.voidAsync(
                    YoutubeAPI.channels( id.length, id ),
                    channel -> cards.add( new ChannelPreviewCard( channel.getId(), channel.getSnippet() ) )
            );

            return ResponseEntity.ok( cards );

        } catch ( IOException e ) {

            YoutubeAPIErrorTemplate errorTemplate = new YoutubeAPIErrorTemplate( e );

            Logger.severe( "YouTubeAPI returns error" );
            Logger.severe( "Reason: " + errorTemplate.getReason() );

            return errorTemplate.makeResponse();

        }
    }
}
