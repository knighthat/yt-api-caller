package me.knighthat.api;

import me.knighthat.api.youtube.YoutubeAPI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KnighthatMeApplication {

    public static void main( String[] args ) {
        SpringApplication.run( KnighthatMeApplication.class, args );

        YoutubeAPI.init();
    }
}
