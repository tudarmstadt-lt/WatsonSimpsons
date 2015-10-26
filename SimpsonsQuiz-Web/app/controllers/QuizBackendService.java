package controllers;


import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import backend.QuizBackend;


public class QuizBackendService {

    private static QuizBackend backend;

    protected QuizBackendService() {
        //init backend
        Config conf = ConfigFactory.load();
        String backendUrl = conf.getString("quizbackend.url");
        String appKey = conf.getString("quizbackend.appKey");

        backend = new QuizBackend(backendUrl, appKey);
    }

    public static QuizBackend getInstance() {
        if (backend == null) {
            new QuizBackendService();
        }
        return backend;
    }

}
