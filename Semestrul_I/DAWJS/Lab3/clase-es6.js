/* Un program ES6 (ECMAScript 2015) ce ilustreaza specificarea unei clase 
   si a unui obiect apartinand acesteia; 
   suplimentar, se defineste o clasa extinsa + obiectul aferent

   Autor: Dr. Sabin Buraga (2016) -- https://profs.info.uaic.ro/~busaco/
   Modificari minore: 12 noiembrie 2017

   Pentru experimentare, se poate recurge la instrumentul Web interactiv 
   JS Bin -- https://jsbin.com/
*/   
const DIST = 7, MAXPOWER = 33;
class Robot {
  constructor (distance = 0) {
    this.power = 0;
    this.distance = distance;
  }
  move () {
    if (this.power < 1) {
      throw new RangeError ('N-am energie');
    }
    this.power--;
    this.distance += DIST;
  }
  addPower () {
    if (this.power >= MAXPOWER) {
      throw new RangeError ('Bateria e plină');
    }
    this.power++;
  }
}

class R2D2 extends Robot {
  constructor (distance) {
    super(distance * 2);
  }
  move () {
    super.move ();
    this.power++; // R2D2 nu consumă energie ;)
  }
}

// instanțiem un robot (distanța ia valoarea implicită)
let robot = new Robot ();
console.log (robot.distance); // valoarea 0
try {
  robot.addPower ();
  robot.move ();
  robot.move (); // va emite exceptia "N-am energie"
} catch (e) {
  console.error (e.message);
} finally {
  console.log ('M-am deplasat cu ' 
    + robot.distance + ' metri, iar energia actuală este ' 
    + robot.power);
}

// instanțiem robotul din clasa R2D2
let r2d2 = new R2D2 (15);
try {
  r2d2.addPower (); // inițial, R2D2 trebuie alimentat cu energie
  r2d2.move ();
} catch (e) {
  console.error (e.message);
}
finally {
  console.log ('Sunt R2D2, m-am deplasat cu ' 
    + r2d2.distance + ' metri, iar energia curentă este ' + r2d2.power);
  // se obține "Sunt R2D2, m-am deplasat cu 37 metri, iar energia curentă este 1"
}