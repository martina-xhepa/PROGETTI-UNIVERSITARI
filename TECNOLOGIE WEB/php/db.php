<?php

function connectionToDb(){
    try {
        $hostname = "localhost";
        $dbname = "shop";
        $user = "root";
        $pass = '';
        $options = [
            \PDO::ATTR_ERRMODE            => \PDO::ERRMODE_EXCEPTION,
            \PDO::ATTR_DEFAULT_FETCH_MODE => \PDO::FETCH_ASSOC,
            \PDO::ATTR_EMULATE_PREPARES   => false,
        ];
        $dns="mysql:host=$hostname;dbname=$dbname";
        $conn = new PDO ($dns, $user,$pass,$options);

        return $conn;

    } catch (PDOException $e) {
        throw new Exception("Error Processing Request", 1);
        die();
    }
}


function getProdotti(){
        try {
            $db = connectionToDb();
            $rows = $db->query("SELECT * FROM `products`");
        } catch (PDOException $error) {
            die('Database error: ' . $error->getMessage());
        }
        
        return $rows;
        
        $db = null;
}

    function getVista($pid){
        try {
            $db = connectionToDb();
            $pid=$db->quote($pid);
            $rows = $db->query("SELECT * FROM `products` WHERE id=$pid");
        } catch (PDOException $error) {
            die('Database error: ' . $error->getMessage());
        }
        
        return $rows;
        
        $db = null;
    }

    function getCategoria($category){
        try {
            $db = connectionToDb();
            $categoty=$db->quote($category);
            $rows = $db->query("SELECT * FROM `products` WHERE name LIKE '%{$category}%'");
        } catch (PDOException $error) {
            die('Database error: ' . $error->getMessage());
        }
        
        return $rows;
        
        $db = null;
    }

    function getListaDesideri($user_id){
        try {
            $db = connectionToDb();
            $user_id=$db->quote($user_id);
            $rows = $db->query("SELECT * FROM `wishlist` WHERE user_id = $user_id");
        } catch (PDOException $error) {
            die('Database error: ' . $error->getMessage());
        }
        
        return $rows;
        
        $db = null;
    }


    function getVetrina($name){
        try {
            $db = connectionToDb();
            $name=$db->quote($name);
            $rows = $db->query("SELECT * FROM `products` WHERE name=$name");
        } catch (PDOException $error) {
            die('Database error: ' . $error->getMessage());
        }
        
        return $rows;
        
        $db = null;
    }

    function getCarrelloUtente($user_id){
        try {
            $db = connectionToDb();
            $user_id=$db->quote($user_id);
            $rows = $db->query("DELETE FROM `cart` WHERE user_id = $user_id");
        } catch (PDOException $error) {
            die('Database error: ' . $error->getMessage());
        }
        
        return $rows;
        
        $db = null;
    }

    function getCarelloAggiornato($user_id, $id){
        try {
            $db = connectionToDb();
            $user_id=$db->quote($user_id);
            $id=$db->quote($id);
            $db->query("DELETE FROM `cart` WHERE id = $id");

            $rows = $db->query("SELECT * FROM `cart` WHERE user_id =$user_id");

        } catch (PDOException $error) {
            die('Database error: ' . $error->getMessage());
        }
        
        return $rows;
        
        $db = null;
    }

    function getListaAggiornata($user_id, $id){
        try {
            $db = connectionToDb();
            $user_id=$db->quote($user_id);
            $id=$db->quote($id);
            $db->query("DELETE FROM `wishlist` WHERE id = $id");

            $rows = $db->query("SELECT * FROM `wishlist` WHERE user_id =$user_id");

        } catch (PDOException $error) {
            die('Database error: ' . $error->getMessage());
        }
        
        return $rows;
        
        $db = null;
    }

    function getCarrello($user_id){
        try {
            $db = connectionToDb();
            $user_id=$db->quote($user_id);
            $rows = $db->query("SELECT * FROM `cart` WHERE user_id = $user_id");
        } catch (PDOException $error) {
            die('Database error: ' . $error->getMessage());
        }
        
        return $rows;
        
        $db = null;
    }

?>