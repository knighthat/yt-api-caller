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

package me.knighthat.api.v2.instance.detail;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ImageSettings;
import lombok.Getter;
import me.knighthat.api.v2.instance.preview.ChannelPreviewCard;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

@Getter
public class ChannelDetails extends ChannelPreviewCard {

    @NotNull
    private final BigInteger subscribers;
    @NotNull
    private final String     banner;
    @NotNull
    private final BigInteger views;
    @NotNull
    private final String     description;

    public ChannelDetails( @NotNull Channel channel ) {
        super( channel );
        this.subscribers = channel.getStatistics().getSubscriberCount();
        this.views = channel.getStatistics().getViewCount();
        this.description = channel.getSnippet().getDescription();

        ImageSettings image = channel.getBrandingSettings().getImage();
        if ( image != null && image.getBannerExternalUrl() != null )
            this.banner = image.getBannerExternalUrl();
        else
            this.banner = "";
    }
}
