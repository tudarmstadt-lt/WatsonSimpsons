<?php
/**
 * Export-Page for Questions
 * User: dath
 * Date: 02.10.2015
 * Time: 13:40
 */

require_once 'include/config.php';
require_once 'include/DbHandler.php';

function array_to_csv_download($array, $filename = "export.csv", $delimiter=";") {
    header('Content-Type: application/csv');
    header('Content-Disposition: attachment; filename="'.$filename.'";');

    // open the "output" stream
    // see http://www.php.net/manual/en/wrappers.php.php#refsect2-wrappers.php-unknown-unknown-unknown-description
    $f = fopen('php://output', 'w');

    foreach ($array as $line) {
        fputcsv($f, $line, $delimiter);
    }
}

function array_to_html_table($array, $headers) {
    echo '<table border="1">';
    echo '<thead>';
    echo '<tr>';
    foreach ($headers[0] as $h) {
        echo '<th>'.$h.'</th>';
    }
    echo '</tr>';
    echo '</thead><tbody>';
    foreach ($array as $line) {
        echo '<tr>';
        foreach ($line as $k=>$v) {
            echo '<td>'.$v.'</td>';
        }
        echo '</tr>';
    }
    echo '</tbody>';
    echo '</table>';
}

if(isset($_POST['auth_key'])) {
    if ($_POST['auth_key'] != EXPORT_AUTH_KEY) {
        die('Forbidden Action! Invalid credentials.');
    }

    $db = new DbHandler();
    // fetching all quiz questions to export
    $result = $db->exportQuestions();

    $result_headers = array(
        array(
            "id",
            "question",
            "correct_answer",
            "false_answer1",
            "false_answer2",
            "false_answer3",
            "category",
            "status",
            "created_at",
            "modified_at",
            "review_num",
            "correct_avg",
            "distractors_avg",
            "formulation_avg",
            "difficulty_avg"
        )
    );

    if(isset($_POST['download']) && $_POST['download'] == true)
        array_to_csv_download(array_merge($result_headers, $result));
    else {
        array_to_html_table($result, $result_headers);
    }

} else {
?>
<h1>Export Quiz Questions</h1>
<p><strong>Authorization is required for exporting!</strong></p>
<form method="post" action="export.php">
    <p><label>Auth:</label> <input type="password" name="auth_key"></p>
    <p><label>Download as CSV?</label> <input type="checkbox" name="download" value="true" checked></p>
    <p><input type="submit"></p>
</form>

<?php
}


