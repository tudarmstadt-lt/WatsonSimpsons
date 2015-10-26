package de.tudarmstadt.lt.simpsonsQuiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.concurrent.ExecutionException;

import backend.types.QuizUser;
import de.tudarmstadt.lt.simpsonsQuiz.util.TypefaceSpan;
import de.tudarmstadt.lt.simpsonsQuiz.util.Util;


public class LoginRegisterActivity extends Activity {

    static SimpsonsQuizApp sqaApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Use application class to maintain global state. */
        sqaApp = (SimpsonsQuizApp) getApplication();
        if (!Util.isNetworkConnected(this)) {
            setContentView(R.layout.layout_no_internet_connection);
            findViewById(R.id.btn_no_internet_retry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginRegisterActivity.this, LoginRegisterActivity.class));
                }
            });
            return;
        } else {
            /* Auto login, if Api Key was saved during previous session */
            String apiKey = sqaApp.loadApiKey(this);
            if (!apiKey.equals("")) {
                LoginViaApiKeyTask lvakt = new LoginViaApiKeyTask();
                lvakt.execute(apiKey);
            }
        }
        setContentView(R.layout.activity_login_register);
        // set custom font for ActionBar Title
        SpannableString s = new SpannableString(getString(R.string.title_activity_login_register));
        s.setSpan(new TypefaceSpan(this, "Simpsonfont.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (getActionBar() != null) {
            // Update the action bar title with the TypefaceSpan instance
            getActionBar().setTitle(s);
        }

        // ColorFilter von Buttons entfernen
        findViewById(R.id.btn_login).getBackground().clearColorFilter();
        findViewById(R.id.btn_register).getBackground().clearColorFilter();
        findViewById(R.id.btn_login_as_guest).getBackground().clearColorFilter();
    }


    public void login(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_login, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView).setTitle("Login")
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        LoginTask lt = new LoginTask();
                        lt.execute(((EditText) dialogView.findViewById(R.id.username)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.password)).getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }

    public void register(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_register, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView).setTitle("Registration")
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        RegisterTask rt = new RegisterTask();
                        rt.execute(((EditText) dialogView.findViewById(R.id.username)).getText().toString(),
                                ((EditText) dialogView.findViewById(R.id.password)).getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();

    }

    /**
     * Create a new Question for the Quiz and send it to the database
     */
    private class LoginTask extends AsyncTask<String, Void, QuizUser> {

        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected QuizUser doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            return sqaApp.getQuizBackend().loginUser(username, password, true);
        }

        @Override
        protected void onPostExecute(QuizUser user) {
            if (user == null) {
                new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(getString(R.string.error_login))
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        return builder.create();
                    }
                }.show(LoginRegisterActivity.this.getFragmentManager(), "error");
            } else {
                LoginRegisterActivity.this.saveApiKey();
                sqaApp.saveUserName(LoginRegisterActivity.this, user.getUsername());
                startActivity(new Intent(LoginRegisterActivity.this, StartPageActivity.class));
            }
        }
    }


    /**
     * Create a new Question for the Quiz and send it to the database
     */
    private class RegisterTask extends AsyncTask<String, Void, QuizUser> {

        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected QuizUser doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            if (sqaApp.getQuizBackend().registerUser(username, password)) {
                return sqaApp.getQuizBackend().loginUser(username, password, true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(QuizUser user) {
            if (user == null) {
                new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(getString(R.string.error_registration))
                                // Add action buttons
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        return builder.create();
                    }
                }.show(LoginRegisterActivity.this.getFragmentManager(), "error");
            } else {
                LoginRegisterActivity.this.saveApiKey();
                sqaApp.saveUserName(LoginRegisterActivity.this, user.getUsername());
                startActivity(new Intent(LoginRegisterActivity.this, StartPageActivity.class));
            }
        }
    }

    private void saveApiKey() {
        sqaApp.saveApiKey(this);
    }

    public void loginAsGuest(View view) {
        sqaApp.saveUserName(LoginRegisterActivity.this, "guest");
        LoginViaApiKeyTask lvakt = new LoginViaApiKeyTask();
        lvakt.execute(sqaApp.getAppProperty("BACKEND_API_KEY_GUEST"));
    }

    /**
     * Checks, if a user is logged in via the api key
     */
    public class LoginViaApiKeyTask extends AsyncTask<String, Void, Boolean> {

        /**
         * The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         */
        protected Boolean doInBackground(String... params) {
            String apiKey = params[0];
            sqaApp.getQuizBackend().setApiKey(apiKey);
            int score = sqaApp.getQuizBackend().getUserScore();
            return score > -1;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                startActivity(new Intent(LoginRegisterActivity.this, StartPageActivity.class));
            } else {
                new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(getString(R.string.error_login))
                                // Add action buttons
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        return builder.create();
                    }
                }.show(LoginRegisterActivity.this.getFragmentManager(), "error");
            }
        }
    }
}
