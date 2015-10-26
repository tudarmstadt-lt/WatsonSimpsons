package de.tudarmstadt.lt.simpsonsQuiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import backend.types.QuizUser;
import de.tudarmstadt.lt.simpsonsQuiz.util.TypefaceSpan;

public class HighscoreActivity extends Activity {

    private static SimpsonsQuizApp sqaApp;
    private static TableLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        /* Use application class to maintain global state. */
        sqaApp = (SimpsonsQuizApp) getApplication();

        // set custom font for ActionBar Title
        SpannableString s = new SpannableString(getString(R.string.title_activity_highscore));
        s.setSpan(new TypefaceSpan(this, "Simpsonfont.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (getActionBar() != null) {
            // Update the action bar title with the TypefaceSpan instance
            getActionBar().setTitle(s);
        }

        layout = (TableLayout) findViewById(R.id.highscore_layout);
        LoadHighscoreTask lht = new LoadHighscoreTask();
        lht.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void addTableRow(TableLayout layout, int rank, String name, int score, boolean textStyleBold){
        TableRow row = new TableRow(HighscoreActivity.this);

        TextView textViewNumber = new TextView(HighscoreActivity.this);
        textViewNumber.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        textViewNumber.setText(rank + ".");
        TableRow.LayoutParams layoutParamsLeft = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        layoutParamsLeft.gravity = Gravity.START;
        textViewNumber.setLayoutParams(layoutParamsLeft);
        row.addView(textViewNumber);

        TextView textViewPlayer = new TextView(HighscoreActivity.this);
        textViewPlayer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        if(name.length()>14){
            name = name.substring(0,11) + "...";
        }
        textViewPlayer.setText(name);
        TableRow.LayoutParams layoutParamsCenter = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        layoutParamsCenter.gravity = Gravity.CENTER;
        textViewPlayer.setLayoutParams(layoutParamsCenter);
        row.addView(textViewPlayer);

        TextView textViewScore = new TextView(HighscoreActivity.this);
        textViewScore.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        textViewScore.setText(String.valueOf(score));
        TableRow.LayoutParams layoutParamsRight = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        layoutParamsRight.gravity = Gravity.END;
        textViewScore.setLayoutParams(layoutParamsRight);
        row.addView(textViewScore);

        if(textStyleBold){
            textViewNumber.setTypeface(null, Typeface.BOLD_ITALIC);
            textViewPlayer.setTypeface(null, Typeface.BOLD_ITALIC);
            textViewScore.setTypeface(null, Typeface.BOLD_ITALIC);
        }

        layout.addView(row);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.showQA) {
            Intent intent = new Intent(this, QAActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.logout) {
            sqaApp.logout(this);
            return true;
        }
        if (id == R.id.help) {
            HelpDialogFragment fdf = HelpDialogFragment.newInstance(HelpDialogFragment.CONTENT_HIGHSCORE);
            fdf.show(getFragmentManager(), "help");
            return true;
        }
        if (id == R.id.about) {
            AboutDialogFragment adf = new AboutDialogFragment();
            adf.show(getFragmentManager(), "about");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sends the rating for a question to the database
     */
    private class LoadHighscoreTask extends AsyncTask<Void, Void, List<QuizUser>> {

        @Override
        protected List<QuizUser> doInBackground(Void... params) {
            return sqaApp.getQuizBackend().getUserHighscores();
        }

        @Override
        protected void onPostExecute(List<QuizUser> highscores) {
            if (highscores == null || highscores.isEmpty()) {
                ErrorDialogFragment edf = new ErrorDialogFragment();
                edf.show(HighscoreActivity.this.getFragmentManager(), "error");
            } else {
                String userName = sqaApp.loadUserName(HighscoreActivity.this);
                int userRank;
                QuizUser tempUser = new QuizUser();
                tempUser.setUsername(userName);
                userRank = highscores.indexOf(tempUser);

                if (highscores.size() > 10) highscores = highscores.subList(0, 10);


                boolean currentUserIsPlayer = false;
                boolean playerUserFound = false;

                for (int i = 0; i < highscores.size(); i++) {
                    if (highscores.get(i).getUsername() != null) {
                        if (highscores.get(i).getUsername().equals(userName)) {
                            currentUserIsPlayer = true;
                            playerUserFound = true;
                        }

                        addTableRow(layout, i + 1, highscores.get(i).getUsername(), highscores.get(i).getScore(), currentUserIsPlayer);
                        if(currentUserIsPlayer)
                            currentUserIsPlayer=false;

                    }
                }

                if (!playerUserFound) {
                    int userScore = 0;
                    try {
                        LoadUserScoreTask lut = new LoadUserScoreTask();
                        lut.execute();
                        userScore = lut.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    addTableRow(layout, userRank + 1, userName, userScore, true);
                }
            }
        }
    }

    /**
     * Sends the rating for a question to the database
     */
    private static class LoadUserScoreTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            return sqaApp.getQuizBackend().getUserScore();
        }
    }

    public static class ErrorDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            final View view = inflater.inflate(R.layout.dialog_quiz_game_feedback, null);
            ((TextView) view.findViewById(R.id.quiz_game_feedback_text)).setText(getString(R.string.error_loading_highscore));

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view).setTitle("Error")
                    // Add action buttons
                    .setNegativeButton(R.string.switch_category, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getActivity(), StartPageActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(getActivity(), HighscoreActivity.class));
                        }
                    });
            return builder.create();
        }
    }
}
