var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var fs = require('fs');


app.get('/', function(req, res){
  
  res.sendFile(__dirname + '/index.html');
});

io.on('connection', function(socket){

     
              socket.on('request', function(value){
                 //    console.log(value);
                     socket.emit('request', loadXml(value) ); 
              });

              
});



    


function loadXml(val){


       var str ="";
       var xml2js = require('xml2js');
       var json = "";
        
                            var data,i,link,blurb,picture,title;
                            var br = "\r\n";
                            var newXml = "<feeds>";

                            var xmldoc = fs.readFileSync('book2.xml', 'utf8');
                            var DOMParser = require('xmldom').DOMParser;
                            var gLink;
                            var gTitle;
                            var gBlurb;
                            var gPicture,jsonText,id,gId;
                            var doc = new DOMParser().parseFromString(
                                xmldoc
                                ,'text/xml');

                                item = doc.getElementsByTagName('item');
                                var count = item.length;
                                var send = parseInt(val) + 3;
                                if(send >= count){
                                  send = count;
                                }
                                var values =[];
                                values = selectTop(send);
                                 console.log(values.length);
                                   for (i=0 ; i < send; i++){

                                       id = item[i].getElementsByTagName('id');
                                       gId = id[0].childNodes[0].nodeValue;
                                       link = item[i].getElementsByTagName('link');
                                       gLink = link[0].childNodes[0].nodeValue;
                                       title = item[i].getElementsByTagName('title');
                                       gTitle = title[0].childNodes[0].nodeValue;
                                       picture = item[i].getElementsByTagName('picture');
                                       gPicture = picture[0].childNodes[0].nodeValue;
                                       blurb = item[i].getElementsByTagName('blurb');
                                       gBlurb = blurb[0].childNodes[0].nodeValue;
                                       str = item[i].getAttribute("id");
                                       newXml +="<item id = \""+str+"\">"+br+"<id>"+gId+"</id>"+br+"<title>"+gTitle+"</title>"+br+"<link>"+gLink+"</link><picture>"+gPicture+"</picture>"+br+"<blurb>"+gBlurb+"</blurb>"+br+"</item>"+br;
                                      
                                    }
                                  /*  var i = 0;
                                    while( i < send ){
                                      id = item[i].getElementsByTagName('id');
                                       if(array.indexOf(id) != -1){
                                        
                                       gId = id[0].childNodes[0].nodeValue;
                                       link = item[i].getElementsByTagName('link');
                                       gLink = link[0].childNodes[0].nodeValue;
                                       title = item[i].getElementsByTagName('title');
                                       gTitle = title[0].childNodes[0].nodeValue;
                                       picture = item[i].getElementsByTagName('picture');
                                       gPicture = picture[0].childNodes[0].nodeValue;
                                       blurb = item[i].getElementsByTagName('blurb');
                                       gBlurb = blurb[0].childNodes[0].nodeValue;
                                       str = item[i].getAttribute("id");
                                       newXml +="<item id = \""+str+"\">"+br+"<id>"+gId+"</id>"+br+"<title>"+gTitle+"</title>"+br+"<link>"+gLink+"</link><picture>"+gPicture+"</picture>"+br+"<blurb>"+gBlurb+"</blurb>"+br+"</item>"+br;
                                        i++;
                                        }

                                    }*/
                                    newXml = newXml +br+"</feeds>";
                                   var parser = new xml2js.Parser();
                                      parser.parseString(newXml.substring(0, newXml.length), function (err, result) {
                                      json = JSON.stringify(result);
                                      }); 
                                   
                            return json;

}


function selectTop(val){
var sel = [];
var mysql      = require('mysql');
var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : '',
  database : 'test'
});

connection.connect();

connection.query('SELECT item_id,count(item_id) as num FROM `rss_feed_user` group by item_id order by num desc limit '+val, function(err, rows, fields) {  

    for (var i = 0; i < rows.length; i++) {
      sel[i] =rows[i].item_id; 
    }
   
});

     connection.end();
    return sel;
}


http.listen(3000, function(){
  console.log('listening on *:3000');
});