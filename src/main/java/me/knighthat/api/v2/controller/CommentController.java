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

import com.google.api.services.youtube.model.CommentThread;
import me.knighthat.api.utils.Concurrency;
import me.knighthat.api.v2.YoutubeAPI;
import me.knighthat.api.v2.error.YoutubeAPIErrorTemplate;
import me.knighthat.api.v2.instance.Comment;
import me.knighthat.api.v2.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping( "/v2" )
public class CommentController {

    private @NotNull List<CommentThread> fetchCommentThreads( long max, @NotNull String videoId ) throws IOException {
        if ( max < 0 )
            /* Set number of results return to maximum allowed */
            max = 100;
        if ( max == 0 )
            /* No need to waste quota on 0 result query */
            return Collections.emptyList();

        return YoutubeAPI.getService()
                         .commentThreads()
                         .list( "id,snippet,replies" )
                         .setKey( YoutubeAPI.API_KEY )
                         .setVideoId( videoId )
                         .setModerationStatus( "published" )
                         .setOrder( "relevance" )
                         .setMaxResults( max )
                         .execute()
                         .getItems();
    }

    @GetMapping( "/comments" )
    @CrossOrigin
    public @NotNull ResponseEntity<?> comments(
            @RequestParam( required = false, defaultValue = "100" ) int max,
            @RequestParam String videoId
    ) {
        try {

            List<Comment> comments = new CopyOnWriteArrayList<>();
            Concurrency.voidAsync(
                    this.fetchCommentThreads( max, videoId ),
                    thread -> comments.add( new Comment( videoId, thread ) )
            );

            /* Sort top-level comments based on likes in descending order */
            comments.sort( ( cmt1, cmt2 ) -> cmt2.getLikes().compareTo( cmt1.getLikes() ) );

            return ResponseEntity.ok( comments );

        } catch ( IOException e ) {

            YoutubeAPIErrorTemplate errorTemplate = new YoutubeAPIErrorTemplate( e );

            Logger.severe( "YouTubeAPI returns error" );
            Logger.severe( "Reason: " + errorTemplate.getReason() );

            return errorTemplate.makeResponse();

        }
    }
}
