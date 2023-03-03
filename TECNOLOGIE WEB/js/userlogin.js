/*fa comunicare la pagina di 
login (../html/userlogin.php) con il server*/

/*dopo alcuni controlli sui dati inseriti, 
essi vengono mandati tramite POST a
../php/userlogin.php che si occupa di interrogare
il server*/

$(document).ready(function(){
  $("#response").hide();
  var p = ("<p></p>");
  $("#response").append(p);
  $("#response").children().css('color', '#f81f07');
  $("#response").css('background-color', '#e89799');

  $("#userLogin").click(function(){

    var email = $("#userEmail").val();
    var pwd = $("#userPassword").val();
      
    if(email == "" || pwd == "" || !email.match(/[^@\s]+@[^@\s]+\.[^@\s]+/) ){
      $("#response").children().text("I valori inseriti non hanno un formato valido");
      $("#response").show().fadeOut(2500);
    }else{
      $.ajax({
        url: '../php/userlogin.php',
        method: 'POST',
        data: {
          userLogin: 1,
          emailPhp: email, 
          passwordPhp: pwd
          },
        dataType: "json",
        success: function(response){
          if(response == "success"){
				    location.href = "../html/homepage.php";
			    }else{
            $("#response").children().text("Email o Password sbagliati");
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