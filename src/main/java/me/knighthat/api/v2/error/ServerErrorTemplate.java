package me.knighthat.api.v2.error;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;

public class ServerErrorTemplate extends AbstractErrorTemplate {

    ServerErrorTemplate( @NotNull Throwable throwable ) { super( throwable ); }

    @Override
    @NotNull ResponseEntity<ServerErrorTemplate> makeResponse() {
        return ResponseEntity.internalServerError().body( this );
    }
}
