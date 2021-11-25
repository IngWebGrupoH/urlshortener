//var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat");
var webSocket = SockJS('/api');

webSocket.onmessage = function (msg) { receievMsg(JSON.parse(msg.data)) }
webSocket.onclose = function() { alert("Server Disconnect You"); location.reload() }
$(document).ready(
    $("#fileInputForm").submit(function () {
        var formFile = new FormData();
        formFile.append('csv',$('#fileInput')[0].files[0]);
        sendMessage("uploadCSV", formFile);
        window.alert($('#fileInput')[0].files[0].name)
    })
);
function sendMessage(type, data) {
    if (data !== "") {
        webSocket.send(JSON.stringify({type: type, data: data}));
    }
}
function receievMsg(msg) {
    $("#CSVResult").append("<p>"+msg.data+"</p>");
}