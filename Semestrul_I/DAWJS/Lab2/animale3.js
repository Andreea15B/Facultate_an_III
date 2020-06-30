// Exemplificam maniera de cautare a metodelor in cadrul lantului de proprietati
// (vezi discutia de la curs)
function Animal (nume, marime) { // definitie initiala   
  // proprietatile privind numele si marimea
  this.nume = nume;
  this.marime = marime;
  
  this.oferaNume = function() {  // metoda definita in interior
    return this.nume;
  };
}

var tux = new Animal ("Tux", 17);
var jox = tux;
var pax = new Animal ("Pax", 15);

console.log ('Numele lui Tux: ' + tux.oferaNume());

// dorim sa suprascriem metoda deja existenta in definitia 'clasei'
Animal.prototype.oferaNume = function() {
  return this.nume + ' ' + this.marime;
};

// suprascriem metoda pentru un obiect particular
pax.oferaNume = function() {
  return this.nume + ' ' + this.marime;
};

// afisam la consola numele celor 3 obiecte definite
console.log ('Numele lui Tux: ' + tux.oferaNume()); // 'Tux' [de ce?]
console.log ('Numele lui Jox: ' + jox.oferaNume()); // 'Tux' [de ce?]
console.log ('Numele lui Pax: ' + pax.oferaNume()); // 'Pax 15'