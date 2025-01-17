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

package me.knighthat.api.v2.exception;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.StringJoiner;

public class ConflictRequestParamException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -243958473019475837L;

    @NotNull
    private final String[] params;

    public ConflictRequestParamException( String... params ) {
        super( "" );
        this.params = params;
    }

    @Override
    public String getMessage() {
        StringJoiner builder = new StringJoiner( ",", "[", "]" );
        for (String p : params)
            builder.add( p );

        return "Only one of these query are accepted %s".formatted( builder.toString() );
    }
}
