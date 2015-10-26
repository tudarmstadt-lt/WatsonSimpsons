<?php
/**
 * QuizBackend-API (Routes)
 * User: dath
 * Date: 12.10.2015
 * Time: 18:50
 */

require_once '../include/DbHandler.php';
require_once '../include/PassHash.php';

// User id from db - Global Variable
$user_id = null;

require '../vendor/autoload.php';

$app = new Slim\Slim();

require_once '../include/ApiHelpers.php';

$app->get('/', function () {
    echo "Simpsons Quiz Backend Api";
});

/******** User ********/

/**
 * User Registration
 * method POST
 * params username, password
 * url /register
 */
$app->post('/register', function () use ($app) {
    // check for required params
    verifyRequiredParams(array('username', 'password'));

    $response = array();

    // reading post params
    $name = $app->request->post('username');
    $password = $app->request->post('password');

    $db = new DbHandler();
    $res = $db->createUser($name, $password);

    if ($res == USER_CREATED_SUCCESSFULLY) {
        $response["error"] = false;
        $response["message"] = "You are successfully registered.";
        echoResponse(201, $response);
    } else {
        if ($res == USER_CREATE_FAILED) {
            $response["error"] = true;
            $response["message"] = "Oops! An error occurred while registering.";
            echoResponse(200, $response);
        } else {
            if ($res == USER_ALREADY_EXISTED) {
                $response["error"] = true;
                $response["message"] = "Sorry, this username already existed.";
                echoResponse(200, $response);
            }
        }
    }
});

/**
 * User Login
 * method POST
 * params username, password
 * url /login
 */
$app->post('/login', function () use ($app) {
    // check for required params
    verifyRequiredParams(array('username', 'password'));

    // reading post params
    $username = $app->request()->post('username');
    $password = $app->request()->post('password');
    $response = array();

    $db = new DbHandler();
    // check for correct username and password
    if ($db->checkLogin($username, $password)) {
        // get the user by username
        $user = $db->getUserByUsername($username);

        if ($user != null) {
            $response["error"] = false;
            $user_resp['userID'] = $user['id'];
            $user_resp['username'] = $user['username'];
            $user_resp['apiKey'] = $user['api_key'];
            $user_resp['role'] = $user['role'];
            $user_resp['score'] = $user['quiz_score'];
            $user_resp['createdAt'] = $user['created_at'];
            $response['user'] = $user_resp;
        } else {
            // unknown error occurred
            $response['error'] = true;
            $response['message'] = "An error occurred. Please try again";
        }
    } else {
        // user credentials are wrong
        $response['error'] = true;
        $response['message'] = 'Login failed. Incorrect credentials';
    }

    echoResponse(200, $response);
});

/**
 * Adding Middle Layer to authenticate every request
 * Checking if the request has valid api key in the 'Authorization' header
 */
function authenticate(\Slim\Route $route)
{
    $response = array();
    $app = \Slim\Slim::getInstance();
    // Getting request headers
    //$user = $app->request->headers->get('Php-Auth-User');
    //$pwd = $app->request->headers->get('Php-Auth-Pw');
    $auth_api = $app->request->headers->get('ApiKey');
    $auth_app = $app->request->headers->get('AppKey');

    // Verifying Authorization Header
    if (isset($auth_app) && isset($auth_api)) {
        $db = new DbHandler();

        // get the api key
        $api_key = $auth_api;
        // validating api key
        if ($auth_app != APP_KEY) {
            $response["error"] = true;
            $response["message"] = "Access Denied. Invalid App key";
            echoResponse(401, $response);
            $app->stop();
        } elseif (!$db->isValidApiKey($api_key)) {
            // api key is not present in users table
            $response["error"] = true;
            $response["message"] = "Access Denied. Invalid Api key";
            echoResponse(401, $response);
            $app->stop();
        } else {
            global $user_id;
            // get user primary key id
            $user = $db->getUserId($api_key);
            if ($user != null) {
                $user_id = $user["id"];
            }
        }
    } else {
        // app and/or api key is missing in header
        $response["error"] = true;
        $response["message"] = "App or Api key is missing";
        echoResponse(400, $response);
        $app->stop();
    }
}

