/*Autore: Martina Xhepa
  Matricola: 891242 */

/*fa comunicare la pagina di 
signup (../html/usersignup.php) con il server*/

/*dopo alcuni controlli sui dati inseriti, 
essi vengono mandati tramite POST a
../php/usersignup.php che si occupa di interrogare
il server e inserire il nuovo utente*/

$(document).ready(function(){
    $("#response").hide();
    var p = ("<p></p>");
    $("#response").append(p);
    $("#response").children().css('color', '#f81f07');
    $("#response").css('background-color', '#e89799');

    $("#signup").click(function(){
        var name = $("#username").val();   
        var email = $("#email").val();
        var pwd = $("#password").val();
      
      if(email == "" || pwd == "" || name == ""){
        $("#response").children().text("I campi non possono essere vuoti");
        $("#response").show().fadeOut(2500);
      }else if(!email.match(/[^@\s]+@[^@\s]+\.[^@\s]+/)){ 
        $("#response").children().text("L'email non è valida");
        $("#response").show().fadeOut(2500);
      }else{
          $.ajax({
              url: '../php/usersignup.php',
              method: 'POST',
              data: {
                  signup: 1,
                  namePhp: name,
                  emailPhp: email, 
                  passwordPhp: pwd
              },
              dataType: "json",
                success: function(response){
                console.log(response);
                if(response == "success"){
                      console.log("success");
                      location.href = "../html/userlogin.php"; 
                }else if(response == "username"){
                  $("#response").children().text("Lo username esiste già");
                  $("#response").show().fadeOut(2500);
                }else {
                  $("#response").children().text("L'email è già registrata");
                  $("#response").show().fadeOut(2500);
                }
  
              },
              error: function(xhr, status, error){
                $("#response").children().text("Spiacenti, si è verificato un errore");
                $("#response").show().fadeOut(2500);
                console.log(xhr);
                console.log(status);
                console.log(error);
              }
    
    
          });
      }
      
    });
  }); 