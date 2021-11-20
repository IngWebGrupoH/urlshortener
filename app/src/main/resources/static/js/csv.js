$(document).ready(
    function() {
        $("#fileInputForm").submit(
            function(event) {
                event.preventDefault();
                var formFile = new FormData();
                formFile.append('csv',$( '#fileInput' )[0].files[0]);
                $.ajax({
                    type : "POST",
                    url : "/api/CSVUpload",
                    data : formFile,
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