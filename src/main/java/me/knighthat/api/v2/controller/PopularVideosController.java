package me.knighthat.api.v2.controller;

import com.google.api.services.youtube.model.Video;
import lombok.SneakyThrows;
import me.knighthat.api.utils.Concurrency;
import me.knighthat.api.utils.SystemInfo;
import me.knighthat.api.v2.YoutubeAPI;
import me.knighthat.api.v2.instance.preview.VideoPreviewCard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
@RequestMapping( "/v2" )
public class PopularVideosController {

    private @NotNull List<Video> popularVideos( long max, @Nullable String region ) throws IOException, IllegalArgumentException {
        if ( max < 0 )
            /* Set number of results return to maximum allowed */
            max = 50;
        if ( max == 0 )
            /* No need to waste quota on 0 result query */
            return Collections.emptyList();

        if ( region == null )
            region = SystemInfo.countryCode();
        /* Invalid country code */
        if ( region.length() != 2 )
            throw new IllegalArgumentException( "\"region\" can only be a 2 characters string!" );

        return YoutubeAPI.getService().videos()
                         .list( "contentDetails,id,snippet,statistics,status,topicDetails" )
                         .setKey( YoutubeAPI.API_KEY )
                         .setChart( "mostPopular" )
                         .setMaxResults( max )
                         .setRegionCode( region )
                         .execute()
                         .getItems();
    }

    @GetMapping( "/popular" )
    @CrossOrigin
    @SneakyThrows( IOException.class )
    public @NotNull ResponseEntity<?> popular(
            @RequestParam( required = false, defaultValue = "50" ) int max,
            @RequestParam( required = false ) String region
    ) {
        List<Video> videos = this.popularVideos( max, region );

        Set<VideoPreviewCard> cards = new CopyOnWriteArraySet<>();
        Concurrency.voidAsync( videos, video -> cards.add( new VideoPreviewCard( video ) ) );

        return ResponseEntity.ok( cards );
    }
}
