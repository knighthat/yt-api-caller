package me.knighthat.api.v2.controller;

import lombok.SneakyThrows;
import me.knighthat.api.utils.Concurrency;
import me.knighthat.api.utils.Sanitizer;
import me.knighthat.api.v2.YoutubeAPI;
import me.knighthat.api.v2.instance.preview.VideoPreviewCard;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
@RequestMapping( "/v2" )
public class PopularVideosController {

    @NotNull
    private final YoutubeAPI service;

    @Autowired
    public PopularVideosController( @NotNull YoutubeAPI service ) {
        this.service = service;
    }

    @GetMapping( "/popular" )
    @CrossOrigin
    @SneakyThrows( IOException.class )
    public @NotNull ResponseEntity<?> popular(
            @RequestParam( required = false, defaultValue = "50" ) int max,
            @RequestParam( required = false ) String region
    ) {
        if ( max < 0 )
            throw new IllegalArgumentException( "\"max\" must be a positive number!" );
        if ( max == 0 )
            return ResponseEntity.ok( Collections.emptyList() );

        region = Sanitizer.countryCode( region );

        Set<VideoPreviewCard> cards = new CopyOnWriteArraySet<>();

        Concurrency.voidAsync(
                service.videos()
                       .setChart( "mostPopular" )
                       .setMaxResults( (long) max )
                       .setRegionCode( region )
                       .execute()
                       .getItems(),
                video -> cards.add( new VideoPreviewCard( video ) )
        );

        return ResponseEntity.ok( cards );
    }
}
