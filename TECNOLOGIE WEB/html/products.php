<?php

/*Autore: Martina Xhepa
Matricola: 891242 */

//Pagina in cui l'amministratore può inserire nuovi prodotti 
//visualizzare quelli già caricati e decidere di eliminarli
include '../components/connect.php';

session_start();

if(!isset($_SESSION['loggedIn'])) {
   header('Location: ../html/userlogin.html');
   exit();
}

$admin_id = $_SESSION['admin_id'];
$admin_name = $_SESSION['admin_name'];

?>

<!DOCTYPE html>
<html lang="en">
<head>
   <meta charset="UTF-8">
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1.0">
   <title>products</title>
   <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css">
   <link rel="stylesheet" href="../css/adminstyle.css">
   <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
   <script src="../js/showProduct.js" type="text/javascript"></script>
	<script src="../js/addProduct.js" type="text/javascript"></script>

</head>
<body>

<?php include '../components/adminheader.php'; ?>

<section class="add-products">

   <h1 class="heading">add product</h1>


   <form id = "productform" enctype="multipart/form-data">

      <div id="response"></div>
      <div class="flex">
         <div class="inputBox">
            <span>product name (required)</span>
            <input type="text" class="box" required maxlength="100" placeholder="enter product name" id ="name" name ="name"/>
         </div>
         <div class="inputBox">
            <span>product price (required)</span>
            <input type="number" min="0" class="box" required max="9999999999" placeholder="enter product price" id ="price" name ="price"/>
         </div>
        <div class="inputBox">
            <span>image 01 (required)</span>
            <input type="file" id="image_01" name="image_01" accept="image/jpg, image/jpeg, image/png, image/webp" class="box" required />
        </div>
        <div class="inputBox">
            <span>image 02 (required)</span>
            <input type="file" id ="image_02" name="image_02" accept="image/jpg, image/jpeg, image/png, image/webp" class="box" required />
        </div>
        <div class="inputBox">
            <span>image 03 (required)</span>
            <input type="file" id ="image_03" name="image_03" accept="image/jpg, image/jpeg, image/png, image/webp" class="box" required />
        </div>
         <div class="inputBox">
            <span>product details (required)</span>
            <textarea id ="details" name="details" placeholder="enter product details" class="box" required maxlength="500" cols="30" rows="10" ></textarea>
         </div>
      </div>
      
      <input type="button" value="add product" class="btn" id ="add_product">
   </form>

</section>

<section class="show-products">

   <h1 class="heading">products added</h1>

   <div id="response"></div>

   <div class="box-container" id="show">
   
   </div>

</section>
   
</body>
</html>