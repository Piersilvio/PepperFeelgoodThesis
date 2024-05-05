package pepper.socialStory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.ListenBuilder;
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.conversation.BodyLanguageOption;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.touch.Touch;
import com.aldebaran.qi.sdk.object.touch.TouchSensor;
import com.aldebaran.qi.sdk.object.touch.TouchState;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetStory extends RobotActivity implements RobotLifecycleCallbacks {

    private ImageView imageView;
    private ImageButton nextParagraph;
    private PlayerView audioView;
    private LinearLayout button_answare;
    private Button answare_right;
    private Button answare_wrong;
    private SimpleExoPlayer simpleAudioExoPlayer; //per l'audio
    private SimpleExoPlayer simpleVideoExoPlayer; //per il video
    private final ArrayList<Bitmap> imageList = new ArrayList<>();
    private final ArrayList<String> story = new ArrayList<>();
    private final ArrayList<String> color = new ArrayList<>();
    private final ArrayList<String> videoName = new ArrayList<>();
    private final ArrayList<String> audioName = new ArrayList<>();
    private String moral;
    private int index = 0;
    private boolean isGameActive = false;
    private boolean isInGame = false;
    private TouchSensor touchSensor;
    private static final int DELAY_TAP = 4; //Tempo di attesa dopo un tap per verificare che sia un tap singolo o doppio


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.get_story);
        imageView = findViewById(R.id.imageView);

        audioView = findViewById(R.id.audioView);
        nextParagraph = findViewById(R.id.nextParagraph);

        getStory();
    }

    private void getStory() {

        class GetParagraphs extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected String doInBackground(String... params) {
                String table = params[0];
                try {
                    URL url = new URL ("http://pepperfeelgood.altervista.org/Cartella%20temporanea%20GETTERS/get_paragraph.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                    String sendData = URLEncoder.encode("table", "UTF-8")+"="+URLEncoder.encode(table, "UTF-8");
                    bufferedWriter.write(sendData);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line).append('\n');
                    }
                    JSONArray jsonArray = new JSONArray(sb.toString());
                    for (int i=0; i < jsonArray.length(); i++ ) {
                        JSONObject ob = jsonArray.getJSONObject(i);
                        story.add(i, ob.get("Testo").toString());
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return sb.toString();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(GetStory.this, "", "Caricamento storia in corso...", true, true);
            }

            @Override
            protected void onPostExecute(String b) {
                super.onPostExecute(b);
                for (int i=0; i < story.size(); i++ ) {
                    getImage(i);
                }
                getColor();
                getVideoName();
                getAudioName();
                getMoral();
                loading.dismiss();
            }
        }

        GetParagraphs getParagraphs = new GetParagraphs();
        getParagraphs.execute(""+PepperStory.storyTitle);
    }

    private void getImage(int id) {

        class GetImage extends AsyncTask<String, Void, Bitmap> {

            @Override
            protected Bitmap doInBackground(String... params) {
                String table = params[0];
                String id = params[1];
                Bitmap image = null;
                try {
                    URL url = new URL ("http://pepperfeelgood.altervista.org/Cartella%20temporanea%20GETTERS/get_image.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                    String sendData = URLEncoder.encode("table", "UTF-8")+"="+URLEncoder.encode(table, "UTF-8")+"&"+URLEncoder.encode("id", "UTF-8")+"="+URLEncoder.encode(id, "UTF-8");
                    bufferedWriter.write(sendData);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    image = BitmapFactory.decodeStream(inputStream);
                    if(image != null){
                        imageList.add(Integer.parseInt(id), image);
                    } else {
                        imageList.add(Integer.parseInt(id), BitmapFactory.decodeResource(getResources(),
                                R.drawable.pepper_talking));
                    }
                    inputStream.close();
                    httpURLConnection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return image;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Bitmap b) {
                super.onPostExecute(b);
            }

        }

        GetImage getImage = new GetImage();
        getImage.execute(""+PepperStory.storyTitle, ""+id);
    }

    private void getColor() {

        class GetColor extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String table = params[0];
                try {
                    URL url = new URL ("http://pepperfeelgood.altervista.org/Cartella%20temporanea%20GETTERS/get_color.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                    String sendData = URLEncoder.encode("table", "UTF-8")+"="+URLEncoder.encode(table, "UTF-8");
                    bufferedWriter.write(sendData);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line).append('\n');
                    }
                    JSONArray jsonArray = new JSONArray(sb.toString());
                    for (int i=0; i < jsonArray.length(); i++ ) {
                        JSONObject ob = jsonArray.getJSONObject(i);
                        color.add(i, ob.get("Colore").toString());
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return sb.toString();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String b) {
                super.onPostExecute(b);
            }

        }

        GetColor getColor = new GetColor();
        getColor.execute(""+PepperStory.storyTitle);
    }


    private void getVideoName() {

        class GetVideoName extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String table = params[0];
                try {
                    URL url = new URL ("http://pepperfeelgood.altervista.org/get_video_name.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                    String sendData = URLEncoder.encode("table", "UTF-8")+"="+URLEncoder.encode(table, "UTF-8");
                    bufferedWriter.write(sendData);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line).append('\n');
                    }
                    JSONArray jsonArray = new JSONArray(sb.toString());
                    for (int i=0; i < jsonArray.length(); i++ ) {
                        JSONObject ob = jsonArray.getJSONObject(i);
                        videoName.add(i, ob.get("NomeVideo").toString());
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return sb.toString();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String b) {
                super.onPostExecute(b);
            }

        }

        GetVideoName getVideoName = new GetVideoName();
        getVideoName.execute(""+PepperStory.storyTitle);
    }


    private void getAudioName() {

        class GetAudioName extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String table = params[0];
                try {
                    URL url = new URL ("http://pepperfeelgood.altervista.org/get_audio_name.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                    String sendData = URLEncoder.encode("table", "UTF-8")+"="+URLEncoder.encode(table, "UTF-8");
                    bufferedWriter.write(sendData);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line).append('\n');
                    }
                    JSONArray jsonArray = new JSONArray(sb.toString());
                    for (int i=0; i < jsonArray.length(); i++ ) {
                        JSONObject ob = jsonArray.getJSONObject(i);
                        audioName.add(i, ob.get("NomeAudio").toString());
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return sb.toString();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String b) {
                super.onPostExecute(b);
            }

        }

        GetAudioName getAudioName = new GetAudioName();
        getAudioName.execute(""+PepperStory.storyTitle);
    }


    private void getMoral() {

        class GetMoral extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String table = params[0];
                try {
                    URL url = new URL ("http://pepperfeelgood.altervista.org/Cartella%20temporanea%20GETTERS/get_moral.php");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                    String sendData = URLEncoder.encode("table", "UTF-8")+"="+URLEncoder.encode(table, "UTF-8");
                    bufferedWriter.write(sendData);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line).append('\n');
                    }
                    moral = sb.toString();
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String b) {
                super.onPostExecute(b);
                getParagraph();
            }
        }

        GetMoral getMoral = new GetMoral();
        getMoral.execute(""+PepperStory.storyTitle);
    }

    private void getParagraph() {

        //FIXME: sistemare la logica degli indici(quando metto == null i video partono, col != null non partono!)

        if (imageList.get(index) == null) {
            imageView.setBackgroundColor(255);
            imageView.setImageBitmap(imageList.get(index));
        } else if (!videoName.get(index).isEmpty()) {

            String storyTableNoSpace = PepperStory.storyTitle;
            storyTableNoSpace = storyTableNoSpace.replaceAll(" ", "%20");
            String string = "http://pepperfeelgood.altervista.org/get_video2.php?table=" + storyTableNoSpace + "&id=" + index;
            Log.d("prova video", "prova stringa connessione: " + string);

            ActivityMediaPlayer mediaplayer = new ActivityMediaPlayer();

            activityMediaPlayer(mediaplayer);


        } else {
            imageView.setImageBitmap(null);
            imageView.setBackgroundColor(Color.parseColor(color.get(index)));
        }

        if (!audioName.get(index).isEmpty()) {
            String storyTableNoSpace = PepperStory.storyTitle;
            storyTableNoSpace = storyTableNoSpace.replaceAll(" ", "%20");
            Log.d("prova video", "prova stringa storyTableNoSpace: " + storyTableNoSpace);
            String string = "http://pepperfeelgood.altervista.org/get_audio.php?table=" + storyTableNoSpace + "&id=" + index;
            Log.d("prova video", "prova stringa connessione: " + string);
            simpleAudioExoPlayer = new SimpleExoPlayer.Builder(this).build();
            audioView.setPlayer(simpleAudioExoPlayer);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "app"));
            MediaSource dataSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(string));
            audioView.setControllerHideOnTouch(true);
            simpleAudioExoPlayer.prepare(dataSource);
            simpleAudioExoPlayer.setPlayWhenReady(true);
            simpleAudioExoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == Player.STATE_ENDED) {
                        Log.d("prova audio", "IS PLAYING: " + simpleAudioExoPlayer.isPlaying());
                        Log.d("prova audio", "SONO NEL LISTENER STATO FINITO");
                        if (simpleVideoExoPlayer == null || !simpleVideoExoPlayer.isPlaying()) {
                            Log.d("flusso", "sono nel getParagraph dell'if dell'end simpleAudioPlayer");
                            nextParagraph.setVisibility(View.VISIBLE);
                            index = index +1;
                            //imageView.setVisibility(imageView.VISIBLE); INUTILE?
                            nextParagraph.setVisibility(View.INVISIBLE);
                            getParagraph();
                        }
                    }
                }
            });
        }
        startTalk();
    }

    private void activityMediaPlayer(Activity activity) {
        Intent intent = new Intent(getApplicationContext(), activity.getClass());
        startActivity(intent);
    }

    public void startTalk() {
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE);
        setSpeechBarDisplayPosition(SpeechBarDisplayPosition.TOP);
        QiSDK.register(this, this);
    }

    @Override
    public void onBackPressed() {
        if(simpleAudioExoPlayer != null) {
            simpleAudioExoPlayer.stop();
            simpleAudioExoPlayer.release();
        }
        //FIXME
        if(simpleVideoExoPlayer != null) {
            simpleVideoExoPlayer.stop();
            simpleVideoExoPlayer.release();
        }
        startActivity(new Intent(GetStory.this, PepperStory.class));
        finish();
    }

    //FIXME
    @Override
    public void onRobotFocusGained(QiContext qiContext) {

        saySplittedParagraph(story.get(index), qiContext);

        Animation animation = AnimationBuilder.with(qiContext).withResources(R.raw.hello_a007).build();
        Animate animate = AnimateBuilder.with(qiContext).withAnimation(animation).build();
        Phrase endStory = new Phrase("La storia è terminata. Grazie a tutti per l'attenzione.");
        Say sayEndStory = SayBuilder.with(qiContext).withPhrase(endStory).build();

        if (index+1 < story.size()) {
            if(simpleVideoExoPlayer == null && simpleAudioExoPlayer == null) {
                Log.d("flusso", "AUDIO NULL E VIDEO NULL");
                index += 1;
                runOnUiThread(() -> imageView.setVisibility(View.VISIBLE));
                runOnUiThread(() -> nextParagraph.setVisibility(View.INVISIBLE));
                runOnUiThread(this::getParagraph);
            } else if (simpleAudioExoPlayer != null && simpleVideoExoPlayer == null) {
                if (!simpleAudioExoPlayer.isPlaying()) {
                    Log.d("flusso", "AUDIO IS PLAYING FALSE E VIDEO NULL");
                    index += 1;
                    runOnUiThread(() -> imageView.setVisibility(View.VISIBLE));
                    runOnUiThread(() -> nextParagraph.setVisibility(View.INVISIBLE));
                    runOnUiThread(this::getParagraph);
                }
            } else if (simpleAudioExoPlayer == null) {
                if (!simpleVideoExoPlayer.isPlaying()) {
                    Log.d("flusso", "VIDEO IS PLAYING FALSE E AUDIO NULL");
                    index += 1;
                    runOnUiThread(() -> imageView.setVisibility(View.VISIBLE));
                    runOnUiThread(() -> nextParagraph.setVisibility(View.INVISIBLE));
                    runOnUiThread(this::getParagraph);
                }
            } else if (!simpleAudioExoPlayer.isPlaying() && !simpleVideoExoPlayer.isPlaying()) {
                Log.d("flusso", "AUDIO E VIDEO IS PLAYING FALSE");
                index += 1;
                runOnUiThread(() -> imageView.setVisibility(View.VISIBLE));
                runOnUiThread(() -> nextParagraph.setVisibility(View.INVISIBLE));
                runOnUiThread(this::getParagraph);
            }
        } else { //SE SIAMO ALL'ULTIMO PARAGRAFO
            Log.d("flusso", "sono nell'else ultimo paragrafo");
            runOnUiThread(() -> nextParagraph.setVisibility(View.INVISIBLE));
            if (!moral.isEmpty()) {
                Phrase morale = new Phrase("\\rspd=85\\\\wait=9\\La morale della storia è: " + moral);
                Say say1 = SayBuilder.with(qiContext).withPhrase(morale).build();
                say1.run();
                Log.d("prova", "prova morale: " + moral);
            }

            Log.d("IS IN GAME?", ""+isInGame);
            if(isInGame){
                if(!isGameActive){
                    Phrase minigameFrase = new Phrase("Giochiamo insieme?");
                    Say sayMinigame = SayBuilder.with(qiContext).withPhrase(minigameFrase).build();
                    sayMinigame.run();
                } else {
                    Phrase minigameFrase = new Phrase("Vuoi rigiocare?");
                    Say sayMinigame = SayBuilder.with(qiContext).withPhrase(minigameFrase).build();
                    sayMinigame.run();
                }

                PhraseSet phraseSet = PhraseSetBuilder.with(qiContext).withTexts("si", "no").build();
                Listen listen = ListenBuilder.with(qiContext).withPhraseSet(phraseSet).build();
                ListenResult listenResult = listen.run();

                if(listenResult.getHeardPhrase().getText().equals("si")) {
                    runOnUiThread(() -> {
                        index = 0;
                        isGameActive = true;
                        isInGame = false;
                        runOnUiThread(this::getParagraph);
                    });
                }

                if(listenResult.getHeardPhrase().getText().equals("no")) { //Torna al menu iniziale
                    isInGame = false;
                    isGameActive = false;
                    animate.async().run();
                    sayEndStory.run();
                    startActivity(new Intent(GetStory.this, PepperStory.class));
                    finish();
                }
            } else { //Torna al menu iniziale
                animate.async().run();
                sayEndStory.run();
                startActivity(new Intent(GetStory.this, PepperStory.class));
                finish();
            }
        }

        nextParagraph.setOnClickListener(view -> {
            Log.d("prova", "sono nell'onclick");
            index = index +1;
            runOnUiThread(() -> imageView.setVisibility(View.VISIBLE));
            runOnUiThread(() -> nextParagraph.setVisibility(View.INVISIBLE));
            getParagraph();
        });

        /*PhraseSet vocalCmd = PhraseSetBuilder.with(qiContext).withTexts("avanti", "prossimo paragrafo", "continua").build();
        Listen listen = ListenBuilder.with(qiContext).withPhraseSets(vocalCmd).build();
        ListenResult listenResult = listen.run();
        //TODO: con il listen, si blocca l'esecuzione e Pepper non parla più. Testarlo con l'async run di sotto e vedere se ascolta.
        //ListenResult listenResult = listen.async().run();
        Log.d("senti", "Heard phrase: " + listenResult.getHeardPhrase().getText()); // Prints "Heard phrase: forwards".

        //TODO: TESTARE QUESTO IF CON PEPPER AGGIUNTO SUCCESSIVAMENTE
        if( (listenResult.getHeardPhrase().getText().equals("avanti") || listenResult.getHeardPhrase().getText().equals("prossimo paragrafo") || listenResult.getHeardPhrase().getText().equals("continua")) && !(index + 1 == story.size()) ) {
            index = index +1;
            runOnUiThread(() -> imageView.setVisibility(imageView.VISIBLE));
            runOnUiThread(() -> nextParagraph.setVisibility(nextParagraph.INVISIBLE));
            getParagraph();
        }*/
    }

    private void saySplittedParagraph(String text, QiContext qiContext) {
        ArrayList<String> pos = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\s:[^\\s:]+:\\s");
        Matcher matcher = pattern.matcher(text);
        String appoggio;
        String emojiCode = "";
        while(matcher.find()) {
            isInGame = true;
            appoggio = matcher.start() + "-" + matcher.end();
            emojiCode = text.substring(matcher.start(), matcher.end()).replaceAll("\\s:|:\\s", "");
            pos.add(appoggio);
        }
        int cont = 0;
        String partialPhrase;
        int dim_pos = pos.size();
        if (dim_pos != 0){
            for(int j=0; j<dim_pos; j++){
                int posFinale = Integer.parseInt(pos.get(j).split("-")[0]);
                if (cont == 0){
                    partialPhrase = text.substring( 0, posFinale );
                    cont += 1;
                } else {
                    partialPhrase = fun(j-1, posFinale, text, pos);
                }
                Phrase frase = new Phrase("\\rspd=85\\" + partialPhrase);
                Say say = SayBuilder.with(qiContext).withPhrase(frase).withBodyLanguageOption(BodyLanguageOption.DISABLED).build();
                say.run();
                //Ricerca animazione corrente della frase analizzata
                for (CodeAnimation code : CodeAnimation.values()) {
                    if (code.name().equals(emojiCode)) {
                        Resources res = getResources();
                        int emojiId = res.getIdentifier(code.name(), "raw", getPackageName());
                        Animation animation = AnimationBuilder.with(qiContext).withResources(emojiId).build();
                        Animate animate = AnimateBuilder.with(qiContext).withAnimation(animation).build();
                        animate.run();

                        detectDoubleTap(qiContext, emojiCode);
                    }
                }
            }
            if ((text.length() > Integer.parseInt((pos.get(dim_pos-1)).split("-")[1]) ) ){
                partialPhrase =  fun(pos.size()-1 , text.length(), text, pos);
                Phrase frase = new Phrase("\\rspd=85\\\\wait=9\\" + partialPhrase);
                Say say = SayBuilder.with(qiContext).withPhrase(frase).withBodyLanguageOption(BodyLanguageOption.DISABLED).build();
                say.run();
            }
        } else { //Nessun :nomeAnimazione: nel paragrafo
            Phrase frase = new Phrase("\\rspd=85\\\\wait=9\\" + text);
            Say say = SayBuilder.with(qiContext).withPhrase(frase).build();
            say.run();
        }
    }

    private void detectDoubleTap(QiContext qiContext, String emojiCode) {
        if(isInGame && isGameActive) {
            Touch touch = qiContext.getTouch();
            touchSensor = touch.getSensor("Head/Touch");

            if(!emojiCode.equals("enumeration_both_hand_a001")){
                Phrase provaTu = new Phrase("\\rspd=85\\\\wait=9\\" + "Ora prova tu");
                Say say1 = SayBuilder.with(qiContext).withPhrase(provaTu).withBodyLanguageOption(BodyLanguageOption.DISABLED).build();
                say1.run();
            } else {
                Phrase provaTu = new Phrase("\\rspd=85\\\\wait=9\\" + "Ora prova a ripeterlo tu");
                Say say1 = SayBuilder.with(qiContext).withPhrase(provaTu).withBodyLanguageOption(BodyLanguageOption.DISABLED).build();
                say1.run();
            }
            //----Gestione esito imitazione del movimento----//
            counterTouch(-1,0);
            long currentInit = System.currentTimeMillis();
            int currentSecInit = (int) (currentInit / 1000) % 60;
            int countTap = counterTouch(currentSecInit, 1);
            Log.d("FUN COUNT", "Count finale "+countTap);
            if(countTap == 1){
                Animation animation1 = AnimationBuilder.with(qiContext).withResources(R.raw.clapping_b001).build();
                Animate animate1 = AnimateBuilder.with(qiContext).withAnimation(animation1).build();
                animate1.run();
                Phrase ris_giusta = new Phrase("\\rspd=85\\\\wait=9\\" + "Corretto!");
                Say say_ris1 = SayBuilder.with(qiContext).withPhrase(ris_giusta).withBodyLanguageOption(BodyLanguageOption.DISABLED).build();
                say_ris1.run();
            } else {
                Animation animation1 = AnimationBuilder.with(qiContext).withResources(R.raw.sad_a003).build();
                Animate animate1 = AnimateBuilder.with(qiContext).withAnimation(animation1).build();
                animate1.run();
                Phrase ris_errata = new Phrase("\\rspd=85\\\\wait=9\\" + "Oh no!");
                Say say_ris1 = SayBuilder.with(qiContext).withPhrase(ris_errata).build();
                say_ris1.run();
            }
        }
    }


    private int counterTouch(int currentSecInit, int tap) {
        AtomicBoolean isTouched = new AtomicBoolean(false);
        AtomicInteger countTouch = new AtomicInteger();
        do {
            touchSensor.addOnStateChangedListener(touchState -> {
                if(touchState.getTouched()) {
                    countTouch.addAndGet(1);
                    isTouched.set(true);
                    touchSensor.removeAllOnStateChangedListeners();
                }
            });

            if(tap!=0) {
                long currentFin = System.currentTimeMillis();
                int currentSecFint = (int) (currentFin / 1000) % 60;

                int interval = currentSecFint - currentSecInit;
                if (interval >= 0) {
                    if (interval > DELAY_TAP) {
                        isTouched.set(true);
                    }
                } else {
                    int tot = currentSecInit - currentSecFint;
                    interval = 60 - tot;
                    if (interval > DELAY_TAP) {
                        isTouched.set(true);
                    }
                }
            }
        } while (!isTouched.get());
        if (countTouch.get() != 0) {
            return tap+1;
        } else {
            return tap;
        }
    }



    public static String fun( int j, int valoreFin, String testo, ArrayList<String> pos){
        return testo.substring( Integer.parseInt(pos.get(j).split("-")[1]), valoreFin);
    }


    @Override
    public void onRobotFocusLost() {
    }

    @Override
    public void onRobotFocusRefused(String reason) {

    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

}
