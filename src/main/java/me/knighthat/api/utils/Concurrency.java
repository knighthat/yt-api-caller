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

import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Concurrency {

    /**
     * Apply consumer to each element of
     * the provided list asynchronously.
     *
     * @param of       list contains elements to be consumed
     * @param consumer function to apply to each element of the provided list
     */
    public static <T> void voidAsync( @Nullable List<T> of, @Nullable Consumer<T> consumer ) {
        if ( of == null || consumer == null )
            return;

        Set<CompletableFuture<Void>> instances = new HashSet<>( of.size() );
        for (T e : of)
            instances.add( CompletableFuture.runAsync( () -> consumer.accept( e ) ) );

        /* Wait for all instances to finish */
        CompletableFuture.allOf( instances.toArray( CompletableFuture[]::new ) ).join();
    }
}
