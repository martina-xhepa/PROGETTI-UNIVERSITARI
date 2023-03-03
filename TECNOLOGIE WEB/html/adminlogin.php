<?php

/*Autore: Martina Xhepa
Matricola: 891242 */

//Pagina Login Amministratore

session_start();

if(isset($_SESSION['loggedIn'])){
    header('Location: ../html/homepage.php');
    exit();
};

?>

<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>adminlogin</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css">
    <link rel="stylesheet" href="../css/adminstyle.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
    <script src="../js/adminlogin.js" type="text/javascript"></script>
</head>
    <body>
        <div class="form-container">

            <form method="post">
                <h3>Admin login!</h3>     
                <p>default username = <span>admin</span> & password = <span>ciao</span></p> 
                <div id="response"></div>
                <input class="box" type="text" id="adminName" placeholder="enter your admin username" maxlength="30" />               
                <input class="box" type="password" id="password" placeholder="enter your password"  maxlength="30"/>
                <input class="btn" type="button" id="login" value="Log in" />                   
            </form>
        </div>
    </body>
</html>