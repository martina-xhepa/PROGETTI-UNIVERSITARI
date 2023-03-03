<?php

/* 
 *Autore: Martina Xhepa
 *Matricola: 891242 
 */

/*Si interroga il DB per verificare che le credenziali siano corrette*/

include '../components/connect.php';

session_start();

if(isset($_SESSION['loggedIn'])) {
    header('Location: ../html/homepage.php');
    exit();
}

if(isset($_POST['login'])) {
    $adminName = htmlspecialchars($_POST["adminPhp"]);
    $password = htmlspecialchars($_POST["passwordPhp"]);
    
    $submitted_pw_hash = md5($password);
    
    $stmt = $conn->prepare('SELECT id,name,password FROM admins WHERE name=?');
    $stmt -> execute([$adminName]);
    $row =$stmt ->fetch();
    if ($row && $submitted_pw_hash == $row['password']){
        $_SESSION['loggedIn']=1;
        $_SESSION['admin_name']=$row['name'];
        $_SESSION['admin_id']=$row['id'];
        echo json_encode("success");
    } else {
        echo json_encode("error");
    }

}

?>