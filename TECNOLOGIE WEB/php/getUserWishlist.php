<?php
session_start();

if(isset($_SESSION['user_id'])){
   $user_id = $_SESSION['user_id'];
}else{
   $user_id = '';
   header('Location: ../html/userlogin.php');
};

include "db.php";
header("Content-type: application/json");

$rows = getListaDesideri($user_id);

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
