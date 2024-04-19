package me.knighthat.api.utils;

import org.checkerframework.checker.index.qual.Positive;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.StringJoiner;

public class ArrayUtils {

    @Contract(
            value = "null,_ -> null",
            pure = true
    )
    public static @Nullable String toString( @Nullable StackTraceElement[] arr, @NotNull String delimiter ) {
        if ( arr == null )
            return null;

        StringJoiner builder = new StringJoiner( delimiter );
        for (StackTraceElement e : arr)
            if ( e != null )
                builder.add( e.toString() );

        return builder.toString();
    }

    @Contract(
            value = "null,_,_ -> null",
            pure = true
    )
    public static @Nullable String toString( @Nullable String[] arr, @NotNull String delimiter, @Positive int from ) {
        if ( arr == null )
            return null;

        StringJoiner builder = new StringJoiner( delimiter );
        for (int i = 0 ; i < arr.length ; i++) {
            if ( i >= from )
                builder.add( arr[i] );
        }

        return builder.toString();
    }
}
