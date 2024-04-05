package me.knighthat.api.v1.instances;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadReplies;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class Comment {

    private final String        id;
    private final String        text;
    private final String        author;
    private final String        authorThumbnail;
    private final Long          likes;
    private final DateTime      since;
    private final List<Comment> replies;

    public Comment( String id, String text, String author, String authorThumbnail, Long likes, DateTime since ) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.authorThumbnail = authorThumbnail;
        this.likes = likes;
        this.since = since;
        this.replies = new CopyOnWriteArrayList<>();
    }

    public Comment( com.google.api.services.youtube.model.Comment comment ) {
        this(
                comment.getId(),
                comment.getSnippet().getTextOriginal(),
                comment.getSnippet().getAuthorDisplayName(),
                comment.getSnippet().getAuthorProfileImageUrl(),
                comment.getSnippet().getLikeCount(),
                comment.getSnippet().getPublishedAt()
        );
    }

    public Comment( CommentThread comment ) {
        this( comment.getSnippet().getTopLevelComment() );

        if ( comment.getReplies() != null && !comment.getReplies().isEmpty() )
            addReplies( comment.getReplies() );

        // Sort replies based on the number of likes (reversed);
        this.replies.sort( ( r1, r2 ) -> r2.likes.compareTo( r1.likes ) );
    }

    private void addReplies( CommentThreadReplies replies ) {
        List<CompletableFuture<Void>> tasks = new ArrayList<>( replies.size() );
        for (com.google.api.services.youtube.model.Comment reply : replies.getComments())
            tasks.add(
                    CompletableFuture.runAsync( () -> this.replies.add( new Comment( reply ) ) )
            );
        CompletableFuture.allOf( tasks.toArray( CompletableFuture[]::new ) ).join();
    }
}