/**
 * Function to restrict requests for specified user role
 */
$restrictedForRole = function ($role = 'user') {
    return function () use ($role) {
        global $user_id;
        $db = new DbHandler();

        if ($db->checkUserRole($user_id, $role) === false) {
            $app = \Slim\Slim::getInstance();
            $response["error"] = true;
            $response["message"] = "Forbidden action for this user role, requires role " . $role;
            echoResponse(403, $response);
            $app->stop();
        }
    };
};

/**
 * Update User data
 * method POST
 * params username, password
 * url /user
 */
$app->post('/user', 'authenticate', function () use ($app) {
    global $user_id;

    // check for required params
    verifyRequiredParams(array('username', 'password'));

    $response = array();

    // reading post params
    $name = $app->request->post('username');
    $password = $app->request->post('password');

    $db = new DbHandler();
    $res = $db->updateUser($user_id, $name, $password);

    if ($res == 1) {
        $response["error"] = false;
        $response["message"] = "User data successfully updated.";
        echoResponse(201, $response);
    } else {
        if ($res == 0) {
            $response["error"] = true;
            $response["message"] = "Oops! An error occurred while updating.";
            echoResponse(200, $response);
        } else {
            if ($res == -1) {
                $response["error"] = true;
                $response["message"] = "Sorry, this username already existed.";
                echoResponse(200, $response);
            } else {
                if ($res == -2) {
                    $response["error"] = false;
                    $response["message"] = "User data is already up to date.";
                    echoResponse(200, $response);
                }
            }
        }
    }
});

/**
 * Deleting current user.
 * method DELETE
 * url /user
 */
$app->delete('/user', 'authenticate', function () use ($app) {
    global $user_id;

    $db = new DbHandler();
    $response = array();
    $result = $db->deleteUser($user_id);
    if ($result) {
        // user deleted successfully
        $response["error"] = false;
        $response["message"] = "User deleted successfully";
    } else {
        // user failed to delete
        $response["error"] = true;
        $response["message"] = "User failed to delete. Please try again!";
    }
    echoResponse(200, $response);
});

/**
 * Fetching user score.
 * method GET
 * url /user/score
 */
$app->get('/user/score', 'authenticate', function () use ($app) {
    global $user_id;

    $response = array();
    $db = new DbHandler();

    $result = $db->getUserScore($user_id);

    if ($result != null) {
        $response["error"] = false;
        $user_resp["username"] = $result["username"];
        $user_resp["score"] = $result["quiz_score"];
        $response["user"] = $user_resp;
        echoResponse(200, $response);
    } else {
        $response["error"] = true;
        $response["message"] = "The requested resource doesn't exists!";
        echoResponse(404, $response);
    }

});

/**
 * Increment score of current user.
 * method POST
 * params points
 * url /user/score
 */
$app->post('/user/score', 'authenticate', function () use ($app) {
    global $user_id;

    // check for required params
    verifyRequiredParams(array('points'));

    $points = $app->request->post('points');

    $response = array();
    $db = new DbHandler();

    $result = $db->incrementUserScore($user_id, $points);

    if ($result) {
        $response["error"] = false;
        $response["message"] = "User Score successfully updated.";

    } else {
        $response["error"] = true;
        $response["message"] = "User Score failed to update! Please try again!";
    }
    echoResponse(200, $response);

});

/**
 * Reset score of current user.
 * method DELETE
 * url /user/score
 */
$app->delete('/user/score', 'authenticate', function () use ($app) {
    global $user_id;

    $response = array();
    $db = new DbHandler();

    $result = $db->resetUserScore($user_id);

    if ($result) {
        $response["error"] = false;
        $response["message"] = "User Score successfully resetted.";

    } else {
        $response["error"] = true;
        $response["message"] = "User Score failed to reset! Please try again!";
    }
    echoResponse(200, $response);

});

/**
 * Listing all user scores in decrementing order (highscores)
 * method GET
 * params limit
 * url /highscores
 */
