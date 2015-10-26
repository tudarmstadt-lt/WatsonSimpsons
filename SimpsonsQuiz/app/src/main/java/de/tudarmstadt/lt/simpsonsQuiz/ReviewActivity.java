package de.tudarmstadt.lt.simpsonsQuiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import backend.types.QuizQuestion;
import de.tudarmstadt.lt.simpsonsQuiz.util.TypefaceSpan;


public class ReviewActivity extends Activity {

    static SimpsonsQuizApp sqaApp;
    ArrayList<AndroidQuizQuestion> quizQuestionList;
    ListIterator<AndroidQuizQuestion> quizQuestionIterator;
    int correctAnswerNumber = -1;
    static int questionID = 0;
    private boolean feedbackMode = false;

    public static final String CLASS_NAME="ReviewActivity";

    private static TextView quizQuestionTextView;
    private static Button quizAnswerButton1;
    private static Button quizAnswerButton2;
    private static Button quizAnswerButton3;
    private static Button quizAnswerButton4;

    private static boolean questionFormulatedWell;
    private static boolean rightAnswerCorrect;
    private static boolean distractorsCorrect;
    private static int difficultyValue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Set UI Elements
        quizQuestionTextView = (TextView) findViewById(R.id.quiz_question);
        quizAnswerButton1 = (Button) findViewById(R.id.quiz_btn_1);
        quizAnswerButton2 = (Button) findViewById(R.id.quiz_btn_2);
        quizAnswerButton3 = (Button) findViewById(R.id.quiz_btn_3);
        quizAnswerButton4 = (Button) findViewById(R.id.quiz_btn_4);

        /* Use application class to maintain global state. */
        sqaApp = (SimpsonsQuizApp) getApplication();

