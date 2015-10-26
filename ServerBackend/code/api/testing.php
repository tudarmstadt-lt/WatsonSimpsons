<?php

require_once '../include/config.php';
require_once '../include/DbHandler.php';

$db = new DbHandler();

$question_id = 6;

if ($db->isAcceptedQuestion($question_id)) {
    echo "Question $question_id was accepted!<br>";
} else {
    echo "Question $question_id was NOT accepted!<br>";
}

$creator = $db->getQuizQuestionCreatorId($question_id);

echo "Creator of Question $question_id is User $creator.<br>";

if ($db->isQuestionCreatorRewarded($question_id)) {
    echo "Creator was already rewarded for Question $question_id.<br>";
} else {
    echo "Creator was NOT rewared for Questions $question_id.<br>";
}

if ($db->isAcceptedQuestion($question_id) && $db->rewardQuestionCreator($question_id)) {
    echo "Creator was rewarded for Question $question_id!<br>";
} else {
    echo "Creator could NOT rewared for Questions $question_id!<br>";
}

if ($db->isQuestionCreatorRewarded($question_id)) {
    echo "Creator was already rewarded for Question $question_id.<br>";
} else {
    echo "Creator was NOT rewared for Questions $question_id.<br>";
}

echo "<hr>";

$question_id = 9;
if ($db->isAcceptedQuestion($question_id)) {
    echo "Question $question_id was accepted!<br>";
} else {
    echo "Question $question_id was NOT accepted!<br>";
}
