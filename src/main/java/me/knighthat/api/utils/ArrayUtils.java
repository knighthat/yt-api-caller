package me.knighthat.api.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.StringJoiner;

public class ArrayUtils {

    /**
     * Convert an array of strings to a single string
     * separated by 'delimiter'.
     * <p>
     * 'delimiter' will not be placed at the beginning
     * and at the end of the final result.
     *
     * @param arr       array to be converted
     * @param delimiter separator between each element
     *
     * @return new string contains every element with 'delimiter' in between, null if provided 'arr' is null.
     */
    @Contract(
            value = "null,_ -> null",
            pure = true
    )
    public static @Nullable String toString( @Nullable String[] arr, @NotNull String delimiter ) {
        if ( arr == null )
            return null;

        StringJoiner builder = new StringJoiner( delimiter );
        for (String e : arr)
            builder.add( e );

        return builder.toString();
    }

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
