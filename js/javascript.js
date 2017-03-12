$('document').ready(function(){
	$('#postfile').on('submit', function (e) {
        e.preventDefault();
        
        var $form = $(this);
        var path = $('#path').val();
        $.ajax({
	        type: "POST",
		    url: '/rest/tp2/file/upload/'+path,
            contentType: false, // obligatoire pour de l'upload
            processData: false, // obligatoire pour de l'upload
            data: new FormData($form[0]),
            success: function (response) {
                // La réponse du serveur
            }
        });
	})
});