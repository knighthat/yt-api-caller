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

package me.knighthat.api.utils;

import me.knighthat.api.v2.exception.ConflictRequestParamException;
import me.knighthat.api.v2.exception.MissingRequestParamException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Sanitizer {

    public static @NotNull String countryCode( @Nullable String region ) {
        if ( region == null )
            return SystemInfo.countryCode();

        if ( region.length() != 2 )
            throw new IllegalArgumentException( "\"region\" must be a 2 characters string!" );

        return region;
    }

    public static void atLeastOneNotNull(
            @Nullable Object obj1,
            @Nullable Object obj2,
            @NotNull String obj1Name,
            @NotNull String obj2Name
    ) {
        if ( obj1 == null && obj2 == null )
            throw new MissingRequestParamException( obj1Name, obj2Name );
        if ( obj1 != null && obj2 != null )
            throw new ConflictRequestParamException( obj1Name, obj2Name );
    }

    public static boolean noReturnExpected( long max ) {
        if ( max < 0 )
            throw new IllegalArgumentException( "\"max\" must be a positive number!" );

        return max == 0;
    }
}
