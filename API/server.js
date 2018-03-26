const express = require('express');
const request = require('request');
const hbs = require('hbs');

var app = express(),
bodyParser = require('body-parser'),
path = require("path");


//https://stackify.com/top-command-line-tools/
//https://github.com/sintaxi/awesome-cli/blob/master/README.md

app.set('view engine','hbs');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
//http://opensourceforu.com/wp-content/uploads/2016/09/Figure-1-Sample-Page-1.jpg
app.use(express.static(__dirname + '/public'));
var arequest = require('ajax-request');

const port = process.env.PORT || 3000;
app.post('/url',(req,res) => {
var durl=req.body.id;
//console.log(durl);
arequest.post({
  url: 'https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/RecognizeText?handwriting=true',
  data: {"url":durl},
  headers: {"Content-Type":"application/json","Ocp-Apim-Subscription-Key":"12afcfa019514c92897b86c0a7492f6a"}
},function(err, resp, body){
  baseurl=resp.headers['operation-location']
  setTimeout(function () {
  request({
    url: baseurl,
    method: 'GET',
    headers: {
"Content-Type":"application/json","Ocp-Apim-Subscription-Key":"12afcfa019514c92897b86c0a7492f6a"
    }
  }, function(err, response, body) {
   //console.log(body);
   var a = JSON.parse(body);
  var b=a.recognitionResult.lines;
  var n= b.length;
  var string = "";
  for(i=0;i<n;i++)
  {
    string=string + b[i].text;
  }
console.log(string);
var keyword="";
var k1=0;
var k2=0;
var k3=0;
for(i=0;i<n;i++){
  for(j=0;j<b[i].words.length;j++)
  {
    if(b[i].words[j].text=='asynchronous' )
    {
      if(k1!=1){
        k1=1;
        keyword = keyword+b[i].words[j].text+",";
      }

    }
    if( b[i].words[j].text=='transfer')
    {
      if(k2!=1){
        k2=1;
        keyword = keyword+b[i].words[j].text+",";
      }
    }
    if( b[i].words[j].text=='Synchronous')
    {
      if(k3!=1){
        k3=1;
        keyword = keyword+b[i].words[j].text+",";
      }
    }

  }

}

console.log(keyword);
var k=((k1+k2+k3)/3)*100;
console.log(k);

var l={
  "text": string,
  "keywords": keyword,
  "marks":k
}
 res.send(JSON.stringify(l));
  });
},7000);
 });

});

app.get('/about',(req,res) => {
res.render('about.hbs', {
  url: 'http://opensourceforu.com/wp-content/uploads/2016/09/Figure-1-Sample-Page-1.jpg'
});

});

app.get('/html',function(req,res){
  res.sendFile(path.join(__dirname, './public', 'handwriting.html'));
});

app.post('/info',(req,res)=>{
var a = req.body;
  app.get('/info',(req,res)=>{
    res.send(a);
  });


});

app.listen(port, () => {
  console.log('started on port ',port);
});
