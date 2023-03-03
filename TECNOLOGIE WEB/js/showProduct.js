$(document).ready(function(){
    //STAMPA IL CARELLO
    showProducts();
});

function showProducts(){
    $.ajax({
        url: '../php/getProduct.php',
        type: 'GET',
        dataType: 'json',
        success: function(response){
            if(response == "empty"){
                var mes = $('<p class="empty">Il tuo carrello è vuoto, corri a fare shopping!</p>');
                $('#response').append(mes);
            }else{
                printShop(response);
            }
        },
        error: function(xhr, status, error){
            var mes = $('<p class="empty">Spiacenti, si è verificato un errore</p>');
            $('#response').append(mes);
            console.log(xhr);
            console.log(status);
            console.log(error);
          }
    });
}

function printShop(json) {
    $('#show').empty();
    json.prodotti.forEach(function (item) {
        var box = $('<form class="box"></form>');
        var img = $('<img src="../uploaded_img/' + item.image_01 + '"' + 'alt="">');
        var prodId = $('<input type="hidden" class="prod_id" name="prod_id" value ="">');
        prodId.attr("value", item.id);
        var divName = $('<div class="name">'+item.name+'</div>');
        var divPrice = $('<div class="price"><span>$</span>'+item.price+' <span>/-</span></div>');
        var divDetails = $('<div class="details">'+item.details+'</div>');
        var divFlex = $('<div class="flex-btn"></div>');
        var delItem = $('<input type="button" class="delete-btn  delete_one" name="delete" value="delete">');
        divFlex.append(delItem);
        box.append(prodId, img, divName, divPrice, divDetails, divFlex);
        $('#show').append(box);
        
    });

    $(".delete_one").on('click', function () {
        var msg = "Product deleted from cart";
        var id = $(this).parent().siblings('.prod_id').val();
        console.log("=========");
        console.log(id);
        $.ajax({
            url: '../php/deleteProductFromShop.php',
            type: 'POST',
            data: {
                msg : msg, 
                idPhp: id,
                delete: 1 
            },
            dataType: "json",
            success: function(response){
                location.href = "../html/products.php";
            }, 
            error:function(xhr, status, error, response){
                var mes = $('<p class="empty">Spiacenti, si è verificato un errore</p>');
                $(this).parent().append(mes);
                console.log(xhr);
                console.log(status);
                console.log(error);
                console.log(response);
              }
        });
    });
}
