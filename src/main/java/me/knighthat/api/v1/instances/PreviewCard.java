package me.knighthat.api.v1.instances;

import com.google.api.client.util.DateTime;
import lombok.Data;

@Data
public abstract class PreviewCard {

    private final String   id;
    private final CardType type;
    private final String   thumbnail;
    private final DateTime since;

    public PreviewCard( String id, CardType type, String thumbnail, DateTime since ) {
        this.id = id;
        this.type = type;
        this.thumbnail = thumbnail;
        this.since = since;
    }
}
