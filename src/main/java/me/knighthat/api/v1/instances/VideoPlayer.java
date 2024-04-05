package me.knighthat.api.v1.instances;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.Video;
import lombok.Data;
import me.knighthat.api.v1.YoutubeAPI;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public final class VideoPlayer {

    public static final VideoPlayer DUMMY = new VideoPlayer();

    private final String             id;
    private final String             title;
    private final String             description;
    private final BigInteger         likes;
    private final BigInteger         views;
    private final DateTime           since;
    private final BigInteger         commentCount;
    private final List<Comment>      comments;
    private final ChannelPreviewCard publisher;

    private VideoPlayer() {
        this(
                "",
                "",
                "",
                BigInteger.ZERO,
                BigInteger.ZERO,
                new DateTime( new Date() ),
                BigInteger.ZERO,
                ChannelPreviewCard.DUMMY
        );
    }

    public VideoPlayer( String id, String title, String description, BigInteger likes, BigInteger views, DateTime since, BigInteger commentCount, ChannelPreviewCard publisher ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.likes = likes;
        this.views = views;
        this.since = since;
        this.commentCount = commentCount;
        this.comments = new CopyOnWriteArrayList<>();
        this.publisher = publisher;
    }

    public VideoPlayer( Video video ) {
        this.id = video.getId();
        this.title = video.getSnippet().getTitle();
        this.description = video.getSnippet().getDescription();
        this.likes = video.getStatistics().getLikeCount();
        this.views = video.getStatistics().getViewCount();
        this.since = video.getSnippet().getPublishedAt();
        this.commentCount = video.getStatistics().getCommentCount();
        this.comments = new CopyOnWriteArrayList<>();
        this.publisher = retrieveChannel( video.getSnippet().getChannelId() );

        if ( this.commentCount.compareTo( BigInteger.ZERO ) > 0 )
            addComments();

        // Sort replies based on the number of likes (reversed);
        this.comments.sort( ( c1, c2 ) -> c2.getLikes().compareTo( c1.getLikes() ) );
    }

    private ChannelPreviewCard retrieveChannel( String id ) {
        try {
            Channel channel = YoutubeAPI.getService()
                                        .channels()
                                        .list( "snippet" )
                                        .setKey( YoutubeAPI.API_KEY )
                                        .setId( id )
                                        .execute()
                                        .getItems()
                                        .get( 0 );
            return new ChannelPreviewCard( channel );
        } catch ( IOException e ) {
            System.err.println( "Error while retrieving channel information of: " + this.id );
            return ChannelPreviewCard.DUMMY;
        }
    }

    private void addComments() {
        try {
            List<CommentThread> comments = YoutubeAPI.getService()
                                                     .commentThreads()
                                                     .list( "id,snippet,replies" )
                                                     .setKey( YoutubeAPI.API_KEY )
                                                     .setVideoId( this.id )
                                                     .setMaxResults( 100L )
                                                     .setModerationStatus( "published" )
                                                     .setOrder( "relevance" )
                                                     .execute()
                                                     .getItems();

            List<CompletableFuture<Void>> tasks = new ArrayList<>( comments.size() );
            for (CommentThread comment : comments)
                tasks.add(
                        CompletableFuture.runAsync( () -> this.comments.add( new Comment( comment ) ) )
                );
            CompletableFuture.allOf( tasks.toArray( CompletableFuture[]::new ) ).join();

        } catch ( IOException e ) {
            System.err.println( "Error occurs while fetching comments of: " + this.id );
            e.printStackTrace();
        }
    }
}