$app->get('/highscores', 'authenticate', function () use ($app) {

    $response = array();
    $db = new DbHandler();

    // fetching all user highscores
    $result = $db->getUserHighscores();

    $response["error"] = false;
    $response["highscoresCount"] = count($result);
    $response["highscores"] = array();

    // looping through result and preparing highscores array
    foreach ($result as $user) {
        $tmp = array();
        $tmp["username"] = $user["username"];
        $tmp["score"] = $user["quiz_score"];
        array_push($response["highscores"], $tmp);
    }

    echoResponse(200, $response);

});

/**
 * Fetch User to given id
 * Restricted for role admin
 * method GET
 * url /user/:id
 */
$app->get('/user/:id', 'authenticate', $restrictedForRole('admin'), function ($user_id) use ($app) {

    $db = new DbHandler();
    $response = array();

    $user = $db->getUserById($user_id);

    if ($user != null) {
        $response["error"] = false;
        $user_resp['userID'] = $user['id'];
        $user_resp['username'] = $user['username'];
        $user_resp['role'] = $user['role'];
        $user_resp['score'] = $user['quiz_score'];
        $user_resp['createdAt'] = $user['created_at'];
        $response['user'] = $user_resp;
        echoResponse(200, $response);
    } else {
        // user not existent
        $response['error'] = true;
        $response['message'] = "User does not exist! Please check the given id (" . $user_id . ").";
        echoResponse(404, $response);
    }
});

/************ Quiz Questions (CRUD) *****************/

/**
 * Creating new quiz question in db
 * method POST
 * params question, correctAnswer, falseAnswer1, falseAnswer2, falseAnswer3, category
 * url /questions
 */
$app->post('/questions', 'authenticate', function () use ($app) {
    // check for required params
    verifyRequiredParams(array(
        'question',
        'correctAnswer',
        'falseAnswer1',
        'falseAnswer2',
        'falseAnswer3',
        'category'
    ));

    $response = array();
    $question = $app->request->post('question');
    $correct_answer = $app->request->post('correctAnswer');
    $false_answer1 = $app->request->post('falseAnswer1');
    $false_answer2 = $app->request->post('falseAnswer2');
    $false_answer3 = $app->request->post('falseAnswer3');
    $category = $app->request->post('category');

    global $user_id;
    $db = new DbHandler();

    // creating new question
    $question_id = $db->createQuizQuestion($question, $correct_answer, $false_answer1, $false_answer2, $false_answer3,
        $category, $user_id);

    if ($question_id != null) {
        $response["error"] = false;
        $response["message"] = "QuizQuestion created successfully";
        $response["question_id"] = $question_id;
    } else {
        $response["error"] = true;
        $response["message"] = "Failed to create QuizQuestion. Please try again";
    }
    echoResponse(201, $response);
});

/**
 * Listing all questions
 * method GET
 * url /questions
 */
$app->get('/questions', 'authenticate', function () use ($app) {

    $response = array();
    $db = new DbHandler();

    $filter_unapproved = $app->request()->get('filter_unapproved');
    $category = $app->request()->get('category');
    $difficulty = $app->request()->get('difficulty');

    if (!isset($filter_unapproved) || $filter_unapproved != "true") {
        $filter_unapproved = false;
    }

    if (!isset($category) || empty($category)) {
        $category = '*';
    }

    if (!isset($difficulty) || empty($difficulty)) {
        $difficulty = 'all';
    }

    // fetching all quiz questions
    $result = $db->getQuizQuestions($difficulty, $filter_unapproved, $category);

    $response["error"] = false;
    $response["questionsCount"] = count($result);
    if ($filter_unapproved == true) {
        $response["questionsFiltered"] = true;
    }
    if ($category != '*') {
        $response["questionsCategory"] = $category;
    }
    if ($difficulty != 'all') {
        $response["questionsDifficulty"] = $difficulty;
    }
    $response["questions"] = array();

    // looping through result and preparing question array
    foreach ($result as $question) {
        $tmp = array();
        $tmp["id"] = $question["id"];
        $tmp["question"] = $question["question"];
        $tmp["correctAnswer"] = $question["correct_answer"];
        $tmp["falseAnswer1"] = $question["false_answer1"];
        $tmp["falseAnswer2"] = $question["false_answer2"];
        $tmp["falseAnswer3"] = $question["false_answer3"];
        $tmp["category"] = $question["category"];
        $tmp["status"] = $question["status"];
        $tmp["createdAt"] = $question["created_at"];
        $tmp["modifiedAt"] = $question["modified_at"];
        array_push($response["questions"], $tmp);
    }

    echoResponse(200, $response);
});

