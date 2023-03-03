<?php

/* 
 *Autore: Martina Xhepa
 *Matricola: 891242 
 */

/*Si controlla che il prodotto non sia già 
nel carrello o nella wishlist*/

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

if(isset($_POST['add_to_wishlist'])) {
    
    $pid = htmlspecialchars($_POST['pidPhp']);
    $name = htmlspecialchars($_POST['namePhp']);
    $price = htmlspecialchars($_POST['pricePhp']);
    $image = htmlspecialchars($_POST['imagePhp']);

    $check_wishlist_numbers = $conn->prepare("SELECT * FROM `wishlist` WHERE name = ? AND user_id = ?");
    $check_wishlist_numbers->execute([$name, $user_id]);

    $check_cart_numbers = $conn->prepare("SELECT * FROM `cart` WHERE name = ? AND user_id = ?");
    $check_cart_numbers->execute([$name, $user_id]);

      if($check_wishlist_numbers->rowCount() > 0){
         echo json_encode("wishlist");
      }elseif($check_cart_numbers->rowCount() > 0){
         echo json_encode("cart");
      }else{
         $insert_wishlist = $conn->prepare("INSERT INTO `wishlist`(user_id, pid, name, price, image) VALUES(?,?,?,?,?)");
         $insert_wishlist->execute([$user_id, $pid, $name, $price, $image]);
         echo json_encode("success");
      }

}

?>