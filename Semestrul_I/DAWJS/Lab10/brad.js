class Glob {
  constructor(radius, x, y, color) {
    this.radius = radius;
    this.x = x;
    this.y = y;
    this.color = color;
    this.opacity = 1;
    this.el = this.createElement();
  }

  createElement() {
    const el = document.createElementNS("http://www.w3.org/2000/svg", "circle");
    el.setAttribute("r", this.radius);
    el.setAttribute("cx", this.x);
    el.setAttribute("cy", this.y);
    el.setAttribute("fill", this.color);
    el.setAttribute("fill-opacity", this.opacity);
    return el;
  }

  toggleOpacity() {
    this.opacity = Math.max(Math.random(), 0.4);
    this.el.setAttribute("fill-opacity", this.opacity);
  }
}

(async function() {
  const response = await fetch(
    "https://upload.wikimedia.org/wikipedia/commons/9/9e/Emojione_1F332.svg"
  );
  const b = await response.text();
  const pg = document.getElementById("pg");
  let selectedSettings = {
    color: null,
    radius: null
  };
  pg.innerHTML = pg.innerHTML + b;

  const globuri = [];

  pg.addEventListener("click", e => {
    // if (!selectedSettings.radius) {
    //   alert("Selecteaza un tip de glob!");
    //   return;
    // }

    const g = new Glob(
      selectedSettings.radius,
      e.offsetX,
      e.offsetY,
      selectedSettings.color
    );
    console.log(g);
    globuri.push(g);
    pg.appendChild(g.el);
  });

  // animation
  let lastToggleTs = 0;
  function blink(timestamp) {
    if (timestamp - lastToggleTs > 200) {
      lastToggleTs = timestamp;
      globuri.forEach(g => {
        g.toggleOpacity();
      });
    }
    requestAnimationFrame(blink);
  }

  blink();

  // settings
  const panel = document.getElementById("panel");
  panel.addEventListener("click", e => {
    const btn = e.target;
    const color = btn.dataset.color;
    const radius = btn.dataset.radius;
    if (color && radius) {
      selectedSettings.color = color;
      selectedSettings.radius = radius;
      console.log(selectedSettings);
      document.getElementById("type").innerHTML = btn.innerHTML;
    }
  });

  // hide/show form
  const formVisibility = document.getElementById("formVisibilityBtn");
  const formular = document.getElementById("formular");
  formVisibility.addEventListener("click", e => {
    if (formular.style.display == "none") formular.style.display = "block";
    else formular.style.display = "none";
  });

  formular.addEventListener("submit", e => {
    e.preventDefault();
    let colorValue = formular.elements["color"].value;
    let sizeValue = formular.elements["size"].value;

    const glob = new Glob (
      sizeValue,
      0,
      0,
      colorValue
    );
    console.log(glob);
    globuri.push(glob);
  });

  // save
  // function saveAsPng() {
  // 	const svgText = pg.innerHTML;
  // 	const canvas = document.getElementById('canvas');
  // }
})();
