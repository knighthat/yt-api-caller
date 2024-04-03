package me.knighthat.api.v1.instances;

import com.google.api.client.util.DateTime;
import lombok.Data;

@Data
public abstract class PreviewCard {

    private final String   id;
    private final CardType type;
    private final String   thumbNail;
    private final DateTime uploadDate;

    public PreviewCard( String id, CardType type, String thumbNail, DateTime uploadDate ) {
        this.id = id;
        this.type = type;
        this.thumbNail = thumbNail;
        this.uploadDate = uploadDate;
    }
}
