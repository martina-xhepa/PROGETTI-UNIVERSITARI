<?php
include "../php/db.php";
header("Content-type: application/json");

$rows = getProdotti();

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
