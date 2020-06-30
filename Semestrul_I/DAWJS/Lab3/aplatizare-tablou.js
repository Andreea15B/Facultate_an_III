/* Exemplificarea metodelor Array.flat() si Array.flatMap() oferite de ECSMAScript 2019 


   A se studia https://exploringjs.com/es2018-es2019/ch_overview.html#ecmascript-2019
*/

// un tablou de tablouri cu nume de membri de echipe
let echipe = [ [ 'Tux', 'Pox' ],            // echipa #1
               [ 'Puq', [ 'Hax', 'Roa' ] ], // echipa #2
               [ 'Tux', 'Roa', 'Muz' ],     // echipa #3
               'Foh' ];                     // un solitar

console.log (echipe);

console.log (echipe.flat ());  // aplatizăm tabloul
console.log (echipe.flat (2)); // aplatizăm în profunzime

// realizăm și o asociere cu flatMap()
console.log (echipe.flatMap (membru => ('/teams/' + membru) ));