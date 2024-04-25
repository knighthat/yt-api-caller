/*
 * Copyright (c) 2024 Knight Hat. All Rights Reserved.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.knighthat.api.v2.controller.playback;

import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import me.knighthat.api.v2.logging.Logger;
import me.knighthat.youtubedl.YoutubeDL;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.function.BiConsumer;

@RestController
@RequestMapping( "/v2/playback" )
public class PlaybackController {

    @GetMapping( path = "/{videoId}", produces = "video/mp4" )
    @SneakyThrows( IOException.class )
    @CrossOrigin
    public void streamVideo( @PathVariable String videoId, HttpServletResponse response ) {

        response.setContentType( "video/mp4" );
        response.setHeader( "Content-Disposition", "inline; filename=\"video.mp4\"" );

        String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

        OutputStream outStream = response.getOutputStream();
        BiConsumer<byte[], Integer> stream = ( buffer, length ) -> {
            try {
                outStream.write( buffer, 0, length );
            } catch ( IOException e ) {
                Logger.severe( "Error occurs while streaming " + videoUrl );
                Logger.severe( "Reason: " + e.getMessage() );
            }
        };

        try {
            YoutubeDL.stream( videoUrl )
                     .flags( () -> Map.of( "--limit-rate", "130K" ) )
                     .stream( 1024, stream );
        } catch ( InterruptedException e ) {
            Logger.severe( "Thread interrupted during stream (" + videoUrl + ")" );
            Logger.severe( "Reason: " + e.getMessage() );
        }

        outStream.close();
    }
}
