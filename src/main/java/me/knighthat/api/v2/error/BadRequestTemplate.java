package me.knighthat.api.v2.error;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;

public final class BadRequestTemplate extends AbstractErrorTemplate {

    public static @NotNull ResponseEntity<BadRequestTemplate> body( @NotNull String reason ) {
        return new BadRequestTemplate( reason, "" ).makeResponse();
    }

    BadRequestTemplate( @NotNull Throwable throwable ) { super( throwable ); }

    BadRequestTemplate( @NotNull String reason, @NotNull String stackTrace ) { super( reason, stackTrace ); }

    @Override
    @NotNull ResponseEntity<BadRequestTemplate> makeResponse() {
        return ResponseEntity.badRequest().body( this );
    }
}
