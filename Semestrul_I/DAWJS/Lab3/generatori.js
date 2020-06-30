/* Un exemplu de utilizare a generatorilor in ES6 */

// declararea unui generator de identificatori numerici
function* idMaker() { 
  var index = 0;
  while (index < 3)
    yield index++;
}

const gen = idMaker();

// obtinerea unui identificator via generatorul declarat
console.log (gen.next().value); // 0
console.log (gen.next().value); // 1
console.log (gen.next().value); // 2
console.log (gen.next().value); // undefined

// iterarea valorilor de identificatori generati
for (let id of idMaker()) {
  console.log (id);
}