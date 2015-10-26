<?php
/**
 * Handling database connection
 * User: dath
 * Date: 12.10.2015
 * Time: 19:00
 */

/**
 * Handling database connection
 */
class DbConnect
{

    private $conn;

    function __construct()
    {
    }

    /**
     * Establishing database connection
     * @return database connection handler
     */
    function connect()
    {
        include_once dirname(__FILE__) . '/config.php';

        try {
            // Connecting to mysql database
            $this->conn = new PDO("mysql:host=" . DB_HOST . ";dbname=" . DB_NAME, DB_USERNAME, DB_PASSWORD,
                array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));

            $this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

            // returing connection resource
            return $this->conn;
        } catch (PDOException $e) {
            echo $e->getMessage();

            return null;
        }
    }

}