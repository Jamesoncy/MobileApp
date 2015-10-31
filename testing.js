var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var fs = require('fs');
var sel = [];
var text2 = "";
var json = "";


app.get('/', function(req, res){
  
  res.sendFile(__dirname + '/index.html');
});

io.on('connection', function(socket){

     
              socket.on('request', function(value,deviceId){
                
                      loadXml(value,deviceId,socket);
              });

              
});



    


function loadXml(val,deviceId,socket){

       sel = [];
       json = "";

       var str ="";
       var xml2js = require('xml2js');
       
        
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
                                var action = "";
        

                        
                                
                                         selectTop(send, function(err, val)
                                           { 
                                              
                                              actionButton(deviceId,val,function(err,data){
                                                  var not_selected = [];
                                                  var top = [];
                                                  var selected = [];
                                                  var linkLike = [];
                                                  var number = 0;
                                                  var index = 0;
                                                  var str = "";
                                                  var action = "";
                                                  for (value =0 ; value < val.length; value ++){
                                                      selected.push(val[value].item_id);
                                                  }
                                                   for (value =0 ; value < data.length; value ++){
                                                      linkLike.push(data[value]);
                                                  }
                                                 
                                               for (i=0 ; i < item.length; i++){
                                                     id = item[i].getElementsByTagName('id');
                                                     gId = id[0].childNodes[0].nodeValue;
                                                     index = selected.indexOf(gId);

                                                             link = item[i].getElementsByTagName('link');
                                                             gLink = link[0].childNodes[0].nodeValue;
                                                             title = item[i].getElementsByTagName('title');
                                                             gTitle = title[0].childNodes[0].nodeValue;
                                                             picture = item[i].getElementsByTagName('picture');
                                                             gPicture = picture[0].childNodes[0].nodeValue;
                                                             blurb = item[i].getElementsByTagName('blurb');
                                                             gBlurb = blurb[0].childNodes[0].nodeValue;
                                                             str = item[i].getAttribute("id");
                                                              if(linkLike.indexOf(gId) > -1 ){
                                                                  action = "Liked";
                                                              }
                                                              else{
                                                                  action = "Like";
                                                              }

                                                       str ="<item id = \""+str+"\">"+br+"<action>"+action+"</action>"+br+"<number>"+number+"</number>"+br+"<id>"+gId+"</id>"+br+"<title>"+gTitle+"</title>"+br+"<link>"+gLink+"</link><picture>"+gPicture+"</picture>"+br+"<blurb>"+gBlurb+"</blurb>"+br+"</item>"+br;
                                                          
                                                     if( index  > -1 ){       
                                                             number = val[index].num; 
                                                             top.push(str);
                                                       
                                                      }
                                                      else{
                                                        not_selected.push(str);
                                                      }   


                                                     
                                                  }

                                                  for(counter = 0 ; counter < top.length; top ++){

                                                      newXml += top[counter];

                                                  }


                                                  if(number.length != send){

                                                      for(counter = 0 ; counter < number.length)


                                                  }
                                               
                                                  newXml = newXml +br+"</feeds>";

                                                         

                                                            var parser = new xml2js.Parser();
                                                              parser.parseString(newXml.substring(0, newXml.length), function (err, result) {
                                                              json = JSON.stringify(result);
                                                              }); 
                                                                 
                                                                
                                                              socket.emit('request', json ); 





                                              });

                                               
                                                   
                                               
                                           });
                                    
                              
        
                                     
}




function selectTop(val, callback){

var mysql = require('mysql');
var connection = mysql.createConnection({
host : 'localhost',
user : 'root',
password : '',
database : 'test'
});

connection.connect();
connection.query('SELECT item_id,count(item_id) as num FROM `rss_feed_user` group by item_id order by num desc limit '+val, function(err, rows, fields) { 

if(err) {
connection.end();
return callback(err);

}

for (var i = 0; i < rows.length; i++) {
sel.push(rows[i]); 

}
connection.end();
callback(null, sel);
});

}


function actionButton(deviceId,data, callback){
var mysql = require('mysql');
var connection = mysql.createConnection({
host : 'localhost',
user : 'root',
password : '',
database : 'test'
});
var inCheck = "";
var like = [];
for(var i = 0 ; i < data.length; i++){
 
        inCheck +=  data[i].item_id+" ";
       
}



inCheck = inCheck.trim();
var replaced = inCheck.replace(/ /g, ',');

connection.connect();

connection.query('SELECT item_id FROM `rss_feed_user` where user_id = ? and item_id in ('+replaced+') ',[deviceId], function(err, rows, fields) { 

if(err) {
connection.end();
return callback(err);

}


for (var i = 0; i < rows.length; i++) {
like.push(rows[i].item_id); 

}

connection.end();
callback(null, like);
});


}



http.listen(3000, function(){
  console.log('listening on *:3000');
});
