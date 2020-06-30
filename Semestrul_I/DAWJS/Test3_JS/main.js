var cache = {};
function fibonacci(number) {
  if (number < 1) {
    localStorage.setItem(number, 0);
    return 0;
  }
  if (number <= 2) {
    localStorage.setItem(number, 1);
    return 1;
  }
  if (number in cache) {
    console.log(localStorage.getItem(number));
    return cache[number];
  }
  var value = fibonacci(number - 1) + fibonacci(number - 2);
  localStorage.setItem(number, value);
  cache[number] = value;
  return value;
}

function writeFibonacci() {
  event.preventDefault();
  var oNewP = document.createElement("p");
  let fibInputValue = document.getElementById("number").value;
  let fibresult = fibonacci(fibInputValue);
  let text = "Fib(" + fibInputValue + "): " + fibresult;
  var oText = document.createTextNode(text);
  oNewP.appendChild(oText);
  document.body.appendChild(oNewP);
}
