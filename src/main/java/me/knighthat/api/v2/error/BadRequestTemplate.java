package me.knighthat.api.v2.error;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;

public final class BadRequestTemplate extends AbstractErrorTemplate {

    BadRequestTemplate( @NotNull Throwable throwable ) { super( throwable ); }

    @Override
    @NotNull ResponseEntity<BadRequestTemplate> makeResponse() {
        return ResponseEntity.badRequest().body( this );
    }
}
