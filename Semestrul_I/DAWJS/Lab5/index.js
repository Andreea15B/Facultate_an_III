// Ex1: Convert zip code to city
const cities = require('cities');
const url = require('url');
const http = require('http');
const app = http.createServer((request, response) => {
  let city = 'not found';
  const query = url.parse(request.url, true).query;
  
  if (query.zipCode) {
    if (cities.zip_lookup(query.zipCode) === undefined) {
        response.writeHead(404, {"Content-Type": "text/html"});
        // Content-Type pentru json este application/json
        response.write(`<h1>Zip code doesn't exist.</h1>`);
    }
    else {
        city = cities.zip_lookup(query.zipCode).city;
        response.writeHead(200, {"Content-Type": "text/html"});
        response.write(`<h1>The city you are in is ${city}.</h1>`);
    }
  }
  response.end();
});

app.listen(3000);
// Portul standard pentru http este 80



// Ex2: Create a Node.js server that serves static html pages
const fs = require('fs');
const url = require('url');
const http = require('http');
const app = http.createServer((request, response) => {
    let html_from_url = url.parse(request.url, true).pathname;
    html_from_url = html_from_url.substr(1);
    let currentPath = process.cwd();
    const fileUrl = new URL(currentPath + '\\' + html_from_url);
    console.log(fileUrl.pathname);
    fs.readFile(fileUrl.pathname, 'utf-8', (err, data) => {
        if (err) throw err;
        response.writeHead(200, {"Content-Type": "text/html"});
        response.write(data);
    });
});
app.listen(3000);