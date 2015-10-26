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
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import backend.types.QuizQuestion;
import de.tudarmstadt.lt.simpsonsQuiz.util.TypefaceSpan;


public class QuizGameMPActivity extends Activity {

    private static SimpsonsQuizApp sqaApp;
    private List<QuizQuestion> quizQuestionList;
    private Iterator<QuizQuestion> quizQuestionIterator;
    private int correctAnswerNumber = -1;
    private boolean feedbackMode = false;
    private static CountDownTimer timer;
    private static int scoreA;
    private static int scoreB;
    private int activePlayer;
    private boolean secondAnswerMode = false;

    private static final int SCORE_PER_RIGHT_ANSWER = 20;
    private static final int SCORE_PER_2ND_RIGHT_ANSWER = 15;
    private static final int SCORE_PER_WRONG_ANSWER = -10;
    private static final int TIME_FOR_BUZZING = 10;
    private static final int TIME_PER_ANSWER = 10; // in seconds
    private static final int TIME_PER_2ND_ANSWER = 5;

    private static final int TIME_BETWEEN_BUZZING_AND_QUIZ = 1000;

    private static final String PLAYERA_HINT = "<= Player A's turn  ";
    private static final String PLAYERB_HINT = "  Player B's turn =>";


    public static final String CLASS_NAME = "QuizGameActivity";

