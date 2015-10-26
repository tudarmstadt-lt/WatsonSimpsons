package de.tudarmstadt.lt.simpsonsQuiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import backend.types.QuizQuestion;
import de.tudarmstadt.lt.simpsonsQuiz.util.TypefaceSpan;


public class CreateQuestionActivity extends Activity implements SelectAnswersFragment.OnFragmentInteractionListener {

    SimpsonsQuizApp sqaApp;

    CreateQuestionFragment createQuestionFragment;
    SelectAnswersFragment selectAnswersFragment;

    public static final String CLASS_NAME = "CreateQuestionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        /* Use application class to maintain global state. */
        sqaApp = (SimpsonsQuizApp) getApplication();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        createQuestionFragment = new CreateQuestionFragment();
        fragmentTransaction.add(R.id.create_question_fragment_container, createQuestionFragment);
        fragmentTransaction.commit();


        // set custom font for ActionBar Title
        SpannableString s = new SpannableString(getString(R.string.title_activity_create_question));
        s.setSpan(new TypefaceSpan(this, "Simpsonfont.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (getActionBar() != null) {
            // Update the action bar title with the TypefaceSpan instance
            getActionBar().setTitle(s);

            // Add Up Button
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.logout) {
            sqaApp.logout(this);
            return true;
        }
        if (id == R.id.help) {
            HelpDialogFragment fdf = HelpDialogFragment.newInstance(HelpDialogFragment.CONTENT_CREATE_QUESTION);
            fdf.show(getFragmentManager(), "help");
            return true;
        }
        if (id == R.id.about) {
            AboutDialogFragment adf = new AboutDialogFragment();
            adf.show(getFragmentManager(), "about");
            return true;
        }
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void generateAnswers(View view) {
        String question = ((EditText) findViewById(R.id.enter_question_question_text)).getText().toString();
        generateAnswers(question);
    }

    public void generateAnswers(String question) {
        if (question.equals("")) {
            createQuestionFragment.setHint(getString(R.string.enter_question_hint_enter_question));
        } else {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            selectAnswersFragment = SelectAnswersFragment.newInstance(question);
            fragmentTransaction.replace(R.id.create_question_fragment_container, selectAnswersFragment);
            fragmentTransaction.addToBackStack(null).commit();
        }
    }

    public void createQuestion(View view) {
        QuizQuestion quizQuestion = selectAnswersFragment.createQuestion();

        if (quizQuestion == null)
            return;

        CreateQuestionTask cqTask = new CreateQuestionTask();
        cqTask.execute(quizQuestion);
    }

    /**
     * Create a new Question for the Quiz and send it to the database
     */
    private class CreateQuestionTask extends AsyncTask<QuizQuestion, Void, Boolean> {

        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected Boolean doInBackground(QuizQuestion... question) {
            return sqaApp.getQuizBackend().addQuestion(question[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                Log.d(CLASS_NAME, "Saved Question");
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateQuestionActivity.this);
                builder.setTitle(getString(R.string.question_successfully_created));
                // Set up the input
                builder.setPositiveButton(getString(R.string.create_new_question), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Start new CreateQuestionActivity
                        Intent intent = new Intent(CreateQuestionActivity.this, CreateQuestionActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton(getString(R.string.nav_to_start), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Start new ReviewActivity
                        Intent intent = new Intent(CreateQuestionActivity.this, StartPageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                builder.show();
            } else {
                ErrorDialogFragment edf = new ErrorDialogFragment();
                edf.show(getFragmentManager(), "error");
                Log.e(CLASS_NAME, "Question could not save to backend!");
            }
        }
    }

    public static class ErrorDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            final View view = inflater.inflate(R.layout.dialog_quiz_game_feedback, null);
            ((TextView) view.findViewById(R.id.quiz_game_feedback_text)).setText(getString(R.string.error_creating_question));

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view).setTitle(getString(R.string.error))
                    // Add action buttons
                    .setNegativeButton(R.string.switch_category, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getActivity(), StartPageActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    .setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            ((CreateQuestionActivity) getActivity()).createQuestion(null);
                        }
                    });
            return builder.create();
        }
    }

}
