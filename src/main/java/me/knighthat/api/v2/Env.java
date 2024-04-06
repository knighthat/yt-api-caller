package me.knighthat.api.v2;

import me.knighthat.api.v2.logging.Logger;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;

public class Env {

    /**
     * This checks for missing environment variables
     * or variable(s) that don't follow conventional rules
     * <p>
     * NOTE: undefined/empty variables are considered
     * missing unless it is not required
     *
     * @throws MissingEnvironmentVariableException if any of the required environment variables is missing or empty
     * @throws IllegalArgumentException            when variable's value contains an illegal character
     * @see VariableNames
     */
    public static void verify() {
        Logger.info( "Verifying environment variables..." );

        for (VariableNames var : VariableNames.values()) {
            String value = System.getenv( var.name() );

            if ( value == null || value.isBlank() )
                if ( var.required )
                    throw new MissingEnvironmentVariableException( var + " is missing!" );
                else {
                    Logger.warning( "Environment variable: " + var + " is missing!" );
                    continue;
                }

            // This will ensure no variable's value contains space character
            if ( value.contains( " " ) )
                throw new IllegalArgumentException( "\"%s\" cannot contain space!".formatted( value ) );
        }

        Logger.info( "Verification completed!" );
    }

    /**
     * A set of environment variables that will
     * be used throughout the lifespan of program.
     * There are 2 types - required and not required.
     * <p>
     * Non-essential ('required' set to false) variable
     * should have 'def' set to value other than null
     * as the fallback value.
     */
    enum VariableNames {
        API_KEY( true, null ),
        APP_NAME( false, "YTF" );

        final boolean required;
        final Object  def;

        VariableNames( boolean required, @Nullable Object def ) {
            this.required = required;
            this.def = def;
        }
    }
}
