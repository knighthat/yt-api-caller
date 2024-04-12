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

package me.knighthat.api.youtube;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Part {

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static class Builder {
        @NotNull
        private String content;
        @NotNull
        private String id;
        @NotNull
        private String snippet;
        @NotNull
        private String statistics;
        @NotNull
        private String status;
        @NotNull
        private String topic;
        @NotNull
        private String branding;
        @NotNull
        private String replies;

        public Builder() {
            this.content = "";
            this.id = "";
            this.snippet = "";
            this.statistics = "";
            this.status = "";
            this.topic = "";
            this.branding = "";
            this.replies = "";
        }

        public @NotNull Builder content() {
            this.content = "contentDetails";
            return this;
        }

        public @NotNull Builder id() {
            this.id = "id";
            return this;
        }

        public @NotNull Builder snippet() {
            this.snippet = "snippet";
            return this;
        }

        public @NotNull Builder statistics() {
            this.statistics = "statistics";
            return this;
        }

        public @NotNull Builder status() {
            this.status = "status";
            return this;
        }

        public @NotNull Builder topic() {
            this.topic = "topicDetails";
            return this;
        }

        public @NotNull Builder branding() {
            this.branding = "brandingSettings";
            return this;
        }

        public @NotNull Builder replies() {
            this.replies = "replies";
            return this;
        }

        public @NotNull List<String> build() {
            List<String> results = new ArrayList<>();

            for (Field field : getClass().getDeclaredFields())
                if ( !Modifier.isStatic( field.getModifiers() ) && field.getType() == String.class )
                    try {
                        field.setAccessible( true );

                        String value = (String) field.get( this );
                        if ( value != null && !value.isEmpty() )
                            results.add( value );

                    } catch ( IllegalAccessException e ) {
                        e.printStackTrace();
                    }

            return results;
        }
    }
}
