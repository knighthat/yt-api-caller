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

import lombok.SneakyThrows;
import me.knighthat.api.utils.Concurrency;
import me.knighthat.api.v2.YoutubeAPI;
import me.knighthat.api.v2.instance.preview.ChannelPreviewCard;
import me.knighthat.api.v2.instance.preview.VideoPreviewCard;
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
    @SneakyThrows( IOException.class )
    public @NotNull ResponseEntity<?> videoCards( @RequestParam String... id ) {
        Set<VideoPreviewCard> cards = new CopyOnWriteArraySet<>();
        Concurrency.voidAsync(
                YoutubeAPI.videos( id.length, null, id ),
                video -> cards.add( new VideoPreviewCard( video ) )
        );

        return ResponseEntity.ok( cards );
    }

    @GetMapping( "/channels" )
    @CrossOrigin
    @SneakyThrows( IOException.class )
    public @NotNull ResponseEntity<?> channelCards( @RequestParam String... id ) {

        Set<ChannelPreviewCard> cards = new CopyOnWriteArraySet<>();
        Concurrency.voidAsync(
                YoutubeAPI.channels( id.length, id ),
                channel -> cards.add( new ChannelPreviewCard( channel.getId(), channel.getSnippet() ) )
        );

        return ResponseEntity.ok( cards );
    }
}
