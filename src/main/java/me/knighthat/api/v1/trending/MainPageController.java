package me.knighthat.api.v1.trending;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelSnippet;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import me.knighthat.api.v1.instances.PreviewCard;
import me.knighthat.api.youtube.YoutubeAPI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping( "/v1" )
@CrossOrigin
public class MainPageController {

    private int findTime( String str ) {
        StringBuilder result = new StringBuilder();
        for (int i = str.length() - 1 ; i >= 0 ; i--) {
            if ( Character.isDigit( str.charAt( i ) ) )
                result.append( str.charAt( i ) );
            else
                break;
        }

        try {
            return Integer.parseInt( result.toString() );
        } catch ( NumberFormatException ignored ) {
            return -1;
        }
    }

    private int convertDuration( String str ) {
        if ( !str.startsWith( "PT" ) )
            return -1;

        int duration = 0;
        int lastStop = 0;
        for (int i = 0 ; i < str.length() ; i++) {
            String strToFind = str.substring( lastStop, i );

            switch (str.charAt( i )) {
                case 'H' -> {
                    duration += findTime( strToFind ) * 60 * 60;
                    lastStop = i;
                }
                case 'M' -> {
                    duration += findTime( strToFind ) * 60;
                    lastStop = i;
                }
                case 'S' -> {
                    duration += findTime( strToFind );
                    lastStop = i;
                }
            }
        }

        return duration;
    }

    @GetMapping( "/popular" )
    @CrossOrigin
    public ResponseEntity<List<PreviewCard>> popular( @RequestParam( required = false ) int max, @RequestParam( required = false ) String region ) {
        List<PreviewCard> cards = new ArrayList<>( max );

        for (Video video : YoutubeAPI.getPopular( max, region )) {
            VideoSnippet vSnippet = video.getSnippet();
            Channel channel = YoutubeAPI.channel( vSnippet.getChannelId(), "snippet" );
            ChannelSnippet cSnippet = channel.getSnippet();

            cards.add( new PreviewCard(
                    video.getId(),
                    vSnippet.getThumbnails().getHigh().getUrl(),
                    convertDuration( video.getContentDetails().getDuration() ),
                    vSnippet.getLocalized().getTitle(),
                    cSnippet.getCustomUrl(),
                    cSnippet.getThumbnails().getDefault().getUrl(),
                    cSnippet.getTitle(),
                    video.getStatistics().getLikeCount(),
                    video.getStatistics().getViewCount(),
                    vSnippet.getPublishedAt()
            ) );
        }

        return ResponseEntity.ok( cards );
    }
}
