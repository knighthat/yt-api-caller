package me.knighthat.api.v1.trending;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import me.knighthat.api.utils.SystemInfo;
import me.knighthat.api.v1.instances.ChannelPreviewCard;
import me.knighthat.api.v1.instances.PreviewCard;
import me.knighthat.api.v1.instances.VideoPreviewCard;
import me.knighthat.api.youtube.YoutubeAPI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
@RequestMapping( "/v1" )
public class SearchController {

    private List<SearchResult> findByKeyword( String keyword, long size, String region ) {
        try {
            return YoutubeAPI.getService()
                             .search()
                             .list( "snippet" )
                             .setKey( YoutubeAPI.API_KEY )
                             .setQ( keyword )
                             .setRegionCode( region == null ? SystemInfo.countryCode() : region )
                             .setMaxResults( size )
                             .execute()
                             .getItems();
        } catch ( IOException e ) {
            System.err.println( "Error occurs while searching with keyword: " + keyword );
            e.printStackTrace();
            return List.of();
        }
    }

    private List<Video> fetchVideos( List<String> videoIds ) {
        if ( videoIds.size() == 0 )
            return List.of();

        StringJoiner ids = new StringJoiner( "," );
        videoIds.forEach( ids::add );

        try {
            return YoutubeAPI.getService()
                             .videos()
                             .list( "snippet,statistics,contentDetails" )
                             .setKey( YoutubeAPI.API_KEY )
                             .setId( ids.toString() )
                             .execute()
                             .getItems();
        } catch ( IOException e ) {
            System.err.println( "Error occurs while fetching videos" );
            e.printStackTrace();
            return List.of();
        }
    }

    private List<Channel> fetchChannels( List<String> channelIds ) {
        if ( channelIds.size() == 0 )
            return List.of();

        StringJoiner ids = new StringJoiner( "," );
        channelIds.forEach( ids::add );

        try {
            return YoutubeAPI.getService()
                             .channels()
                             .list( "snippet" )
                             .setKey( YoutubeAPI.API_KEY )
                             .setId( ids.toString() )
                             .execute()
                             .getItems();
        } catch ( IOException e ) {
            System.err.println( "Error occurs while fetching channels" );
            e.printStackTrace();
            return List.of();
        }
    }

    @GetMapping( "/search" )
    @CrossOrigin
    public ResponseEntity<Set<PreviewCard>> search(
            @RequestParam String key,
            @RequestParam( required = false, defaultValue = "1" ) int max,
            @RequestParam( required = false ) String region
    ) {
        final List<String> videoIds = new ArrayList<>();
        final List<String> channelIds = new ArrayList<>();

        findByKeyword( key, max, region ).forEach( result -> {
            ResourceId resId = result.getId();
            switch (resId.getKind()) {
                case "youtube#video" -> videoIds.add( resId.getVideoId() );
                case "youtube#channel" -> channelIds.add( resId.getChannelId() );
            }
        } );

        Set<PreviewCard> cards = new CopyOnWriteArraySet<>();

        List<CompletableFuture<Void>> futureTasks = new ArrayList<>( videoIds.size() + channelIds.size() );
        for (Video video : fetchVideos( videoIds ))
            futureTasks.add(
                    CompletableFuture.runAsync( () -> cards.add( new VideoPreviewCard( video ) ) )
            );
        for (Channel channel : fetchChannels( channelIds ))
            futureTasks.add(
                    CompletableFuture.runAsync( () -> cards.add( new ChannelPreviewCard( channel ) ) )
            );
        CompletableFuture.allOf( futureTasks.toArray( CompletableFuture[]::new ) ).join();

        return ResponseEntity.ok( cards );
    }
}
