// ilustrarea folosirii instantelor de tip Map oferite de ES6

// instantiem un obiect Map pentru a stoca atribute 
// despre profilul unui utilizator in forma cheie-valoare
let profil = new Map();

// stabilim chei (aici, pasiune si mediu)
profil.set ('pasiune', { // un obiect
  tip: 'muzica', gen: 'rock', stil: 'progresiv', 
  grupFavorit: [ 'Pink Floyd', 'Yes' ] });

profil.set ('mediu', 'urban');  

// verificam existenta unei chei
if (profil.has ('mediu')) {
  profil.set ('mediu', 'cosmic');  
}

// redam la consola valorile cheilor
console.log (profil.get ('pasiune'));
console.log (profil.get ('pasiune').grupFavorit[0]);

// iterarea cheilor & valorilor aferente
profil.forEach((val, cheie) => console.log(`cheie: ${cheie}, valoare: ${val}`));
