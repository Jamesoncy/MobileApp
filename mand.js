var http = require('http');
var fs = require('fs');
var xmldom = require('xmldom').DOMParser;

// Loading the index file . html displayed to the client
var server = http.createServer(function(req, res) {
    fs.readFile('./index.html', 'utf-8', function(error, content) {
        res.writeHead(200, {"Content-Type": "text/html"});
        res.end(content);
    });
});

// Loading socket.io
var io = require('socket.io').listen(server);

// When a client connects, we note it in the console
io.sockets.on('connection', function (socket) {

             console.log("user connect");
             socket.on('chat message', function(msg){
                                var json = loadXMLDoc("book2.xml");
                              io.sockets.emit('message', json );
              });

});


 





 function loadXMLDoc(filePath) {
    
     var output, doc;
    var number;
    var item;
    try {
            fs.readFile(filePath, 'utf-8', function(error, content) {
                 doc = new xmldom().parseFromString(content,"application/xml");
                 item = doc.getElementsByTagName('item');
                 console.log(item.length);
                
            });
     } catch (ex) {console.log(ex)}

    return "dasda";
     
 }



server.listen(3000);