        // set custom font for ActionBar Title
        SpannableString s = new SpannableString(getString(R.string.title_activity_quiz));
        s.setSpan(new TypefaceSpan(this, "Simpsonfont.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        getActionBar().setTitle(s);

        if(savedInstanceState == null || !savedInstanceState.containsKey("quizQuestionList")) {

            quizQuestionList = new ArrayList<AndroidQuizQuestion>();

            LoadQuestionsTask lqtask = new LoadQuestionsTask();
            lqtask.execute();
        } else {
            // Restore values from previous session
            quizQuestionList = savedInstanceState.getParcelableArrayList("quizQuestionList");
            quizQuestionIterator = quizQuestionList.listIterator(savedInstanceState.getInt("quizQuestionIteratorNextIndex",0));
            nextQuestion();
        }


        quizAnswerButton1.getBackground().clearColorFilter();
        quizAnswerButton2.getBackground().clearColorFilter();
        quizAnswerButton3.getBackground().clearColorFilter();
        quizAnswerButton4.getBackground().clearColorFilter();
        quizAnswerButton1.setClickable(false);
        quizAnswerButton2.setClickable(false);
        quizAnswerButton3.setClickable(false);
        quizAnswerButton4.setClickable(false);
        findViewById(R.id.quiz_btn_next_qst).getBackground().clearColorFilter();
        findViewById(R.id.quiz_btn_rate).getBackground().clearColorFilter();
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

        //noinspection SimplifiableIfStatement
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
            HelpDialogFragment fdf = HelpDialogFragment.newInstance(HelpDialogFragment.CONTENT_REVIEW);
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

    public void quizAnswerButtonClick(View view){
        if(feedbackMode) return;
        int selectedAnswer = -1;

        switch(view.getId()){
            case R.id.quiz_btn_1:
                selectedAnswer = 0;
                break;
            case R.id.quiz_btn_2:
                selectedAnswer = 1;
                break;
            case R.id.quiz_btn_3:
                selectedAnswer = 2;
                break;
            case R.id.quiz_btn_4:
                selectedAnswer = 3;
                break;
            default:
        }
        boolean correctAnswerSelected = (selectedAnswer == correctAnswerNumber);


        if(correctAnswerSelected) {
            // mutate verhindert, dass die Farben der Buttons für andere Buttons wiederverwendet werden
            view.getBackground().mutate().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        } else {
            view.getBackground().mutate().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
            switch(correctAnswerNumber){
                case 0:
                    findViewById(R.id.quiz_btn_1).getBackground().mutate().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
                    break;
                case 1:
                    findViewById(R.id.quiz_btn_2).getBackground().mutate().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
                    break;
                case 2:
                    findViewById(R.id.quiz_btn_3).getBackground().mutate().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
                    break;
                case 3:
                    findViewById(R.id.quiz_btn_4).getBackground().mutate().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
                    break;
            }
        }

        findViewById(R.id.quiz_feedback_section).setVisibility(View.VISIBLE);
        findViewById(R.id.quiz_btn_rate).setEnabled(true);
        feedbackMode = true;
    }


    public void rateQuestionButtonClick(View view) {

        DialogFragment newFragment = new AnswerFeedbackDialogFragment();
        newFragment.show(getFragmentManager(), "rating");
    }

    public void newQuestionButtonClick(View view) {
        findViewById(R.id.quiz_btn_1).getBackground().mutate().clearColorFilter();
        findViewById(R.id.quiz_btn_2).getBackground().mutate().clearColorFilter();
        findViewById(R.id.quiz_btn_3).getBackground().mutate().clearColorFilter();
        findViewById(R.id.quiz_btn_4).getBackground().mutate().clearColorFilter();
        findViewById(R.id.quiz_feedback_section).setVisibility(View.INVISIBLE);
        feedbackMode = false;
        nextQuestion();
    }

    /**
     * Get next Question and sets to UI
     */
    public void nextQuestion() {
        quizAnswerButton1.setClickable(true);
        quizAnswerButton2.setClickable(true);
        quizAnswerButton3.setClickable(true);
        quizAnswerButton4.setClickable(true);
        questionFormulatedWell = false;
        rightAnswerCorrect = false;
        distractorsCorrect = false;
        difficultyValue = 0;
        if(quizQuestionIterator.hasNext()) {
            AndroidQuizQuestion nextQuestion = quizQuestionIterator.next();

            // Prepare Answers (Shuffling)
            List<String> quizAnswers = new ArrayList<String>();

            quizAnswers.add(nextQuestion.getCorrectAnswer());
            quizAnswers.add(nextQuestion.getFalseAnswer1());
            quizAnswers.add(nextQuestion.getFalseAnswer2());
            quizAnswers.add(nextQuestion.getFalseAnswer3());

            Collections.shuffle(quizAnswers);

            // set Question ID
            questionID = nextQuestion.getId();

            // Remember Correct Answer
            correctAnswerNumber = quizAnswers.indexOf(nextQuestion.getCorrectAnswer());

            // Set Text to UI
            quizQuestionTextView.setText(nextQuestion.getQuestion());
            quizAnswerButton1.setText(quizAnswers.get(0));
            quizAnswerButton2.setText(quizAnswers.get(1));
            quizAnswerButton3.setText(quizAnswers.get(2));
            quizAnswerButton4.setText(quizAnswers.get(3));

            

        } else {
            quizQuestionTextView.setText(getString(R.string.quiz_no_further_questions));
            quizAnswerButton1.setVisibility(View.INVISIBLE);
            quizAnswerButton2.setVisibility(View.INVISIBLE);
            quizAnswerButton3.setVisibility(View.INVISIBLE);
            quizAnswerButton4.setVisibility(View.INVISIBLE);

            Log.d(CLASS_NAME, "No next quiz question");
        }
    }



    public static class AnswerFeedbackDialogFragment extends DialogFragment {
        CheckBox questionFormulatedWellCheckBox;
        CheckBox rightAnswerCorrectCheckBox;
        CheckBox distractorsCorrectCheckBox;
        SeekBar difficultySeekBar;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            final View view = inflater.inflate(R.layout.dialog_review, null);

            questionFormulatedWellCheckBox = ((CheckBox) view.findViewById(R.id.quiz_feedback_question_formulation));
            rightAnswerCorrectCheckBox = ((CheckBox) view.findViewById(R.id.quiz_feedback_right_answer));
            distractorsCorrectCheckBox = ((CheckBox) view.findViewById(R.id.quiz_feedback_distractors));
            difficultySeekBar = ((SeekBar) view.findViewById(R.id.quiz_feedback_difficulty));

            questionFormulatedWellCheckBox.setChecked(questionFormulatedWell);
            rightAnswerCorrectCheckBox.setChecked(rightAnswerCorrect);
            distractorsCorrectCheckBox.setChecked(distractorsCorrect);
            difficultySeekBar.setProgress(difficultyValue);

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view).setTitle(getString(R.string.review_question))
                    // Add action buttons
                    .setPositiveButton(R.string.submit_rating, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            questionFormulatedWell = questionFormulatedWellCheckBox.isChecked();
                            rightAnswerCorrect = rightAnswerCorrectCheckBox.isChecked();
                            distractorsCorrect = distractorsCorrectCheckBox.isChecked();
                            difficultyValue = difficultySeekBar.getProgress();
                            int difficulty = difficultyValue*2+1;

                            RateQuestionTask rqt = new RateQuestionTask();
                            rqt.execute(questionID, (rightAnswerCorrect) ? 1 : 0, (distractorsCorrect) ? 1 : 0, (questionFormulatedWell) ? 1 : 0, difficulty);
                            dialog.dismiss();
                            getActivity().findViewById(R.id.quiz_btn_rate).setEnabled(false);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            onCancel(dialog);
                        }
                    });
            return builder.create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            questionFormulatedWell = questionFormulatedWellCheckBox.isChecked();
            rightAnswerCorrect = rightAnswerCorrectCheckBox.isChecked();
            distractorsCorrect = distractorsCorrectCheckBox.isChecked();
            difficultyValue = difficultySeekBar.getProgress();
        }
    }

    /**
     * Create a new Question for the Quiz and send it to the database
     */
    private class LoadQuestionsTask extends AsyncTask<Void, Void, Void> {

        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected Void doInBackground(Void... params) {
            List<QuizQuestion> questions;

            questions = sqaApp.getQuizBackend().getQuestionsForReview();

            if (questions != null) {
                // Clear local quizQuestionList.
                quizQuestionList.clear();
                for (QuizQuestion q : questions) {
                    quizQuestionList.add(new AndroidQuizQuestion(q));
                }
                Log.d(CLASS_NAME, "Loaded Questions: " + quizQuestionList.size());

                // initialize Iterator and set nextQuestion for UI
                quizQuestionIterator = quizQuestionList.listIterator();

            } else {
                Log.e(CLASS_NAME, "Could not load questions from backend!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(quizQuestionList.size()!=0) {
                nextQuestion();
            } else {
                ErrorDialogFragment edf = new ErrorDialogFragment();
                edf.show(getFragmentManager(), "error");
            }
        }
    }

    /**
     * Sends the rating for a question to the database
     */
    private static class RateQuestionTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            sqaApp.getQuizBackend().setQuestionReview(params[0], params[1] > 0, params[2] > 0, params[3] > 0, params[4]);
            return null;
        }
    }

    public static class ErrorDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            final View view = inflater.inflate(R.layout.dialog_quiz_game_feedback, null);
            ((TextView) view.findViewById(R.id.quiz_game_feedback_text)).setText(getString(R.string.error_loading_questions));

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
                            startActivity(new Intent(getActivity(), ReviewActivity.class));
                        }
                    });
            return builder.create();
        }
    }
}
