// Functie de traversare a arborelui DOM asociat paginii Web curente
function traversare (rezultat) {
	// folosim o functie anonima -- closure
	// 'continut' va reprezenta un tablou avand drept elemente 
	// siruri de caractere ce includ numele elementului parinte,
	// valoarea nodurilor de tip text si 
	// lungimea sirurilor de caractere corespunzatoare valorilor
	// tuturor nodurilor de tip text din arborele DOM 
	// cu radacina precizata de 'element'
  var continut = (function (element) { 
	if (element.nodeType == 3) { // nod text (din DOM) 
		// tablou cu un singur membru
		return [
		  element.parentNode.nodeName + ": '" + element.nodeValue + 
		  "' (" + element.nodeValue.length + ")<br />" 
		  ];
	} 
	var elemente = []; // sau, echivalent, var elemente = new Array(); 
	// recursiv, adaugam in tabloul 'elemente' fiecare informatie
	// preluata din nodul-copil al arborelui DOM cu radacina 'element' 
	for (var index = 0, copil; copil = element.childNodes[index]; index++) { 
		elemente.push (arguments.callee (copil)); 
	} 
	return elemente; // returnam tabloul creat
  }) (document.documentElement);

  // inseram elementele tabloului in cadrul nodului desemnat de parametrul 'rezultat'
  rezultat.innerHTML = continut.toString().replace(/,/g, " "); // inlocuim virgulele cu spatii
}