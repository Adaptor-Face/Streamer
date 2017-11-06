package com.example.krist.streamer.Data;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.view.Surface;

import java.io.IOException;

/**
 * Created by Kristoffer on 2017-11-06.
 */

public class MediaCodecHelper {

    public static MediaCodec setUpMediaEncoder(Surface surface) throws IOException {
        MediaFormat format = MediaFormat.createVideoFormat("video/avc", 480,320);
        format.setInteger( MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        format.setInteger( MediaFormat.KEY_I_FRAME_INTERVAL, 5 );
        format.setInteger( MediaFormat.KEY_FRAME_RATE, 15 );
        format.setInteger( MediaFormat.KEY_CAPTURE_RATE, 15 );
        format.setInteger( MediaFormat.KEY_BIT_RATE, 125000 );
        MediaCodec encoder = MediaCodec.createEncoderByType( "video/avc" );
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        return encoder;
    }
    public static MediaCodec setUpMediaDecoder(Surface surface) throws IOException {
        MediaFormat format = MediaFormat.createVideoFormat("video/avc", 480,320);
        format.setInteger( MediaFormat.KEY_CAPTURE_RATE, 15 );
        MediaCodec encoder = MediaCodec.createDecoderByType( "video/avc" );
        encoder.configure(format, surface, null, 0);
        return encoder;
    }
}
