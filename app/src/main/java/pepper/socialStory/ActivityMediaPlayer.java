package pepper.socialStory;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class ActivityMediaPlayer extends Activity implements SurfaceHolder.Callback
{
    private MediaPlayer mediaPlayer;
    private SurfaceHolder holder;
    private SurfaceView surface;
    private String url;

    public ActivityMediaPlayer() {
        // Inizializzazione, se necessario
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_video);

        Intent intent = getIntent();
        String gettingUrl = intent.getStringExtra("url");

        this.url = gettingUrl;

        surface = findViewById(R.id.videoView);
        holder = surface.getHolder();
        holder.addCallback(this);
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(this, Uri.parse(url));
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );

            Log.d("valore MediaPlayer()", "mediaPlayer: " + mediaPlayer);

            mediaPlayer.prepareAsync();
            //mediaPlayer.start();

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MediaPlayer", "Error occurred: what=" + what + ", extra=" + extra);
                return false;
            });

            mediaPlayer.setDisplay(holder);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }
}
