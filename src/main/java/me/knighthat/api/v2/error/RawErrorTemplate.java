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

package me.knighthat.api.v2.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class RawErrorTemplate extends AbstractErrorTemplate {

    public static @NotNull ResponseEntity<RawErrorTemplate> body( @NotNull HttpStatus status, @NotNull String reason ) {
        return new RawErrorTemplate( status, reason, "" ).makeResponse();
    }

    @JsonIgnore
    @NotNull
    private final HttpStatus status;

    private RawErrorTemplate( @NotNull HttpStatus status, @NotNull String reason, @NotNull String stackTrace ) {
        super( reason, stackTrace );
        this.status = status;
    }

    @Override
    @NotNull ResponseEntity<RawErrorTemplate> makeResponse() {
        return ResponseEntity.status( this.status ).body( this );
    }
}
