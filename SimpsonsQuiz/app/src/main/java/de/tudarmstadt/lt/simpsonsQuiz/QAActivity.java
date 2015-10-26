

package de.tudarmstadt.lt.simpsonsQuiz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import answerProcessing.EntityCollection;
import answerProcessing.Pipeline;
import answerProcessing.QuizPipeline;
import answerProcessing.types.Image;
import answerProcessing.types.PossibleAnswer;
import answerProcessing.types.Question;
import answerProcessing.types.QuizAnswer;
import de.tudarmstadt.lt.simpsonsQuiz.util.TypefaceSpan;
import imagefinder.ImageFinder;
import jwatson.JWatson;
import jwatson.answer.WatsonAnswer;
import uk.co.deanwild.flowtextview.FlowTextView;


public class QAActivity extends Activity {

    JWatson watson;

    SimpsonsQuizApp sqaApp;

    private static EditText questionText;
    private static TextView answerTextViewBodyDebug;
    private static FlowTextView answerTextViewBody;
    private static TextView answerTextViewHeader;
    private static ImageView answerImage;
    private static ProgressDialog progDialog;
    private static Button nextAnswerButton;

    private final static int numberOfSelectedAnswersForPipeline = 2;


    private boolean debugMode = false;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa);

        /* Use application class to maintain global state. */
        sqaApp = (SimpsonsQuizApp) getApplication();

        // Set UI Elements
        questionText = (EditText) findViewById(R.id.questionText);
        answerTextViewBodyDebug = (TextView) findViewById(R.id.answerTextViewBodyDebug);
        answerTextViewBody = (FlowTextView) findViewById(R.id.answerTextViewBody);
        answerTextViewHeader = (TextView) findViewById(R.id.answerTextViewHeader);
        answerImage = (ImageView) findViewById(R.id.answerImage);
        nextAnswerButton = (Button) findViewById(R.id.btn_qa_next_answer);

        nextAnswerButton.setEnabled(false);

        // Set OnClick Listener for AskWatson Button
        questionText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    findViewById(R.id.btn_qa_ask_watson).performClick();
                    return true;
                }
                return false;
            }
        });

        // Set custom fonts
        Typeface custom_font = Typeface.createFromAsset(getAssets(), String.format(Locale.US, "fonts/%s", "Simpsonfont.ttf"));

        Button askWatsonButton = (Button) findViewById(R.id.btn_qa_ask_watson);
        askWatsonButton.setTypeface(custom_font);

        nextAnswerButton.setTypeface(custom_font);

        // set custom font for ActionBar Title
        SpannableString s = new SpannableString(getString(R.string.qaActivity_header));
        s.setSpan(new TypefaceSpan(this, "Simpsonfont.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (getActionBar() != null) {

            // Update the action bar title with the TypefaceSpan instance
            getActionBar().setTitle(s);

            // Add Up Button
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.btn_qa_ask_watson).getBackground().clearColorFilter();
        findViewById(R.id.btn_qa_next_answer).getBackground().clearColorFilter();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString("CurrentAnswer", answerTextViewBody.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        answerTextViewBody.setText(savedInstanceState.getString("CurrentAnswer"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_qa, menu);
        MenuItem debugModeSwitch = menu.findItem(R.id.debugModeSwitch);
        debugModeSwitch.setChecked(debugMode);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // if debug mode switch is activated, show Scores
            case R.id.debugModeSwitch:
                debugMode = !item.isChecked();
                item.setChecked(debugMode);
                if (debugMode) {
                    setDebugModeScores(true);
                } else {
                    answerTextViewBodyDebug.setText("");
                }
                return true;

            case R.id.help:
                HelpDialogFragment fdf = HelpDialogFragment.newInstance(HelpDialogFragment.CONTENT_QA);
                fdf.show(getFragmentManager(), "help");
                return true;
            case R.id.about:
                AboutDialogFragment adf = new AboutDialogFragment();
                adf.show(getFragmentManager(), "about");
                return true;
            case R.id.logout:
                sqaApp.logout(this);
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return false;
        }
    }

    private void setDebugModeScores(boolean enabled) {
        if (!enabled) {
            answerTextViewBodyDebug.setText("");
            return;
        }
        if (debugMode && pAnswer != null) {
            answerTextViewBodyDebug.setText("Score = " + String.format("%.1f", pAnswer.getScore() * 100) + "%"
                    + "\n" + pAnswer.getURL());
        }
    }

    public void askWatson(View view) {
        WatsonQuery watsonQuery = new WatsonQuery();
        String question =
                questionText.getText().toString();
        progDialog = new ProgressDialog(QAActivity.this);
        progDialog.setMessage("Loading...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(true);
        progDialog.show();
        watsonQuery.execute(question);
        nextAnswerButton.setEnabled(true);
    }

    private QuizAnswer pAnswer;

    // save the iterator, not the list item
    private Iterator<QuizAnswer> bestAnswerSentenceIterator;

    public void nextAnswer(View view) {
        /*if(bestAnswerSentenceIterator == null){
            bestAnswerSentenceIterator = bestAnswerSentences.iterator();
        }*/

        if (bestAnswerSentenceIterator.hasNext()) {
            pAnswer = bestAnswerSentenceIterator.next();
            setTextViews(pAnswer);
            setDebugModeScores(true);
        } else {
            answerTextViewHeader.setText("");
            answerImage.setImageBitmap(null);
            answerTextViewHeader.setVisibility(View.GONE);
            answerImage.setVisibility(View.GONE);
            answerTextViewBody.setText("There are no further answers!");
            answerTextViewBodyDebug.setText("");
            setDebugModeScores(false);
            nextAnswerButton.setEnabled(false);
        }
    }


    /**
     * Sends a query to the Watson Server and executes the pipeline
     */
    private class WatsonQuery extends AsyncTask<String, Void, List<QuizAnswer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected List<QuizAnswer> doInBackground(String... question) {
            try {
                watson = new
                        JWatson(sqaApp.getAppProperty("WATSON_USER"), sqaApp.getAppProperty("WATSON_PWD"), sqaApp.getAppProperty("WATSON_URL"));
            } catch (IOException e) {
                //messageBox(QAActivity.this, "Error during Watson Initialisation", e.getMessage());
                e.printStackTrace();
                return null;
            }
            WatsonAnswer wAnswer;
            try {
                wAnswer = watson.askQuestion(question[0]);
            } catch (IOException e) {
                //messageBox(QAActivity.this, "Error during Watson Execution", e.getMessage());
                e.printStackTrace();
                return null;
            }

            Question pQuestion = new Question(question[0]);
            try {
                // set question type information
                pQuestion = Pipeline.annotateQuestion(pQuestion, sqaApp.getAppProperty("REMOTE_NLP_URL"), wAnswer);

                List<PossibleAnswer> possibleAnswers = new ArrayList<>();
                for (int i = 0; i < numberOfSelectedAnswersForPipeline; i++) {
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
                List<QuizAnswer> result = QuizPipeline.executeQAPipeline(pQuestion, possibleAnswers, sqaApp.getAppProperty("REMOTE_NLP_URL"));
                for (QuizAnswer answer : result) {

                    boolean isEpisodeorSeason = answer.isEpisodeOrSeason();
                    imagefinder.types.Image image = ImageFinder.findPicture(
                            answer.getImageName(), isEpisodeorSeason, 220);
                    answer.setImage(new Image(image.getName(), image.getWidth(), image
                            .getHeight(), image.getUrl(), image.getDescriptionurl(),
                            image.getThumburl(), image.getTitle()));
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * The system calls this to perform work in the UI thread and
         * delivers
         * the result from doInBackground()
         */
        protected void onPostExecute(List<QuizAnswer> result) {
            if (result != null && !result.isEmpty()) {
                bestAnswerSentenceIterator = result.listIterator();

                pAnswer = bestAnswerSentenceIterator.next();
                setTextViews(pAnswer);
                setDebugModeScores(true);
            } else {
                answerTextViewBody.setText(getString(R.string.qa_error_try_again));
            }
            dismissProgDialogHandler.obtainMessage().sendToTarget();
        }
    }


    /**
     * removes the ProgressView
     */
    private static Handler dismissProgDialogHandler;

    static {
        dismissProgDialogHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (progDialog.isShowing())
                    progDialog.dismiss();
                return true;
            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        QuizAnswer answer;

        public DownloadImageTask(ImageView bmImage, QuizAnswer answer) {
            this.bmImage = bmImage;
            this.answer = answer;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bmImage.setImageDrawable(null);
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            answerTextViewBody.setText(answer.getSentence());
            answerTextViewHeader.setText(answer.getAnswer());
        }
    }

    private void setTextViews(QuizAnswer answer) {
        answerTextViewHeader.setVisibility(View.VISIBLE);
        answerImage.setVisibility(View.VISIBLE);
        DownloadImageTask dit = new DownloadImageTask(answerImage, answer);
        try {
            dit.execute(answer.getImage().getThumburl());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

