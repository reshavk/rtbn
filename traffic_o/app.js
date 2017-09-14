
var express = require('express');
var bodyParser = require('body-parser');
var path = require('path');
var app = express();
var firebase = require("firebase");
var Promises = require("bluebird");
var fs = require('fs');
var https = require('https');

var options = {
 key: fs.readFileSync('key.pem'),
 cert: fs.readFileSync('cert.pem')
};

var recv_data;
var config = {
apiKey: "AIzaSyAPQTbXzEk-06VADNsxSQ6i0OXZeDyh3Jo",
authDomain: "realtimebusnavigationsystem.firebaseapp.com",
databaseURL: "https://realtimebusnavigationsystem.firebaseio.com",
storageBucket: "realtimebusnavigationsystem.appspot.com",
};

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

firebase.initializeApp(config);
db = firebase.database();


app.use("/assets/js",  express.static(__dirname + '/assets/js'));
app.use("/assets/css", express.static(__dirname + '/assets/css'));
app.use("/assets/fonts",  express.static(__dirname + '/assets/fonts'));
app.use("/images",  express.static(__dirname + '/images'));

app.get('/', function (req, res) {
    res.sendFile('index.html', { root: __dirname  } );

});

var nearby = [];
app.post('/', function(request, response){
    lat = request.body['coords[latitude]'];
    lng = request.body['coords[longitude]'];
    //console.log(lat + " " + lng);
    var get_bus_data;
    return new Promises(function(resolve,reject){
        var ref = db.ref("CurrentLocation");
        ref.once('value', function(result){
            if(result.val() === undefined)
            {
                console.log("undefined data");
                reject();
            }
            else
            {
                resolve(result.val());
            }
        }).then((result)=>{
            get_bus_data = result.toJSON();
            return get_bus_data;
        }).then((result)=>{
            //response.send(result);
            var str = JSON.stringify(result);
            var arr = str.split(/[{'" ,:"'}]+/);
	    nearby.length = 0;
            for(var i = 1; i< arr.length-1; i++){
                var id = arr[i++];
                var Lat = arr[i];
                var x = parseFloat(arr[i++]);
                var Lng = arr[i];
                var y = parseFloat(arr[i]);

                //console.log(x + " " + y);

                //console.log(getDistance(lat, lng, x, y));
                if( getDistance(lat, lng, x, y) <= 5){
                    nearby.push(id);
                    nearby.push(Lat);
                    nearby.push(Lng);
                    
                }
            }
            //console.log(nearby);
	    response.send(nearby);
            
        })
    })

});

https.createServer(options, app).listen(3000, function () {
  console.log('Started!');
});

function getDistance(lat1, lon1, lat2, lon2){
    var R = 6371;
    var dLat = deg2rad(lat2-lat1);
    var dLon = deg2rad(lon2-lon1);
    var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
            Math.sin(dLon/2) * Math.sin(dLon/2);
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    var d = R * c;
    return d;
}

function deg2rad(deg){
    return deg * (Math.PI/180)
}

