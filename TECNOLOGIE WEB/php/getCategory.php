<?php
include 'db.php';
header("Content-type: application/json");

/*print "{\n";*/

$rows = getCategoria($_POST["category"]);

$count = $rows->rowCount();
if ($count == 0) {
    echo json_encode("empty");
} else {
    // encode to json all the rows in our result set
    print "{\n";
    print "  \"prodotti\": ";
    print json_encode($rows->fetchAll(PDO::FETCH_ASSOC));
    print "\n}\n";
}
?>
