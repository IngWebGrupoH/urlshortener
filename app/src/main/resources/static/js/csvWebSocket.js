//var webSocket = SockJS('/');
function connect() {
    
}
function sleep (time) {
    return new Promise((resolve) => setTimeout(resolve, time));
  }
function send() {
  let messages=[];
  let images=[];
  if(($('#bc').is(':checked'))) {
    ws = new WebSocket("ws://localhost:8080/websocket/CSVUploadQR");
    ws.onclose = function() { alert("Server Disconnect You"); location.reload() }
    ws.onmessage = function(event) {
      if(event.data =="QRMessage"){
          var funtion=ws.onmessage;
          ws.onmessage = function(image){
            var count = 0
            var res = ""
            images.push(base64.encodestring(image))
            $("#result").empty()
            messages.forEach(element => {
              res= res + element + "\t"
              if(images.length>count){
                $("#result").html(
                  "<img id=ItemPreview src=data:image/png;base64,"+images[count]+">");
                $("#result").html("\n");
              }else{
                res = res +"\n"
              }
              count = count + 1
            });
        $("#result").html(res);
          };
          ws.onmessage = funtion
      }else{
        messages.push(event.data+"\t"+"Analyzing"+"\t");
        var count = 0
        var res = ""
        $("#result").empty()
        messages.forEach(element => {
          res= res + element + "\t"
          if(images.length>count){
            res = res +"<img id=ItemPreview src=data:image/png;base64,"+images[count]+"\n"+">"
          }else{
            res = res +"\n"
          }
          count = count + 1
        });
        $("#result").html(res);
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