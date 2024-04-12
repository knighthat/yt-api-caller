package me.knighthat.api.utils;

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
}
