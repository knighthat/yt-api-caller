package me.knighthat.api.v1;

import com.google.api.services.youtube.model.Video;
import me.knighthat.api.v1.instances.VideoPlayer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping( "/v1" )
public class VideoPlayerController {

    private VideoPlayer fetchVideoPlayer( String id ) {
        try {
            Video video = YoutubeAPI.getService()
                                    .videos()
                                    .list( "id,snippet,statistics" )
                                    .setKey( YoutubeAPI.API_KEY )
                                    .setId( id )
                                    .execute()
                                    .getItems()
                                    .get( 0 );

            return new VideoPlayer( video );
        } catch ( IOException e ) {
            System.err.println( "Error occurs while creating VideoPlayer id: " + id );
            e.printStackTrace();

            return VideoPlayer.DUMMY;
        }
    }

    @GetMapping( value = "/player/{id}" )
    @CrossOrigin
    public ResponseEntity<VideoPlayer> videoPlayer( @PathVariable( "id" ) String id ) {
        ResponseEntity.BodyBuilder builder;

        if ( !id.isEmpty() ) {
            VideoPlayer player = fetchVideoPlayer( id );
            if ( player == VideoPlayer.DUMMY )
                builder = ResponseEntity.internalServerError();
            else
                return ResponseEntity.ok( player );
        } else
            builder = ResponseEntity.badRequest();

        return builder.build();
    }
}
