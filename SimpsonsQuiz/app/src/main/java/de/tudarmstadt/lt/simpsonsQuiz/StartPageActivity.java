package de.tudarmstadt.lt.simpsonsQuiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.tudarmstadt.lt.simpsonsQuiz.util.TypefaceSpan;


public class StartPageActivity extends Activity {

    static SimpsonsQuizApp sqaApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // set custom font for ActionBar Title
        SpannableString s = new SpannableString(getString(R.string.title_activity_select_category));
        s.setSpan(new TypefaceSpan(this, "Simpsonfont.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getActionBar().setTitle(s);

        /* Use application class to maintain global state. */
        sqaApp = (SimpsonsQuizApp) getApplication();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.showQA) {
            Intent intent = new Intent(this, QAActivity.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.logout) {
            sqaApp.logout(this);
            return true;
        }
        if(id == R.id.help) {
            HelpDialogFragment fdf = HelpDialogFragment.newInstance(HelpDialogFragment.CONTENT_ALL);
            fdf.show(getFragmentManager(), "help");
            return true;
        }
        if(id == R.id.about) {
            AboutDialogFragment adf = new AboutDialogFragment();
            adf.show(getFragmentManager(), "about");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickButton(View view){
        Intent intent;
        switch(view.getId()){
            case R.id.approve_new_questions:
                intent = new Intent(this, ReviewActivity.class);
                startActivity(intent);
                break;
            case R.id.cat_new_quiz:
                intent = new Intent(this, QuizGameActivity.class);
                startActivity(intent);
                break;
            case R.id.cat_new_quiz_mp:
                intent = new Intent(this, QuizGameMPActivity.class);
                startActivity(intent);
                break;
            case R.id.add_new_question:
                intent = new Intent(this, CreateQuestionActivity.class);
                startActivity(intent);
                break;
            case R.id.highscore:
                intent = new Intent(this, HighscoreActivity.class);
                startActivity(intent);
                break;
            case R.id.help:
                HelpDialogFragment fdf = new HelpDialogFragment();
                fdf.show(getFragmentManager(), "help");
                break;
        }
    }
}
