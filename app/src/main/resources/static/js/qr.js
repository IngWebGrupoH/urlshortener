$(document).ready(
    function() {
        $("#qrForm").submit(
            function(event) {
                event.preventDefault();
                $.ajax({
                    type : "GET",
                    url : "/api/URLToQR",
                    data : $(this).serialize(),
                    success : function(image, request) {
                        $("#result").html(
                            "<img id=ItemPreview src=data:image/png;base64,"+image+">");
                    },
                    error : function(e) {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>"+JSON.stringify(e)+"</div>");
                    }
                });
            });
    });