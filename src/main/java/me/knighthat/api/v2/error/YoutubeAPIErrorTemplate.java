package me.knighthat.api.v2.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;
import me.knighthat.api.utils.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
public class YoutubeAPIErrorTemplate {

    @NotNull
    private static final Gson GSON = new Gson();

    @JsonIgnore
    @NotNull
    private final HttpStatus status;
    @NotNull
    private final String     reason;
    @NotNull
    private final String     stackTrace;

    public YoutubeAPIErrorTemplate( @NotNull Throwable throwable ) {
        String stackTrace = ArrayUtils.toString( throwable.getStackTrace(), "\n" );
        if ( stackTrace == null )
            stackTrace = "empty!";

        String message = throwable.getMessage();
        int start = message.indexOf( "{" );
        int stop = message.lastIndexOf( "}" ) + 1;
        JsonObject reason = GSON.fromJson( message.substring( start, stop ), JsonObject.class );

        this.status = HttpStatus.valueOf( reason.get( "code" ).getAsInt() );
        this.reason = reason.get( "message" ).getAsString();
        this.stackTrace = stackTrace;
    }

    public @NotNull ResponseEntity<YoutubeAPIErrorTemplate> makeResponse() {
        return ResponseEntity.status( this.status ).body( this );
    }
}
