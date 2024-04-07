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

package me.knighthat.api.v2.instance;

import com.google.api.client.util.DateTime;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class InfoContainer {

    @NotNull
    private final String   id;
    @NotNull
    private final DateTime since;

    public InfoContainer( @NotNull String id, @NotNull DateTime since ) {
        this.id = id;
        this.since = since;
    }
}
