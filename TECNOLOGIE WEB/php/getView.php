<?php

/*Autore: Martina Xhepa
Matricola: 891242

interroga il DB per ottenere i dati del prodotto*/
include 'db.php';
header("Content-type: application/json");

$rows = getVista($_POST["pid"]);

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
