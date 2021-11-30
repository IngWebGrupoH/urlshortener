$(document).ready(
    function() {
        $("#qrForm").submit(
            function(event) {
                event.preventDefault();
                println("------------------------------LLego aqui --------------------")
                $.ajax({
                    type : "GET",
                    url : "/api/URLToQR",
                    data : $(this).serialize(),
                    success : function() {
                        $("#result").html(
                            "<img src=example01.png>");
                    },
                    error : function() {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });