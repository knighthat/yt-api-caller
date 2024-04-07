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
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelSnippet;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ChannelPreviewCard extends PreviewCard {

    @NotNull
    private final String title;
    @NotNull
    private final String url;
    @NotNull
    private final String thumbnail;

    public ChannelPreviewCard(
            @NotNull String id,
            @NotNull DateTime since,
            @NotNull String title,
            @NotNull String url,
            @NotNull String thumbnail
    ) {
        super( id, since, Type.CHANNEL );
        this.title = title;
        this.url = url;
        this.thumbnail = thumbnail;
    }

    public ChannelPreviewCard( @NotNull Channel channel ) {
        this(
                channel.getId(),
                channel.getSnippet().getPublishedAt(),
                channel.getSnippet().getTitle(),
                channel.getSnippet().getCustomUrl(),
                channel.getSnippet().getThumbnails().getMedium().getUrl()
        );
    }

    public ChannelPreviewCard( @NotNull String id, @NotNull ChannelSnippet snippet ) {
        super( id, snippet.getPublishedAt(), Type.CHANNEL );

        /*
        On some channel (e.g. Topic) does not have
        custom url, use good ol' link format to
        prevent this from throwing error.
        */
        String url = "channel/" + this.getId();
        if ( snippet.getCustomUrl() != null )
            url = snippet.getCustomUrl();

        this.title = snippet.getTitle();
        this.url = url;
        this.thumbnail = snippet.getThumbnails().getMedium().getUrl();
    }
}
