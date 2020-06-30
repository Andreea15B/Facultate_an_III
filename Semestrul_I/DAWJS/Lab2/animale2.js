// Exemplificarea conceptelor 'prototype' + 'hoisting'
function Animal (nume, marime) { // definiție inițială   
    this.nume = nume;   
    this.marime = marime;
}

// instanțiem obiecte de tip Animal
var tux = new Animal ("Tux", 17);
var pax = new Animal ("Pax", 15);

// pe baza prototipurilor, definim noi metode
// disponibile pentru toate obiectele din clasă
// (aceste definitii sunt disponibile in intreg programul -- hoisting)
Animal.prototype.oferaNume = function () { return this.nume; };
Animal.prototype.oferaMarime = function () { return this.marime; };

console.log (tux.oferaMarime());
console.log (pax.oferaMarime());