/* Un program JavaScript ilustrand utilizarea de functii anonime

   Autor: Dr. Sabin Buraga (2011, 2018) - https://profs.info.uaic.ro/~busaco/
   
   Pentru experimentare, se poate recurge la https://jsbin.com/
*/

// calculul mediei a N numere
var media = function () {   
  var suma = 0;
  for (var iter = 0, lung = arguments.length; iter < lung; iter++) {
  	suma += arguments[iter];
  }
  return suma / arguments.length;
};

console.log ( media (9, 10, 7, 8, 7) );
console.log ( media (3, 9, 9) );

console.log ( typeof (media) ); // care e tipul variabilei 'media'?
console.log ( media() );        // ce se va afisa?