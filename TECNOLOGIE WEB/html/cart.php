<?php

/*Autore: Martina Xhepa
  Matricola: 891242 */

//Pagina dedicata alla visualizzazione del carrello dell'utente

include '../components/connect.php';

session_start();

if(!isset($_SESSION['userLoggedIn'])) {
   header('Location: ../html/userlogin.html');
   exit();
}

$user_id = $_SESSION['user_id'];
$user_name = $_SESSION['user_name'];

?>

<!DOCTYPE html>
<html lang="en">
<head>
   <meta charset="UTF-8">
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1.0">
   <title>shopping cart</title>
   <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css">
   <link rel="stylesheet" href="../css/userstyle.css">
   <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
	<script src="../js/cart.js" type="text/javascript"></script>

</head>
<body>
   
<?php include '../components/userheader.php'; ?>

<section class="products shopping-cart" id="clear">

   <h3 class="heading">shopping cart</h3>

   <div class="box-container" id="cart-print">
   </div>

   <div class="cart-total" id="cart-total">
   </div>

</section>

<?php include '../components/footer.php'; ?>

</body>
</html>