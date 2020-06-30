// Functie JavaScript are genereaza un tabel via DOM
function genereazaTabel () {
  var DIM = 5; // dimensiunea tabelului 
               // (ar fi trebuit 'const', dar MSIE nu are suport pentru asa ceva)
  
  // vom insera tabelul generat la inceputul paginii Web
  var corp = document.getElementsByTagName ("body").item (0);
  // cream tabelul
  var tabel = document.createElement ("table");
  var corpTabel = document.createElement ("tbody");
  // generam si liniile de tabel...
  for (var rand = 0; rand < DIM; rand++) {
    var randCurent = document.createElement ("tr");
    for (var coloana = 0; coloana < DIM; coloana++) {
		// ...inclusiv continutul celulelor
		var celula = document.createElement ("td");
		var continut = document.createTextNode ("textul din linia " + rand + 
			" si coloana " + coloana);
		celula.appendChild (continut);
		randCurent.appendChild (celula);
    }
    corpTabel.appendChild (randCurent);
  }
  tabel.appendChild (corpTabel);
  corp.appendChild (tabel);
  // stabilim un chenar pentru tabel 
  // (ar fi fost indicat sa recurgem la CSS -- tema)
  tabel.setAttribute ("border", "2");
}