<?php

/*Autore: Martina Xhepa
  Matricola: 891242 */

//Logout utente
    session_start();

    if(isset($_SESSION['loggedIn'])){
        header('Location: ../php/adminlogout.php');
        exit();
    }

    session_unset();
    session_destroy();
    header('Location: ../html/userlogin.php');
    exit();
?>