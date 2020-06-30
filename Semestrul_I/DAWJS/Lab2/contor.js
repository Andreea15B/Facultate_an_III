/* Un program JavaScript ilustrand incapsularea
   (studiu de caz: operatii asupra unui contor)

   Autor: Dr. Sabin Buraga (2011, 2012, 2018) - https://profs.info.uaic.ro/~busaco/
   
   Pentru experimentare, se poate recurge la https://jsbin.com/
*/

// defineste un contor de valori
var makeCounter = function () {
  var contorPrivat = 0;     // un contor de valori (initial, zero)
  function changeBy (val) { // functie privata
    contorPrivat += val;    // ce modifica valoarea contorului
  }
  return {                  // functii publice (expuse)
    increment: function() {
      changeBy (1);
    },
    decrement: function() {
      changeBy (-1);
    },
    value: function() {
      return contorPrivat;
    }
  };
};

// cream 2 contoare
var contor1 = makeCounter ();
var contor2 = makeCounter ();

console.log (contor1.value ()); /* 0 */
contor1.increment ();
contor1.increment ();
console.log (contor1.value ()); /* 2 */
contor1.decrement ();
console.log (contor1.value ()); /* 1 */
console.log (contor2.value ()); /* 0 */