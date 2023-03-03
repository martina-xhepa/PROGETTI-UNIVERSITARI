<?php

/*Autore: Martina Xhepa
  Matricola: 891242 */

/*Homepage*/

session_start();

include '../components/connect.php';

if(isset($_SESSION['userLoggedIn'])){
   $user_id = $_SESSION['user_id'];
   $user_name = $_SESSION['user_name'];
}else if(isset($_SESSION['loggedIn'])){
   $admin_id = $_SESSION['admin_id'];
   $admin_name = $_SESSION['admin_name'];
}else{
   header('Location: ../html/userlogin.php');
   exit();
}

?>

<!DOCTYPE html>
<html lang="en">
<head>
   <meta charset="UTF-8">
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1.0">
   <title>home</title>
   <link rel="stylesheet" href="https://unpkg.com/swiper@8/swiper-bundle.min.css" />
   <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css">
   <link rel="stylesheet" href="../css/userstyle.css">
   <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
   <link href="../css/stile.css" type="text/css" rel="stylesheet">
	<script src="../js/query.js" type="text/javascript"></script>

</head>
<body>

<?php include '../components/userheader.php'; ?>

<div class="slideshow-container">

<div class="mySlides fade">
  <div class="numbertext">1 / 3</div>
  <img class="slideImg" src="../image/slide1.jpg">
  <div class="text">Fast For Fun</div>
</div>

<div class="mySlides fade">
  <div class="numbertext">2 / 3</div>
  <img class="slideImg" src="../image/slide2.jpg">
  <div class="text">Fast For Fun</div>
</div>

<div class="mySlides fade">
  <div class="numbertext">3 / 3</div>
  <img class="slideImg" src="../image/slide3.jpg">
  <div class="text">Fast For Fun</div>
</div>

</div>
<br>

<div class= "dot-container "style="text-align:center">
  <span class="dot"></span> 
  <span class="dot"></span> 
  <span class="dot"></span> 
</div>

<section class="category">

   <h1 class="heading">shop by category</h1>

   <div class="swiper category-slider">

   <div class="swiper-wrapper">

   <a href="../html/category.php?category=SCI" class="swiper-slide slide">
      <img src="../image/icona1.jpg" alt="">
      <h3>SCI</h3>
   </a>

   <a href="../html/category.php?category=scarponi" class="swiper-slide slide">
      <img src="../image/icona2.jpg" alt="">
      <h3>scarponi</h3>
   </a>

   <a href="../html/category.php?category=casco" class="swiper-slide slide">
      <img src="../image/icona3.jpg" alt="">
      <h3>casco</h3>
   </a>

   <a href="../html/category.php?category=maschera" class="swiper-slide slide">
      <img src="../image/icona4.jpg" alt="">
      <h3>maschera</h3>
   </a>
   </div>

   <div class="swiper-pagination"></div>

   </div>

</section>

<?php include '../components/footer.php'; ?>

<script src="https://unpkg.com/swiper@8/swiper-bundle.min.js"></script>


<script>

 var swiper = new Swiper(".category-slider", {
   loop:true,
   spaceBetween: 20,
   pagination: {
      el: ".swiper-pagination",
      clickable:true,
   },
   breakpoints: {
      0: {
         slidesPerView: 2,
       },
      650: {
        slidesPerView: 3,
      },
      768: {
        slidesPerView: 4,
      },
      1024: {
        slidesPerView: 4,
      },
   },
});

</script>

</body>
</html>