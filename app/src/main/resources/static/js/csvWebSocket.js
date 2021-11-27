//var webSocket = SockJS('/');
function connect() {
    
}
function sleep (time) {
    return new Promise((resolve) => setTimeout(resolve, time));
  }
function send() {
    ws = new WebSocket("ws://localhost:8080/websocket/CSVUpload");
    ws.onclose = function() { alert("Server Disconnect You"); location.reload() }
    ws.onmessage = function(event) {
      $("#result").append("<p>"+event.data+"</p>");

    };
    sleep(500).then(() => {
        var reader = new FileReader();
        $("#result").html("");
        reader.onload = function(event) {
            console.log('File content:', event.target.result);
            ws.send(event.target.result);
        };
        reader.readAsText($('#fileInput')[0].files[0]);
    });
    
}