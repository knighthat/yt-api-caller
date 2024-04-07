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
import lombok.Getter;
import me.knighthat.api.v2.instance.InfoContainer;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class PreviewCard extends InfoContainer {

    @NotNull
    private final Type type;

    public PreviewCard( @NotNull String id, @NotNull DateTime since, @NotNull Type type ) {
        super( id, since );
        this.type = type;
    }

    public enum Type {
        VIDEO,
        CHANNEL;

        Type() { }
    }
}
