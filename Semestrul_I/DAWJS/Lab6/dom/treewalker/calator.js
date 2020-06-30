/* Program JavaScript (ES6) ce ilustreaza folosirea obiectului TreeWalker
   Varianta online: https://jsfiddle.net/busaco/ofm958vr/
   
   Autor: Sabin Buraga - https://profs.info.uaic.ro/~busaco/

   Ultima actualizare: 14 noiembrie 2018 
*/
"use es6";
// instantiem un obiect TreeWalker
// pentru a parcurge arborele DOM
let calator = document.createTreeWalker(
  document.body,
  // selectam doar elementele
  NodeFilter.SHOW_ELEMENT,
  // ...si le filtram (acceptam doar <p>, <div> si <strong>)
  {
    acceptNode: nod => {
      if (nod.nodeName == 'P' ||
        nod.nodeName == 'DIV' || 
        nod.nodeName == 'STRONG')
        return NodeFilter.FILTER_ACCEPT;
    }
  }
);

let noduri = [];

console.log ("bum");
// baleiem toate nodurile gasite si le plasam in tabloul 'noduri'
while (calator.nextNode())
  noduri.push(calator.currentNode);

// listam nodurile gasite
let elem = document.getElementById('info');
noduri.forEach(nod => {
  // plasam informatiile in DOM, in <div> 
  // inainte de ultimul nod copil
  elem.insertAdjacentText('beforeend', nod.outerHTML + "\u25CF");
  // si la consola browser-ului Web
  console.log(`Element ${nod.nodeName}: ${nod.textContent}`);
});

