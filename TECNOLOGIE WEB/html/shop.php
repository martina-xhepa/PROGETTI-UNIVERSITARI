<?php
/*Autore: Martina Xhepa
Matricola: 891242 */

/*Pagina in cui si visualizzano tutti i prodotti,
indistintamente dalla categoria*/
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
   <title>shop</title>
   <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css">
   <link rel="stylesheet" href="../css/userstyle.css">
   <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
	<script src="../js/products.js" type="text/javascript"></script>

</head>
<body>

<?php include '../components/userheader.php'; ?>

<section class="products">

   <h1 class="heading">latest products</h1>

   <div class="box-container" id="product-section">

   </div>

</section>

<?php include '../components/footer.php'; ?>
</body>
</html>