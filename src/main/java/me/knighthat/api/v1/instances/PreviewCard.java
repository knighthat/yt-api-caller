package me.knighthat.api.v1.instances;

import com.google.api.client.util.DateTime;

import java.math.BigInteger;

public record PreviewCard( String id,
                           String videoThumbnail,
                           int duration,
                           String videoTitle,
                           String channelUrl,
                           String channelThumbnail,
                           String channelTitle,
                           BigInteger likeCount,
                           BigInteger viewCount,
                           DateTime uploadTime
) { }
