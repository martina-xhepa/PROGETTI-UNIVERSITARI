<?php

/* Autore: Martina Xhepa 
Matricola: 891242 */


/* Pagina di Login utente */

session_start();

if(isset($_SESSION['userLoggedIn']) || isset($_SESSION['loggedIn'])){
    header('Location: ../html/homepage.php');
    exit();
};

?>
<!DOCTYPE html>
<html lang = "en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>userlogin</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css">
    <link rel="stylesheet" href="../css/userstyle.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
    <script src="../js/userlogin.js" type="text/javascript"></script>


</head>
    <body>

        <div class="form-container">
            <form method="post">
                <h3>login now!</h3>     
                <p>default username = <span>martina@ciao.it</span> & password = <span>12345678</span></p> 
                <div id="response"></div>
                <input class="box" type="email" id="userEmail" placeholder="enter your email" maxlength="100" />               
                <input class="box" type="password" id="userPassword" placeholder="enter your password"  maxlength="30"/>
                <input class="btn" type="button" id="userLogin" value="Log in" />     
                <p>Don't you have an account yet?</p>    
                <a href="../html/usersignup.php" class="option-btn">Sign up!</a> 
                <a href="../html/adminlogin.php" class="option-btn">I am an admin!</a>          
            </form>
        </div>
    </body>
</html>