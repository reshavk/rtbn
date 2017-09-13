
var express = require('express');
var bodyParser = require('body-parser');
var path = require('path');
var app = express();

var firebase = require("firebase");
var Promises = require("bluebird")

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


app.post('/', function(request, response){

	lat = request.body['coords[latitude]'];
	lng = request.body['coords[longitude]'];
	console.log(lat + " " + lng);
	var get_bus_data;
	return new Promises(function(resolve,reject){
		var ref = db.ref("CurrentLocation");
		ref.once('value', function(result){
			if e(result.val() === undefined)
			{
				console.log("undefined data");
				reject();
			}
			else
			{
				resolve(result.val());
			}
			
		})
		.then((result)=>{
			get_bus_data = result.toJSON();
			console.log(get_bus_data);
			return get_bus_data;
			//console.log(get_bus_data);
		})
		.then((result)=>{
			//console.log(result);
			response.send(result.CurrentLocation);
				
		})

		
	})

});


app.listen(3000, function(){
	console.log('Server Started');

})


