//var webSocket = SockJS('/');
function sleep(time) {
    return new Promise((resolve) => setTimeout(resolve, time));
}

function send() {
    $("#resultCSVQR").empty();
    $("#result").empty();
    $("#resultStatus").empty();
    $("#resultSimple").empty();
    $("#resultQr").empty();
    $("#resultCSVStatus").empty();

    if (($('#bc').is(':checked'))) {
        ws = new WebSocket("ws://localhost:8080/websocket/CSVUpload");
        wsQR = new WebSocket("ws://localhost:8080/websocket/CSVUploadQR");
        wsStatus = new WebSocket("ws://localhost:8080/websocket/CSVUploadStatus");
        ws.onclose = function() { console.log("CSV processed") }
        wsQR.onclose = function() { console.log("CSV_QR processed") }
        wsStatus.onclose = function() { console.log("CSV_Status processed") }
        wsStatus.onmessage = function(msg) {
            console.log(msg.data)
            if (msg.data == "seguro") {

                $("#resultCSVStatus").append('<p><img src="js/images/check_g.webp" width="60" height="60"></img></p>');
            } else if (msg.data == "no seguro") {

                $("#resultCSVStatus").append('<p><img src="js/images/check_r.webp" width="60" height="60"></img></p>');
            }
        }
        wsQR.binaryType = "arraybuffer";
        wsQR.onmessage = function(msg) {
            console.log(msg)
            console.log("<p>" + '<img src=data:image/png;base64,' + msg.data + '></img>' +
                "</p>");
            $("#resultCSVQR").append("<p>" + '<img src=data:image/png;base64,' + msg.data + ' width="60" height="60"></img>' +
                "</p>");
        }
        ws.onmessage = function(event) {
            var funtion = ws.onmessage;
            ws.onmessage = funtion
            $("#result").append("<br></br><p>" + event.data + '</p>');
        };
        sleep(500).then(() => {
            var reader = new FileReader();
            reader.onload = function(event) {
                console.log('File content:', event.target.result);
                ws.send(event.target.result);
                wsQR.send(event.target.result);
                wsStatus.send(event.target.result);

            };
            reader.readAsText($('#fileInput')[0].files[0]);
        });

    } else {
        ws = new WebSocket("ws://localhost:8080/websocket/CSVUpload");
        ws.onclose = function() { console.log("CSV processed") }
        ws.onmessage = function(event) {
            $("#result").append("<br></br><p>" + event.data + '</p>');

        };
        wsStatus = new WebSocket("ws://localhost:8080/websocket/CSVUploadStatus");
        wsStatus.onclose = function() { console.log("CSV_Status processed") }
        wsStatus.onmessage = function(msg) {
            console.log(msg.data)
            if (msg.data == "seguro") {

                $("#resultCSVStatus").append("<p>" + '<img src="js/images/check_g.webp" width="60" height="60"></img>' +
                    "</p>");
            } else if (msg.data == "no seguro") {

                $("#resultCSVStatus").append("<p>" + '<img src="js/images/check_r.webp" width="60" height="60"></img>' +
                    "</p>");
            }
        }
        sleep(500).then(() => {
            var reader = new FileReader();
            reader.onload = function(event) {
                console.log('File content:', event.target.result);
                ws.send(event.target.result);
                wsStatus.send(event.target.result);
            };
            reader.readAsText($('#fileInput')[0].files[0]);
        });
    }
}