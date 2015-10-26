package models;

import java.util.List;

import backend.QuizBackend;
import controllers.QuizBackendService;

/**
 * Created by Admin on 07.10.2015.
 */
public class Highscore {

    public List<backend.types.QuizUser> highscore;


    public void loadHighscore(String apiKey) {

        QuizBackend quizBackend = QuizBackendService.getInstance();

        highscore = quizBackend.getUserHighscores(apiKey);
        if (highscore.size() > 10) highscore = highscore.subList(0, 10);

    }

    public static int getScore(String apiKey) {
        QuizBackend quizBackend = QuizBackendService.getInstance();

        return quizBackend.getUserScore(apiKey);
    }
}
