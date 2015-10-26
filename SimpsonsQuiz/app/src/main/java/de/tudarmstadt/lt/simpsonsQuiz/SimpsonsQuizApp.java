package de.tudarmstadt.lt.simpsonsQuiz;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import answerProcessing.EntityCollection;
import backend.QuizBackend;
/**
 * Created by dath on 10.08.15.
 */
public class SimpsonsQuizApp extends Application {

    private static final String CLASS_NAME = SimpsonsQuizApp.class.getSimpleName();

    private static final String WATSON_URL = "watsonUrl";
    private static final String WATSON_USER = "watsonUsername";
    private static final String WATSON_PWD = "watsonPassword";

    private static final String REMOTE_NLP_URL = "remoteNLPUrl";

    private static final String BACKEND_URL = "backendUrl";
    private static final String BACKEND_APP_KEY = "backendAppKey";
    private static final String BACKEND_API_KEY_GUEST = "backendApiKeyGuest";

    private static final String PROPS_FILE = "simpsonsquiz.properties";

    private Properties sharedProperties;

    private QuizBackend quizBackend;

    public SimpsonsQuizApp() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Read from properties file
        Properties props = new java.util.Properties();
        Context context = getApplicationContext();
        try {
            AssetManager assetManager = context.getAssets();
            props.load(assetManager.open(PROPS_FILE));
            Log.i(CLASS_NAME, "Found configuration file: " + PROPS_FILE);
        } catch (FileNotFoundException e) {
            Log.e(CLASS_NAME, "The "+PROPS_FILE+" file was not found.", e);
        } catch (IOException e) {
            Log.e(CLASS_NAME, "The "+PROPS_FILE+" file could not be read properly.", e);
        }

        quizBackend = new QuizBackend(props.getProperty(BACKEND_URL), props.getProperty(BACKEND_APP_KEY));

        sharedProperties = props;
        
        EntityCollection.initEntityInfos();
    }

    public String getAppProperty(String name) {
        switch (name) {
            case "WATSON_URL":
                return sharedProperties.getProperty(WATSON_URL);
            case "WATSON_USER":
                return sharedProperties.getProperty(WATSON_USER);
            case "WATSON_PWD":
                return sharedProperties.getProperty(WATSON_PWD);
            case "REMOTE_NLP_URL":
                return sharedProperties.getProperty(REMOTE_NLP_URL);
            case "BACKEND_URL":
                return sharedProperties.getProperty(BACKEND_URL);
            case "BACKEND_APP_KEY":
                return sharedProperties.getProperty(BACKEND_APP_KEY);
            case "BACKEND_API_KEY_GUEST":
                return sharedProperties.getProperty(BACKEND_API_KEY_GUEST);
            default:
                return "";
        }
    }

    public QuizBackend getQuizBackend() {
        if(quizBackend==null){
            // Read from properties file
            Properties props = new java.util.Properties();
            Context context = getApplicationContext();
            try {
                AssetManager assetManager = context.getAssets();
                props.load(assetManager.open(PROPS_FILE));
                Log.i(CLASS_NAME, "Found configuration file: " + PROPS_FILE);
            } catch (FileNotFoundException e) {
                Log.e(CLASS_NAME, "The "+PROPS_FILE+" file was not found.", e);
            } catch (IOException e) {
                Log.e(CLASS_NAME, "The "+PROPS_FILE+" file could not be read properly.", e);
            }
            quizBackend = new QuizBackend(props.getProperty(BACKEND_URL), props.getProperty(BACKEND_APP_KEY));
        }
        return quizBackend;
    }

    public void saveApiKey(Context ctx){
        ctx.getSharedPreferences("userData",MODE_PRIVATE).edit().putString(getString(R.string.apiKey), getQuizBackend().apiKey).commit();
    }

    public String loadApiKey(Context ctx){
        return ctx.getSharedPreferences("userData",MODE_PRIVATE).getString(getString(R.string.apiKey), "");
    }

    public void saveUserName(Context ctx, String userName){
        ctx.getSharedPreferences("userName",MODE_PRIVATE).edit().putString(getString(R.string.userName), userName).commit();
    }

    public String loadUserName(Context ctx){
        return ctx.getSharedPreferences("userName",MODE_PRIVATE).getString(getString(R.string.userName), "");
    }

    public void logout(Context ctx){
        ctx.getSharedPreferences("userData", MODE_PRIVATE).edit().putString(getString(R.string.apiKey), "").commit();
        quizBackend.setApiKey("");
        Intent intent = new Intent(ctx, LoginRegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
