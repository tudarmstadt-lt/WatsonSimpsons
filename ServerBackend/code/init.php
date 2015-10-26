<?php
/**
 * Initialization Script.
 * Please DELETE from your web server after initialization!
 * User: dath
 * Date: 23.10.2015
 * Time: 16:00
 */

/* *** Please enter your database credentials in config.php and define the following values. ***
   *** IMPORTANT: Please DELETE this file after initialization form your web server!!!       *** */
$admin_pwd = "";
$guest_apiKey = "";
$guest_pwd = "";

/* ********** DO NOT CHANGE THE FOLLOWING! ********** */

if(empty($admin_pwd) || empty($guest_apiKey) || empty($guest_pwd))
    die("Please define Admin-Pwd, Guest-Pwd and Guest-ApiKey in init.php!");


require_once 'include/config.php';

// opening db connection
require_once 'include/DbConnect.php';
$db = new DbConnect();
$dbc = $db->connect();

try {

    if(is_null($dbc))
        die("<br>Error while connection with database. Please check config.php!");

    /* Create Tables */
    $dbc->query("CREATE TABLE IF NOT EXISTS `question_review` (
                  `question_id` int(11) NOT NULL,
                  `user_id` int(11) NOT NULL,
                  `answer_correct` tinyint(1) NOT NULL DEFAULT '0',
                  `answer_distractors` tinyint(1) NOT NULL DEFAULT '0',
                  `question_formulation` tinyint(1) NOT NULL DEFAULT '0',
                  `question_difficulty` int(2) NOT NULL DEFAULT '1',
                  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");

    $dbc->query("CREATE TABLE `quizquestions` (
                  `id` int(11) NOT NULL,
                  `question` text CHARACTER SET utf8 NOT NULL,
                  `correct_answer` text CHARACTER SET utf8 NOT NULL,
                  `false_answer1` text CHARACTER SET utf8 NOT NULL,
                  `false_answer2` text CHARACTER SET utf8 NOT NULL,
                  `false_answer3` text CHARACTER SET utf8 NOT NULL,
                  `category` varchar(200) CHARACTER SET utf8 NOT NULL,
                  `status` int(1) NOT NULL DEFAULT '0',
                  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
                  `modified_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");

    $dbc->query("CREATE TABLE `users` (
                  `id` int(11) NOT NULL,
                  `username` varchar(250) COLLATE utf8_bin NOT NULL,
                  `password_hash` text COLLATE utf8_bin NOT NULL,
                  `role` varchar(100) COLLATE utf8_bin NOT NULL DEFAULT 'user',
                  `api_key` varchar(100) CHARACTER SET utf8 NOT NULL,
                  `status` tinyint(1) NOT NULL DEFAULT '1',
                  `quiz_score` int(20) NOT NULL DEFAULT '0',
                  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
                ) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");

    $dbc->query("CREATE TABLE `user_questions` (
                  `user_id` int(11) NOT NULL,
                  `question_id` int(11) NOT NULL,
                  `status` varchar(100) CHARACTER SET utf8 NOT NULL,
                  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");

    /* Add Indexes */
    $dbc->query("ALTER TABLE `question_review` ADD PRIMARY KEY (`question_id`,`user_id`);");
    $dbc->query("ALTER TABLE `quizquestions` ADD PRIMARY KEY (`id`);");
    $dbc->query("ALTER TABLE `users` ADD PRIMARY KEY (`id`), ADD UNIQUE KEY `username` (`username`);");
    $dbc->query("ALTER TABLE `user_questions` ADD PRIMARY KEY (`user_id`,`question_id`), ADD KEY `question_id` (`question_id`);");

    /* Add Auto Increments */
    $dbc->query("ALTER TABLE `quizquestions` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1;");
    $dbc->query("ALTER TABLE `users` MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=1;");

    /* Add Constraints */
    $dbc->query("ALTER TABLE `question_review`
                  ADD CONSTRAINT `question_review_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `quizquestions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;");

    $dbc->query("ALTER TABLE `user_questions`
                    ADD CONSTRAINT `user_questions_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `quizquestions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                    ADD CONSTRAINT `user_questions_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;");

    echo "Successfully initialized database tables. <br>";
} catch (PDOException $e) {
    echo "Initialization failed!<br><br>";
    echo "<pre>".$e->getMessage()."</pre>";
    exit();
} finally {
    $dbc = null;
    $db = null;
}

/* Use DbHandler */
require_once 'include/DbHandler.php';
$dbh = new DbHandler();
$failed = false;

/* Create Admin-User */
if($dbh->createUser("admin", $admin_pwd, "admin") == USER_CREATED_SUCCESSFULLY)
    echo "Created Admin-User.<br>";
else {
    $failed = true;
    echo "Error while creating Admin-User!<br>";
}
/* Create Guest-User */
if($dbh->createUser("guest", $guest_pwd, "user", $guest_apiKey) == USER_CREATED_SUCCESSFULLY)
    echo "Created Guest-User.<br>";
else {
    $failed = true;
    echo "Error while creating Guest-User!<br>";
}
// Remove DbHandler
$dbh = null;

if(!$failed)
    echo "<br>Initialization finished.<br>Remeber to DELETE the init.php from your web server!";


