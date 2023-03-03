<?php
include '../components/connect.php';

session_start();

if(!isset($_SESSION['loggedIn'])) {
    header('Location: ../html/userlogin.php');
    exit();
};

if(isset($_POST['delete'])){

    $id = htmlspecialchars($_POST['idPhp']);
    $delete_product_image = $conn->prepare("SELECT * FROM `products` WHERE id = ?");
    $delete_product_image->execute([$id]);
    $fetch_delete_image = $delete_product_image->fetch(PDO::FETCH_ASSOC);
    unlink('../uploaded_img/'.$fetch_delete_image['image_01']);
    unlink('../uploaded_img/'.$fetch_delete_image['image_02']);
    unlink('../uploaded_img/'.$fetch_delete_image['image_03']);
    $delete_product = $conn->prepare("DELETE FROM `products` WHERE id = ?");
    $delete_product->execute([$id]);
    $delete_cart = $conn->prepare("DELETE FROM `cart` WHERE pid = ?");
    $delete_cart->execute([$id]);
    $delete_wishlist = $conn->prepare("DELETE FROM `wishlist` WHERE pid = ?");
    $delete_wishlist->execute([$id]);

    $select_products = $conn->prepare("SELECT * FROM `products`");
    $select_products->execute();

    if($select_products->rowCount() > 0){
        echo json_encode("success");
     }else{
        echo json_encode("empty");
     }
 };
?>
