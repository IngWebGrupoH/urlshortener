$(document).ready(
    function() {
        $("#fileInputForm").submit(
            function(event) {
                event.preventDefault();
                var formFile = new FormData();
                formFile.append('csv',$('#fileInput')[0].files[0]);
                $.ajax({
                    type : "POST",
                    url : "/api/CSVUpload",
                    data : formFile,
                    enctype : 'multipart/form-data',
                    contentType: false,
                    processData: false,
                    success : function(msg, status, request) {
                        //Convert from arraylist to file
                        //https://stackoverflow.com/questions/14964035/how-to-export-javascript-array-info-to-csv-on-client-side
                        let csvContent = "data:text/csv;charset=utf-8,";
                        var arraylist=msg.data.substring(1,msg.data.length-1).split(",");
                        for(var i = 0; i < arraylist.length; i++) {
                            csvContent=csvContent + arraylist[i]+"\r\n";
                        }
                        var encodedUri = encodeURI(csvContent);
                        var link = document.createElement("a");
                        link.setAttribute("href", encodedUri);
                        link.setAttribute("download", "shorterUrls.csv");
                        document.body.appendChild(link); // Required for FF

                        link.click(); // This will download the data file named "my_data.csv".
                    },
                    error : function() {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });