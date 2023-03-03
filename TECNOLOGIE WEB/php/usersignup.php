<?php

/* 
 *Autore: Martina Xhepa
 *Matricola: 891242 
 */

/*Si interroga il DB per verificare che le credenziali siano accettabili
e si inserisce il nuovo utente*/

include '../components/connect.php';

session_start();

if(isset($_SESSION['userLoggedIn'])) {
    header('Location: ../html/homepage.php');
    exit();
}

if(isset($_POST['signup'])) {
    $userEmail = htmlspecialchars($_POST["emailPhp"]);
    $password = htmlspecialchars($_POST["passwordPhp"]);
    $userName = htmlspecialchars($_POST["namePhp"]);
    $submitted_pw_hash = md5($password);

    $select_user = $conn->prepare('SELECT * FROM `users` WHERE email = ?');
    $select_user->execute([$userEmail]);
    /*$row = $select_user->fetch(PDO::FETCH_ASSOC);*/
    $row = $select_user->fetch();

    if($select_user->rowCount()>0){
        echo json_encode("email");
    }else{
        $select_use = $conn->prepare('SELECT * FROM `users` WHERE name = ?');
        $select_use->execute([$userName]);
        /*$row = $select_user->fetch(PDO::FETCH_ASSOC);*/
        $ro = $select_use->fetch();
        if($select_use->rowCount()>0){
            echo json_encode("username");
        }else{
            $insert_user = $conn->prepare("INSERT INTO `users`(name, email, password) VALUES(?,?,?)");
            $insert_user->execute([$userName, $userEmail, $submitted_pw_hash]);
            echo json_encode("success");

        }

    }
    

}

?>