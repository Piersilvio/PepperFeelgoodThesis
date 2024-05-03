package pepper.socialStory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ActivityMediaPlayer extends Activity implements SurfaceHolder.Callback
{
    private MediaPlayer mediaPlayer;
    private SurfaceHolder holder;
    private SurfaceView surface;
    private String string = "http://pepperfeelgood.altervista.org/get_video2.php?table=testVideo&id=0";

    public ActivityMediaPlayer(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_video);

        surface = (SurfaceView) findViewById(R.id.videoView);
        holder = surface.getHolder();
        holder.addCallback(this);
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

            mediaPlayer = MediaPlayer.create(this, R.raw.video);

            Log.d("valore getHolder()", "surface.getHolder(): " + surface.getHolder());
            Log.d("valore MediaPlayer()", "mediaPlayer: " + mediaPlayer);

            mediaPlayer.setDisplay(holder);
            mediaPlayer.setOnPreparedListener(
                    new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                        }
                    }
            );
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                }
            });
    }
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }
}
