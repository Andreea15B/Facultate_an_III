/* Un program JavaScript ilustrand folosirea obiectului predefinit JSON

   Autor: Dr. Sabin Buraga (2015) - http://profs.info.uaic.ro/~busaco/
   
   Pentru experimentare, se poate recurge la http://jsbin.com/
*/

// definim un sir de caractere ce specifica diverse constructii JSON
// (caz concret: preluarea datelor via Ajax ori WebSocket-uri de la un serviciu Web ori API)
var sirTux = '{ "nume": "Tux", "stoc": 33, "model": [ "candid", "viteaz" ] }';
var tux;
try { // incercam sa procesam sirul de caractere pentru a genera un obiect JS via obiectul JSON
  tux = JSON.parse (sirTux); // sir -> obiect JS
} catch (e) {
  console.log (e.message);   // eroare :(
}
// obiectul 'tux' generat cu JSON.parse
console.log (tux); 
// serializare ca sir de caractere
console.log (JSON.stringify(tux));