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

package me.knighthat.api.zip;

import me.knighthat.api.utils.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor {

    @NotNull
    private static final String SEPARATOR = String.valueOf( File.separatorChar );

    public static void extractHere( @NotNull ZipInputStream zis ) throws IOException {
        ZipEntry zipEntry;
        while ((zipEntry = zis.getNextEntry()) != null) {
            // Only keep files inside "youtube_dl" directory
            String[] parts = zipEntry.getName().split( SEPARATOR );
            if ( parts.length <= 2 || !parts[1].equals( "youtube_dl" ) )
                continue;

            // All files start with "ytdl-org-youtube-dl-*/youtube_dl"
            // This step is to remove that first part
            String path = ArrayUtils.toString( parts, SEPARATOR, 1 );
            if ( path == null )
                continue;

            File file = new File( path );
            if ( zipEntry.isDirectory() ) {
                if ( !file.isDirectory() && !file.mkdirs() )
                    throw new IOException( "Failed to create directory " + file );
                continue;
            }

            // fix for Windows-created archives
            File parent = file.getParentFile();
            if ( !parent.isDirectory() && !parent.mkdirs() )
                throw new IOException( "Failed to create directory " + parent );

            // write file content
            FileOutputStream fos = new FileOutputStream( file );
            fos.write( zis.readAllBytes() );
            fos.close();
        }
    }
}
