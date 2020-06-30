const jsdom = require("jsdom");
const { JSDOM } = jsdom;

const https = require("https");
const url = "https://www.w3.org/TR/";
const request = require("request");

request.get(url, (error, response, body) => {
    const dom = new JSDOM(body);
    var denumire = dom.window.document.getElementsByTagName("h2");
    var i;
    for (i = 0; i < denumire.length; i++) {
        console.log(denumire[i].nodeName);
    }
    var editori = dom.window.document.getElementsByClassName("editorlist");
    for (i = 0; i < editori.length; i++) {
        console.log(editori[i].nodeName);
    }
});
  