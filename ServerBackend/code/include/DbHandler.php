<?php

/**
 * DbHandler for QuizBackend-API
 * User: dath
 * Date: 12.10.2015
 * Time: 18:40
 */
class DbHandler
{

    private $conn;

    // question difficulty levels (min, max]
    public static $question_difficulties = array(
        'easy' => array(0, 3.5),
        'medium' => array(3.5, 6.5),
        'hard' => array(6.5, 9),
        'all' => array(0, 9)
    );

    function __construct()
    {
        require_once dirname(__FILE__) . '/DbConnect.php';
        // opening db connection
        $db = new DbConnect();
        $this->conn = $db->connect();
    }

    /* ------------- `users` table method ------------------ */

    /**
     * Creating new user
     * @param String $username User name
     * @param String $password User login password
     * @param String $role User role (optional, default: user)
     * @param String $apiKey Manual defined apiKey (will be generated if null)
     *
     * @return int response code
     */
    public function createUser($username, $password, $role = "user", $apiKey = null)
    {
        require_once 'PassHash.php';

        // First check if user already existed in db
        if (!$this->isUserExists($username)) {
            // Generating password hash
            $password_hash = PassHash::hash($password);

            // Generating API key if not given
            if(!is_null($apiKey))
                $api_key = $apiKey;
            else
                $api_key = $this->generateApiKey();


            // insert query
            $stmt = $this->conn->prepare("INSERT INTO users(username, password_hash, role, api_key, status) values(:username, :pwd_hash, :role, :apikey, 1)");
            $stmt->bindParam(":username", $username, PDO::PARAM_STR);
            $stmt->bindParam(":pwd_hash", $password_hash, PDO::PARAM_STR);
            $stmt->bindParam(":role", $role, PDO::PARAM_STR);
            $stmt->bindParam(":apikey", $api_key, PDO::PARAM_STR);

            $result = $stmt->execute();

            // Check for successful insertion
            if ($result) {
                // User successfully inserted
                return USER_CREATED_SUCCESSFULLY;
            } else {
                // Failed to create user
                return USER_CREATE_FAILED;
            }
        } else {
            // User with same username already existed in the db
            return USER_ALREADY_EXISTED;
        }

    }

    /**
     * Update a user
     * @param int $user_id User id
     * @param String $username User name
     * @param String $password User login password
     * @return int response code
     */
    public function updateUser($user_id, $username, $password)
    {
        require_once 'PassHash.php';

        $current_user = $this->getUserById($user_id);

        // First check if user existed in db and if username if not already taken by another user
        if (!empty($current_user) && ($username == $current_user['username'] || !$this->isUserExists($username))) {

            if ($username == $current_user['username'] && PassHash::check_password($current_user['password_hash'],
                    $password)
            ) {
                return -2;
            }

            // Generating password hash
            $password_hash = PassHash::hash($password);

            // update query
            $stmt = $this->conn->prepare("UPDATE users SET username = :username, password_hash = :pwd_hash WHERE id = :uid");
            $stmt->bindParam(":uid", $user_id, PDO::PARAM_INT);
            $stmt->bindParam(":username", $username, PDO::PARAM_STR);
            $stmt->bindParam(":pwd_hash", $password_hash, PDO::PARAM_STR);

            $result = $stmt->execute();

            // Check for successful update
            if ($result) {
                // User successfully updated
                return 1;
            } else {
                // Failed to create user
                return 0;
            }
        } else {
            // User with same username already existed in the db
            return -1;
        }

    }

    /**
     * Deleting a user
     * @param int $user_id id of user to delete
     */
    public function deleteUser($user_id)
    {
        $stmt = $this->conn->prepare("DELETE u FROM users u WHERE u.id = :uid");
        $stmt->bindParam(":uid", $user_id, PDO::PARAM_INT);

        return $stmt->execute();
    }

    /**
     * Checking user login
     * @param String $username User login name
     * @param String $password User login password
     * @return boolean User login status success/fail
     */
    public function checkLogin($username, $password)
    {

        // fetching user by username
        $stmt = $this->conn->prepare("SELECT password_hash FROM users WHERE username = ?");

        $stmt->bindParam(1, $username, PDO::PARAM_STR);

        if ($stmt->execute()) {
            // Found user with the username
            // Now verify the password

            $password_hash = $stmt->fetch(PDO::FETCH_COLUMN);

            if (PassHash::check_password($password_hash, $password)) {
                // User password is correct
                return true;
            } else {
                // user password is incorrect
                return false;
            }
        } else {

            // user not existed for the username
            return false;
        }
    }

