//var webSocket = SockJS('/');
function sleep (time) {
    return new Promise((resolve) => setTimeout(resolve, time));
  }
function send() {
  $("#resultCSVQR").empty();
  $("#result").empty();
  $("#resultQr").empty();
  if(($('#bc').is(':checked'))) {
    ws = new WebSocket("ws://localhost:8080/websocket/CSVUpload");
    wsQR = new WebSocket("ws://localhost:8080/websocket/CSVUploadQR");
    ws.onclose = function() { console.log("CSV processed") }
    wsQR.onclose = function() { console.log("CSV_QR processed") }
    wsQR.binaryType = "arraybuffer";
    wsQR.onmessage = function(msg){
      console.log(msg)
      $("#resultCSVQR").append(
        "<p><img id=ItemPreview src=data:image/png;base64,"+msg.data+"></p>");
    }
    ws.onmessage = function(event) {
        var funtion=ws.onmessage;
        ws.onmessage = funtion
        $("#result").append("<p>"+event.data+"    "+'<img src="js/images/clock.png" width="60" height="60"></img>'+
                          "</p>");
    };
    sleep(500).then(() => {
        var reader = new FileReader();
        reader.onload = function(event) {
            console.log('File content:', event.target.result);
            ws.send(event.target.result);
            wsQR.send(event.target.result);
        };
        reader.readAsText($('#fileInput')[0].files[0]);
    });
  }else{
    ws = new WebSocket("ws://localhost:8080/websocket/CSVUpload");
    ws.onclose = function() { console.log("CSV processed") }
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
}
// public method for encoding an Uint8Array to base64
function encode (input) {
  var keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
  var output = "";
  var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
  var i = 0;

  while (i < input.length) {
      chr1 = input[i++];
      chr2 = i < input.length ? input[i++] : Number.NaN; // Not sure if the index 
      chr3 = i < input.length ? input[i++] : Number.NaN; // checks are needed here

      enc1 = chr1 >> 2;
      enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
      enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
      enc4 = chr3 & 63;

      if (isNaN(chr2)) {
          enc3 = enc4 = 64;
      } else if (isNaN(chr3)) {
          enc4 = 64;
      }
      output += keyStr.charAt(enc1) + keyStr.charAt(enc2) +
                keyStr.charAt(enc3) + keyStr.charAt(enc4);
  }
  return output;
}