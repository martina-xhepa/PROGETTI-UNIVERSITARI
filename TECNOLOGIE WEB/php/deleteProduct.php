<?php

/* Autore: Martina Xhepa
   Matricola: 891242 */

/*Elimina il prodotto dal carrello dell'utente se 
POST['listOrCart'] è 1, se è 0 lo elimina dalla wishlist */
session_start();

if(isset($_SESSION['user_id'])){
   $user_id = $_SESSION['user_id'];
}else{
   $user_id = '';
   header('Location: ../html/userlogin.php');
};

include "db.php";
header("Content-type: application/json");


$id = htmlspecialchars($_POST['idPhp']);
$isCart = htmlspecialchars($_POST['listOrCart']);

if($isCart==1){
    $rows = getCarelloAggiornato($user_id, $id);
}else{
    $rows = getListaAggiornata($user_id, $id);
}


$count = $rows->rowCount();
if ($count == 0) {
    echo json_encode("empty");
} else {
    // encode to json all the rows in our result set
    echo json_encode("success");
}
?>
