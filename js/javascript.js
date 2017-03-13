$('document').ready(function(){
	$('#postfile').on('submit', function (e) {
        e.preventDefault();
        var token = $('#token').val();
        var $form = $(this);
        var path = $('#path').val();
        $.ajax({
	        type: "POST",
		    url: '/file/upload' + token + "&path="+path,
            contentType: false, // obligatoire pour de l'upload
            processData: false, // obligatoire pour de l'upload
            data: new FormData($form[0]),
            success: function (response) {
            	location.reload();
            },
            error: function(){
            	window.location.replace("/user");
            }
        });
	});
	$('.renamefile').on('submit', function (e) {
        e.preventDefault();
        var token = $('#token').val();
        var $form = $( this );
        $.ajax({
            type: "PUT",
    	    url: '/file/rename' + token,
    	    data: $form.serialize(),
            success: function (response) {
            	location.reload();
            },
            error: function(){
            	window.location.replace("/user");
            }
        });
	});
	
	$('#createfile').on('submit', function (e) {
		e.preventDefault();
	    var token = $('#token').val();
        var filename = $("#mkdir").val();
        var path = $('#path').val();
        $.ajax({
	        type: "POST",
		    url: '/folder/mkdir' + token + "&path=" + (path == "" ? "" : path+"/") + filename,
            success: function (response) {
            	location.reload();
            },
            error: function(){
            	window.location.replace("/user");
            }
        });
	});
	$('#connectuser').on('submit', function (e) {
		e.preventDefault();
        var $form = $( this );
        $.ajax({
	        type: "POST",
		    url: '/user/connect',
    	    data: $form.serialize(),
            success: function (response,statut) {
            	window.location.replace("/folder?path=&token="+response);
            },
            error : function(){
            	location.reload();
            }
        });
	});
});

function supprime(filename){
    var token = $('#token').val();
    var path = $('#path').val();
	$.ajax({
        type: "DELETE",
	    url: '/file/' + token + "&path=" + (path == "" ? "" : path+"/") + filename,
        success: function (response) {
        	location.reload();
        },
        error: function(){
        	window.location.replace("/user");
        }
    });
}

function showrename(i){
	$("#rename"+i).show();
	$("#"+i).hide();
}