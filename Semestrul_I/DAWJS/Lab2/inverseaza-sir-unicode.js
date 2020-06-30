// definirea unei functii recurse pentru inversarea unui sir de caractere
// (se observa si utilizarea Unicode in cadrul programului)
function inverseazăȘir (șir) {
  return șir === '' ? '' : 
    inverseazăȘir (șir.substr (1)) + șir.charAt (0);
}

// testare (folosind diverse caractere Unicode)
console.log (inverseazăȘir ("☞Iñtërnâțiônàlîzætiøn☜"));