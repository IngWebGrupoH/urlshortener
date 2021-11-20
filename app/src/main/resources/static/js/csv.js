$(document).ready(
    function() {
        $("#fileInput").submit(
            function(event) {
                $.ajax({
                    type : "POST",
                    url : "/api/CSVUpload",
                    data : $(this).serialize(),
                    enctype : 'multipart/form-data',
                    success : function(msg, status, request) {
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + request.getResponseHeader('Location')
                            + "'>"
                            + request.getResponseHeader('Location')
                            + "</a></div>");
                    },
                    error : function() {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });