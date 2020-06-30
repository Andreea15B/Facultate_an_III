/* Un program JavaScript demonstrand crearea de pseudo-clase, 
   pe baza prototipurilor.

   Autor: Dr. Sabin Buraga (2007, 2011, 2012, 2018) - https://profs.info.uaic.ro/~busaco/
   
   Pentru rulare, se poate recurge la https://jsbin.com/
*/

// o 'clasa' privitoare la animale
function Animal (nume, marime) { // definitie initiala   
  // doar proprietatile privind numele si marimea
  this.nume = nume;
  this.marime = marime;
}

// noi metode adaugate la prototip
Animal.prototype.oferaNume = function() {
  return this.nume;
};
Animal.prototype.oferaMarime = function() {
  return this.marime;
};
Animal.prototype.oferaNumeMare = function () {   
  return this.nume.toUpperCase ();
};
Animal.prototype.toString = function () { // suprascriere
  return '<animal>' + this.oferaNume () + '</animal>';
};

// instantiere
var tux = new Animal ("Tux", 17);

// afisarea in consola a rezultatelor
// intoarse de metodele definite
console.log ( tux.oferaMarime() );
console.log ( tux.oferaNumeMare() );
console.log ( tux.toString() );

// verificam carui tip de date apartine instanta
console.log ( tux instanceof Object );
console.log ( tux instanceof Array );