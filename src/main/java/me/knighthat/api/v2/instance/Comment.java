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

package me.knighthat.api.v2.instance;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadReplies;
import lombok.Getter;
import me.knighthat.api.utils.Concurrency;
import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class Comment extends InfoContainer {

    @NotNull
    private final String        videoId;
    @NotNull
    private final String        text;
    @NotNull
    private final String        author;
    @NotNull
    private final String        authorThumbnail;
    @Positive
    private final Long          likes;
    @NotNull
    private final List<Comment> replies;

    public Comment(
            @NotNull String id,
            @NotNull DateTime since,
            @NotNull String videoId,
            @NotNull String text,
            @NotNull String author,
            @NotNull String authorThumbnail,
            @Positive Long likes ) {
        super( id, since );
        this.videoId = videoId;
        this.text = text;
        this.author = author;
        this.authorThumbnail = authorThumbnail;
        this.likes = likes;
        this.replies = new CopyOnWriteArrayList<>();
    }

    public Comment( @NotNull String videoId, @NotNull com.google.api.services.youtube.model.Comment comment ) {
        this(
                comment.getId(),
                comment.getSnippet().getPublishedAt(),
                videoId,
                comment.getSnippet().getTextOriginal(),
                comment.getSnippet().getAuthorDisplayName(),
                comment.getSnippet().getAuthorProfileImageUrl(),
                comment.getSnippet().getLikeCount()
        );
    }

    public Comment( @NotNull String videoId, @NotNull CommentThread commentThread ) {
        this( videoId, commentThread.getSnippet().getTopLevelComment() );

        CommentThreadReplies replies = commentThread.getReplies();
        if ( replies != null && !replies.isEmpty() )
            Concurrency.voidAsync(
                    replies.getComments(),
                    comment -> this.replies.add( new Comment( videoId, comment ) )
            );
    }

    @Contract( pure = true )
    public @NotNull @Unmodifiable List<Comment> getReplies() { return List.copyOf( this.replies ); }
}
