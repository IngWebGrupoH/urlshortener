$(document).ready(
    function() {
        $("#resultCSVQR").empty();
        $("#result").empty();
        $("#resultStatus").empty();
        $("#resultQr").empty();
        $("#resultCSVStatus").empty();
        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                $.ajax({
                    type: "POST",
                    url: "/api/link",
                    data: $(this).serialize(),
                    success: function(msg, status, request) {
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='" +
                            request.getResponseHeader('Location') +
                            "'>" +
                            request.getResponseHeader('Location') +
                            "</a></div>");
                        if (msg.seguro) {

                            $("#resultStatus").html('<p><img src="js/images/check_g.webp" width="60" height="60"></img></p>');
                        } else {

                            $("#resultStatus").html("<p>" + '<img src="js/images/check_r.webp" width="60" height="60"></img>' +
                                "</p>");
                        }

                    },
                    error: function() {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });

            });
    }

);