package me.knighthat.api;

import me.knighthat.api.v2.logging.Logger;
import me.knighthat.api.zip.ZipExtractor;
import me.knighthat.youtubedl.YoutubeDL;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.zip.ZipInputStream;

@SpringBootApplication
public class KnighthatMeApplication {

    @NotNull
    private static final String REPO_NAME   = "youtube-dl";
    @NotNull
    private static final String REPO_AUTHOR = "ytdl-org";

    public static void main( String[] args ) {
        SpringApplication.run( KnighthatMeApplication.class, args );

        boolean exit = false;
        short exitCode = 0;

        /* Startup stage - Load environment variables & start YouTubeAPI */
        try {
            me.knighthat.api.v2.Env.verify();
        } catch ( Exception e ) {
            Logger.severe( "Error occurs during startup!" );
            Logger.severe( "Reason: " + e.getMessage() );

            exit = true;
            exitCode = 1;
        }

        /*
            Load dependencies
        */
        byte retry = 0;
        boolean downloadYoutubeDL = false;
        do {
            try {
                if ( downloadYoutubeDL ) {
                    String repoUrl = "https://api.github.com/repos/%s/%s/zipball/".formatted( REPO_AUTHOR, REPO_NAME );

                    try (
                            CloseableHttpClient httpClient = HttpClients.createDefault() ;
                            CloseableHttpResponse responseContent = httpClient.execute( new HttpGet( repoUrl ) ) ;
                            InputStream contentStream = responseContent.getEntity().getContent() ;
                            ZipInputStream zis = new ZipInputStream( contentStream )
                    ) {
                        ZipExtractor.extractHere( zis );
                    }
                }

                YoutubeDL.init( "python", "youtube_dl/__main__.py" );
            } catch ( IOException e ) {
                Logger.severe( "Error occurs while verifying youtube-dl version!" );
                Logger.severe( "Reason: " + e.getMessage() );

                exit = true;
                exitCode = 127;
            } catch ( InterruptedException e ) {
                Logger.severe( "Verification process interrupted!" );
                Logger.severe( "Reason: " + e.getMessage() );

                exit = true;
                exitCode = 128;
            } catch ( UnsupportedOperationException e ) {
                retry++;
                downloadYoutubeDL = true;
            }
        } while (retry < 2 || exit);

        /* Running stage - initialize YouTube Service */
        try {
            me.knighthat.api.v2.YoutubeAPI.init();
        } catch ( GeneralSecurityException | IOException e ) {
            Logger.severe( "Error occurs while setting up Youtube Service!" );
            Logger.severe( "Reason: " + e.getMessage() );

            exit = true;
            exitCode = 1;
        }

        if ( exit )
            System.exit( exitCode );
    }
}
