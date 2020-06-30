/* Un exemplu de generare a numerelor lui Fibonacci pe baza generatorilor ES6

   adaptare dupa http://es6-features.org/#GeneratorMatching
*/  
let fib = function* (numere) { 
  let prec = 0, curent = 1;
  while (numere-- > 0) { 
  	[ prec, curent ] = [ curent, prec + curent ]; 
  	yield curent; 
  } 
} 

// primele 10 numere
console.log ([ ...fib(10) ]); // [1, 2, 3, 5, 8, 13, 21, 34, 55, 89]

// al doilea din secvență, apoi restul de la al patrulea încolo
let [ , n2, , ...restul ] = fib(10);
console.log (n2, restul); // 2 si [5, 8, 13, 21, 34, 55, 89]