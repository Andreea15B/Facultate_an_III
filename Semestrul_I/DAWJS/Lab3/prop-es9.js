/* Exemplu folosind operatorul '...' in contextul proprietatilor
   obiectelor, operator descris de specificatia ES9 
   
   Autor: Dr. Sabin Buraga (2018) - https://profs.info.uaic.ro/~busaco/

   Pentru experimentare, se poate recurge la https://jsbin.com/
*/

// preluarea proprietăților enumerabile dintr-un obiect
let { nume, mărime, ...altele } = 
    { nume: 'Tux', mărime: 2, oferta: true, disponibil: false };

console.log (altele);
console.log (`Jucăria se numește ${nume}; oferta: ${altele.oferta}`);
