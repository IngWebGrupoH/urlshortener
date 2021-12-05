$(document).ready(
    function() {
        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                if(($('#bc').is(':checked'))) {
                    $("#resultCSVQR").empty();
                    $("#result").empty();
                    $("#resultQr").empty();
                    $.ajax({
                        type : "GET",
                        url : "/api/URLToQR",
                        data : $(this).serialize(),
                        success : function(image, request) {
                            $("#resultQr").html(
                                "<img id=ItemPreview src=data:image/png;base64,"+image+">");
                        },
                        error : function(e) {
                            $("#resultQr").html(
                                "<div class='alert alert-danger lead'>ERROR</div>");
                        }
                    });
                }else{
                    $("#resultQr").html(
                        "");
                    
                }
            });
    });