/**
 * Exporting all questions with review information
 * Restricted for role admin
 * method GET
 * url /questions/export
 */
$app->get('/questions/export', 'authenticate', $restrictedForRole('admin'), function () use ($app) {

    $response = array();
    $db = new DbHandler();

    // fetching all quiz questions
    $result = $db->exportQuestions();

    $response["error"] = false;
    $response['questionsCount'] = count($result);
    $response["questions"] = array();

    // looping through result and preparing question array
    foreach ($result as $question) {
        $tmp = array();
        $tmp["id"] = $question["id"];
        $tmp["question"] = $question["question"];
        $tmp["correctAnswer"] = $question["correct_answer"];
        $tmp["falseAnswer1"] = $question["false_answer1"];
        $tmp["falseAnswer2"] = $question["false_answer2"];
        $tmp["falseAnswer3"] = $question["false_answer3"];
        $tmp["category"] = $question["category"];
        $tmp["status"] = $question["status"];
        $tmp["createdAt"] = $question["created_at"];
        $tmp["modifiedAt"] = $question["modified_at"];
        $tmp["reviewNum"] = $question["review_num"];
        $tmp["difficultyAvg"] = $question["difficulty_avg"];
        array_push($response["questions"], $tmp);
    }

    echoResponse(200, $response);
});

/**
 * Listing all questions questions unseen by user (neither answered nor modified)
 * method GET
 * url /questions/user
 */
$app->get('/questions/user', 'authenticate', function () use ($app) {
    global $user_id;

    $response = array();
    $db = new DbHandler();

    $filter_unapproved = $app->request()->get('filter_unapproved');
    $category = $app->request()->get('category');
    $difficulty = $app->request()->get('difficulty');

    if (!isset($filter_unapproved) || $filter_unapproved != "true") {
        $filter_unapproved = false;
    }

    if (!isset($category) || empty($category)) {
        $category = '*';
    }

    if (!isset($difficulty) || empty($difficulty)) {
        $difficulty = 'all';
    }

    // fetching all user questions
    $result = $db->getQuizQuestionsForUser($user_id, $difficulty, $category, $filter_unapproved);

    $response["error"] = false;
    $response['questionsCount'] = count($result);
    if ($filter_unapproved == true) {
        $response["questionsFiltered"] = true;
    }
    if ($category != '*') {
        $response["questionsCategory"] = $category;
    }
    if ($difficulty != 'all') {
        $response["questionsDifficulty"] = $difficulty;
    }
    $response["questions"] = array();

    // looping through result and preparing questions array
    foreach ($result as $question) {
        $tmp = array();
        $tmp["id"] = $question["id"];
        $tmp["question"] = $question["question"];
        $tmp["correctAnswer"] = $question["correct_answer"];
        $tmp["falseAnswer1"] = $question["false_answer1"];
        $tmp["falseAnswer2"] = $question["false_answer2"];
        $tmp["falseAnswer3"] = $question["false_answer3"];
        $tmp["category"] = $question["category"];
        $tmp["status"] = $question["status"];
        $tmp["createdAt"] = $question["created_at"];
        $tmp["modifiedAt"] = $question["modified_at"];
        array_push($response["questions"], $tmp);
    }

    echoResponse(200, $response);
});

/**
 * Listing all quiz questions not reviewed by user
 * method GET
 * url /questions/review
 */
