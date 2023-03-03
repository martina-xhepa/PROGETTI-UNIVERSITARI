<?php

/* 
 *Autore: Martina Xhepa
 *Matricola: 891242 
 */

/*Si verifica che il prodotto non sia nella wishlist, 
se così fosse lo si elimina e lo si aggiunge al carrello.
Se è già nel carrello no viene aggiunto*/

include '../components/connect.php';

session_start();

if(!isset($_SESSION['userLoggedIn'])) {
    header('Location: ../html/userlogin.php');
    exit();
}

if(isset($_SESSION['user_id'])){
   $user_id = $_SESSION['user_id'];
}else{
   header('Location: ../html/userlogin.php');
   exit();
}

if(isset($_POST['add_to_cart'])) {
    
    $pid = htmlspecialchars($_POST['pidPhp']);
    $name = htmlspecialchars($_POST['namePhp']);
    $price = htmlspecialchars($_POST['pricePhp']);
    $image = htmlspecialchars($_POST['imagePhp']);
    $qty = htmlspecialchars($_POST['qtyPhp']);

    $check_cart_numbers = $conn->prepare("SELECT * FROM `cart` WHERE name = ? AND user_id = ?");
    $check_cart_numbers->execute([$name, $user_id]);

    if($check_cart_numbers->rowCount() > 0){
      echo json_encode("exists");
    }else{

      $check_wishlist_numbers = $conn->prepare("SELECT * FROM `wishlist` WHERE name = ? AND user_id = ?");
      $check_wishlist_numbers->execute([$name, $user_id]);

      if($check_wishlist_numbers->rowCount() > 0){
         $delete_wishlist = $conn->prepare("DELETE FROM `wishlist` WHERE name = ? AND user_id = ?");
         $delete_wishlist->execute([$name, $user_id]);
      }

      $insert_cart = $conn->prepare("INSERT INTO `cart`(user_id, pid, name, price, quantity, image) VALUES(?,?,?,?,?,?)");
      $insert_cart->execute([$user_id, $pid, $name, $price, $qty, $image]);
      echo json_encode("success");
      
   }
    
}

?>