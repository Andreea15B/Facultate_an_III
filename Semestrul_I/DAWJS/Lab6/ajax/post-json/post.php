<?php
/* Un program PHP care preia date JSON transmise prin POST de client
   si le trimite inapoi (echo)
*/   
function eJSONValid ($sir) { // verifica daca datele JSON sunt corecte
   json_decode ($sir);
   return json_last_error() == JSON_ERROR_NONE;
}

// preluam de la intrarea standard datele transmise de client
// (aici, cele dintr-o cerere POST)
$date = trim(file_get_contents("php://input"));

if (eJSONValid ($date)) {
	// trimitem datele JSON inapoi
	header ("Content-type: application/json");
	echo json_decode ($date);
} else {	
	die ('Date incorecte');
}	
?>