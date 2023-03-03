<header class="header">

   <section class="flex">

      <a href="../html/homepage.php" class="logo">MatterHorn<span>.</span></a>

      <nav class="navbar">
         <a href="../html/homepage.php">home</a>
         <a href="../html/shop.php">shop</a>
         <a href = "../php/userlogout.php">Log out</a> 

         <?php
         if(isset($_SESSION['loggedIn'])){
            echo '<a  href="../html/admindashboard.php">AdminPanel</a>';
         }
         ?>
      </nav>


      <div class="icons">
         <div id="menu-btn" class="fas fa-bars"></div>
         <?php
         if(isset($_SESSION['userLoggedIn'])){
            echo '<a href="../html/wishlist.php"><i class="fas fa-heart"></i><span></span></a>
            <a href="../html/cart.php"><i class="fas fa-shopping-cart"></i><span></span></a>';
         }
         ?>
         <div id="user-btn" class="fas fa-user"></div>
      </div>

   </section>

</header>