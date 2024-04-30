package pepper.socialStory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ListenBuilder;
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.conversation.Say;

public class PepperStory extends RobotActivity implements RobotLifecycleCallbacks {
    private int flagPage = 0;
    private int flagStart = 0;
    private int i;
    protected static String storyTitle;
    private EditText searchStory;
    private ArrayList<String> results;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pepperstory);
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE);
        setSpeechBarDisplayPosition(SpeechBarDisplayPosition.TOP);
        QiSDK.register(this, this);

        searchStory = findViewById(R.id.search_story);
        ImageButton search = findViewById(R.id.search_button);
        search.setOnClickListener(view -> searchTitles());

        findViewById(R.id.button_atoz).setOnClickListener(v -> {
            flagPage = 0;
            findViewById(R.id.next_page).setVisibility(View.VISIBLE);
            findViewById(R.id.next).setVisibility(View.VISIBLE);
            findViewById(R.id.prev).setVisibility(View.GONE);
            findViewById(R.id.previous).setVisibility(View.GONE);
            getTitles(); });

        findViewById(R.id.button_ztoa).setOnClickListener(v -> {
            flagPage = 1;
            findViewById(R.id.next_page).setVisibility(View.VISIBLE);
            findViewById(R.id.next).setVisibility(View.VISIBLE);
            findViewById(R.id.prev).setVisibility(View.GONE);
            findViewById(R.id.previous).setVisibility(View.GONE);
            getTitles(); });

        findViewById(R.id.next_page).setOnClickListener(v -> {
            nextPage();
            findViewById(R.id.prev).setVisibility(View.VISIBLE);
            findViewById(R.id.previous).setVisibility(View.VISIBLE);});


        findViewById(R.id.previous).setOnClickListener(v -> {
            previousPage();
            findViewById(R.id.next_page).setVisibility(View.VISIBLE);
            findViewById(R.id.next).setVisibility(View.VISIBLE);});

        getTitles();
    }

    public void getTitles() {
        if(flagPage == 0) Collections.sort(GetTitles.titleList);
        if(flagPage == 1) Collections.sort(GetTitles.titleList, Collections.reverseOrder());
        i=0;

        if (GetTitles.titleList.size() <= 5) {
            findViewById(R.id.next_page).setVisibility(View.GONE);
            findViewById(R.id.next).setVisibility(View.GONE);
        } else if (GetTitles.titleList.size() > 5) {
            findViewById(R.id.next_page).setVisibility(View.VISIBLE);
            findViewById(R.id.next).setVisibility(View.VISIBLE);
        }

        try {
            TextView firstTitle = findViewById(R.id.titolo1);
            firstTitle.setText(GetTitles.titleList.get(i++));
            findViewById(R.id.titolo1).setOnClickListener(v -> {
                storyTitle = String.valueOf(firstTitle.getText());
                startActivity(new Intent(PepperStory.this, GetStory.class));
                finish();
            });

            TextView secondTitle = findViewById(R.id.titolo2);
            secondTitle.setText(GetTitles.titleList.get(i++));
            findViewById(R.id.titolo2).setOnClickListener(v -> {
                storyTitle = String.valueOf(secondTitle.getText());
                startActivity(new Intent(PepperStory.this, GetStory.class));
                finish();
            });

            TextView thirdTitle = findViewById(R.id.titolo3);
            thirdTitle.setText(GetTitles.titleList.get(i++));
            findViewById(R.id.titolo3).setOnClickListener(v -> {
                storyTitle = String.valueOf(thirdTitle.getText());
                startActivity(new Intent(PepperStory.this, GetStory.class));
                finish();
            });

            TextView fourthTitle = findViewById(R.id.titolo4);
            fourthTitle.setText(GetTitles.titleList.get(i++));
            findViewById(R.id.titolo4).setOnClickListener(v -> {
                storyTitle = String.valueOf(fourthTitle.getText());
                startActivity(new Intent(PepperStory.this, GetStory.class));
                finish();
            });

            TextView fifthTitle = findViewById(R.id.titolo5);
            fifthTitle.setText(GetTitles.titleList.get(i++));
            findViewById(R.id.titolo5).setOnClickListener(v -> {
                storyTitle = String.valueOf(fifthTitle.getText());
                startActivity(new Intent(PepperStory.this, GetStory.class));
                finish();
            });
        } catch (IndexOutOfBoundsException ignored) {}
    }

    public void searchTitles() {
        String searchInput = searchStory.getText().toString();
        results = new ArrayList<>();
        if (searchInput.isEmpty()) {
            getTitles();
        } else {
            for (String s : GetTitles.titleList) {
                if(s.contains(searchInput)) {
                    results.add(s);
                }
            }
            if (!(results.isEmpty())) {
                showSearchResults();
            } else {
                Toast.makeText(PepperStory.this, "Nessuna storia trovata", Toast.LENGTH_SHORT).show();
            }
            Log.d("risultati", "risultati ricerca: " +results.toString());
        }

    }

    public void showSearchResults() {
        //VENGONO VISUALIZZATE SOLO LE PRIME 5 STORIE TROVATE
        if (flagPage == 0) Collections.sort(results);
        if (flagPage == 1) Collections.sort(results, Collections.reverseOrder());
        i = 0;

        findViewById(R.id.next_page).setVisibility(View.GONE);
        findViewById(R.id.next).setVisibility(View.GONE);

        TextView firstTitle = findViewById(R.id.titolo1);
        firstTitle.setText(results.get(i++));
        findViewById(R.id.titolo1).setOnClickListener(v -> {
            storyTitle = String.valueOf(firstTitle.getText());
            startActivity(new Intent(PepperStory.this, GetStory.class));
            finish();
        });
        try {
            TextView secondTitle = findViewById(R.id.titolo2);
            secondTitle.setText(results.get(i++));
            findViewById(R.id.titolo2).setOnClickListener(v -> {
                storyTitle = String.valueOf(secondTitle.getText());
                startActivity(new Intent(PepperStory.this, GetStory.class));
                finish();
            });
        } catch (IndexOutOfBoundsException exception) {
            TextView secondTitle = findViewById(R.id.titolo2);
            secondTitle.setText("");
            TextView thirdTitle = findViewById(R.id.titolo3);
            thirdTitle.setText("");
            TextView fourthTitle = findViewById(R.id.titolo4);
            fourthTitle.setText("");
            TextView fifthTitle = findViewById(R.id.titolo5);
            fifthTitle.setText("");
        }
        try {
            TextView thirdTitle = findViewById(R.id.titolo3);
            thirdTitle.setText(results.get(i++));
            findViewById(R.id.titolo3).setOnClickListener(v -> {
                storyTitle = String.valueOf(thirdTitle.getText());
                startActivity(new Intent(PepperStory.this, GetStory.class));
                finish();
            });
        } catch (IndexOutOfBoundsException exception) {
            TextView thirdTitle = findViewById(R.id.titolo3);
            thirdTitle.setText("");
            TextView fourthTitle = findViewById(R.id.titolo4);
            fourthTitle.setText("");
            TextView fifthTitle = findViewById(R.id.titolo5);
            fifthTitle.setText("");
        }
        try {
            TextView fourthTitle = findViewById(R.id.titolo4);
            fourthTitle.setText(results.get(i++));
            findViewById(R.id.titolo4).setOnClickListener(v -> {
                storyTitle = String.valueOf(fourthTitle.getText());
                startActivity(new Intent(PepperStory.this, GetStory.class));
                finish();
            });
        } catch (IndexOutOfBoundsException exception) {
            TextView fourthTitle = findViewById(R.id.titolo4);
            fourthTitle.setText("");
            TextView fifthTitle = findViewById(R.id.titolo5);
            fifthTitle.setText("");
        }
        try {
            TextView fifthTitle = findViewById(R.id.titolo5);
            fifthTitle.setText(results.get(i++));
            findViewById(R.id.titolo5).setOnClickListener(v -> {
                storyTitle = String.valueOf(fifthTitle.getText());
                startActivity(new Intent(PepperStory.this, GetStory.class));
                finish();
            });
        } catch (IndexOutOfBoundsException exception) {
            TextView fifthTitle = findViewById(R.id.titolo5);
            fifthTitle.setText("");
        }
    }

    public void nextPage() {
        QiSDK.register(this, this);
        try {
            if (i <= GetTitles.titleList.size()) {
                TextView firstTitle = findViewById(R.id.titolo1);
                firstTitle.setText(GetTitles.titleList.get(i++));
            }
        } catch (IndexOutOfBoundsException ignored) {}
        try {
            if (i <= GetTitles.titleList.size()) {
                TextView secondTitle = findViewById(R.id.titolo2);
                secondTitle.setText(GetTitles.titleList.get(i++));
            }
        } catch (IndexOutOfBoundsException Exception) {
            i+=3;
            TextView secondTitle = findViewById(R.id.titolo2);
            secondTitle.setText("");
            TextView thirdTitle = findViewById(R.id.titolo3);
            thirdTitle.setText("");
            TextView fourthTitle = findViewById(R.id.titolo4);
            fourthTitle.setText("");
            TextView fifthTitle = findViewById(R.id.titolo5);
            fifthTitle.setText("");
        }
        try {
            if (i <= GetTitles.titleList.size()) {
                TextView thirdTitle = findViewById(R.id.titolo3);
                thirdTitle.setText(GetTitles.titleList.get(i++));
            }
        } catch (IndexOutOfBoundsException Exception) {
            i+=2;
            TextView thirdTitle = findViewById(R.id.titolo3);
            thirdTitle.setText("");
            TextView fourthTitle = findViewById(R.id.titolo4);
            fourthTitle.setText("");
            TextView fifthTitle = findViewById(R.id.titolo5);
            fifthTitle.setText("");
        }
        try {
            if (i <= GetTitles.titleList.size()) {
                TextView fourthTitle = findViewById(R.id.titolo4);
                fourthTitle.setText(GetTitles.titleList.get(i++));
            }
        } catch (IndexOutOfBoundsException Exception) {
            i++;
            TextView fourthTitle = findViewById(R.id.titolo4);
            fourthTitle.setText("");
            TextView fifthTitle = findViewById(R.id.titolo5);
            fifthTitle.setText("");
        }
        try {
            if (i <= GetTitles.titleList.size()) {
                TextView fifthTitle = findViewById(R.id.titolo5);
                fifthTitle.setText(GetTitles.titleList.get(i++));
            }
        } catch (IndexOutOfBoundsException Exception) {
            TextView fifthTitle = findViewById(R.id.titolo5);
            fifthTitle.setText("");
        }

        if (i >= GetTitles.titleList.size()) {
            findViewById(R.id.next_page).setVisibility(View.GONE);
            findViewById(R.id.next).setVisibility(View.GONE);
        }
    }

    public void previousPage() {
        QiSDK.register(this, this);
        if(i <= 10) {
            findViewById(R.id.prev).setVisibility(View.GONE);
            findViewById(R.id.previous).setVisibility(View.GONE);
            getTitles();
        } else {
            TextView firstTitle = findViewById(R.id.titolo5);
            firstTitle.setText(GetTitles.titleList.get(i - 6));

            TextView secondTitle = findViewById(R.id.titolo4);
            secondTitle.setText(GetTitles.titleList.get(i - 7));

            TextView thirdTitle = findViewById(R.id.titolo3);
            thirdTitle.setText(GetTitles.titleList.get(i - 8));

            TextView fourthTitle = findViewById(R.id.titolo2);
            fourthTitle.setText(GetTitles.titleList.get(i - 9));

            TextView fifthTitle = findViewById(R.id.titolo1);
            fifthTitle.setText(GetTitles.titleList.get(i - 10));
            i-=5;
        }
    }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        if(flagStart == 0) {
            Phrase initialPhrase = new Phrase("Scegli una storia presente nella lista per farmela raccontare. Per scorrere le pagine puoi dire avanti o indietro");
            Say say = SayBuilder.with(qiContext).withPhrase(initialPhrase).build();
            say.run();
            flagStart = 1;
        }

        PhraseSet vocalCmd = PhraseSetBuilder.with(qiContext).withTexts("avanti", "indietro").build();
        Log.d("TEMP", "vocalCMD: "+vocalCmd.getPhrases());

        List<Phrase> phraseList = new ArrayList<>();
        for(String title: GetTitles.titleList) {
            Phrase phrase = new Phrase(title);
            phraseList.add(phrase);
        }
        PhraseSet titles = PhraseSetBuilder.with(qiContext).withPhrases(phraseList).build();
        Log.d("TEMP", "Titoli (phraselist): " +titles.getPhrases());

        Listen listen = ListenBuilder.with(qiContext).withPhraseSets(vocalCmd, titles).build();
        ListenResult listenResult = listen.run();
        Log.d("senti", "Heard phrase: " + listenResult.getHeardPhrase().getText());

        if(listenResult.getHeardPhrase().getText().equals("avanti")) {
            runOnUiThread(() -> {
                nextPage();
                findViewById(R.id.prev).setVisibility(View.VISIBLE);
                findViewById(R.id.previous).setVisibility(View.VISIBLE);
            });
        }

        if(listenResult.getHeardPhrase().getText().equals("indietro")) {
            runOnUiThread(() -> {
                previousPage();
                findViewById(R.id.next_page).setVisibility(View.VISIBLE);
                findViewById(R.id.next).setVisibility(View.VISIBLE);
            });
        }

        for(int i = 0; i < GetTitles.titleList.size(); i++) {
            if(GetTitles.titleList.get(i).equalsIgnoreCase(listenResult.getHeardPhrase().getText())) {
                storyTitle = GetTitles.titleList.get(i);
                Phrase selectedStory = new Phrase("Sto per raccontarvi la storia "+storyTitle);
                Say sayStory = SayBuilder.with(qiContext).withPhrase(selectedStory).build();
                sayStory.run();
                startActivity(new Intent(PepperStory.this, GetStory.class));
                finish();
            }
        }
    }

    @Override
    public void onRobotFocusLost() {
    }

    @Override
    public void onRobotFocusRefused(String reason) { }
    
    @Override
    public void onBackPressed() {
        startActivity(new Intent(PepperStory.this, MainActivity.class));
        GetTitles.titleList.clear();
        finish();
    }
}
