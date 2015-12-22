package de.tudarmstadt.lt.simpsonsQuiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import answerProcessing.EntityCollection;
import answerProcessing.Pipeline;
import answerProcessing.QuizPipeline;
import answerProcessing.types.PossibleAnswer;
import answerProcessing.types.Question;
import answerProcessing.types.QuizAnswer;
import backend.types.QuizQuestion;
import jwatson.JWatson;
import jwatson.answer.WatsonAnswer;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectAnswersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectAnswersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectAnswersFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String QUESTION = "";

    private String question;

    private OnFragmentInteractionListener mListener;

    private static LinearLayout answerContainer;
    private static ProgressDialog progDialog;

    private static final String CLASS_NAME = "CREATE_QUESTION";

    private final static int numberOfSelectedAnswersForQuiz = 3;
    private final static int numberOfShownAlternativeAnswers = 10;
    View view;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param question Parameter 1.
     * @return A new instance of fragment SelectAnswersFragment.
     */
    public static SelectAnswersFragment newInstance(String question) {
        SelectAnswersFragment fragment = new SelectAnswersFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION, question);
        fragment.setArguments(args);
        return fragment;
    }

    public SelectAnswersFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = getArguments().getString(QUESTION);
        }

    }

    /**
     * answerStates:
     * 0    nicht selektiert
     * 1    als richtige Antwort selektiert
     * 2    als falsche Antwort selektiert
     */
    List<Integer> answerStates;
    List<QuizAnswer> answers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_select_answers, container, false);
        ((TextView) view.findViewById(R.id.select_answers_question_text)).setText(question);

        answerContainer = (LinearLayout) view.findViewById(R.id.select_answers_answer_container);
        view.findViewById(R.id.create_question_button).getBackground().clearColorFilter();


        // Get Answers from Watson
        SelectAnswersTask selectAnswers = new SelectAnswersTask();
        progDialog = new ProgressDialog(getActivity());
        progDialog.setMessage("Loading...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(true);
        progDialog.show();

        selectAnswers.execute(question);
        System.out.println("view:" + answers);
        return view;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Creates the Object from the entered question and the selected answers
     */
    public QuizQuestion createQuestion() {

        QuizQuestion quizQuestion = new QuizQuestion();

        if(question.length() > 0 && question.charAt(question.length() -1 ) != '?')
            question += "?";

        quizQuestion.setQuestion(question);

        quizQuestion.setCategory("APP");

        String correctAnswer = "";
        List<String> falseAnswers = new ArrayList<String>();

        Button answerButton;
        // iteriert über alle AnswerButtons außer dem "+" Button
        for (int i = 0; i < answerContainer.getChildCount() - 1; ++i) {
            answerButton = (Button) answerContainer.getChildAt(i);
            // Correct answer if green / state = 1
            if (answerStates.get(answerButton.getId()) == 1)
                correctAnswer = (String) answerButton.getText();
                // False answer if red / state = 2
            else if (answerStates.get(answerButton.getId()) == 2)
                falseAnswers.add((String) answerButton.getText());
        }

        if (falseAnswers.size() != 3 || correctAnswer.equals("")) {
            Log.e(CLASS_NAME, "Wrong number of answers selected");
            ((TextView) view.findViewById(R.id.enter_question_hint_answer_selection)).setText(getString(R.string.enter_question_hint_answer_selection));
            return null;
        }

        quizQuestion.setCorrectAnswer(correctAnswer);
        quizQuestion.setFalseAnswer1(falseAnswers.get(0));
        quizQuestion.setFalseAnswer2(falseAnswers.get(1));
        quizQuestion.setFalseAnswer3(falseAnswers.get(2));

        Log.d(CLASS_NAME, "Question: " + question);
        Log.d(CLASS_NAME, "Correct Answer: " + correctAnswer);
        Log.d(CLASS_NAME, "False Answers: " + falseAnswers.toString());

        return quizQuestion;

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }

    private class WrongAnswerClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (answerStates.get(v.getId()) == 0) {
                v.getBackground().mutate().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
                answerStates.set(v.getId(), 2);
            } else {
                v.getBackground().mutate().setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);
                answerStates.set(v.getId(), 0);
            }
        }
    }

    private class CorrectAnswerClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            for (int i = 0; i < answers.size(); i++) {
                if (answerStates.get(i) == 1) {
                    answerStates.set(i, 0);
                    answerContainer.findViewById(i).getBackground().mutate().clearColorFilter();
                    break;
                }
            }
            v.getBackground().mutate().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
            answerStates.set(v.getId(), 1);
            return true;
        }
    }

    /**
     * Uses Watson and Post-Proecessing to pre-select possible answers for the quiz question
     */
    private class SelectAnswersTask extends AsyncTask<String, Void, List<QuizAnswer>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected List<QuizAnswer> doInBackground(String... question) {
            JWatson watson;
            answers = new ArrayList<QuizAnswer>();
            SimpsonsQuizApp sqaApp = (SimpsonsQuizApp) getActivity().getApplication();
            try {
                watson = new
                        JWatson(sqaApp.getAppProperty("WATSON_USER"), sqaApp.getAppProperty("WATSON_PWD"), sqaApp.getAppProperty("WATSON_URL"));
            } catch (IOException e) {
                new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(getString(R.string.error_watson_init))
                                // Add action buttons
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        return builder.create();
                    }
                }.show(getActivity().getFragmentManager(), "error");
                e.printStackTrace();
                return answers;
            }
            WatsonAnswer wAnswer;
            try {
                wAnswer = watson.askQuestion(question[0]);
            } catch (IOException e) {
                new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(getString(R.string.error_watson_exec))
                                // Add action buttons
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        return builder.create();
                    }
                }.show(getActivity().getFragmentManager(), "error");
                e.printStackTrace();
                return answers;
            }
            try {
                Question pQuestion = new Question(question[0]);
                List<PossibleAnswer> possibleAnswers = new ArrayList<PossibleAnswer>();
                // set question type information
                pQuestion = Pipeline.annotateQuestion(pQuestion, sqaApp.getAppProperty("REMOTE_NLP_URL"), wAnswer);

                for (int i = 0; i < numberOfSelectedAnswersForQuiz; i++) {
                    if (wAnswer.getAnswerInformation().getAnswers().size() > i && wAnswer.getAnswerInformation().getEvidencelist().size() > i) {
                        String title = wAnswer.getAnswerInformation().getEvidencelist().get(i).getTitle();
                        PossibleAnswer pAnswer = new PossibleAnswer(pQuestion,
                                wAnswer.getAnswerInformation().getEvidencelist().get(i).getText(),
                                wAnswer.getAnswerInformation().getAnswers().get(i).getConfidence(),
                                EntityCollection.retrieveMetadata(title),
                                title);
                        possibleAnswers.add(pAnswer);
                    }
                }

                List<QuizAnswer> generatedAnswers = QuizPipeline.executePipeline(pQuestion, possibleAnswers, sqaApp.getAppProperty("REMOTE_NLP_URL"), numberOfShownAlternativeAnswers);
                answers.addAll(generatedAnswers);
                System.out.println("task:" + answers);
            } catch (Exception e) {
                //QAActivity.messageBox(getActivity(), "Error during Pipeline Execution", e.getMessage());
                e.printStackTrace();
                return null;
            }

            return answers;
        }

        /**
         * The system calls this to perform work in the UI thread and
         * delivers
         * the result from doInBackground()
         */
        @Override
        protected void onPostExecute(List<QuizAnswer> result) {

            if (result == null) {
                ErrorDialogFragment edf = new ErrorDialogFragment();
                edf.show(getFragmentManager(), "error");


                getActivity().getFragmentManager().popBackStack();
            }

            if (progDialog.isShowing())
                progDialog.dismiss();


            answerStates = new ArrayList<Integer>(); //0:default, 1: correct answer, 2: wrong answer

            if (!answers.isEmpty() && answers.get(0).getNeTypes().contains("UNKNOWN")) {
                // Ungültiger Question Type
                UnknownQuestionTypeDialogFragment uqtdf = new UnknownQuestionTypeDialogFragment();
                uqtdf.show(getFragmentManager(), "unknown_question_type");
                answers.clear();
            } else {
                for (int i = 0; i < answers.size(); i++) {

                    answerStates.add(0);

                    Button answerButton = new Button(getActivity());
                    answerButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    answerButton.setText(answers.get(i).getAnswer());
                    answerButton.setId(i);
                    answerButton.setOnClickListener(new WrongAnswerClickListener());
                    answerButton.setOnLongClickListener(new CorrectAnswerClickListener());
                    answerContainer.addView(answerButton);
                }
            }

            Button addAdditionalAnswerButton = new Button(getActivity());
            addAdditionalAnswerButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            addAdditionalAnswerButton.setText("+");
            addAdditionalAnswerButton.setId(-1);
            addAdditionalAnswerButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    // Create a Dialog

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.add_individual_answer);
                    // Set up the input
                    final EditText input = new EditText(getActivity());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newAnswer = input.getText().toString();
                            answers.add(new QuizAnswer(newAnswer, null));
                            answerStates.add(0);

                            Button newAnswerButton = new Button(getActivity());
                            newAnswerButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            newAnswerButton.setText(newAnswer);
                            newAnswerButton.setId(answers.size() - 1);
                            newAnswerButton.setOnClickListener(new WrongAnswerClickListener());
                            newAnswerButton.setOnLongClickListener(new CorrectAnswerClickListener());
                            answerContainer.addView(newAnswerButton, answerContainer.getChildCount() - 1);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            });
            answerContainer.addView(addAdditionalAnswerButton);
        }
    }

    /**
     * überprüft ob die korrekte anzahl richtiger und falscher antworten selektiert wurde
     *
     * @return
     */
    private boolean checkAnswers() {
        int wrongAnswerCount = 0;
        int correctAnswerCount = 0;
        for (int j : answerStates) {
            if (j == 2) wrongAnswerCount++;
            else if (j == 1) correctAnswerCount++;
        }

        return wrongAnswerCount == 3 && correctAnswerCount == 1;
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
                            ((CreateQuestionActivity) getActivity()).generateAnswers(getActivity().findViewById(R.id.create_question_button));
                        }
                    });
            return builder.create();
        }
    }

    public static class UnknownQuestionTypeDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            final View view = inflater.inflate(R.layout.dialog_quiz_game_feedback, null);
            ((TextView) view.findViewById(R.id.quiz_game_feedback_text)).setText(getString(R.string.unknown_question_type));

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view).setTitle(getString(R.string.unknown_question_type))
                    // Add action buttons
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            return builder.create();
        }
    }
}
