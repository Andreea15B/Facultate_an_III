/* Un program JavaScript ce ilustreaza utilizeaza functiilor ca parametri
   (calcularea valorii unor greutati pe baza marimii)

   Autor: Dr. Sabin Buraga (2011, 2012, 2018) - https://profs.info.uaic.ro/~busaco/
   
   Pentru rulare, se poate folosi https://jsbin.com/
*/

// genereaza un tablou privind greutati calculate
// pe baza unei formule depinzand de valoarea marimii
function genereazaTablouGreutati (tablou, calcul) {
  var rezultat = [ ];
  for (var contor = 0; contor < tablou.length; contor++) {
    rezultat[contor] = calcul (tablou[contor]);
  }
  return rezultat;
}

function calculGreutate (marime) {
  return marime * 33.3; // greutatea depinde de marime
}

// niste marimi
var marimi = [17, 20, 7, 14];
// instantiem tabloul greutatilor
var greutati = [ ];
// ...iar pe baza marimilor, generam tabloul greutatilor
var greutati = genereazaTablouGreutati (marimi, calculGreutate);

// afisam la consola fiecare greutate calculata
for (var elem in greutati) {
  console.log(greutati[elem]);
}