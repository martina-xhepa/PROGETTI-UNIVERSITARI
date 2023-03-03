/*Autore: Martina Xhepa
  Matricola: 891242 */

$(document).ready(function(){
    console.log("uela");
    loadWishList();
});

function loadWishList(){
    $.ajax({
        url: '../php/getUserWishlist.php',
        type: 'GET',
        dataType: 'json',
        success: function(response){
            if(response == "empty"){
                var mes = $('<p class="empty">La tua wishList è vuota, corri a fare shopping!</p>');
                $('#wish').append(mes);
            }else{
                printWishList(response);
            }
        },
        error: function(xhr, status, error){
            var mes = $('<p class="empty">Spiacenti, si è verificato un errore</p>');
            $('#wish').append(mes);
            console.log(xhr);
            console.log(status);
            console.log(error);
          }
    });
}


function printWishList(json) {
    var $total = 0;
    $('#wish').empty();
    json.prodotti.forEach(function (item) {
        console.log(item.price);
        console.log(item.name);
        console.log(item.pid);
        var form = $('<form class="box"></form>');
        var wishId = $('<input type="hidden" class="wishlist_id" name="wishlist_id" value ="">');
        wishId.attr("value", item.id);
        var pid = $('<input type="hidden" class="pid_s" name="pid" value ="">');
        pid.attr("value", item.pid);
        var name = $('<input type="hidden" class="name_s" name="name"  value ="">');
        name.attr("value", item.name);
        var price = $('<input type="hidden" class="price_s" name="price"  value ="">');
        price.attr("value", item.price);
        var image_01 = $('<input type="hidden" class="image_01_s" name="image_01"  value ="">');
        image_01.attr("value", item.image);
        var view = $('<a href="" class="fas fa-eye"></a>');
        view.attr("href", "../html/view.php?pid="+ item.pid);
        console.log(item.image);
        var img = $('<img src="../uploaded_img/' + item.image + '"' + 'alt="">');
        var divName = $('<div class="name">'+item.name+'</div>');
        var divFlex = $('<div class="flex flex_s"></div>');
        var divPrice = $('<div class="price"><span>$</span>'+item.price+' <span>/-</span></div>');
        var qty = $('<input type="number" class="qty_s" name="qty" min="1" max="99" value="1">');
        divFlex.append(divPrice);
        divFlex.append(qty);
        var submit = $('<input type="button" value="add to cart" class="btn add_to_cart" name="add_to_cart">');
        var delItem = $('<input type="button" class="delete-btn  delete_one" name="delete" value="delete item">');
        form.append(wishId, pid, name, price, image_01, view, img, divName, divFlex, submit, delItem);
        $('#wish').append(form); 

        $total = $total + item.price;
        
    });

    var par = $('<p>grand total : <span>$'+$total+'/-</span></p>');
    $('#total').prepend(par); 


    $(".delete_one").on('click', function () {
        var msg = "Product deleted from cart";
        var id = $(this).siblings('.wishlist_id').val();
        console.log("=========");
        console.log(id);
        $.ajax({
            url: '../php/deleteProduct.php',
            type: 'POST',
            data: {
                msg : msg, 
                idPhp: id,
                listOrCart: 0
            },
            dataType: 'json',
            success: function(response){
                if(response == "empty" || response == "success"){
                console.log(response);
                }else{
                    console.log("deletee");
                    console.log(response);

                }

                location.href = "../html/wishlist.php";
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


    $('.add_to_cart').on('click', function(){
        var name = $(this).siblings('.name_s').val();
        console.log(name);
        var pid = $(this).siblings('.pid_s').val();
        var price = $(this).siblings('.price_s').val();
        var image = $(this).siblings('.image_01_s').val();
        var qty = $(this).siblings('.flex_s').children('.qty_s').val();
        console.log(qty);

        $.ajax({
            url: '../php/addToCart.php',
            type: 'POST',
            data: {
                add_to_cart: 1, 
                namePhp: name, 
                pidPhp: pid, 
                pricePhp: price, 
                imagePhp: image, 
                qtyPhp: qty
            },
            dataType: 'json',
            success: function(response){
                location.href = "../html/wishlist.php";
            },
            error:function(xhr, status, error){
                var mes = $('<p class="empty">Spiacenti, si è verificato un errore</p>');
                $(this).parent().append(mes);
                console.log(xhr);
                console.log(status);
                console.log(error);
              }
        });
        
    });
    
}
