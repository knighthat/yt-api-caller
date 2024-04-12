package me.knighthat.api.v2;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.common.base.Preconditions;
import me.knighthat.api.v2.logging.Logger;
import me.knighthat.api.youtube.Part;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Component
public class YoutubeAPI {

    public static final String  API_KEY = System.getenv( "API_KEY" );
    public static final String  APP_NAME;
    private static      YouTube service;

    static {
        String appName = System.getenv( "APP_NAME" );
        if ( appName == null || appName.isBlank() )
            APP_NAME = Env.VariableNames.APP_NAME.def.toString();
        else
            APP_NAME = appName;
    }

    public static void init() throws GeneralSecurityException, IOException {
        Logger.info( "Initializing YouTube Service..." );

        service = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                null
        ).setApplicationName( APP_NAME ).build();

        Logger.info( "YouTube Service is up and running!" );
    }

    /**
     * @return portal to talk to YouTubeAPI
     *
     * @throws NullPointerException if service hasn't been initialized at startup
     */
    public static YouTube getService() throws NullPointerException {
        Preconditions.checkNotNull( service, "YouTubeAPI hasn't been initialized yet!" );
        return service;
    }

    public @NotNull YouTube.Videos.List videos() throws IOException {
        Part.Builder part = Part.builder()
                                .content()
                                .id()
                                .snippet()
                                .statistics()
                                .status()
                                .topic();

        return getService().videos()
                           .list( part.build() )
                           .setKey( API_KEY );
    }

    public @NotNull YouTube.Channels.List channels() throws IOException {
        Part.Builder part = Part.builder()
                                .branding()
                                .content()
                                .snippet()
                                .statistics()
                                .status()
                                .topic();

        return getService().channels()
                           .list( part.build() )
                           .setKey( API_KEY );
    }

    public @NotNull YouTube.Search.List search() throws IOException {
        return getService().search()
                           .list( Part.builder().snippet().build() )
                           .setKey( API_KEY );
    }

    public @NotNull YouTube.CommentThreads.List comments() throws IOException {
        Part.Builder part = Part.builder()
                                .id()
                                .snippet()
                                .replies();

        return getService().commentThreads()
                           .list( part.build() )
                           .setKey( API_KEY );
    }

    public @NotNull YouTube.Comments.List replies() throws IOException {
        return getService().comments()
                           .list( Part.builder().id().snippet().build() )
                           .setKey( API_KEY );
    }
}
