$(document).ready(function(){
    //STAMPA IL CARELLO
    reloadCart();
});

function reloadCart(){
    $.ajax({
        url: '../php/getUserCart.php',
        type: 'GET',
        dataType: 'json',
        success: function(response){
            if(response == "empty"){
                var mes = $('<p class="empty">Il tuo carrello è vuoto, corri a fare shopping!</p>');
                $('#cart-print').append(mes);
                var shop = $('<a href="../html/shop.php" class="option-btn">continue shopping</a>');
                $("#cart-total").append(shop);
            }else{
                printCart(response);
            }
        },
        error: function(xhr, status, error){
            var mes = $('<p class="empty">Spiacenti, si è verificato un errore</p>');
            $('#cart-print').append(mes);
            console.log(xhr);
            console.log(status);
            console.log(error);
          }
    });
}

function printCart(json) {
    var $total = 0;
    $('#cart-print').empty();
    json.prodotti.forEach(function (item) {
        var form = $('<form class="box"></form>');

        var cartId = $('<input type="hidden" class="cart_id" name="cart_id" value ="">');
        cartId.attr("value", item.id);
        var view = $('<a href="" class="fas fa-eye"></a>');
        view.attr("href", "../html/view.php?pid="+ item.pid);
        var img = $('<img src="../uploaded_img/' + item.image + '"' + 'alt="">');
        var divName = $('<div class="name">'+item.name+'</div>');
        var divFlex = $('<div class="flex"></div>');
        var divPrice = $('<div class="price"><span>$</span>'+item.price+' <span>/-</span></div>');
        var qty = $('<div>'+item.quantity+'</div>');
        divFlex.append(divPrice, qty);
        var subTotal = item.price * item.quantity;
        var sub = $('<div class="sub-total"> sub total : <span>$'+subTotal+'/-</span> </div>')
        var delItem = $('<input type="button" class="delete-btn  delete_one" name="delete" value="delete item">');
        form.append(cartId, view, img, divName, divFlex, sub, delItem);
        $('#cart-print').append(form); 
        $total = $total + subTotal;
        
    });

    var grandTotal = $('<p id="total">grand total : <span>$'+$total+'/-</span></p>');
    var shop = $('<a href="../html/shop.php" class="option-btn">continue shopping</a>');
    var delAll = $('<input type="button" class="delete-btn" name="delete_all" id ="delete_all" value="delete all item">');
    var checkout = $('<a href="../html/checkout.php" id="checkout" class="btn">proceed to checkout</a>');
    $("#cart-total").append(grandTotal, shop, delAll, checkout);


    $("#delete_all").click(function () {
        var msg = "Cart cleared successfully";
        $.ajax({
            url: '../php/clearCart.php',
            type: 'GET',
            data: {"msg" : msg},
            success: function(){
                $('#cart-print').empty();
                $("#cart-total").append(shop);
                var mes = $('<p class="empty">Il tuo carrello è vuoto, corri a fare shopping!</p>');
                $('#cart-print').append(mes);
                var grandTotal = $('<p id="total">grand total : <span>$0/-</span></p>');
                var shop = $('<a href="../html/shop.php" class="option-btn">continue shopping</a>');
                $("#cart-total").append(shop);
                console.log("lessssssgo");

            },
            error:function(xhr, status, error){
                var mes = $('<p class="empty">Spiacenti, si è verificato un errore</p>');
                $('#cart-total').append(mes);
                console.log(xhr);
                console.log(status);
                console.log(error);
              }
        });
    });

    $(".delete_one").on('click', function () {
        var msg = "Product deleted from cart";
        var id = $(this).siblings('.cart_id').val();
        console.log("=========");
        console.log(id);
        $.ajax({
            url: '../php/deleteProduct.php',
            type: 'POST',
            data: {
                msg : msg, 
                idPhp: id, 
                listOrCart: 1
            },
            dataType: "json",
            success: function(response){
                if(response == "empty" || response == "success"){

                    console.log("sparisci");
                    console.log(response);
                }else{
                    console.log("deletee");
                    console.log(response);

                }

                location.href = "../html/cart.php";
                console.log("sparisci");

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
