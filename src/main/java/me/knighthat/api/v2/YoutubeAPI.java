package me.knighthat.api.v2;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import com.google.common.base.Preconditions;
import me.knighthat.api.utils.ArrayUtils;
import me.knighthat.api.utils.SystemInfo;
import me.knighthat.api.v2.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

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

    /**
     * This method will send HTTP request to YouTubeAPI
     * to get information about video(s) based on provided id(s).
     *
     * @param max    number of maximum results YouTubeAPI will return
     * @param region country code, will be replaced by system's country code if value is null
     * @param id     set of ids to query info, at least 1 id must be provided
     *
     * @return list of Videos can be found by YouTubeAPI
     *
     * @throws IOException              when there's something wrong with the API
     * @throws IllegalArgumentException if no id were provided or region had abnormal length
     */
    public static @NotNull List<Video> videos( long max, @Nullable String region, @NotNull String... id ) throws IOException, IllegalArgumentException {
        if ( id.length < 1 )
            throw new IllegalArgumentException( "No channel id were provided!" );

        if ( max < 0 )
            /*
            Set number of results return by YouTubeAPI to 'ids' size
            if provided value is a sub-zero number.
            */
            max = id.length;
        if ( max == 0 )
            /* No need to waste quota on 0 result query */
            return Collections.emptyList();

        if ( region == null )
            region = SystemInfo.countryCode();
        /* Invalid country code */
        if ( region.length() != 2 )
            throw new IllegalArgumentException( "\"region\" can only be a 2 characters string!" );

        return getService().videos()
                           .list( "contentDetails,id,snippet,statistics,status,topicDetails" )
                           .setKey( API_KEY )
                           .setId( ArrayUtils.toString( id, "," ) )
                           .setMaxResults( max )
                           .setRegionCode( region )
                           .execute()
                           .getItems();
    }

    /**
     * Retrieve information about a channel by its channel's id.
     *
     * @param max number of maximum results YouTubeAPI will return
     * @param id  set of ids to query info, at least 1 id must be provided
     *
     * @return list of Channels can be found by YouTubeAPI
     *
     * @throws IOException              when there's something wrong with the API
     * @throws IllegalArgumentException if no id were provided or region had abnormal length
     */
    public static @NotNull List<Channel> channels( long max, @NotNull String... id ) throws IOException, IllegalArgumentException {
        if ( id.length < 1 )
            throw new IllegalArgumentException( "No channel id were provided!" );

        if ( max < 0 )
            /*
            Set number of results return by YouTubeAPI to 'ids' size
            if provided value is a sub-zero number.
            */
            max = id.length;
        else if ( max == 0 )
            /* No need to waste quota on 0 result query */
            return Collections.emptyList();

        return getService().channels()
                           .list( "brandingSettings,contentDetails,snippet,statistics,status,topicDetails" )
                           .setKey( API_KEY )
                           .setId( ArrayUtils.toString( id, "," ) )
                           .setMaxResults( max )
                           .execute()
                           .getItems();
    }
}
