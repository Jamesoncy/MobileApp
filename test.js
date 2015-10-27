var http = require('http');
var fs = require('fs');
var XMLPath = "book.xml";

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
            var rawJSON = loadXMLDoc(XMLPath);
             io.socket.emit('message', rawJSON);
         });


    
     
   
 //socket.emit('message', "test");
	

    setInterval(function() {
            var json = loadXMLDoc(XMLPath);
              io.sockets.emit('message', json );
            }, 5000 );
    });

 function loadXMLDoc(filePath) {
        var fs = require('fs');
        var xml2js = require('xml2js');
        var json;
        try {
            var fileData = fs.readFileSync(filePath, 'ascii');

            var parser = new xml2js.Parser();
            parser.parseString(fileData.substring(0, fileData.length), function (err, result) {
            json = JSON.stringify(result);
            
        });
 
        res.end(json);
        
    } catch (ex) {console.log(ex)}

    return json;
 }



server.listen(3000);