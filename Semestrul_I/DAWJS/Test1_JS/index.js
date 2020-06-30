

class Regex {
    constructor (expression) {
        this.expression = new RegExp(expression);
    }
    function checkCorrect(expression) {
        if(/^([a-z0-9])$/.test(expression)) return true;
        return false;
    }
    function add(expression) {
        var fs = require('fs');
        var data = fs.readFileSync('package_regex.json', 'utf8');
        var expresii = JSON.parse(data);

        expresii.expressions[expresii.expressions.length-1].id = expresii.expressions.length;
        expresii.expressions[expresii.expressions.length-1].expr = expression;
    }
    function read(id) {
        var fs = require('fs');
        var data = fs.readFileSync('package_regex.json', 'utf8');
        var expresii = JSON.parse(data);

        console.log(expresii.expressions[id-1].expr);
    }
    function deleted(id) {
        var fs = require('fs');
        var data = fs.readFileSync('package_regex.json', 'utf8');
        var expresii = JSON.parse(data);

        for (var i = 0; i < expresii.expressions.length; i++) {
            if (expresii.expressions[i].id === id) {
                delete expresii.expressions[i].id;
                break;
            }
        }
    }
    async function update(id, expression) {
        var fs = require('fs');
        var data = fs.readFileSync('package_regex.json', 'utf8');
        var expresii = JSON.parse(data);

        for (var i = 0; i < expresii.expressions.length; i++) {
            if (expresii.expressions[i].id === id) {
                expresii.expressions[i].id = expression;
                break;
            }
        }
    }

    var promise = new Promise(function(resolve, reject) {
        var updated = await update(1, "/ab+c/i");
        if(updated) resolve("Resolved.");
        else reject(Error("Eroare."));
    });

    promise.then((result) => {
        console.log(result);
      }, function(err) {
        console.log(err);
    });
}

let rgx = new Regex("/ab+c/i");
rgx.read(1);
rgx.delete(1);
rgx.update(1, "/ab+c/i");