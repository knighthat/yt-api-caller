package me.knighthat.api.v2.error;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;

public class ServerErrorTemplate extends AbstractErrorTemplate {

    public static @NotNull ResponseEntity<ServerErrorTemplate> body( @NotNull String reason ) {
        return new ServerErrorTemplate( reason, "" ).makeResponse();
    }

    ServerErrorTemplate( @NotNull Throwable throwable ) { super( throwable ); }

    ServerErrorTemplate( @NotNull String reason, @NotNull String stackTrace ) { super( reason, stackTrace ); }

    @Override
    @NotNull ResponseEntity<ServerErrorTemplate> makeResponse() {
        return ResponseEntity.internalServerError().body( this );
    }
}
