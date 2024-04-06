package me.knighthat.api.v2.logging;

import org.jetbrains.annotations.NotNull;

public class Logger {

    private static final @NotNull java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger( "YTF" );

    public static void info( @NotNull String s ) {
        LOGGER.info( s );
    }

    public static void warning( @NotNull String s ) {
        LOGGER.warning( s );
    }

    public static void severe( @NotNull String s ) {
        LOGGER.severe( s );
    }
}