$app->get('/questions/review', 'authenticate', function () use ($app) {
    global $user_id;

    $response = array();
    $db = new DbHandler();

    // fetching all review questions
    $result = $db->getQuizQuestionsToReview($user_id);

    $response["error"] = false;
    $response['questionsCount'] = count($result);
    $response["questions"] = array();

    // looping through result and preparing questions array
    foreach ($result as $question) {
        $tmp = array();
        $tmp["id"] = $question["id"];
        $tmp["question"] = $question["question"];
        $tmp["correctAnswer"] = $question["correct_answer"];
        $tmp["falseAnswer1"] = $question["false_answer1"];
        $tmp["falseAnswer2"] = $question["false_answer2"];
        $tmp["falseAnswer3"] = $question["false_answer3"];
        $tmp["category"] = $question["category"];
        $tmp["status"] = $question["status"];
        $tmp["createdAt"] = $question["created_at"];
        $tmp["modifiedAt"] = $question["modified_at"];
        $tmp["reviewNum"] = $question["review_num"];
        array_push($response["questions"], $tmp);
    }

    echoResponse(200, $response);
});

/**
 * Listing single quiz question
 * method GET
 * url /questions/:id
 * Will return 404 if the question does not exists
 */
$app->get('/questions/:id', 'authenticate', function ($question_id) {
    $response = array();
    $db = new DbHandler();

    // fetch question
    $result = $db->getQuizQuestion($question_id);

    if ($result != null) {
        $response["error"] = false;

        $question = array();
        $question["id"] = $result["id"];
        $question["question"] = $result["question"];
        $question["correctAnswer"] = $result["correct_answer"];
        $question["falseAnswer1"] = $result["false_answer1"];
        $question["falseAnswer2"] = $result["false_answer2"];
        $question["falseAnswer3"] = $result["false_answer3"];
        $question["category"] = $result["category"];
        $question["status"] = $result["status"];
        $question["createdAt"] = $result["created_at"];
        $question["modifiedAt"] = $result["modified_at"];

        $response["question"] = $question;
        echoResponse(200, $response);
    } else {
        $response["error"] = true;
        $response["message"] = "The requested resource doesn't exists!";
        echoResponse(404, $response);
    }
});

/**
 * Updating existing question
 * Restricted for role editor
 * method POST
 * params question, correctAnswer, falseAnswer1, falseAnswer2, falseAnswer3, category
 * url /question/edit/:id
 */
$app->post('/questions/edit/:id', 'authenticate', $restrictedForRole('editor'), function ($question_id) use ($app) {
    // check for required params
    verifyRequiredParams(array(
        'question',
        'correctAnswer',
        'falseAnswer1',
        'falseAnswer2',
        'falseAnswer3',
        'category'
    ));

    global $user_id;

    $question = $app->request->post('question');
    $correct_answer = $app->request->post('correctAnswer');
    $false_answer1 = $app->request->post('falseAnswer1');
    $false_answer2 = $app->request->post('falseAnswer2');
    $false_answer3 = $app->request->post('falseAnswer3');
    $category = $app->request->post('category');
    //$status = $app->request->put('status');

    $db = new DbHandler();
    $response = array();

    // updating question
    $result = $db->updateQuizQuestion($question_id, $question, $correct_answer, $false_answer1, $false_answer2,
        $false_answer3, $category, $user_id);
    if ($result) {
        // question updated successfully
        $response["error"] = false;
        $response["message"] = "QuizQuestion updated successfully";
    } else {
        // question failed to update
        $response["error"] = true;
        $response["message"] = "QuizQuestion failed to update. Please try again!";
    }
    echoResponse(200, $response);
});

/**
 * Deleting question.
 * method DELETE
 * url /questions/:id
 */
$app->delete('/questions/:id', 'authenticate', $restrictedForRole('admin'), function ($question_id) use ($app) {

    $db = new DbHandler();
    $response = array();
    $result = $db->deleteQuizQuestion($question_id);
    if ($result) {
        // question deleted successfully
        $response["error"] = false;
        $response["message"] = "QuizQuestion deleted successfully";
    } else {
        // question failed to delete
        $response["error"] = true;
        $response["message"] = "QuizQuestion failed to delete. Please try again!";
    }
    echoResponse(200, $response);
});

/**
 * Retrieve user status for given question
 * method GET
 * url /questions/user-status/:id
 */
