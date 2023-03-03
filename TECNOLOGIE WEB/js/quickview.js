/*Autore: Martina Xhepa 
Matricola: 891242

Genera il contenuto di un certo prodotto dopo aver
mandato una richiesta ajax a ..php/getView.php
*/

$(document).ready(function(){
    const url = window.location.search;
    var pid = new URLSearchParams(url).get('pid');
    loadQuickView(pid);
});

function loadQuickView(pid){
    $.ajax({
        url: '../php/getView.php',
        type: 'POST',
        data: {
            pid: pid
        },
        dataType: 'json',
        success: function(response){
            if(response == "empty"){
                var mes = $('<p class="empty">Non è presente una quick view per il prodotto</p>');
                $('#view').append(mes);
            }else{
                printView(response);
            }
        },
        error: function(xhr, status, error){
            var mes = $('<p class="empty">Spiacenti, si è verificato un errore</p>');
            $('#view').append(mes);
            console.log(xhr);
            console.log(status);
            console.log(error);
          }
    });
}

function printView(json) {
    $('#view').empty();
    var headone = $('<h1 class="heading">quick view</h1>');
    json.prodotti.forEach(function (item) {

        var form = $('<form class="box"></form>');
        var divRow = $('<div class="row"></div>');
        var imgContainer = $('<div class="image-container"></div>');
        var imgMain = $('<div class="main-image"></div>');
        var img = $('<img src="../uploaded_img/' + item.image_01 + '"' + 'alt=""  id = "mainPhoto">');
        imgMain.append(img);
        var imgSub = $('<div class="sub-image"></div>');
        var img1 = $('<img src="../uploaded_img/' + item.image_01 + '"' + 'alt="" class = "subPhoto">');
        var img2 = $('<img src="../uploaded_img/' + item.image_02 + '"' + 'alt="" class = "subPhoto">');
        var img3 = $('<img src="../uploaded_img/' + item.image_03 + '"' + 'alt="" class = "subPhoto">');
        imgSub.append(img1, img2, img3);
        imgContainer.append(imgMain, imgSub);
        var content = $('<div class="content"></div>');
        var divName = $('<div class="name">'+item.name+'</div>');
        var divFlex = $('<div class="flex"></div>');
        var divPrice = $('<div class="price"><span>$</span>'+item.price+' <span>/-</span></div>');
        divFlex.append(divPrice);
        var divDetails = $('<div class="details">'+item.details+'</div>');
        content.append(divName, divFlex, divDetails);
        divRow.append(imgContainer, content);
        form.append(divRow);
        $('#view').append(headone, form);
    });


    $('.subPhoto').on('click', function(){
        var source = this.src;
        $('#mainPhoto').attr('src', source);
    });
}
