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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

public class UrlParser {

    @NotNull
    @Unmodifiable
    private static final Map<String, String> SPECIAL_CHARS;

    static {
        SPECIAL_CHARS = Map.of(
                "%20", " ",
                "%2B", "+",
                "%26", "&",
                "%3F", "?",
                "%2F", "/",
                "%3A", ":",
                "%3B", ";",
                "%3D", "=",
                "%23", "#",
                "%24", "$"
        );
    }

    @Contract( pure = true )
    public static @NotNull String specialCharacters( @NotNull String str ) {
        String result = str;

        for (Map.Entry<String, String> entry : SPECIAL_CHARS.entrySet())
            result = result.replace( entry.getKey(), entry.getValue() );

        return result;
    }

    @Contract( pure = true )
    public static @NotNull @Unmodifiable List<String> specialCharacters( @NotNull List<String> list ) {
        return list.stream().map( UrlParser::specialCharacters ).toList();
    }
}
