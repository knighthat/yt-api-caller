/*
 * Copyright (c) 2024 Knight Hat. All Rights Reserved.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.knighthat.api.v2.instance.preview;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.Video;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

@Getter
public class VideoPreviewCard extends PreviewCard {

    @NotNull
    private final VideoDuration duration;
    @NotNull
    private final String        title;
    @NotNull
    private final BigInteger    likes;
    @NotNull
    private final BigInteger    views;
    @NotNull
    private final String        publisherId;

    public VideoPreviewCard(
            @NotNull String id,
            @NotNull DateTime since,
            @NotNull VideoDuration duration,
            @NotNull String title,
            @NotNull BigInteger likes,
            @NotNull BigInteger views,
            @NotNull String publisherId
    ) {
        super( id, since, Type.VIDEO );
        this.duration = duration;
        this.title = title;
        this.likes = likes;
        this.views = views;
        this.publisherId = publisherId;
    }

    public VideoPreviewCard( @NotNull Video video ) {
        this(
                video.getId(),
                video.getSnippet().getPublishedAt(),
                VideoDuration.fromString( video.getContentDetails().getDuration() ),
                video.getSnippet().getTitle(),
                video.getStatistics().getLikeCount(),
                video.getStatistics().getViewCount(),
                video.getSnippet().getChannelId()
        );
    }

    public record VideoDuration( int hours, int minutes, int seconds ) {

        public static VideoDuration ZEROS = new VideoDuration( 0, 0, 0 );

        /**
         * Convert YouTube's time format 'PT12H36M00S'
         * to a class that represents each unit;
         *
         * @param str YouTube's duration format, must start with "PT"
         *
         * @return a class contains number of hours, minutes, and seconds converted from provided string
         */
        public static VideoDuration fromString( String str ) {
            if ( !str.startsWith( "PT" ) )
                return ZEROS;

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
