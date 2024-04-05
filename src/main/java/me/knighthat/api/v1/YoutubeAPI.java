package me.knighthat.api.v1;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import com.google.common.base.Preconditions;
import me.knighthat.api.utils.SystemInfo;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.StringJoiner;

public class YoutubeAPI {

    public static final String  API_KEY = System.getenv( "YOUTUBE_API_KEY" );
    private static      YouTube YT_SERVICE;

    private static String list( String... parts ) {
        assert parts.length > 0;

        StringJoiner list = new StringJoiner( "," );
        for (String p : parts)
            list.add( p );

        return list.toString();
    }

    public static void init() {
        try {
            YT_SERVICE = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    null
            ).setApplicationName( "YTF" ).build();
        } catch ( GeneralSecurityException | IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public static YouTube getService() {
        Preconditions.checkNotNull( YT_SERVICE, "YoutubeAPI has NOT been initialized yet!" );
        return YT_SERVICE;
    }

    public static List<Video> getPopular( long size, String region ) {
        Preconditions.checkNotNull( YT_SERVICE, "YoutubeAPI has NOT been initialized yet!" );

        try {
            return YT_SERVICE.videos()
                             .list( "contentDetails,snippet,statistics,id" )
                             .setKey( API_KEY )
                             .setChart( "mostPopular" )
                             .setRegionCode( region == null ? SystemInfo.countryCode() : region )
                             .setMaxResults( size )
                             .execute()
                             .getItems();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public static Video video( String id, String... parts ) {
        Preconditions.checkNotNull( YT_SERVICE, "YoutubeAPI has NOT been initialized yet!" );

        try {
            return YT_SERVICE.videos()
                             .list( list( parts ) )
                             .setId( id )
                             .setKey( API_KEY )
                             .execute()
                             .getItems()
                             .get( 0 );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public static Channel channel( String id, String... parts ) {
        Preconditions.checkNotNull( YT_SERVICE, "YoutubeAPI has NOT been initialized yet!" );

        try {
            return YT_SERVICE.channels()
                             .list( list( parts ) )
                             .setKey( API_KEY )
                             .setId( id )
                             .execute()
                             .getItems()
                             .get( 0 );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }
}