$app->get('/questions/user-status/:id', 'authenticate', function ($question_id) use ($app) {

    global $user_id;

    $db = new DbHandler();
    $response = array();

    // getting relation status
    $result = $db->getUserQuestionRelation($user_id, $question_id);
    if ($result != null) {
        $question_user_status = array();
        $response["error"] = false;
        $question_user_status["questionID"] = $result["question_id"];
        $question_user_status["status"] = $result["status"];
        $question_user_status["timestamp"] = $result["timestamp"];
        $response["questionUserStatus"] = $question_user_status;
    } else {
        $response["error"] = true;
        $response["message"] = "Setting user status to quiz question failed. Please try again!";
    }
    echoResponse(200, $response);
});

/**
 * Set user status for given question.
 * method POST
 * params status
 * url /questions/user-status/:id
 */
$app->post('/questions/user-status/:id', 'authenticate', function ($question_id) use ($app) {
    // check for required params
    verifyRequiredParams(array('status'));

    global $user_id;

    $status = $app->request->post('status');

    $db = new DbHandler();
    $response = array();

    // updating relation
    $result = $db->setUserQuestionRelation($user_id, $question_id, $status);
    if ($result) {
        // question updated successfully
        $response["error"] = false;
        $response["message"] = "Set user status to quiz question successfully";
    } else {
        // question failed to update
        $response["error"] = true;
        $response["message"] = "Setting user status to quiz question failed. Please try again!";
    }
    echoResponse(200, $response);
});

/**
 * Manually approve a question.
 * Restricted for role approver
 * method POST
 * params status
 * url /questions/approve/:id
 */
$app->post('/questions/approve/:id', 'authenticate', $restrictedForRole('approver'),
    function ($question_id) use ($app) {
        // check for required params
        verifyRequiredParams(array('status'));

        global $user_id;

        $status = $app->request->post('status');

        $db = new DbHandler();
        $response = array();

        // approve question
        $result = $db->approveQuizQuestion($question_id, $user_id, $status);
        if ($result) {
            // question approved successfully
            $response["error"] = false;
            $response["message"] = "QuizQuestion approved successfully.";

            // Reward question creator if necessary
            if (!$db->isQuestionCreatorRewarded($question_id) && $db->isAcceptedQuestion($question_id)) {
                if ($db->rewardQuestionCreator($question_id)) {
                    $response["message"] .= " Question creator scored " . SCORE_FOR_ACCEPTED_QUESTION . " points for accepted question.";
                    $response["reward"]["creator"] = SCORE_FOR_ACCEPTED_QUESTION;
                } else {
                    $response["error"] = true;
                    $response["message"] .= " But reward for question creator could not be saved!";
                    $response["reward"]["creator"] = 0;
                }
            }
        } else {
            // question failed to approve
            $response["error"] = true;
            $response["message"] = "QuizQuestion failed to approve. Please try again!";
        }
        echoResponse(200, $response);
    });

/**
 * Set single quiz question review of user
 * method POST
 * params review
 * url - /question/review/:id
 */
