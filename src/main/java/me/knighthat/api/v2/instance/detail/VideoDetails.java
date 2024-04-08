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

import com.google.api.services.youtube.model.Video;
import lombok.Getter;
import me.knighthat.api.v2.instance.preview.VideoPreviewCard;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

@Getter
public class VideoDetails extends VideoPreviewCard {

    @NotNull
    private final String     description;
    @NotNull
    private final BigInteger commentCount;

    public VideoDetails( @NotNull Video video ) {
        super( video );
        this.description = video.getSnippet().getDescription();
        this.commentCount = video.getStatistics().getCommentCount();
    }
}
