package me.knighthat.api.v2.error;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import me.knighthat.api.v2.exception.ConflictRequestParamException;
import me.knighthat.api.v2.exception.MissingRequestParamException;
import me.knighthat.api.v2.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler( MissingServletRequestParameterException.class )
    public ResponseEntity<BadRequestTemplate> handleMissingServletRequestParameterException( MissingServletRequestParameterException e ) {
        return new BadRequestTemplate( e ).makeResponse();
    }

    @ExceptionHandler( IllegalArgumentException.class )
    public ResponseEntity<BadRequestTemplate> handleIllegalArgumentException( IllegalArgumentException e ) {
        return new BadRequestTemplate( e ).makeResponse();
    }

    @ExceptionHandler( NullPointerException.class )
    public ResponseEntity<ServerErrorTemplate> handleNullPointerException( NullPointerException e ) {
        return new ServerErrorTemplate( e ).makeResponse();
    }

    @ExceptionHandler( IllegalStateException.class )
    public ResponseEntity<ServerErrorTemplate> handleIllegalStateException( IllegalStateException e ) {
        return new ServerErrorTemplate( e ).makeResponse();
    }

    @ExceptionHandler( GoogleJsonResponseException.class )
    public ResponseEntity<YoutubeAPIErrorTemplate> handleUndeclaredThrowableException( GoogleJsonResponseException e ) {
        YoutubeAPIErrorTemplate template = new YoutubeAPIErrorTemplate( e );

        Logger.severe( "YouTubeAPI returns error" );
        Logger.severe( "Reason: " + template.getReason() );

        return template.makeResponse();
    }

    @ExceptionHandler( NoResourceFoundException.class )
    public ResponseEntity<Map<String, String>> handleNoResourceFoundException( @NotNull NoResourceFoundException e ) {
        Logger.warning( "NoResourceFoundException: " + e.getMessage() );

        return ResponseEntity.status( 404 ).body( Map.of( "reason", e.getMessage() ) );
    }

    @ExceptionHandler( MissingRequestParamException.class )
    public ResponseEntity<RawErrorTemplate> handleMissingRequestParamException( @NotNull MissingRequestParamException e ) {
        return RawErrorTemplate.body( HttpStatus.NOT_FOUND, e.getMessage() );
    }

    @ExceptionHandler( ConflictRequestParamException.class )
    public ResponseEntity<RawErrorTemplate> handleConflictRequestParamException( @NotNull ConflictRequestParamException e ) {
        return RawErrorTemplate.body( HttpStatus.CONFLICT, e.getMessage() );
    }
}
