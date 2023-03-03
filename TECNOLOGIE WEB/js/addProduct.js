$(document).ready(function(){
    $("#response").hide();
    var p = ("<p></p>");
    $("#response").append(p);
    $("#response").children().css('color', '#f81f07');
    $("#response").css('background-color', '#e89799');
  
      $("#add_product").click(function(){
  
        let firstImage = $('#image_01').prop('files')[0];
        console.log(firstImage);
  
        let secondImage = $('#image_02').prop('files')[0];
        console.log(secondImage);
  
        let thirdImage = $('#image_03').prop('files')[0];
        console.log(thirdImage);

        var file = $('#image_01')[0].files[0];
        var nameIm = file.name;
  
          console.log(file.name);
          console.log(nameIm);
        
  
        var name = $("#name").val();
        var price = $("#price").val();
        var details = $("#details").val();
        var add_product = 1;
        console.log(name);
        console.log(price);
        console.log(details);
        console.log(add_product);
  
         var formData = new FormData();
         formData.append('image_01', firstImage);
         formData.append('image_02', secondImage);
         formData.append('image_03', thirdImage);
         formData.append('name', name);
         formData.append('price', price);
         formData.append('details', details);
         formData.append('add_product', add_product);
  
        if(name == "" || price == "" || details == ""){
          $("#response").children().text("Inputs cannot be empty");
          $("#response").show().fadeOut(2500);
        }else{
              $.ajax({
              url: "../php/addProduct.php",
              type: "POST",
              data:  formData,
              contentType: false,
                    cache: false,
              processData:false,
                success: function (response) {
                  if(response == 'success') {
                    $("#response").children().text("Product added succesfully");
                    $("#response").show().fadeOut(2500);
                    $("#response").children().css('color', '#3ce78c');
                    $("#response").css('background-color', '#0c5212');


                    var box = $('<div class="box"></div>');
                    var img = $('<img src="../uploaded_img/'+nameIm+'"' + 'alt="">');
                    console.log(img);
                    var divName = $('<div class="name">'+name+'</div>');
                    var divPrice = $('<div class="price"><span>$</span>'+price+' <span>/-</span></div>');
                    var divDetails = $('<div class="details"><span>'+details+'</span></div>');
                    var divFlex = $('<div class="flex-btn"></div>');
                    var delBtn = $('<a href="products.php?delete='+response+'" class="delete-btn">delete</a>');
                    divFlex.append(delBtn);
                    box.append(img, divName, divPrice, divDetails, divFlex);
                    $('#show').prepend(box); 


                    console.log(response);

                    


                } else {
                  $("#response").children().text("Something went wrong");
                  $("#response").show().fadeOut(2500);
                }
              },
                error: function(xhr, status, error){
                  console.log(xhr);
                  console.log(status);
                  console.log(error);
                }
      
      
            });
        }
        
        
      });
    }); 
  
    