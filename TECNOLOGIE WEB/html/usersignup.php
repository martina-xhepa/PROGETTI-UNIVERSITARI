<?php

/*Autore:Martina Xhepa
  Matricola: 891242*/

//Pagina registrazione utente

session_start();

if(isset($_SESSION['userLoggedIn']) || isset($_SESSION['loggedIn'])){
    header('Location: ../html/homepage.php');
    exit();
};

?>

<!DOCTYPE html>
<html lang="en">
<head>
   <meta charset="UTF-8">
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1.0">
   <title>usersignup</title>
   <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css">
   <link rel="stylesheet" href="../css/userstyle.css">

   <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
	<script src="../js/usersignup.js" type="text/javascript"></script>
</head>

<body>

<section class="form-container">

   <form method="post">
      <h3>sign up now</h3>
      <div id="response"></div>
      <input class = "box" type="text" id="username" placeholder="enter your username" maxlength="20"/>
      <input class = "box" type="email" id="email" placeholder="enter your email" maxlength="50"/>
      <input class = "box" type="password" id="password" placeholder="enter your password" maxlength="20"/>
      <input class = "btn" type="button" value="SignUp now!" id="signup">
      <p>already have an account?</p>
      <a href="../html/userlogin.php" class="option-btn">login now</a>
   </form>

</section>

</body>
</html>