    /**
     * Checking for duplicate user by user name
     * @param String $username User name to check in db
     * @return boolean
     */
    private function isUserExists($username)
    {
        $stmt = $this->conn->prepare("SELECT id from users WHERE username = ?");
        $stmt->bindParam(1, $username, PDO::PARAM_STR);
        $stmt->execute();

        return ($stmt->fetch() !== false);
    }

    /**
     * Fetching user by user name
     * @param String $username unique User name
     * @return Array user data
     */
    public function getUserByUsername($username)
    {
        $stmt = $this->conn->prepare("SELECT * FROM users WHERE username = ?");
        $stmt->bindParam(1, $username, PDO::PARAM_STR);
        if ($stmt->execute()) {
            $user = $stmt->fetch(PDO::FETCH_ASSOC);

            return $user;
        } else {
            return null;
        }
    }

    /**
     * Fetching user by id
     * @param int $user_id User id
     * @return Array user data
     */
    public function getUserById($user_id)
    {
        $stmt = $this->conn->prepare("SELECT * FROM users WHERE id = :id");
        $stmt->bindParam(':id', $user_id, PDO::PARAM_INT);
        if ($stmt->execute()) {
            $user = $stmt->fetch(PDO::FETCH_ASSOC);

            return $user;
        } else {
            return null;
        }
    }

    /**
     * Fetching user api key
     * @param String $user_id user id primary key in user table
     * @return String api key
     */
    public function getApiKeyById($user_id)
    {
        $stmt = $this->conn->prepare("SELECT api_key FROM users WHERE id = ?");
        $stmt->bindParam(1, $user_id, PDO::PARAM_INT);
        if ($stmt->execute()) {
            $api_key = $stmt->fetchColumn(0);

            return $api_key;
        } else {
            return null;
        }
    }

    /**
     * Fetching user id by api key
     * @param String $api_key user api key
     * @return int $user_id id of the user
     */
    public function getUserId($api_key)
    {
        $stmt = $this->conn->prepare("SELECT id FROM users WHERE api_key = ?");
        $stmt->bindParam(1, $api_key, PDO::PARAM_STR);
        if ($stmt->execute()) {
            $user_id = $stmt->fetch(PDO::FETCH_ASSOC);

            return $user_id;
        } else {
            return null;
        }
    }

    /**
     * Validating user api key
     * If the api key is there in db, it is a valid key
     * @param String $api_key user api key
     * @return boolean
     */
    public function isValidApiKey($api_key)
    {
        $stmt = $this->conn->prepare("SELECT id from users WHERE api_key = ?");
        $stmt->bindParam(1, $api_key, PDO::PARAM_STR);

        return ($stmt->execute() && $stmt->rowCount() > 0);
    }

    /**
     * Generating random Unique SHA1 String for user Api key
     */
    private function generateApiKey()
    {
        return sha1(uniqid(rand(), true));
    }

    /**
     * Checks whether a user has the rights of a specific user role.
     * @param int $user_id id of the user
     * @param String $role the role to check against
     * @return bool true, if user has given user role or higher
     */
    public function checkUserRole($user_id, $role)
    {
        $user = $this->getUserById($user_id);
        if ($user == null) {
            return false;
        }
        $user_role = $user["role"];

        switch ($role) {
            case 'admin':
                return ($user_role == 'admin');
            case 'editor':
                return (in_array($user_role, array('editor', 'admin')));
            case 'approver':
                return (in_array($user_role, array('approver', 'admin')));
            case 'user':
                return (in_array($user_role, array('user', 'approver', 'editor', 'admin')));
            default:
                return false;

        }
    }

    /**
     * Returns the participation score
     * @param int $user_id id of the user
     * @return int the participation score of the user
     */
    public function getUserScore($user_id)
    {
        $stmt = $this->conn->prepare("SELECT username, quiz_score FROM users WHERE id = ?");
        $stmt->bindParam(1, $user_id, PDO::PARAM_INT);
        if ($stmt->execute()) {
            $score = $stmt->fetch(PDO::FETCH_ASSOC);

            return $score;
        } else {
            return -1;
        }
    }