$app->post('/questions/review/:id', 'authenticate', function ($question_id) use ($app) {
    // check for required params
    verifyRequiredParams(array('answerCorrect', 'answerDistractors', 'questionFormulation', 'questionDifficulty'));

    global $user_id;

    $correct_answer = $app->request->post('answerCorrect');
    $answer_distractors = $app->request->post('answerDistractors');
    $question_formulation = $app->request->post('questionFormulation');
    $question_difficulty = $app->request->post('questionDifficulty');

    $db = new DbHandler();
    $response = array();
    $new_review = false;

    // create review if not exists otherwise update
    if (is_null($db->getQuestionReview($question_id, $user_id))) {
        $result = $db->createQuestionReview($question_id, $user_id, $correct_answer, $answer_distractors,
            $question_formulation, $question_difficulty);
        $new_review = true;
    } else {
        $result = $db->updateQuestionReview($question_id, $user_id, $correct_answer, $answer_distractors,
            $question_formulation, $question_difficulty);
    }

    if ($result) {
        // question updated successfully
        $response["error"] = false;
        $response["message"] = "Set Question Review successfully.";

        if ($new_review) {
            if ($db->incrementUserScore($user_id, SCORE_FOR_REVIEW)) {
                $response["message"] .= " User scored " . SCORE_FOR_REVIEW . " point for reviewing.";
                $response["reward"]["review"] = SCORE_FOR_REVIEW;
            } else {
                $response["error"] = true;
                $response["message"] .= " But reward for review could not be saved!";
                $response["reward"]["review"] = 0;
            }

            if (!$db->isQuestionCreatorRewarded($question_id) && $db->isAcceptedQuestion($question_id)) {
                if ($db->rewardQuestionCreator($question_id)) {
                    $response["message"] .= " Question creator scored " . SCORE_FOR_ACCEPTED_QUESTION . " points for accepted question.";
                    $response["reward"]["creator"] = SCORE_FOR_ACCEPTED_QUESTION;
                } else {
                    $response["error"] = true;
                    $response["message"] .= " But reward for question creator could not be saved!";
                    $response["reward"]["creator"] = 0;
                }
            }
        }
    } else {
        // question failed to update
        $response["error"] = true;
        $response["message"] = "Question Review failed to set. Please try again!";
    }
    echoResponse(200, $response);
});

/**
 * Get user review of single quiz question
 * method GET
 * url /questions/review/:id
 * Will return 404 if the question was not reviewed by the user
 */
$app->get('/questions/review/:id', 'authenticate', function ($question_id) {
    global $user_id;

    $response = array();
    $db = new DbHandler();

    // fetch review
    $result = $db->getQuestionReview($question_id, $user_id);

    if ($result != null) {
        $response["error"] = false;

        $review = array();
        $review["questionID"] = $result["question_id"];
        $review["userID"] = $result["user_id"];
        $review["answerCorrect"] = $result["answer_correct"];
        $review["answerDistractors"] = $result["answer_distractors"];
        $review["questionFormulation"] = $result["question_formulation"];
        $review["questionDifficulty"] = $result["question_difficulty"];
        $review["timestamp"] = $result["timestamp"];

        $response["questionReview"] = $review;
        echoResponse(200, $response);
    } else {
        $response["error"] = true;
        $response["message"] = "The requested resource doesn't exists (review)!";
        echoResponse(404, $response);
    }
});

/**
 * Deleting single quiz question review for user.
 * method DELETE
 * url /questions/review/:id
 */
$app->delete('/questions/review/:id', 'authenticate', function ($question_id) use ($app) {
    global $user_id;

    $db = new DbHandler();
    $response = array();

    $result = $db->deleteQuestionReview($question_id, $user_id);
    if ($result) {
        // question deleted successfully
        $response["error"] = false;
        $response["message"] = "Question Review deleted successfully";
    } else {
        // question failed to delete
        $response["error"] = true;
        $response["message"] = "Question Review failed to delete. Please try again!";
    }
    echoResponse(200, $response);
});

/**
 * Get average review of a single quiz question
 * method GET
 * url /questions/avg-review/:id
 * Will return 404 if the question was not rated by the user
 */
$app->get('/questions/avg-review/:id', 'authenticate', function ($question_id) {

    $response = array();
    $db = new DbHandler();

    // fetch average review values
    $result = $db->getQuestionAvgReview($question_id);

    if ($result != null && $result["question_id"] != null && $result["review_num"] != null) {
        $response["error"] = false;

        $review = array();
        $review["questionID"] = $result["question_id"];
        $review["num"] = $result["review_num"];
        $review["avgAnswerCorrect"] = $result["correct_avg"];
        $review["avgAnswerDistractors"] = $result["distractors_avg"];
        $review["avgQuestionFormulation"] = $result["formulation_avg"];
        $review["avgQuestionDifficulty"] = $result["difficulty_avg"];

        $response["questionReview"] = $review;
        echoResponse(200, $response);
    } else {
        $response["error"] = true;
        $response["message"] = "The requested resource doesn't exists (avg-review)!";
        echoResponse(404, $response);
    }
});

$app->run();
