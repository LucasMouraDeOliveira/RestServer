$('document').ready(function(){
	$('#postfile').on('submit', function (e) {
        e.preventDefault();
        var token = $('#token').val();
        var $form = $(this);
        var path = $('#path').val();
        $.ajax({
	        type: "POST",
		    url: '/rest/tp2/file/upload/'+path + token,
            contentType: false, // obligatoire pour de l'upload
            processData: false, // obligatoire pour de l'upload
            data: new FormData($form[0]),
            success: function (response) {
            	location.reload();
            },
            error: function(){
            	window.location.replace("/rest/tp2/user");
            }
        });
	});
	$('.renamefile').on('submit', function (e) {
        e.preventDefault();
        var token = $('#token').val();
        var $form = $( this );
        $.ajax({
            type: "PUT",
    	    url: '/rest/tp2/file/rename' + token,
    	    data: $form.serialize(),
            success: function (response) {
            	location.reload();
            },
            error: function(){
            	window.location.replace("/rest/tp2/user");
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
		    url: '/rest/tp2/file/mkdir/'+ path + "/" + filename + token,
            success: function (response) {
            	location.reload();
            },
            error: function(){
            	window.location.replace("/rest/tp2/user");
            }
        });
	});
	$('#connectuser').on('submit', function (e) {
		e.preventDefault();
        var $form = $( this );
        $.ajax({
	        type: "GET",
		    url: '/rest/tp2/user/connect',
    	    data: $form.serialize(),
            success: function (response,statut) {
            	window.location.replace("/rest/tp2/folder/?token="+response);
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
	    url: '/rest/tp2/file/'+ path + "/" + filename+ token,
        success: function (response) {
        	location.reload();
        },
        error: function(){
        	window.location.replace("/rest/tp2/user");
        }
    });
}

function showrename(i){
	$("#rename"+i).show();
	$("#"+i).hide();
}