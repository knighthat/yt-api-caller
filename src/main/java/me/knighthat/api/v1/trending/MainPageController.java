package me.knighthat.api.v1.trending;

import com.google.api.services.youtube.model.Video;
import me.knighthat.api.v1.YoutubeAPI;
import me.knighthat.api.v1.instances.PreviewCard;
import me.knighthat.api.v1.instances.VideoPreviewCard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping( "/v1" )
@CrossOrigin
public class MainPageController {

    @GetMapping( "/popular" )
    public ResponseEntity<List<PreviewCard>> popular( @RequestParam( required = false ) int max, @RequestParam( required = false ) String region ) {
        List<PreviewCard> cards = new ArrayList<>( max );

        for (Video video : YoutubeAPI.getPopular( max, region ))
            cards.add( new VideoPreviewCard( video ) );

        return ResponseEntity.ok( cards );
    }
}
