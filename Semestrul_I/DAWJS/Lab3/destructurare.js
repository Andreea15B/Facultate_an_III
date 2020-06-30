/* Diverse exemple privind utilizarea destructurarii in ES6 */

// selectarea elementelor dorite dintr-o listă (tablou)
let [ primul, , ultimul ] = [ 'Vlad', 'Ana', Date.now() ];
console.log (primul);  // "Vlad"
console.log (ultimul); // data curentă în secunde

// oferă coordonatele geografice pentru Iași
function furnizeazaCoordonate () {
  return { lat: 47.16667, long: 27.6 };
}
var { lat, long } = furnizeazaCoordonate ();
console.log (lat);

// furnizează un număr natural generat aleatoriu dintr-un anumit interval
function genAleator ({ min = 1, max = 300 } = { }) {
  return Math.floor(Math.random() * (max - min)) + min;
}
for (let it of [1, 2, 3, 4]) { 	// 4 numere generate aleatoriu
  console.log (genAleator ());
}