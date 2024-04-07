package me.knighthat.api.v2.error;


import lombok.Data;
import me.knighthat.api.utils.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;

@Data
public abstract class AbstractErrorTemplate {

    @NotNull
    private final String reason;
    @NotNull
    private final String stackTrace;
    
    AbstractErrorTemplate( @NotNull Throwable throwable ) {
        String stackTrace = ArrayUtils.toString( throwable.getStackTrace(), "\n" );
        if ( stackTrace == null )
            stackTrace = "empty!";

        this.reason = throwable.getMessage();
        this.stackTrace = stackTrace;
    }

    abstract @NotNull ResponseEntity<? extends AbstractErrorTemplate> makeResponse();
}