package com.fly1tkg.squarecrop;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;


public class MainActivity extends Activity implements TextureView.SurfaceTextureListener {
    private TextureView mTextureView;
    private MediaPlayer mMediaplayer = new MediaPlayer();
    private int mVideoWidth;
    private int mVideoHeight;
    private float mPreviousX;
    private float mPreviousY;
    private Matrix mMatrix;
    private int mTextureX;
    private int mTextureY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            AssetFileDescriptor afd = getAssets().openFd("out.mp4");
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            Log.d("", "video size: " + width + "x" + height);
            mVideoHeight = Integer.parseInt(height);
            mVideoWidth = Integer.parseInt(width);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTextureView = (TextureView) findViewById(R.id.textureView);
        mTextureView.setSurfaceTextureListener(this);
        mTextureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPreviousX = x;
                        mPreviousY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateTexture((int) (mPreviousX - x), (int) (mPreviousY - y));
                        mPreviousX = x;
                        mPreviousY = y;
                        break;
                }
                return true;
            }
        });

        mMatrix = new Matrix();

        mMatrix.setScale(1.777f, 1, 0, 0);
        mTextureView.setTransform(mMatrix);
    }

    private void updateTexture(final int dx, final int dy) {
        Log.d("", "dx: " + dx + ", dy: " + dy);
        mTextureX += dx;
        mTextureY += dy;
        mMatrix.setScale(1.777f, 1, mTextureX, mTextureY);
        mTextureView.setTransform(mMatrix);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);
        try {
            AssetFileDescriptor afd = getAssets().openFd("out.mp4");
            mMediaplayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mMediaplayer.setSurface(surface);
            mMediaplayer.setLooping(true);
            mMediaplayer.prepareAsync();
            mMediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaplayer.start();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mMediaplayer != null) {
            mMediaplayer.stop();
            mMediaplayer.release();
            mMediaplayer = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }
}
