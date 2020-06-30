/* Exemplu de utilizare a functiilor asincrone

   Adaptare dupa un exemplu oferit de cartea de la 
   http://exploringjs.com/es2016-es2017/ch_async-functions.html
*/   
const MAXITER = 7; 
async function funcAsinc1() {
  for (let i = 1; i <= MAXITER; i++) {
    console.log (`Asincron 1: ${i}`);
  };
  return "Java";
}

async function funcAsinc2() {
  for (let i = 1; i <= MAXITER; i++) {
    console.log (`Asincron 2: ${i}`);
  };
  return "Script";
}

async function funcAsincP() {
    // se asteapta executia ambelor functii asincrone
    const [rez1, rez2] = await Promise.all([
      funcAsinc1(),
      funcAsinc2(),
    ]);
    return rez1 + rez2;
}

// deoarece o functie asincrona intoarce un obiect Promise,
// putem folosi metoda then(); 
// pentru a trata erorile se va recurge la catch()
funcAsincP().then(r => console.log(`Rezultat: ${r}`));

console.log("Program principal");