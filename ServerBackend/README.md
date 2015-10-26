# Simpsons Quiz ServerBackend

## About
For managing SimpsonsQuiz related data like users, questions and reviews a RESTful ServerBackend was implemented.
The ServerBackend mainly acts as intermediary between the database and the SimpsonsQuiz applications. It was programmed in
PHP and uses [Slim](http://www.slimframework.com/), a micro framework for PHP.

## Prerequisites
* (Apache) Web Server with PHP version 5.3.0 or newer and enabled mod_rewrite
* MySQL database

## Installation

1. Rename 'code/include/config_sample.php' to 'config.php'

2. Set database configuration (credentials/hostname) and Auth-Keys in 'code/include/config.php'

3. Set Admin-Password, Guest-Password and Guest-ApiKey in 'code/init.php'

4. Copy files from code-directory to your webserver

5. Run the 'init.php' for database initialization

6. Delete 'init.php' from your web server if initialization was successfully.

7. Now the ServerBackend should be working.

The Backend-URL for the clients is: '<URL of ServerBackend directory>/api/'.

## API-Calls
The ServerBackend offers different functionality over its REST API which are listed in at '<URL of ServerBackend directory>/api/overview.txt' or locally in 'api/overview.txt'.

HTTP-Requests to the ServerBackend have to be send with corresponding HTTP-Method and Content-Type `application/x-www-form-urlencoded`, responses are sent as JSON-Objects with Content-Type `application/json` to the client.

For security reasons every request must contain the secret APP-Key (defined in 'config.php').
With the exception of register and login, all requests also must contain an user-specific API-Key that authorizes and identifies the requesting user (generated and save in database table _users_).
These keys has to be send as `AppKey` responding `ApiKey` header fields with corresponding values.

## Export Quiz Questions

The ServerBackend offers with 'export.php' an export solution.
Open '<URL of ServerBackend directory>/export.php' in your Browser.
With the `EXPORT_AUTH_KEY` defined in 'config.php' you can authorize for exporting.
You can either download the questions as CSV or show them as HTML page by deactivating the checkbox.
