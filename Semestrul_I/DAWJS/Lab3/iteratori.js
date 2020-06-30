/* Recurgerea la iteratori pentru a genera secvente infinite de valori in ES6 */
const VAL = 7, MAX = 69;

// genereaza o secventa de numere multiplicate cu o valoare
let secvMult = { 
  [Symbol.iterator]() {
  	let curent = 0; // contor disponibil doar in interiorul iteratorului
  	return { 
  		next () {   // se expune metoda next() pentru a obtine urmatoarea valoare
  			curent++;
  			// metoda next() va intoarce obligatoriu 2 valori
  			return {
  				done: false, // 'true' semnaleaza faptul ca secventa se termina
  				value: curent * VAL // elementul curent al secventei
  			};
  		}
  	};
  }
}

for (let numar of secvMult) {
	if (numar > MAX) 
		break; // secventa fiind infinita, trebuie sa folosim 'break'
	console.log (numar);
}