    private static TextView quizQuestionTextView;
    private static Button quizAnswerButton1;
    private static Button quizAnswerButton2;
    private static Button quizAnswerButton3;
    private static Button quizAnswerButton4;
    private static ProgressBar progressBar;
    private static LinearLayout buzzerContainer;
    private static LinearLayout answerButtonContainer;
    private static LinearLayout bottomContainer;
    private static LinearLayout space;
    private static TextView spaceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_game_mp);

        // Set UI Elements
        quizQuestionTextView = (TextView) findViewById(R.id.quiz_question);
        quizAnswerButton1 = (Button) findViewById(R.id.quiz_btn_1);
        quizAnswerButton2 = (Button) findViewById(R.id.quiz_btn_2);
        quizAnswerButton3 = (Button) findViewById(R.id.quiz_btn_3);
        quizAnswerButton4 = (Button) findViewById(R.id.quiz_btn_4);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        buzzerContainer = (LinearLayout) findViewById(R.id.quiz_mp_buzzer_container);
        answerButtonContainer = (LinearLayout) findViewById(R.id.quiz_answer_container);
        bottomContainer = (LinearLayout) findViewById(R.id.quiz_mp_botton_container);
        space = (LinearLayout) findViewById(R.id.quiz_mp_space);
        spaceText = (TextView) findViewById(R.id.quiz_mp_space_text);

        /* Use application class to maintain global state. */
        sqaApp = (SimpsonsQuizApp) getApplication();

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
        TextView scoreAView = (TextView) findViewById(R.id.quiz_mp_score_a);
        TextView scoreBView = (TextView) findViewById(R.id.quiz_mp_score_b);
        scoreAView.setText("0");
        scoreBView.setText("0");
        scoreA = 0;
        scoreB = 0;

        setAnswerButtonsClickable(false);
        setBuzzerButtonsClickable(false);

        quizAnswerButton1.getBackground().clearColorFilter();
        quizAnswerButton2.getBackground().clearColorFilter();
        quizAnswerButton3.getBackground().clearColorFilter();
        quizAnswerButton4.getBackground().clearColorFilter();
        findViewById(R.id.quiz_game_mp_buzzer_a).getBackground().clearColorFilter();
        findViewById(R.id.quiz_game_mp_buzzer_b).getBackground().clearColorFilter();
        findViewById(R.id.quiz_btn_next_qst).getBackground().clearColorFilter();
    }

    public void addScore(int a, int b) {
        scoreA += a;
        scoreB += b;
        TextView scoreAView = (TextView) findViewById(R.id.quiz_mp_score_a);
        TextView scoreBView = (TextView) findViewById(R.id.quiz_mp_score_b);
        scoreAView.setText(String.valueOf(scoreA));
        scoreBView.setText(String.valueOf(scoreB));
    }

    public void playerBuzzer(View view) {
        if (view.getId() == R.id.quiz_game_mp_buzzer_a) {
            activePlayer = 0;
            setHint(PLAYERA_HINT);
            spaceText.setText(getString(R.string.A));
        } else {
            activePlayer = 1;
            setHint(PLAYERB_HINT);
            spaceText.setText(getString(R.string.B));
        }

        timer.cancel();
        progressBar.setProgress(0);
        setBuzzerButtonsClickable(false);
        buzzerContainer.setVisibility(View.GONE);
        space.setVisibility(View.VISIBLE);
        findViewById(R.id.quiz_btn_1).getBackground().clearColorFilter();
        findViewById(R.id.quiz_btn_2).getBackground().clearColorFilter();
        findViewById(R.id.quiz_btn_3).getBackground().clearColorFilter();
        findViewById(R.id.quiz_btn_4).getBackground().clearColorFilter();

        // Execute some code after 2 seconds have passed
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                space.setVisibility(View.GONE);
                answerButtonContainer.setVisibility(View.VISIBLE);
                setAnswerButtonsClickable(true);
                startAnsweringTimer();
                resetButtonCanvas();
            }
        }, TIME_BETWEEN_BUZZING_AND_QUIZ);
    }


    public void quizAnswerButtonClick(View view) {
        if (feedbackMode) return;
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
            if (secondAnswerMode) {
                if (activePlayer == 0) {
                    addScore(SCORE_PER_2ND_RIGHT_ANSWER, 0);
                } else {
                    addScore(0, SCORE_PER_2ND_RIGHT_ANSWER);
                }
            } else {
                if (activePlayer == 0) {
                    addScore(SCORE_PER_RIGHT_ANSWER, 0);
                } else {
                    addScore(0, SCORE_PER_RIGHT_ANSWER);
                }
            }

            setNextQuestionButtonVisible();
            feedbackMode = true;
        } else {
            view.getBackground().mutate().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
            if (secondAnswerMode) {
                // Beide player haben die Frage falsch beantwortet
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

                setNextQuestionButtonVisible();
                feedbackMode = true;
            } else {
                secondChanceMode(view);
            }
        }

    }

    private void secondChanceMode(final View view) {
        setAnswerButtonsClickable(false);
        if (activePlayer == 0) {
            addScore(SCORE_PER_WRONG_ANSWER, 0);
        } else {
            addScore(0, SCORE_PER_WRONG_ANSWER);
        }
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            public void run() {
                if (activePlayer == 0) {
                    setHint(PLAYERB_HINT);
                    spaceText.setText(getString(R.string.B));
                } else {
                    setHint(PLAYERA_HINT);
                    spaceText.setText(getString(R.string.A));
                }
                answerButtonContainer.setVisibility(View.GONE);
                space.setVisibility(View.VISIBLE);
                Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    public void run() {
                        space.setVisibility(View.GONE);
                        answerButtonContainer.setVisibility(View.VISIBLE);
                        setAnswerButtonsClickable(true);

                        // Der erste Player hat die Frage falsch beantwortet, der 2. bekommt ebenfalls die Chance, die Frage zu beantworten
                        if (view != null) {
                            view.setClickable(false);
                        }
                        secondAnswerMode = true;
                        start2ndAnsweringTimer();


                        // Player wechseln
                        activePlayer = (activePlayer + 1) % 2;
                    }
                }, TIME_BETWEEN_BUZZING_AND_QUIZ);
            }
        }, TIME_BETWEEN_BUZZING_AND_QUIZ);

    }

    public void newQuestionButtonClick(View view) {
        setAnswerButtonsClickable(false);
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

            ((TextView) findViewById(R.id.quiz_mp_answers_l)).setText(
                    "1. " + quizAnswers.get(0)
                            + "\n3. " + quizAnswers.get(2));

            ((TextView) findViewById(R.id.quiz_mp_answers_r)).setText(
                    "2. " + quizAnswers.get(1)
                            + "\n4. " + quizAnswers.get(3));

            secondAnswerMode = false;
            startBuzzingTimer();
            setBuzzerButtonsClickable(true);
            setHint("");

            answerButtonContainer.setVisibility(View.GONE);
            buzzerContainer.setVisibility(View.VISIBLE);

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
                Log.d(CLASS_NAME, "Loaded Questions for MP: " + quizQuestionList.size());

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
            String title;
            if (scoreA > scoreB) {
                title = "Player A won!";
                ((TextView) view.findViewById(R.id.quiz_game_feedback_text)).setText("You scored " + scoreA + " points!");
            } else if (scoreB > scoreA) {
                title = "Player B won!";
                ((TextView) view.findViewById(R.id.quiz_game_feedback_text)).setText("You scored " + scoreB + " points!");
            } else {
                title = "Draw!";
                ((TextView) view.findViewById(R.id.quiz_game_feedback_text)).setText("You both scored " + scoreA + " points!");
            }

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view).setTitle(title)
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
                            startActivity(new Intent(getActivity(), QuizGameMPActivity.class));
                        }
                    });
            return builder.create();
        }
    }

    private void startBuzzingTimer() {
        progressBar.getProgressDrawable().mutate().clearColorFilter();
        progressBar.setProgress(0);
        timer = new MyTimer(TIME_FOR_BUZZING);
        timer.start();
    }

    private void startAnsweringTimer() {
        progressBar.getProgressDrawable().mutate().clearColorFilter();
        progressBar.setProgress(0);
        timer = new AnsweringTimer(TIME_PER_ANSWER);
        timer.start();
    }

    private void start2ndAnsweringTimer() {
        progressBar.getProgressDrawable().mutate().clearColorFilter();
        progressBar.setProgress(0);
        timer = new MyTimer(TIME_PER_2ND_ANSWER);
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
            secondChanceMode(null);
        }
    }

    private class MyTimer extends CountDownTimer {

        public MyTimer(int seconds) {
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
            setAnswerButtonsClickable(false);
            setBuzzerButtonsClickable(false);
            setNextQuestionButtonVisible();
            feedbackMode = true;
        }
    }

    private void setAnswerButtonsClickable(boolean clickable) {
        space.setVisibility(View.GONE);
        findViewById(R.id.quiz_btn_1).setClickable(clickable);
        findViewById(R.id.quiz_btn_2).setClickable(clickable);
        findViewById(R.id.quiz_btn_3).setClickable(clickable);
        findViewById(R.id.quiz_btn_4).setClickable(clickable);
    }

    private void setBuzzerButtonsClickable(boolean clickable) {
        space.setVisibility(View.GONE);
        findViewById(R.id.quiz_game_mp_buzzer_a).setClickable(clickable);
        findViewById(R.id.quiz_game_mp_buzzer_b).setClickable(clickable);
    }

    private void resetButtonCanvas() {
        findViewById(R.id.quiz_btn_1).getBackground().clearColorFilter();
        findViewById(R.id.quiz_btn_2).getBackground().clearColorFilter();
        findViewById(R.id.quiz_btn_3).getBackground().clearColorFilter();
        findViewById(R.id.quiz_btn_4).getBackground().clearColorFilter();
    }

    private void setHint(String text) {
        findViewById(R.id.quiz_feedback_section).setVisibility(View.VISIBLE);
        findViewById(R.id.quiz_btn_next_qst).setVisibility(View.GONE);
        findViewById(R.id.quiz_game_mp_player_hint).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.quiz_game_mp_player_hint)).setText(text);
    }

    private void setNextQuestionButtonVisible() {
        findViewById(R.id.quiz_feedback_section).setVisibility(View.VISIBLE);
        findViewById(R.id.quiz_game_mp_player_hint).setVisibility(View.GONE);
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
                            startActivity(new Intent(getActivity(), QuizGameMPActivity.class));
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
