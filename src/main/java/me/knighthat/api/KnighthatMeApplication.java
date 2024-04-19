package me.knighthat.api;

import me.knighthat.api.v2.logging.Logger;
import me.knighthat.api.youtubedl.YoutubeDL;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.security.GeneralSecurityException;

@SpringBootApplication
public class KnighthatMeApplication {

    public static void main( String[] args ) {
        SpringApplication.run( KnighthatMeApplication.class, args );

        /* Startup stage - Load environment variables & start YouTubeAPI */
        try {
            me.knighthat.api.v2.Env.verify();
        } catch ( Exception e ) {
            Logger.severe( "Error occurs during startup!" );
            Logger.severe( "Reason: " + e.getMessage() );

            System.exit( 1 );
        }

        /*
            Load dependencies
        */
        try {
            YoutubeDL.init();
        } catch ( IOException e ) {
            Logger.severe( "Error occurs while verifying youtube-dl version!" );
            Logger.severe( "Reason: " + e.getMessage() );
            System.exit( 1 );
        } catch ( InterruptedException e ) {
            Logger.severe( "Thread interrupted during youtube-dl verification process" );
            Logger.severe( "Reason: " + e.getMessage() );
            System.exit( 1 );
        }


        /* Running stage - initialize YouTube Service */
        try {
            me.knighthat.api.v2.YoutubeAPI.init();
        } catch ( GeneralSecurityException | IOException e ) {
            Logger.severe( "Error occurs while setting up Youtube Service!" );
            Logger.severe( "Reason: " + e.getMessage() );

            System.exit( 1 );
        }
    }
}
