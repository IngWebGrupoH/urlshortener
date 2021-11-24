//var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat");
var webSocket = new SockJS('/chat');

webSocket.onmessage = function (msg) { receievMsg(JSON.parse(msg.data)) }
webSocket.onclose = function() { alert("Server Disconnect You"); }
webSocket.onopen = function() {
    var name = "";
    while (name == "") name = prompt("Enter your name");
    sendMessage("join", name);
}

$("#fileInputForm").submit(function () {
    var formFile = new FormData();
    formFile.append('csv',$('#fileInput')[0].files[0]);
    sendMessage("uploadCSV", formFile);
});
function sendMessage(type, data) {
    if (data !== "") {
        webSocket.send(JSON.stringify({type: type, data: data}));
        $("#msg").val("");
        $("#msg").focus();
    }
}
function receievMsg(msg) {
    $("#CSVResult").append("<p>"+msg.data+"</p>");
}