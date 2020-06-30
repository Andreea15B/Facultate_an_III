/* Exemplu referitor la folosirea machetelor de substitutie 
   a valorilor (template literals), o facilitate ES6 */

var articol = { // obiect JS oferind date despre un articol
  titlu: 'Machete ES6',
  subiect: 'facilități privind substituția de valori',
  slogan: 'Generare HTML cu JS',
  termeni: [ 'ES6', 'JavaScript', 'HTML5' ]
};

let { titlu, subiect, slogan, termeni } = articol; 	// destructurare
let html = `<article>
  <header><h1>${titlu}</h1></header>
  <section>
    <h2>Articol despre ${subiect}</h2>
    <p>${slogan}</p>
  </section>
  <footer class='subsol'>
    <ul>
      ${termeni.map (termen => 
                     `<li>${termen}</li>`).join ('\n\t\t\t')}
    </ul>
  </footer>
</article>`;

console.log (html);