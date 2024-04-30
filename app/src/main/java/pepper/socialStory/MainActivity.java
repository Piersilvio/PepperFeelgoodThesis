package pepper.socialStory;

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
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.conversation.Say;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {

    GetTitles getTitles = new GetTitles(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE);
        setSpeechBarDisplayPosition(SpeechBarDisplayPosition.TOP);
        QiSDK.register(this, this);
        setContentView(R.layout.activity_main);
        getTitles.execute();
        findViewById(R.id.button).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PepperStory.class));
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Animation animation = AnimationBuilder.with(qiContext).withResources(R.raw.hello_a007).build(); // Build the animation.
        Animate animate;
        animate = AnimateBuilder.with(qiContext).withAnimation(animation).build(); // Build the animate action.
        animate.async().run();

        Phrase phrase = new Phrase("Benvenuti in Pepper Social Story. Premi il tasto play sul tablet oppure di' avvia per iniziare.");
        Say say = SayBuilder.with(qiContext).withPhrase(phrase).build();
        say.run();

        PhraseSet vocalCmd = PhraseSetBuilder.with(qiContext).withTexts("avvia", "inizia", "parti").build();
        Listen listen = ListenBuilder.with(qiContext).withPhraseSets(vocalCmd).build();
        ListenResult listenResult = listen.run();
        Log.d("senti", "Heard phrase: " + listenResult.getHeardPhrase().getText()); // Prints "Heard phrase: forwards".


        if(listenResult.getHeardPhrase().getText().equals("avvia") || listenResult.getHeardPhrase().getText().equals("inizia") || listenResult.getHeardPhrase().getText().equals("parti")) {
            startActivity(new Intent(MainActivity.this, PepperStory.class));
            finish();
        }
    }

    @Override
    public void onRobotFocusLost() {
        //TEMP
        QiSDK.unregister(this, this);
    }

    @Override
    public void onRobotFocusRefused(String reason) {
    }

}