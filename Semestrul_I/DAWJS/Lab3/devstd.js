/* Exemplu folosind operatorul '**' definit de specificatia ES7 
   Aici: calculul deviatiei standard 
  
   
   Adaptare dupa https://github.com/30-seconds/30-seconds-of-code
   
   Pentru experimentare, se poate recurge la https://jsbin.com/
*/

// prelucrări statistice
var numere = [1, 7, 50, 74, 9, 85, 51, 12, 7, 15];

// funcție ce calculează deviația standard
const devStd = (valori, pop = false) => {
  const media = valori.reduce((tablou, val) => tablou + val, 0) / valori.length;
  return Math.sqrt(
    valori.reduce((acurat, val) => acurat.concat((val - media) ** 2), []).reduce((acurat, val) => acurat + val, 0) /
      (valori.length - (pop ? 0 : 1))
  );
};

// afișarea deviației standard
console.log (devStd (numere)); // eșantion (sample)
console.log (devStd(numere, true)); // populație (population)