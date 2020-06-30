// const express = require('express')
// var busboy = require('connect-busboy');
// var path = require('path');
// var fs = require('fs-extra'); 

var http = require('http'),
    express = require('express'),
    Busboy = require('busboy'),
    path = require('path'),
    fs = require('fs');
const app = express()
const port = 3000

app.use(express.static('.'));

app.get('/', (req, res) => res.render('index.html'));

app.post('/fileupload', function (req, res) {
    var busboy = new Busboy({ headers: req.headers });
    busboy.on('file', function (fieldname, file, filename, encoding, mimetype) {
        filename = "file_uploaded.jpg";
        var saveTo = path.join(__dirname, 'uploads/' + filename);
        file.pipe(fs.createWriteStream(saveTo));
    });

    busboy.on('finish', function () {
        res.redirect('/');
    });

    return req.pipe(busboy);
});


app.listen(port, () => console.log(`Example app listening on port ${port}!`))