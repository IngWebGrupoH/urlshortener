//var webSocket = SockJS('/');
var nElement = 0;
var nImage = 0;
function connect() {
    
}
function sleep (time) {
    return new Promise((resolve) => setTimeout(resolve, time));
  }
function send() {
  let result=[];
  let messages=[];
  let images=[];
  if(($('#bc').is(':checked'))) {
    ws = new WebSocket("ws://localhost:8080/websocket/CSVUploadQR");
    ws.onclose = function() { alert("Server Disconnect You"); location.reload() }
    ws.onmessage = function(event) {
      if(event.data =="QRMessage"){
          var funtion=ws.onmessage;
          ws.onmessage = function(image){
            images.push(image);
            ws.onmessage = funtion;
            result.forEach(element => {
              $("#result").clear();
              $("#result").append(result)
              $("#result").append("<img id=ItemPreview src=data:image/png;base64,"+element+">");
              nImage = nImage +1;
            });
            $("#result").html("<p>"+result+"</p>");
          };
      }else{
        messages.push(event.data+"\t"+"Analyzing"+"\t");
        result.forEach(element => {
          $("#result").clear();
          $("#result").append(result)
          $("#result").append("<img id=ItemPreview src=data:image/png;base64,"+element+">");
          nImage = nImage +1;
        });
        $("#result").html("<p>"+result+"</p>");
        nElement = nElement + 1;
      }

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
  }else{
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
    
    
}