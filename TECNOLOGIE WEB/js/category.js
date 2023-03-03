/*
Autore: Martina Xhepa
Matricola: 891242
 */

$(document).ready(function(){
    console.log("uela");
    const url = window.location.search;
    var category = new URLSearchParams(url).get('category');
    loadCategory(category);
});

function loadCategory(category){
    $.ajax({
        url: '../php/getCategory.php',
        type: 'POST',
        data: {
            category: category
        },
        dataType: 'json',
        success: function(response){
            if(response == "empty"){
                var mes = $('<p class="empty">no products added yet!</p>');
                $('#category').append(mes); 
            }else{
                printCategory(response);
            }
        },
        error:function(xhr, status, error){
            var mes = $('<p class="empty">Spiacenti, si è verificato un errore</p>');
            $('#category').append(mes);
            console.log(xhr);
            console.log(status);
            console.log(error);
          }
    });
}


function printCategory(json) {

    console.log("sciiiiiii");

    $('#category').empty();
    json.prodotti.forEach(function (item) {
        var form = $('<form class="box"></form>');
        var resp = $('<div class = response></div>');
        var pid = $('<input type="hidden" class="pid_s" name="pid" value ="">');
        pid.attr("value", item.id);
        var name = $('<input type="hidden" class="name_s" name="name"  value ="">');
        name.attr("value", item.name);
        var price = $('<input type="hidden" class="price_s" name="price"  value ="">');
        price.attr("value", item.price);
        var image_01 = $('<input type="hidden" class="image_01_s" name="image_01"  value ="">');
        image_01.attr("value", item.image_01);
        var button = $('<input type="button" class="fas fa-heart add_to_wishlist" name="add_to_wishlist" value ="&#10084;">');
        var view = $('<a href="" class="fas fa-eye"></a>');
        view.attr("href", "../html/view.php?pid="+ item.id);
        console.log(item.image_01);
        var img = $('<img src="../uploaded_img/' + item.image_01 + '"' + 'alt="">');
        var divName = $('<div class="name">'+item.name+'</div>');
        var divFlex = $('<div class="flex flex_s"></div>');
        var divPrice = $('<div class="price"><span>$</span>'+item.price+' <span>/-</span></div>');
        var qty = $('<input type="number" class="qty_s" name="qty" min="1" max="99" value="1">');
        divFlex.append(divPrice);
        divFlex.append(qty);
        var submit = $('<input type="button" value="add to cart" class="btn add_to_cart" name="add_to_cart">');
        form.append(pid, name, price, image_01, button, view, img, divName, divFlex, resp, submit);
        $('#category').append(form);   
    });

    
    $('.add_to_cart').on('click', function(){
        var resp = $(this).siblings('.response');
        resp.hide();
        var p = ("<p></p>");
        resp.append(p);
        resp.children().css('color', '#f81f07');
        resp.css('background-color', '#e89799');

        var name = $(this).siblings('.name_s').val();
        console.log(name);
        var pid = $(this).siblings('.pid_s').val();
        var price = $(this).siblings('.price_s').val();
        var image = $(this).siblings('.image_01_s').val();
        var qty = $(this).siblings('.flex_s').children('.qty_s').val();

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
            dataType: "json",
            success: function(response){
                if(response == "success"){
                    console.log("aggiuntooo");
                    resp.children().text("Aggiunto al carrello");
                    resp.show().fadeOut(2500);
                }else{
                    resp.children().text("Già presente");
                    resp.show().fadeOut(2500);
                }
                console.log(response);
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

    $('.add_to_wishlist').on('click', function(){
        var resp = $(this).siblings('.response');
        resp.hide();
        var p = ("<p></p>");
        resp.append(p);
        resp.children().css('color', '#f81f07');
        resp.css('background-color', '#e89799');

        var name = $(this).siblings('.name_s').val();
        console.log(name);
        var pid = $(this).siblings('.pid_s').val();
        var price = $(this).siblings('.price_s').val();
        var image = $(this).siblings('.image_01_s').val();

        $.ajax({
            url: '../php/addToWishlist.php',
            type: 'POST',
            data: {
                add_to_wishlist: 1, 
                namePhp: name, 
                pidPhp: pid, 
                pricePhp: price, 
                imagePhp: image
            },
            dataType: "json",
            success: function(response){
                if(response == "success"){
                    console.log("aggiuntooo");
                    resp.children().text("Aggiunto alla lista");
                    resp.show().fadeOut(2500);
                }else if(response=="cart"){
                    console.log("carrello");
                    resp.children().text("E' già nel carrello");
                    resp.show().fadeOut(2500);
                }else{
                    resp.children().text("Già nella lista");
                    resp.show().fadeOut(2500);
                    console.log("wishlist");
                }
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
