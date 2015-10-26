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
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import backend.types.QuizQuestion;
import de.tudarmstadt.lt.simpsonsQuiz.util.TypefaceSpan;


public class QuizGameActivity extends Activity {

    private static SimpsonsQuizApp sqaApp;
    private List<QuizQuestion> quizQuestionList;
    private Iterator<QuizQuestion> quizQuestionIterator;
    private int correctAnswerNumber = -1;
    private static CountDownTimer timer;
    private int score;

    private static final int SCORE_PER_RIGHT_ANSWER = 10;
    private static final int TIME_PER_ANSWER = 20; // in seconds


    public static final String CLASS_NAME = "QuizGameActivity";

    private static TextView quizQuestionTextView;
    private static Button quizAnswerButton1;
    private static Button quizAnswerButton2;
    private static Button quizAnswerButton3;
    private static Button quizAnswerButton4;
    private static ProgressBar progressBar;
    private static LinearLayout bottomContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_game);

        // Set UI Elements
        quizQuestionTextView = (TextView) findViewById(R.id.quiz_question);
        quizAnswerButton1 = (Button) findViewById(R.id.quiz_btn_1);
        quizAnswerButton2 = (Button) findViewById(R.id.quiz_btn_2);
        quizAnswerButton3 = (Button) findViewById(R.id.quiz_btn_3);
        quizAnswerButton4 = (Button) findViewById(R.id.quiz_btn_4);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        LinearLayout answerButtonContainer = (LinearLayout) findViewById(R.id.quiz_answer_container);
        bottomContainer = (LinearLayout) findViewById(R.id.quiz_mp_botton_container);

        /* Use application class to maintain global state. */
        sqaApp = (SimpsonsQuizApp) getApplication();
        timer = new AnsweringTimer(TIME_PER_ANSWER);

        quizQuestionList = new ArrayList<>();

        LoadQuestionsTask lqtask = new LoadQuestionsTask();
        lqtask.execute();


        // set custom font for ActionBar Title
        SpannableString s = new SpannableString(getString(R.string.title_activity_quiz_game_mp));
        s.setSpan(new TypefaceSpan(this, "Simpsonfont.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (getActionBar() != null) {
            // Update the action bar title with the TypefaceSpan instance
            getActionBar().setTitle(s);
        }

        // reset the score
        setScore(0);

        quizAnswerButton1.getBackground().clearColorFilter();
        quizAnswerButton2.getBackground().clearColorFilter();
        quizAnswerButton3.getBackground().clearColorFilter();
        quizAnswerButton4.getBackground().clearColorFilter();
        findViewById(R.id.quiz_btn_next_qst).getBackground().clearColorFilter();
        startAnsweringTimer();
        answerButtonContainer.setVisibility(View.VISIBLE);
        setAnswerButtonsClickable(true);

        timer.cancel();

    }
    private void setScore(int score) {
        this.score = score;
        ((TextView) findViewById(R.id.quiz_game_score_view)).setText("Score: " + score);
    }


    public void quizAnswerButtonClick(View view) {
        int selectedAnswer = -1;

        // stop Timer
        timer.cancel();

        switch (view.getId()) {
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

        if (correctAnswerSelected) {
            view.getBackground().mutate().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
            setScore(score + SCORE_PER_RIGHT_ANSWER);
        }
        else{
            view.getBackground().mutate().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
            switch (correctAnswerNumber) {
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
        setAnswerButtonsClickable(false);
        setNextQuestionButtonVisible();
    }



    public void newQuestionButtonClick(View view) {
        findViewById(R.id.quiz_btn_1).getBackground().mutate().clearColorFilter();
        findViewById(R.id.quiz_btn_2).getBackground().mutate().clearColorFilter();
        findViewById(R.id.quiz_btn_3).getBackground().mutate().clearColorFilter();
        findViewById(R.id.quiz_btn_4).getBackground().mutate().clearColorFilter();
        findViewById(R.id.quiz_feedback_section).setVisibility(View.INVISIBLE);
        nextQuestion();
    }


    /**
     * Get next Question and sets to UI
     */
    public void nextQuestion() {
        if (quizQuestionIterator != null && quizQuestionIterator.hasNext()) {
            QuizQuestion nextQuestion = quizQuestionIterator.next();

            // Prepare Answers (Shuffling)
            List<String> quizAnswers = new ArrayList<>();

            quizAnswers.add(nextQuestion.getCorrectAnswer());
            quizAnswers.add(nextQuestion.getFalseAnswer1());
            quizAnswers.add(nextQuestion.getFalseAnswer2());
            quizAnswers.add(nextQuestion.getFalseAnswer3());

            Collections.shuffle(quizAnswers);

            // Remember Correct Answer
            correctAnswerNumber = quizAnswers.indexOf(nextQuestion.getCorrectAnswer());

            // Set Text to UI
            quizQuestionTextView.setText(nextQuestion.getQuestion());
            quizAnswerButton1.setText(quizAnswers.get(0));
            quizAnswerButton2.setText(quizAnswers.get(1));
            quizAnswerButton3.setText(quizAnswers.get(2));
            quizAnswerButton4.setText(quizAnswers.get(3));
            setAnswerButtonsClickable(true);
            startAnsweringTimer();

        } else {
            FeedbackDialogFragment fdf = new FeedbackDialogFragment();
            fdf.show(getFragmentManager(), "feedback");
        }
    }

    /**
     * Create a new Question for the Quiz and send it to the database
     */
    public class LoadQuestionsTask extends AsyncTask<Void, Void, Void> {

        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected Void doInBackground(Void... params) {
            List<QuizQuestion> questions = sqaApp.getQuizBackend().getQuestionsForGame();
            if (questions != null) {
                // Clear local quizQuestionList.
                quizQuestionList.clear();
                for (QuizQuestion q : questions) {
                    quizQuestionList.add(q);
                }
                Log.d(CLASS_NAME, "Loaded Questions for SP: " + quizQuestionList.size());

                // initialize Iterator and set nextQuestion for UI
                quizQuestionIterator = quizQuestionList.iterator();

            } else {
                Log.e(CLASS_NAME, "Could not load questions from backend!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (quizQuestionList.size() == 9) {
                bottomContainer.setVisibility(View.VISIBLE);
                nextQuestion();
            } else {
                ErrorDialogFragment edf = new ErrorDialogFragment();
                edf.show(getFragmentManager(), "error");
            }
        }
    }


    public static class FeedbackDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            final View view = inflater.inflate(R.layout.dialog_quiz_game_feedback, null);

            ((TextView) view.findViewById(R.id.quiz_game_feedback_text)).setText("You scored " + ((QuizGameActivity)getActivity()).score + " points!");


            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view).setTitle(getString(R.string.quiz_game_congrats))
                    // Add action buttons
                      .setNegativeButton(R.string.switch_category, new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int id) {
                              Intent intent = new Intent(getActivity(), StartPageActivity.class);
                              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                              startActivity(intent);
                          }
                    })
                    .setPositiveButton(R.string.new_game, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(getActivity(), QuizGameActivity.class));
                        }
                    });
            return builder.create();
        }
    }


    private void startAnsweringTimer() {
        progressBar.getProgressDrawable().mutate().clearColorFilter();
        progressBar.setProgress(0);
        timer.start();
    }

    private class AnsweringTimer extends CountDownTimer {

        public AnsweringTimer(int seconds) {
            super(seconds * 1000, seconds * 10);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            progressBar.incrementProgressBy(1);
        }

        @Override
        public void onFinish() {
            progressBar.setProgress(progressBar.getMax());
            progressBar.getProgressDrawable().mutate().setColorFilter(0xFFFF0000, PorterDuff.Mode.SRC_IN);
            setNextQuestionButtonVisible();
            setAnswerButtonsClickable(false);
        }
    }

    private void setAnswerButtonsClickable(boolean clickable) {
        findViewById(R.id.quiz_btn_1).setClickable(clickable);
        findViewById(R.id.quiz_btn_2).setClickable(clickable);
        findViewById(R.id.quiz_btn_3).setClickable(clickable);
        findViewById(R.id.quiz_btn_4).setClickable(clickable);
    }


    private void setNextQuestionButtonVisible() {
        findViewById(R.id.quiz_feedback_section).setVisibility(View.VISIBLE);
        findViewById(R.id.quiz_btn_next_qst).setVisibility(View.VISIBLE);
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
                            startActivity(new Intent(getActivity(), QuizGameActivity.class));
                        }
                    });
            return builder.create();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }
}
