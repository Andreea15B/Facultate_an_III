// un exemplu JavaScript privind diversele valori pe care le pot lua indecsii unui tablou
// via constructia for...in 
var vector = [ ];
vector[0] = "zero";
vector[new Date ().getTime ()] = "now"; 
vector[3.14] = "pi";

for (var elem in vector) {
  console.log ("vector[" + elem + "] = " + 
               vector[elem] + 
               ", typeof ( " + elem +") == " + 
               typeof (elem));
}