    /**
     * Increments the participation user score
     * @param int $user_id id of the user
     * @param int $points amount of points to increment
     * @return bool true, if incrementation was successfully
     */
    public function incrementUserScore($user_id, $points)
    {
        $stmt = $this->conn->prepare("UPDATE users SET quiz_score = quiz_score + :points WHERE id = :uid");
        $stmt->bindParam('points', $points, PDO::PARAM_INT);
        $stmt->bindParam('uid', $user_id, PDO::PARAM_INT);
        if ($stmt->execute() && $stmt->rowCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Resets the participation user score to zero.
     * @param int $user_id id of the user
     * @return bool true, if reset was successfully
     */
    public function resetUserScore($user_id)
    {
        $stmt = $this->conn->prepare("UPDATE users SET quiz_score = 0 WHERE id = ?");
        $stmt->bindParam(1, $user_id, PDO::PARAM_INT);
        if ($stmt->execute() && $stmt->rowCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Fetching a list with usernames and highscores.
     * @return array username and score pairs in descending score order
     */
    public function getUserHighscores()
    {
        $stmt = $this->conn->query("SELECT username, quiz_score FROM users ORDER BY quiz_score DESC");
        if ($stmt) {
            $score = $stmt->fetchAll(PDO::FETCH_ASSOC);

            return $score;
        } else {
            return array();
        }
    }

    /* ------------- `quizquestion` table method ------------------ */

    /**
     * Creating new question
     * @param String $question question text
     * @param String $correct_answer the correct answer to the question
     * @param String $false_answer1 first false answer to the question
     * @param String $false_answer2 second false answer to the question
     * @param String $false_answer3 third false answer to the question
     * @param String $category question category
     * @param int $user_id id of the user
     * @return mixed id
     */
    public function createQuizQuestion(
        $question,
        $correct_answer,
        $false_answer1,
        $false_answer2,
        $false_answer3,
        $category,
        $user_id
    ) {
        $stmt = $this->conn->prepare("INSERT INTO quizquestions(question, correct_answer, false_answer1, false_answer2, false_answer3, category, created_at) VALUES(?, ?, ?, ?, ?, ?, now())");
        $result = $stmt->execute(array(
            $question,
            $correct_answer,
            $false_answer1,
            $false_answer2,
            $false_answer3,
            $category
        ));

        if ($result) {
            // question row created
            // now assign the question to user
            $new_question_id = $this->conn->lastInsertId();
            $res = $this->setUserQuestionRelation($user_id, $new_question_id, "created");
            if ($res) {
                // question created successfully
                return $new_question_id;
            } else {
                // question failed to create
                return null;
            }
        } else {
            // question failed to create
            return null;
        }
    }

    /**
     * Fetching single quiz question
     * @param String $question_id id of the question
     * @return Array the question data
     */
    public function getQuizQuestion($question_id)
    {
        $stmt = $this->conn->prepare("SELECT * from quizquestions WHERE id = :id");
        $stmt->bindParam(":id", $question_id, PDO::PARAM_INT);
        if ($stmt->execute()) {
            return $stmt->fetch(PDO::FETCH_ASSOC);
        } else {
            return null;
        }
    }

    /**
     * Fetching all quiz questions
     * @param String $difficulty Difficulty level of questions (easy, medium, hard)
     * @param boolean $filter_unapproved set to true if unapproved questions should be filtered out
     * @param String $category questions with given category
     * @return array selected quiz questions
     */
    public function getQuizQuestions($difficulty = 'all', $filter_unapproved = false, $category = '*')
    {
        $diff_min = DbHandler::$question_difficulties[$difficulty][0];
        $diff_max = DbHandler::$question_difficulties[$difficulty][1];

        if ($filter_unapproved == true) {
            if (AUTO_APPROVING === true) {
                $stmt = $this->conn->prepare("SELECT * FROM quizquestions qq WHERE qq.category LIKE :cat AND 
												 (qq.status = 1 AND qq.status = 1 AND
                                                  qq.id IN (SELECT qr.qid FROM (SELECT question_id as qid, AVG(question_difficulty) as difficulty_avg FROM
                                                  question_review GROUP BY question_id) AS qr WHERE :diff_min < qr.difficulty_avg AND qr.difficulty_avg <= :diff_max))
											  OR  qq.id IN (SELECT qr.qid FROM (SELECT question_id as qid, COUNT(*) as review_num,
                                                  AVG(answer_correct) as correct_avg, AVG(answer_distractors) as distractors_avg,
                                                  AVG(question_formulation) as formulation_avg, AVG(question_difficulty) as difficulty_avg FROM
                                                  question_review GROUP BY question_id) AS qr WHERE qr.review_num >= :num_threshold AND
                                                  qr.correct_avg >= :correct_threshold AND qr.distractors_avg >= :distractors_threshold AND
                                                  qr.formulation_avg >= :formulation_threshold AND :diff_min < qr.difficulty_avg AND qr.difficulty_avg <= :diff_max)");

                $stmt->bindValue(':num_threshold', AUTO_APPROVING_NUM_THRESHOLD, PDO::PARAM_INT);
                $stmt->bindValue(':correct_threshold', AUTO_APPROVING_CORRECT_THRESHOLD, PDO::PARAM_INT);
                $stmt->bindValue(':distractors_threshold', AUTO_APPROVING_DISTRACTORS_THRESHOLD, PDO::PARAM_INT);
                $stmt->bindValue(':formulation_threshold', AUTO_APPROVING_FORMULATION_THRESHOLD, PDO::PARAM_INT);

            } else {
                $stmt = $this->conn->prepare("SELECT * FROM quizquestions qq WHERE qq.category LIKE :cat AND qq.status = 1 AND
                                                  qq.id IN (SELECT qr.qid FROM (SELECT question_id as qid, AVG(question_difficulty) as difficulty_avg FROM
                                                  question_review GROUP BY question_id) AS qr WHERE :diff_min < qr.difficulty_avg AND qr.difficulty_avg <= :diff_max)");
            }
			$stmt->bindValue(':diff_min', $diff_min, PDO::PARAM_INT);
        	$stmt->bindValue(':diff_max', $diff_max, PDO::PARAM_INT);
        } else {
			if($difficulty == 'all') {
				$stmt = $this->conn->prepare("SELECT * FROM quizquestions qq WHERE qq.category LIKE :cat");
			} else {
            	$stmt = $this->conn->prepare("SELECT * FROM quizquestions qq WHERE qq.category LIKE :cat AND
                                          		qq.id IN (SELECT qr.qid FROM (SELECT question_id as qid, AVG(question_difficulty) as difficulty_avg FROM
                                          		question_review GROUP BY question_id) AS qr WHERE :diff_min < qr.difficulty_avg AND qr.difficulty_avg <= :diff_max)");
				$stmt->bindValue(':diff_min', $diff_min, PDO::PARAM_INT);
        		$stmt->bindValue(':diff_max', $diff_max, PDO::PARAM_INT);
			}
        }
        if ($category == '*') {
            $stmt->bindValue(':cat', '%', PDO::PARAM_STR);
        } else {
            $stmt->bindValue(':cat', '%' . $category . '%', PDO::PARAM_STR);
        }
        if ($stmt->execute()) {
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
        } else {
            return array();
        }
    }

    /**
     * Fetching all quiz questions unseen by user (neither answered nor modified)
     * @param int $user_id id of the user
     * @param String $difficulty Difficulty level of questions (easy, medium, hard)
     * @param String $category filter questions by given category
     * @param boolean $filter_unapproved set to true if unapproved questions should be filtered out
     * @return array matching quiz questions
     */
    public function getQuizQuestionsForUser($user_id, $difficulty = 'all', $category = '*', $filter_unapproved = false)
    {
        $diff_min = DbHandler::$question_difficulties[$difficulty][0];
        $diff_max = DbHandler::$question_difficulties[$difficulty][1];

        if ($filter_unapproved == true) {
            if (AUTO_APPROVING === true) {
                $stmt = $this->conn->prepare("SELECT * FROM quizquestions qq WHERE qq.category LIKE :cat AND
                                              qq.id NOT IN (SELECT uq.question_id FROM user_questions uq WHERE uq.user_id = :uid) AND 
											 (qq.status = 1 AND
                                              qq.id IN (SELECT qr.qid FROM (SELECT question_id as qid, AVG(question_difficulty) as difficulty_avg FROM
                                              question_review GROUP BY question_id) AS qr WHERE :diff_min < qr.difficulty_avg AND qr.difficulty_avg <= :diff_max)
											 ) OR
                                              qq.id IN (SELECT qr.qid FROM (SELECT question_id as qid, COUNT(*) as review_num,
                                              AVG(answer_correct) as correct_avg, AVG(answer_distractors) as distractors_avg,
                                              AVG(question_formulation) as formulation_avg, AVG(question_difficulty) as difficulty_avg FROM
                                              question_review GROUP BY question_id) AS qr WHERE qr.review_num >= :num_threshold AND
                                              qr.correct_avg >= :correct_threshold AND qr.distractors_avg >= :distractors_threshold AND
                                              qr.formulation_avg >= :formulation_threshold AND :diff_min < qr.difficulty_avg AND qr.difficulty_avg <= :diff_max)");

                $stmt->bindValue(':num_threshold', AUTO_APPROVING_NUM_THRESHOLD, PDO::PARAM_INT);
                $stmt->bindValue(':correct_threshold', AUTO_APPROVING_CORRECT_THRESHOLD, PDO::PARAM_INT);
                $stmt->bindValue(':distractors_threshold', AUTO_APPROVING_DISTRACTORS_THRESHOLD, PDO::PARAM_INT);
                $stmt->bindValue(':formulation_threshold', AUTO_APPROVING_FORMULATION_THRESHOLD, PDO::PARAM_INT);

            } else {
                $stmt = $this->conn->prepare("SELECT * FROM quizquestions qq WHERE qq.category LIKE :cat AND
                                              qq.id NOT IN (SELECT uq.question_id FROM user_questions uq WHERE uq.user_id = :uid) AND qq.status = 1 AND
                                              qq.id IN (SELECT qr.qid FROM (SELECT question_id as qid, AVG(question_difficulty) as difficulty_avg FROM
                                              question_review GROUP BY question_id) AS qr WHERE :diff_min < qr.difficulty_avg AND qr.difficulty_avg <= :diff_max)");
            }
			$stmt->bindValue(':diff_min', $diff_min, PDO::PARAM_INT);
		  	$stmt->bindValue(':diff_max', $diff_max, PDO::PARAM_INT);
        } else {
			if($difficulty == 'all') {
				$stmt = $this->conn->prepare("SELECT * FROM quizquestions qq WHERE qq.category LIKE :cat");
			} else {
            	$stmt = $this->conn->prepare("SELECT * FROM quizquestions qq WHERE qq.category LIKE :cat AND
                                          	qq.id NOT IN (SELECT uq.question_id FROM user_questions uq WHERE uq.user_id = :uid) AND
                                          	qq.id IN (SELECT qr.qid FROM (SELECT question_id as qid, AVG(question_difficulty) as difficulty_avg FROM
                                          	question_review GROUP BY question_id) AS qr WHERE :diff_min < qr.difficulty_avg AND qr.difficulty_avg <= :diff_max)");
		        $stmt->bindValue(':diff_min', $diff_min, PDO::PARAM_INT);
		        $stmt->bindValue(':diff_max', $diff_max, PDO::PARAM_INT);
			}
        }

        $stmt->bindParam(':uid', $user_id, PDO::PARAM_INT);

        if ($category == '*') {
            $stmt->bindValue(':cat', '%', PDO::PARAM_STR);
        } else {
            $stmt->bindValue(':cat', '%' . $category . '%', PDO::PARAM_STR);
        }

        if ($stmt->execute()) {
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
        } else {
            return array();
        }

    }

    /**
     * Fetching all quiz questions not reviewed by user
     * @param int $user_id id of the user
     * @return array not reviewed matching quiz questions
     */
    public function getQuizQuestionsToReview($user_id)
    {
        $stmt = $this->conn->prepare("SELECT qq.*, qr.review_num FROM quizquestions qq LEFT JOIN
                                      (SELECT question_id as qid, COUNT(*) as review_num FROM question_review GROUP BY question_id) AS qr ON qq.id = qr.qid
                                      WHERE qq.id NOT IN (SELECT uq.question_id FROM user_questions uq WHERE uq.user_id = :uid AND uq.status LIKE '%created%') AND
                                      qq.id NOT IN (SELECT qrev.question_id FROM question_review qrev WHERE qrev.user_id = :uid) ORDER BY qr.review_num");

        $stmt->bindParam(':uid', $user_id, PDO::PARAM_INT);

        if ($stmt->execute()) {
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
        } else {
            return array();
        }

    }

    /**
     * Updating a quiz question
     * @param int $question_id id of the question to update
     * @param String $question question text
     * @param String $correct_answer the correct answer to the question
     * @param String $false_answer1 first false answer to the question
     * @param String $false_answer2 second false answer to the question
     * @param String $false_answer3 third false answer to the question
     * @param String $category question category
     * @param int $user_id id of the user
     * @return bool true, if updated successfully
     */
    public function updateQuizQuestion(
        $question_id,
        $question,
        $correct_answer,
        $false_answer1,
        $false_answer2,
        $false_answer3,
        $category,
        $user_id
    ) {
        $status = 2;
        $stmt = $this->conn->prepare("UPDATE quizquestions SET question = :question,
                                        correct_answer = :correct, false_answer1 = :false1, false_answer2 = :false2, false_answer3 = :false3,
                                        category = :category, status = :status WHERE id = :id");

        $result = $stmt->execute(array(
            'question' => $question,
            'correct' => $correct_answer,
            'false1' => $false_answer1,
            'false2' => $false_answer2,
            'false3' => $false_answer3,
            'category' => $category,
            'status' => $status,
            'id' => $question_id
        ));

        return $result && $this->setUserQuestionRelation($user_id, $question_id, "modified");
    }

    /**
     * Deleting a quiz question
     * @param String $question_id id of the question to delete
     * @return bool result
     */
    public function deleteQuizQuestion($question_id)
    {
        $stmt = $this->conn->prepare("DELETE q FROM quizquestions q WHERE q.id = :qid");
        $stmt->bindParam(":qid", $question_id, PDO::PARAM_INT);

        return $stmt->execute() && $stmt->rowCount() > 0;
    }

    /**
     * Approve a quiz question
     * @param int $question_id id of the question to approve
     * @param int $user_id id of the user
     * @param int $status set to 1 for approving
     * @return mixed result
     */
    public function approveQuizQuestion($question_id, $user_id, $status = 1)
    {

        $stmt = $this->conn->prepare("UPDATE quizquestions SET status = :status WHERE id = :id");
        $stmt->bindParam(':id', $question_id, PDO::PARAM_INT);
        $stmt->bindParam(':status', $status, PDO::PARAM_INT);
        $result = $stmt->execute();

        $result_uqrel = $this->setUserQuestionRelation($user_id, $question_id, "approved");

        return $result && $result_uqrel;
    }

    /**
     * Checks whether the question is accepted or not
     * @param int $question_id id of the question to check
     * @return bool true, if the question is accepted, otherwise false
     */
    public function isAcceptedQuestion($question_id)
    {
        if (AUTO_APPROVING === true) {
            $stmt = $this->conn->prepare("SELECT * FROM quizquestions qq WHERE qq.id = :qid AND qq.status = 1 OR
                                              qq.id IN (SELECT qr.qid FROM (SELECT question_id as qid, COUNT(*) as review_num,
                                              AVG(answer_correct) as correct_avg, AVG(answer_distractors) as distractors_avg,
                                              AVG(question_formulation) as formulation_avg, AVG(question_difficulty) as difficulty_avg FROM
                                              question_review GROUP BY question_id) AS qr WHERE qr.qid = :qid AND qr.review_num >= :num_threshold AND
                                              qr.correct_avg >= :correct_threshold AND qr.distractors_avg >= :distractors_threshold AND
                                              qr.formulation_avg >= :formulation_threshold)");

            $stmt->bindParam('qid', $question_id, PDO::PARAM_INT);
            $stmt->bindValue('num_threshold', AUTO_APPROVING_NUM_THRESHOLD, PDO::PARAM_INT);
            $stmt->bindValue('correct_threshold', AUTO_APPROVING_CORRECT_THRESHOLD, PDO::PARAM_INT);
            $stmt->bindValue('distractors_threshold', AUTO_APPROVING_DISTRACTORS_THRESHOLD, PDO::PARAM_INT);
            $stmt->bindValue('formulation_threshold', AUTO_APPROVING_FORMULATION_THRESHOLD, PDO::PARAM_INT);

        } else {
            $stmt = $this->conn->prepare("SELECT * FROM quizquestions qq WHERE qq.id = :qid AND qq.status = 1");
            $stmt->bindParam(':qid', $question_id, PDO::PARAM_INT);
        }

        if ($stmt->execute() && $stmt->rowCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Export all quiz questions together with review information
     * @return array list of all questions with average review information
     */
    public function exportQuestions()
    {
        $stmt = $this->conn->prepare("SELECT qq.*, qr.review_num, qr.correct_avg, qr.distractors_avg, qr.formulation_avg, qr.difficulty_avg FROM quizquestions qq
                                      LEFT JOIN (SELECT question_id as qid, COUNT(*) as review_num, AVG(answer_correct) as correct_avg,
                                      AVG(answer_distractors) as distractors_avg, AVG(question_formulation) as formulation_avg,
                                      AVG(question_difficulty) as difficulty_avg FROM question_review GROUP BY question_id) AS qr ON qq.id = qr.qid ORDER BY qq.created_at");
        if ($stmt->execute()) {
            return $stmt->fetchAll(PDO::FETCH_ASSOC);
        } else {
            return array();
        }
    }

    /**
     * Fetching the user id of the question creator
     * @param int $question_id id of question
     * @return int user id of question creator
     */
    public function getQuizQuestionCreatorId($question_id)
    {
        $stmt = $this->conn->prepare("SELECT user_id FROM user_questions WHERE question_id = :qid AND status LIKE '%created%'");
        $stmt->bindParam('qid', $question_id);

        if ($stmt->execute() && $stmt->rowCount() > 0) {
            $result = $stmt->fetchColumn();

            return $result;
        } else {
            return -1;
        }

    }

    /* ------------- `user_questions` table method ------------------ */

    /**
     * Function to create relation of a user to a question
     * @param int $user_id id of the user
     * @param int $question_id id of the quiz question
     * @param String $status status of the relation
     */
    public function createUserQuestionRelation($user_id, $question_id, $status)
    {
        $stmt = $this->conn->prepare("INSERT INTO user_questions(user_id, question_id, status) values(?, ?, ?)");
        $stmt->bindParam(1, $user_id, PDO::PARAM_INT);
        $stmt->bindParam(2, $question_id, PDO::PARAM_INT);
        $stmt->bindParam(3, $status, PDO::PARAM_STR);
        $result = $stmt->execute();

        return $result;
    }

    /**
     * Function to get relation of user to a question
     * @param int $user_id id of the user
     * @param int $question_id id of the quiz question
     * @return array relation of user to question
     */
    public function getUserQuestionRelation($user_id, $question_id)
    {
        $stmt = $this->conn->prepare("SELECT * FROM user_questions WHERE user_id = :uid AND question_id = :qid");
        $stmt->bindParam(':uid', $user_id, PDO::PARAM_INT);
        $stmt->bindParam(':qid', $question_id, PDO::PARAM_INT);
        if ($stmt->execute() && $stmt->rowCount() > 0) {
            return $stmt->fetch(PDO::FETCH_ASSOC);
        } else {
            return null;
        }
    }

    /**
     * Function to update status of user to a question
     * @param int $user_id id of the user
     * @param int $question_id id of the quiz question
     * @param String $status status of the relation
     */
    public function updateUserQuestionRelation($user_id, $question_id, $status)
    {
        $stmt = $this->conn->prepare("UPDATE user_questions SET status = :status WHERE user_id = :uid AND question_id = :qid");
        $stmt->bindParam(':uid', $user_id, PDO::PARAM_INT);
        $stmt->bindParam(':qid', $question_id, PDO::PARAM_INT);
        $stmt->bindParam(':status', $status, PDO::PARAM_STR);
        $result = $stmt->execute();

        return $result;
    }

    /**
     * Function to set status of user to a question
     * (updates if already exists otherwise creates it)
     * @param int $user_id id of the user
     * @param int $question_id id of the quiz question
     * @param String $status status of the relation
     * @return boolean result of database operation
     */
    public function setUserQuestionRelation($user_id, $question_id, $status)
    {
        if (!is_null($this->getUserQuestionRelation($user_id, $question_id))) {
            return $this->updateUserQuestionRelation($user_id, $question_id, $status);
        } else {
            return $this->createUserQuestionRelation($user_id, $question_id, $status);
        }
    }

    /* ------------- `question_review` table method ------------------ */

    /**
     * Function to create review of a question from specified user
     * @param int $question_id id of the quiz question
     * @param int $user_id id of the user
     * @param int $answer_correct 1 if correct marked answer of the question is the right answer, otherwise 0
     * @param int $answer_distractors 1 if the answer distractors were wisely chosen, otherwise 0
     * @param int $question_formulation 1 if the question was well-formulated, otherwise 0
     * @param int $question_difficulty difficulty level of the question
     */
    public function createQuestionReview(
        $question_id,
        $user_id,
        $answer_correct,
        $answer_distractors,
        $question_formulation,
        $question_difficulty
    ) {
        $stmt = $this->conn->prepare("INSERT INTO question_review(user_id, question_id, answer_correct,
                                      answer_distractors, question_formulation, question_difficulty) values(?, ?, ?, ?, ?, ?)");
        $stmt->bindParam(1, $user_id, PDO::PARAM_INT);
        $stmt->bindParam(2, $question_id, PDO::PARAM_INT);
        $stmt->bindParam(3, $answer_correct);
        $stmt->bindParam(4, $answer_distractors);
        $stmt->bindParam(5, $question_formulation);
        $stmt->bindParam(6, $question_difficulty, PDO::PARAM_INT);
        $result = $stmt->execute();

        return $result;
    }

    /**
     * Function to get review of a question from specified user
     * @param int $question_id id of the quiz question
     * @param int $user_id id of the user (optional)
     * @return array review of user for question
     */
    public function getQuestionReview($question_id, $user_id = null)
    {
        if (is_null($user_id)) {
            return getQuestionAvgReview($question_id);
        }

        $stmt = $this->conn->prepare("SELECT * FROM question_review WHERE user_id = :uid AND question_id = :qid");
        $stmt->bindParam(':uid', $user_id, PDO::PARAM_INT);
        $stmt->bindParam(':qid', $question_id, PDO::PARAM_INT);

        if ($stmt->execute() && $stmt->rowCount() > 0) {
            return $stmt->fetch(PDO::FETCH_ASSOC);
        } else {
            return null;
        }
    }

    /**
     * Function to update review of user to a question
     * @param int $question_id id of the quiz question
     * @param int $user_id id of the user
     * @param int $answer_correct 1 if correct marked answer of the question is the right answer, otherwise 0
     * @param int $answer_distractors 1 if the answer distractors were wisely chosen, otherwise 0
     * @param int $question_formulation 1 if the question was well-formulated, otherwise 0
     * @param int $question_difficulty difficulty level of the question
     */
    public function updateQuestionReview(
        $question_id,
        $user_id,
        $answer_correct,
        $answer_distractors,
        $question_formulation,
        $question_difficulty
    ) {
        $stmt = $this->conn->prepare("UPDATE question_review SET answer_correct = :correct, answer_distractors = :distractors,
                                      question_formulation = :formulation, question_difficulty = :difficulty WHERE user_id = :uid AND question_id = :qid");
        $stmt->bindParam(':uid', $user_id, PDO::PARAM_INT);
        $stmt->bindParam(':qid', $question_id, PDO::PARAM_INT);
        $stmt->bindParam(':correct', $answer_correct);
        $stmt->bindParam(':distractors', $answer_distractors);
        $stmt->bindParam(':formulation', $question_formulation);
        $stmt->bindParam(':difficulty', $question_difficulty);
        $result = $stmt->execute();

        return $result;
    }

    /**
     * Deleting a question review
     * @param int $question_id id of the quiz question
     * @param int $user_id id of the user
     */
    public function deleteQuestionReview($question_id, $user_id)
    {
        $stmt = $this->conn->prepare("DELETE q FROM question_review q WHERE q.question_id = :qid AND q.user_id = :uid");
        $stmt->bindParam(":qid", $question_id, PDO::PARAM_INT);
        $stmt->bindParam(":uid", $user_id, PDO::PARAM_INT);

        return $stmt->execute();
    }

    /**
     * Function to get average review values of a question
     * @param int $question_id id of the quiz question
     * @return array result
     */
    public function getQuestionAvgReview($question_id)
    {
        $stmt = $this->conn->prepare("SELECT question_id, COUNT(*) as review_num, AVG(answer_correct) as correct_avg,
                                      AVG(answer_distractors) as distractors_avg, AVG(question_formulation) as formulation_avg,
                                      AVG(question_difficulty) as difficulty_avg FROM question_review WHERE question_id = :qid GROUP BY question_id");

        $stmt->bindParam(':qid', $question_id, PDO::PARAM_INT);

        if ($stmt->execute() && $stmt->rowCount() > 0) {
            return $stmt->fetch(PDO::FETCH_ASSOC);
        } else {
            return null;
        }
    }

    /* ------------- method for rewarding ------------------ */

    /**
     * Checks whether the question creator was already rewarded or not
     * @param int $question_id id of the question
     * @return bool true, if creator already rewarded, otherwise false
     */
    public function isQuestionCreatorRewarded($question_id)
    {
        $creator_user_id = $this->getQuizQuestionCreatorId($question_id);
        $result = $this->getUserQuestionRelation($creator_user_id, $question_id);

        if ($result != null && $result["status"] == "created-rewarded") {
            return true;
        }

        return false;

    }

    /**
     * Rewards the question creator for his accepted question if not already rewarded
     * @param int $question_id id of the question
     * @return bool true, if the creator was rewarded, otherwise false
     */
    public function rewardQuestionCreator($question_id)
    {

        if ($this->isQuestionCreatorRewarded($question_id)) {
            return false;
        }

        $creator_user_id = $this->getQuizQuestionCreatorId($question_id);

        if ($this->incrementUserScore($creator_user_id,
                SCORE_FOR_ACCEPTED_QUESTION) && $this->setUserQuestionRelation($creator_user_id, $question_id,
                "created-rewarded")
        ) {
            return true;
        }

        return false;
    }
}























