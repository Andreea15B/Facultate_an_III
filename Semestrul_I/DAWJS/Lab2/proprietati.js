// ilustreaza iterarea proprietati asociate unui obiect
var pinguin = { 'nume': 'Tux', 'marime': 17, 'oferta': true };
for (var proprietate in pinguin) {   
  console.log (proprietate + ' = ' + pinguin[proprietate]);
}