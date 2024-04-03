package me.knighthat.api.v1.instances;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import lombok.Getter;
import me.knighthat.api.youtube.YoutubeAPI;

import java.math.BigInteger;

@Getter
public final class VideoPreviewCard extends PreviewCard {

    private final VideoDuration      duration;
    private final String             title;
    private final BigInteger         likes;
    private final BigInteger         views;
    private final ChannelPreviewCard publisher;

    public VideoPreviewCard( Video video ) {
        super(
                video.getId(),
                CardType.VIDEO,
                video.getSnippet().getThumbnails().getHigh().getUrl(),
                video.getSnippet().getPublishedAt()
        );
        this.duration = VideoDuration.fromString( video.getContentDetails().getDuration() );
        this.title = video.getSnippet().getTitle();
        this.likes = video.getStatistics().getLikeCount();
        this.views = video.getStatistics().getViewCount();

        Channel channel = YoutubeAPI.channel( video.getSnippet().getChannelId(), "snippet" );
        this.publisher = channel != null ? new ChannelPreviewCard( channel ) : ChannelPreviewCard.DUMMY;
    }

    @Override
    @JsonIgnore
    public String getThumbnail() { return super.getThumbnail(); }

    record VideoDuration( int hours, int minutes, int seconds ) {

        public static VideoDuration DEFAULT = new VideoDuration( 0, 0, 0 );

        public static VideoDuration fromString( String str ) {
            if ( !str.startsWith( "PT" ) )
                return DEFAULT;

            // Start @2 to skip "PT"
            int start = 2, end = start, n = str.length();
            int hours = 0, minutes = 0, seconds = 0;

            while (end < n) {
                char c = str.charAt( end );
                if ( Character.isDigit( c ) ) {
                    end++;
                    continue;
                }

                int value = Integer.parseInt( str.substring( start, end ) );
                switch (c) {
                    case 'H':
                        hours = value;
                    case 'M':
                        minutes = value;
                    case 'S':
                        seconds = value;
                }
                start = end + 1;
                end = start;
            }

            return new VideoDuration( hours, minutes, seconds );
        }
    }
}
