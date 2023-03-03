<?php
/* 
 *Autore: Martina Xhepa
 *Matricola: 891242 
 */

/*Si interroga il DB per verificare che le credenziali siano corrette */

include '../components/connect.php';

session_start();

if(isset($_SESSION['userLoggedIn'])) {
    header('Location: ../html/homepage.php');
    exit();
}

if(isset($_POST['userLogin'])) {   
    $userEmail = htmlspecialchars($_POST["emailPhp"]);
    $password = htmlspecialchars($_POST["passwordPhp"]);
    $submitted_pw_hash = md5($password);

    $select_user = $conn->prepare('SELECT * FROM `users` WHERE email = ?');
    $select_user->execute([$userEmail]);
    /*$row = $select_user->fetch(PDO::FETCH_ASSOC);*/
    $row = $select_user->fetch();
    
    if ($row && $submitted_pw_hash == $row['password']){
        $_SESSION['userLoggedIn']=1;
        $_SESSION['user_name']=$row['name'];
        $_SESSION['user_id']=$row['id'];
        echo json_encode("success");
    } else {
        echo json_encode("error");
    }

}

?>