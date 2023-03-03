/*fa comunicare la pagina di 
login (../html/adminlogin.php) con il server*/

/*dopo alcuni controlli sui dati inseriti, 
essi vengono mandati tramite POST a
../php/adminlogin.php che si occupa di interrogare
il server*/

$(document).ready(function(){
    $("#response").hide();
    var p = ("<p></p>");
    $("#response").append(p);
    $("#response").children().css('color', '#f81f07');
    $("#response").css('background-color', '#e89799');
    
    $("#login").click(function(){
  
        var admin = $("#adminName").val();
        var pwd = $("#password").val();
      
      if(admin == "" || pwd == ""){
        $("#response").children().text("I campi non possono essere nulli");
        $("#response").show().fadeOut(2500);
      }else{
          $.ajax({
              url: '../php/adminlogin.php',
              method: 'POST',
              data: {
                  login: 1,
                  adminPhp: admin, 
                  passwordPhp: pwd
              },
              dataType: "json",
              success: function(response){
                console.log(response);
                if(response == "success"){
                  console.log("success");
                  location.href = "../html/homepage.php";
                }else{
                  $("#response").children().text("Ricontrolla le tue credenziali!");
                  $("#response").show().fadeOut(2500);
                }
  
              },
              error: function(xhr, status, error){
                $("#response").children().text("Spiacenti, qualcosa è andato storto");
                $("#response").show().fadeOut(2500);
                console.log(xhr);
                console.log(status);
                console.log(error);
              }
    
    
          });
      }
      
    });
  }); 