<?php
/**
 * Config-File for QuizBackend
 * Please set database configuration, App-Key and Auth-Key for export!
 * Save it then as config.php
 *
 * User: dath
 * Date: 12.10.2015
 * Time: 18:30
 */

/**
 * Database configuration
 */
define('DB_USERNAME', '');
define('DB_PASSWORD', '');
define('DB_HOST', '');
define('DB_NAME', '');

/**
 * Constants for return values of user creation
 */
define('USER_CREATED_SUCCESSFULLY', 0);
define('USER_CREATE_FAILED', 1);
define('USER_ALREADY_EXISTED', 2);

/**
 * Settings for Auto-Approving via user reviews
 */
define('AUTO_APPROVING', true);
define('AUTO_APPROVING_NUM_THRESHOLD', 3);
define('AUTO_APPROVING_CORRECT_THRESHOLD', 0.8);
define('AUTO_APPROVING_DISTRACTORS_THRESHOLD', 0.6);
define('AUTO_APPROVING_FORMULATION_THRESHOLD', 0.5);

/**
 * Points for user participation scoring
 */
define('SCORE_FOR_REVIEW', 1);
define('SCORE_FOR_ACCEPTED_QUESTION', 5);

/**
 * Auth-Keys
 */
define('APP_KEY', '');
define('EXPORT_AUTH_KEY', '');
