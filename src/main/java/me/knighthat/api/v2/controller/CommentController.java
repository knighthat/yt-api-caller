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
import me.knighthat.api.v2.instance.Comment;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping( "/v2" )
public class CommentController {

    @NotNull
    private final YoutubeAPI service;

    @Autowired
    public CommentController( @NotNull YoutubeAPI service ) {
        this.service = service;
    }

    @GetMapping( "/comments" )
    @CrossOrigin
    @SneakyThrows( IOException.class )
    public @NotNull ResponseEntity<?> comments(
            @RequestParam( required = false, defaultValue = "100" ) int max,
            @RequestParam String videoId
    ) {
        if ( max < 0 )
            throw new IllegalArgumentException( "\"max\" must be a positive number!" );
        if ( max == 0 )
            return ResponseEntity.ok( Collections.emptyList() );

        List<Comment> comments = new CopyOnWriteArrayList<>();
        Concurrency.voidAsync(
                service.comments()
                       .setVideoId( videoId )
                       .setModerationStatus( "relevance" )
                       .setMaxResults( (long) max )
                       .execute()
                       .getItems(),
                thread -> comments.add( new Comment( videoId, thread ) )
        );

        /* Sort top-level comments based on likes in descending order */
        comments.sort( ( cmt1, cmt2 ) -> cmt2.getLikes().compareTo( cmt1.getLikes() ) );

        return ResponseEntity.ok( comments );
    }
}
