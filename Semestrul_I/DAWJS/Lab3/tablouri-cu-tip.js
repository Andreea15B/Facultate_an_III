/* Utilizarea tablourilor cu tip (typed arrays)

   adaptare dupa un exemplu disponibil la http://es6-features.org/#TypedArrays
*/  
// definim o structura de date pe 24 de octeti, similara celei din limbajul C
// struct Animal { unsigned long id; char nume[16]; float marime }
class Animal { 
  constructor (mem = new ArrayBuffer(24)) { // alocam memoria necesara
    this.mem = mem; 
  }; 
  // gestionarea memoriei corespunzatoare datelor de stocat (un buffer)
  set mem (date) { // stocarea datelor in zona de memorie alocata
    this._mem = date; 
    this._id = new Uint32Array (this._mem, 0, 1); 
    this._nume = new Uint8Array (this._mem, 4, 16); 
    this._marime = new Float32Array (this._mem, 20, 1); 
  }
  get mem () { // obținerea datelor stocate
    return this._mem; 
  } 
  // metode setter/getter pentru fiecare membru in parte
  set id (v) { 
    this._id[0] = v; 
  } 
  get id () { 
    return this._id[0]; 
  } 
  set nume (v) { 
    this._nume = v; 
  } 
  get nume () { 
    return this._nume; 
  } 
  set marime (v) { 
    this._marime[0] = v; 
  } 
  get marime () { 
    return this._marime[0]; 
  } 
} 

let tux = new Animal(); 
tux.id = 69; 
tux.nume = "Tux"; 
tux.marime = 15.74;

console.log (`Obiectul ${tux.nume} are identificatorul ${tux.id} 
si marimea ${tux.marime}.`);