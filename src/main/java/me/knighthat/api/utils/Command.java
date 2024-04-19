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

package me.knighthat.api.utils;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Command {

    public static @NotNull List<String> captureOutput( @NotNull String... args ) throws InterruptedException, IOException {
        List<String> outputs = new ArrayList<>();

        Process process = new ProcessBuilder( args ).start();
        if ( process.waitFor() == 0 ) {
            InputStreamReader reader = new InputStreamReader( process.getInputStream() );
            BufferedReader bReader = new BufferedReader( reader );

            String line;
            while ((line = bReader.readLine()) != null) {
                if ( !line.isBlank() )
                    outputs.add( line );
            }

            bReader.close();
            reader.close();
        }

        return outputs;
    }
}
