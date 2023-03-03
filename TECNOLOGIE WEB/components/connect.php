<?php

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
} catch (PDOException $e) {
    throw new Exception("Error Processing Request", 1);
    die();
}

?>
