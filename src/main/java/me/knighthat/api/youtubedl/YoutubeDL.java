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

package me.knighthat.api.youtubedl;

import me.knighthat.api.utils.Command;
import me.knighthat.api.v2.logging.Logger;
import me.knighthat.api.zip.ZipExtractor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

public class YoutubeDL {

    @NotNull
    private static final String REPO_NAME          = "youtube-dl";
    @NotNull
    private static final String REPO_AUTHOR        = "ytdl-org";
    @NotNull
    private static final String PYTHON_VERSION     = "3";
    @NotNull
    private static final String YOUTUBE_DL_VERSION = "2021.12.17";

    private static boolean isVersionLaterThan( @NotNull String versionStr, @NotNull String reqVersion ) {
        String[] verParts = versionStr.split( "\\." );
        String[] reqParts = reqVersion.split( "\\." );

        for (int i = 0 ; i < Math.min( verParts.length, reqParts.length ) ; i++) {
            String current = verParts[i], required = reqParts[i];

            try {
                int v1 = Integer.parseInt( verParts[i] );
                int v2 = Integer.parseInt( reqParts[i] );

                if ( v1 != v2 )
                    return v1 > v2;
            } catch ( NumberFormatException e ) {
                Logger.severe( "%s and %s must be numbers!".formatted( current, required ) );
                return false;
            }
        }

        return verParts.length >= reqParts.length;
    }

    public static void init() throws IOException, InterruptedException, UnsupportedOperationException {
        boolean downloadYoutubeDl = true;

        /*
            Verify Python & Python version
        */
        List<String> pyVerOutputs = Command.captureOutput( "python", "--version" );
        String pyVerStr = pyVerOutputs.get( 0 ).split( " " )[1];

        if ( !isVersionLaterThan( pyVerStr, PYTHON_VERSION ) )
            throw new UnsupportedOperationException();

        /*
            Verify youtube-dl & its version
        */
        try {
            List<String> ytdlVerOutputs = Command.captureOutput( "python", "youtube_dl/__main__.py", "--version" );
            String ytdlVerStr = ytdlVerOutputs.get( 0 );

            downloadYoutubeDl = !isVersionLaterThan( ytdlVerStr, YOUTUBE_DL_VERSION );
        } catch ( IOException | IndexOutOfBoundsException ignored ) {
        }

        /*
            Download youtube-dl (if needed)
        */
        if ( downloadYoutubeDl ) {
            String repoUrl = "https://api.github.com/repos/%s/%s/zipball/".formatted( REPO_AUTHOR, REPO_NAME );

            try (
                    CloseableHttpClient httpClient = HttpClients.createDefault() ;
                    CloseableHttpResponse responseContent = httpClient.execute( new HttpGet( repoUrl ) ) ;
                    InputStream contentStream = responseContent.getEntity().getContent() ;
                    ZipInputStream zis = new ZipInputStream( contentStream )
            ) {
                ZipExtractor.extractHere( zis );
            }
        }
    }
}
