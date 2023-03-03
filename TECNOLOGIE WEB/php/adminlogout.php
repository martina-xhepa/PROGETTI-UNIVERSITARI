<?php
/*Autore: Martina Xhepa
  Matricola: 891242 */

//Logout amministratore
    session_start();

    session_unset();
    session_destroy();
    header('Location: ../html/userlogin.php');
    exit();
?>