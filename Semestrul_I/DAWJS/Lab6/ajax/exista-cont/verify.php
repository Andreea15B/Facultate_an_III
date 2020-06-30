<?php
/* Program PHP care verifica existenta unui nume de cont 
   prin parcurgerea unui document XML memorand informatii 
   despre participantii la un concurs (se foloseste Simple XML).
   
   In fapt, am implementat un serviciu Web rudimentar
   (ce poate fi invocat conform paradigmei REST).

   Autor: Sabin-Corneliu Buraga - http://profs.info.uaic.ro/~busaco/
   Ultima actualizare: 22 mai 2017
*/

// locatia unde este stocat documentul XML
define ('DOCXML', './particip.xml');

// trimitem tipul continutului; aici, XML
header ('Content-type: text/xml');

// functie care verifica daca un nume de participant deja exista
// returneaza 1 daca numele exista, 0 in caz contrar
function checkIfNameExists ($aName) {
	// incarcam fisierul XML cu datele despre participanti
	if (!($xml = simplexml_load_file (DOCXML))) {
    	return 0;
  	}
	// parcurgem toti participantii gasiti prin XPath...
	foreach ($xml->xpath('/participants/participant/name') as $name) {
		// comparam numele, ignorand minusculele de majuscule
		if (!strcasecmp($aName, $name)) {
			return 1;
		}
	}
	return 0;
}
?>
<response>
  <result><?php echo checkIfNameExists ($_REQUEST['name']); ?></result>
</response>