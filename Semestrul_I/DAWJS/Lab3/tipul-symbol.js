/* Utilizarea tipului de date Symbol din ES6

   adaptare dupa un exemplu oferit de http://exploringjs.com/es6/ch_symbols.html
*/  
const ST_VESEL = Symbol('😀');
const ST_TRIST = Symbol('😞');
const ST_NEUTRU = Symbol('😐');

function oferaStareComplementara (stare) {
  switch (stare) {
    case ST_VESEL: return ST_TRIST;
    case ST_TRIST: return ST_VESEL;
    default: throw new Error('Stare de spirit necunoscuta');
  }
}  

try {
  console.log (oferaStareComplementara (ST_TRIST));
  // conversia la sir de caractere trebuie realizata explicit
  console.log (oferaStareComplementara (ST_VESEL).toString());
  // apelam functia avand ca argument un simbol nedorit
  console.log (oferaStareComplementara (ST_NEUTRU));
} catch (e) {
  console.log (e.message);
}