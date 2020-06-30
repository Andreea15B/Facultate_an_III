#define _CRT_SECURE_NO_WARNINGS
#include <Windows.h>
#include "glew.h"
#include "glut.h"

#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <assert.h>
#include <float.h>
#include <unordered_set>

// dimensiunea ferestrei in pixeli
#define dim 300
// numarul maxim de iteratii pentru testarea apartenentei la multimea Julia-Fatou
#define NRITER_JF 5000
// modulul maxim pentru testarea apartenentei la multimea Julia-Fatou
#define MODMAX_JF 10000000
// ratii pentru CJuliaFatou
#define RX_JF 0.01
#define RY_JF 0.01

// numarul maxim de iteratii pentru testarea apartenentei la multimea Mandelbrot 
#define NRITER_MB 101
// ratii pentru Mandlebrot
#define RX_MB 0.01
#define RY_MB 0.01

unsigned char prevKey;
int nivel = 0;

class CComplex {
public:
	CComplex() : re(0.0), im(0.0) {}
	CComplex(double re1, double im1) : re(re1 * 1.0), im(im1 * 1.0) {}
	CComplex(const CComplex& c) : re(c.re), im(c.im) {}
	~CComplex() {}

	CComplex& operator=(const CComplex& c) {
		re = c.re;
		im = c.im;
		return *this;
	}

	double getRe() { return re; }
	void setRe(double re1) { re = re1; }

	double getIm() { return im; }
	void setIm(double im1) { im = im1; }

	double getModul() { return sqrt(re * re + im * im); }

	int operator==(CComplex& c1) {
		return ((re == c1.re) && (im == c1.im));
	}

	CComplex pow2() {
		CComplex rez;
		rez.re = powl(re * 1.0, 2) - powl(im * 1.0, 2);
		rez.im = 2.0 * re * im;
		return rez;
	}

	friend CComplex operator+(const CComplex& c1, const CComplex& c2);
	friend CComplex operator*(CComplex& c1, CComplex& c2);

	void print(FILE* f) {
		fprintf(f, "%.20f%+.20f i", re, im);
	}

private:
	double re, im;
};

CComplex operator+(const CComplex& c1, const CComplex& c2) {
	CComplex rez(c1.re + c2.re, c1.im + c2.im);
	return rez;
}

CComplex operator*(CComplex& c1, CComplex& c2) {
	CComplex rez(c1.re * c2.re - c1.im * c2.im, c1.re * c2.im + c1.im * c2.re);
	return rez;
}

class CMandelbrot {
private:
	int numberIterations;

public:
	CMandelbrot() { numberIterations = NRITER_MB; }
	CMandelbrot(int v) { numberIterations = v; }
	~CMandelbrot() {}

	void set_numberIterations(int nr) { numberIterations = nr; }
	int get_numbetIterations() { return numberIterations; }

	// testam daca x apartine multimii Mandelbrot
	int verify_belong(CComplex& x) {
		CComplex z[2];
		z[0] = CComplex(0, 0);
		for (int i = 1; i <= numberIterations; i++) {
			z[1] = z[0] * z[0] + x;
			if (z[1].getModul() > 2) return i;
			z[0] = z[1];
		}
		return 0;
	}
	
	// afisam punctele care apartin multii Mandelbrot si punctele care nu apartin multimii Mandelbrot,
	// colorandu-le cu culori diferite (nuante de albastru)
	void display_MandleBrot(double xmin, double xmax, double ymin, double ymax) {
		glPushMatrix();
		glLoadIdentity();
		glBegin(GL_POINTS);
		// folosim acest tip de date deoarece stocheaza elemente unice
		std::unordered_set<double> colors;

		for (double x = xmin; x <= xmax; x += RX_MB)
			for (double y = ymin; y <= ymax;y += RY_MB) {
				CComplex z(x, y);
				if (verify_belong(z) == 0) glVertex2d(x / xmax, y / ymax);
				else {
					colors.insert(static_cast<double>(verify_belong(z)) / static_cast<double>(numberIterations));
					glColor3f(0, 0 , (float(verify_belong(z)) / static_cast<double>(numberIterations)));
					glVertex2d(x / xmax, y / ymax);
					glColor3f(1.0, 0.1, 0.1); // rosu
				}
			}
		glEnd();
		glPopMatrix();
	}
};

class C2coord {
public:
	C2coord() { m.x = m.y = 0; }
	C2coord(double x, double y) {
		m.x = x;
		m.y = y;
	}
	C2coord(const C2coord& p) {
		m.x = p.m.x;
		m.y = p.m.y;
	}

	C2coord& operator=(C2coord& p) {
		m.x = p.m.x;
		m.y = p.m.y;
		return *this;
	}

	int operator==(C2coord& p) { return ((m.x == p.m.x) && (m.y == p.m.y));  }

protected:
	struct SDate {
		double x, y;
	} m;
};

class CPunct : public C2coord {
public:
	CPunct() : C2coord(0.0, 0.0) {}
	CPunct(double x, double y) : C2coord(x, y) {}

	CPunct& operator=(const CPunct& p) {
		m.x = p.m.x;
		m.y = p.m.y;
		return *this;
	}

	void getxy(double& x, double& y) {
		x = m.x;
		y = m.y;
	}

	int operator==(CPunct& p) { return ((m.x == p.m.x) && (m.y == p.m.y)); }

	void marcheaza() {
		glBegin(GL_POINTS);
		glVertex2d(m.x, m.y);
		glEnd();
	}

	void print(FILE* fis) { fprintf(fis, "(%+f,%+f)", m.x, m.y); }
};

class CVector : public C2coord {
public:
	CVector() : C2coord(0.0, 0.0) { normalizare(); }
	CVector(double x, double y) : C2coord(x, y) { normalizare();  }

	CVector& operator=(CVector& p) {
		m.x = p.m.x;
		m.y = p.m.y;
		return *this;
	}

	int operator==(CVector& p) { return ((m.x == p.m.x) && (m.y == p.m.y)); }

	bool precision_equality(CVector& p, double epsilon) const {
		return (fabs(m.x - p.m.x) < epsilon) && (fabs(m.y - p.m.y) < epsilon);
	}

	CPunct getDest(CPunct& orig, double lungime) {
		double x, y;
		orig.getxy(x, y);
		CPunct p(x + m.x * lungime, y + m.y * lungime);
		return p;
	}

	void rotatie(double grade) {
		double x = m.x;
		double y = m.y;
		double t = 2 * (4.0 * atan(1)) * grade / 360.0;
		m.x = x * cos(t) - y * sin(t);
		m.y = x * sin(t) + y * cos(t);
		normalizare();
	}

	void deseneaza(CPunct p, double lungime) {
		double x, y;
		p.getxy(x, y);
		glColor3f(1.0, 0.1, 0.1);
		glBegin(GL_LINE_STRIP);
		glVertex2d(x, y);
		glVertex2d(x + m.x * lungime, y + m.y * lungime);
		glEnd();
	}

	void print(FILE* fis) { fprintf(fis, "%+fi %+fj", C2coord::m.x, C2coord::m.y); }

private:
	void normalizare() {
		double d = sqrt(C2coord::m.x * C2coord::m.x + C2coord::m.y * C2coord::m.y);
		if (d != 0.0) {
			C2coord::m.x = C2coord::m.x * 1.0 / d;
			C2coord::m.y = C2coord::m.y * 1.0 / d;
		}
	}
};

class CImage1 {
private:
	CVector right = CVector(1, 0);
	CVector left = CVector(-1, 0);
	CVector up = CVector(0, 1);
	CVector down = CVector(0, -1);

public:
	void patrat(double lungime, CPunct& punct) {
		double x, y;
		punct.getxy(x, y);
		punct = right.getDest(punct, lungime / 2);
		up.deseneaza(punct, lungime / 2);
		punct = up.getDest(punct, lungime / 2);
		left.deseneaza(punct, lungime);
		punct = left.getDest(punct, lungime);
		down.deseneaza(punct, lungime);
		punct = down.getDest(punct, lungime);
		right.deseneaza(punct, lungime);
		punct = right.getDest(punct, lungime);
		up.deseneaza(punct, lungime / 2);
		punct = up.getDest(punct, lungime / 2);
		punct = CPunct(x, y);
	}
	void patrate(double lungimea, int nivel, double factordiviziune, CPunct punct) {
		assert(factordiviziune != 0);
		if (nivel == 0) { return; }
		double x, y;
		punct.getxy(x, y);
		punct = right.getDest(punct, lungimea);
		patrat(lungimea * factordiviziune, punct);
		patrate(lungimea * factordiviziune, nivel - 1, factordiviziune, punct);
		punct = up.getDest(punct, lungimea);
		patrat(lungimea * factordiviziune, punct);
		patrate(lungimea * factordiviziune, nivel - 1, factordiviziune, punct);
		punct = left.getDest(punct, lungimea);
		patrat(lungimea * factordiviziune, punct);
		patrate(lungimea * factordiviziune, nivel - 1, factordiviziune, punct);
		punct = left.getDest(punct, lungimea);
		patrat(lungimea * factordiviziune, punct);
		patrate(lungimea * factordiviziune, nivel - 1, factordiviziune, punct);
		punct = down.getDest(punct, lungimea);
		patrat(lungimea * factordiviziune, punct);
		patrate(lungimea * factordiviziune, nivel - 1, factordiviziune, punct);
		punct = down.getDest(punct, lungimea);
		patrat(lungimea * factordiviziune, punct);
		patrate(lungimea * factordiviziune, nivel - 1, factordiviziune, punct);
		punct = right.getDest(punct, lungimea);
		patrat(lungimea * factordiviziune, punct);
		patrate(lungimea * factordiviziune, nivel - 1, factordiviziune, punct);
		punct = right.getDest(punct, lungimea);
		patrat(lungimea * factordiviziune, punct);
		patrate(lungimea * factordiviziune, nivel - 1, factordiviziune, punct);
		punct = up.getDest(punct, lungimea);
		patrat(lungimea * factordiviziune, punct);
		patrate(lungimea * factordiviziune, nivel - 1, factordiviziune, punct);
		punct = CPunct(x, y);
	}

	void deseneaza(double lungimea, int nivel) {
		CPunct punct(0, 0);
		patrat(lungimea, punct);
		patrate(lungimea, nivel, 0.33, punct);
		punct = CPunct(0, 0);
	}
};

// curba lui Hilbert
class CImage3 {
public:
	CVector
		up = CVector(0.0, 1.0),
		downCVector = CVector(0.0, -1.0),
		leftup = CVector(0.0, 1.0),
		leftdown = CVector(0.0, -1.0),
		rightup = CVector(0.0, 1.0),
		rightdown = CVector(0.0, -1.0);
	double precision = 0.0001;

	CImage3() {
		leftup.rotatie(60.0);
		leftdown.rotatie(-60.0);
		rightup.rotatie(-60.0);
		rightdown.rotatie(60.0);
	}
	void hexagonala(double lungimea, int nivel, double factordiviziune, CPunct p, CVector v) {
		assert(factordiviziune != 0);
		if (nivel == 0) {
			v.deseneaza(p, lungimea);
			return;
		}
		CPunct p1;
		if (v.precision_equality(up, precision) || v.precision_equality(leftdown, precision) || v.precision_equality(rightdown, precision)) {
			//dreapta
			v.rotatie(-60.0);
			hexagonala(lungimea * factordiviziune, nivel - 1, factordiviziune, p, v);
			p1 = v.getDest(p, lungimea * factordiviziune);
			v.rotatie(60.0);
			hexagonala(lungimea * factordiviziune, nivel - 1, factordiviziune, p1, v);
			p1 = v.getDest(p1, lungimea * factordiviziune);
			v.rotatie(60.0);
			hexagonala(lungimea * factordiviziune, nivel - 1, factordiviziune, p1, v);
		}
		else {
			//stanga
			v.rotatie(60.0);
			hexagonala(lungimea * factordiviziune, nivel - 1, factordiviziune, p, v);
			p1 = v.getDest(p, lungimea * factordiviziune);
			v.rotatie(-60.0);
			hexagonala(lungimea * factordiviziune, nivel - 1, factordiviziune, p1, v);
			p1 = v.getDest(p1, lungimea * factordiviziune);
			v.rotatie(-60.0);
			hexagonala(lungimea * factordiviziune, nivel - 1, factordiviziune, p1, v);
		}
	}
	void afisare(double lungime, int nivel) {
		auto p = CPunct(-0.75, -0.75);
		auto v = CVector(0, 1);
		hexagonala(lungime, nivel, 0.5, p, v);
	}
};

// arbore Perron
class CImage2 {
public:
	void figura2(double lungime, int nivel, double factordiviziune, CPunct& p, CVector& v) {
		if (nivel != 0) {
			CPunct p1, p2;
			v.rotatie(-45);
			v.deseneaza(p, lungime);
			p1 = v.getDest(p, lungime);
			figura2(lungime * factordiviziune, nivel - 1, factordiviziune, p1, v);

			v.rotatie(90);
			v.deseneaza(p, lungime);
			p1 = v.getDest(p, lungime);
			p2 = p1;

			v.rotatie(10);
			v.deseneaza(p1, lungime);
			p1 = v.getDest(p1, lungime);
			figura2(lungime * factordiviziune, nivel - 1, factordiviziune, p1, v);

			p1 = p2;
			v.rotatie(-55);
			v.deseneaza(p1, lungime);
			p1 = v.getDest(p1, lungime);
			p2 = p1;

			v.rotatie(-90);
			v.deseneaza(p1, lungime / 2);
			p1 = v.getDest(p1, lungime / 2);
			figura2(lungime * factordiviziune, nivel - 1, factordiviziune, p1, v);

			p1 = p2;
			v.rotatie(120);
			v.deseneaza(p1, lungime / 2);
			p1 = v.getDest(p1, lungime / 2);
			figura2(lungime * factordiviziune, nivel - 1, factordiviziune, p1, v);
			v.rotatie(-30);
		}
	}

	void afisare(double lungime, int nivel) {
		CVector v(0.0, -1.0);
		CPunct p(0.0, 1.0);

		v.deseneaza(p, 0.1);
		p = v.getDest(p, 0.1);
		figura2(lungime, nivel, 0.4, p, v);
	}
};

void printText(char* c, const char* stringLevel, const char* name) {
	sprintf(c, "%2d", nivel);
	glRasterPos2d(-0.98, -0.98);
	for (int i = 0; i < strlen(stringLevel); i++) {
		glutBitmapCharacter(GLUT_BITMAP_9_BY_15, stringLevel[i]);
	}
	glutBitmapCharacter(GLUT_BITMAP_9_BY_15, c[0]);
	glutBitmapCharacter(GLUT_BITMAP_9_BY_15, c[1]);

	glRasterPos2d(-1.0, -0.9);
	for (int i = 0; i < strlen(name); i++) {
		glutBitmapCharacter(GLUT_BITMAP_9_BY_15, name[i]);
	}
}

// multimea Mandlebrot si punctele care nu apartin multimii
void Display1() {
	auto cmb = CMandelbrot(21);
	glColor3f(1.0, 0.1, 0.1);
	cmb.display_MandleBrot(-2, 2, -2, 2);
}

void Display2() {
	CImage1 cap;
	char c[3];
	
	glRasterPos2d(-0.98, -0.98);
	const char* displayNivel = "Nivel=";
	const char* displayName = "Imaginea 1";
	printText(c, displayNivel, displayName);

	glPushMatrix();
	glLoadIdentity();
	glScaled(1, 1, 1);
	glTranslated(0.0, 0.0, 0.0);
	cap.deseneaza(0.5, nivel);
	glPopMatrix();
	nivel++;
}

void Display3() {
	CImage3 cap;
	char c[3];
	glRasterPos2d(-0.98, -0.98);
	const char* displayNivel = "Nivel=";
	const char* displayName = "Imaginea 3";
	printText(c, displayNivel, displayName);
	glRasterPos2d(-1.0, -0.9);

	glPushMatrix();
	glLoadIdentity();
	glScaled(1, 1, 1);
	glTranslated(0.0, 0.0, 0.0);
	cap.afisare(1.5, nivel);
	glPopMatrix();
	nivel++;
}

void Display4() {
	CImage2 cfigura2;
	cfigura2.afisare(0.3, nivel);

	char c[3];
	const char* displayNivel = "Nivel=";
	const char* displayName = "Imaginea 2";
	printText(c, displayNivel, displayName);

	nivel++;
}

void Init(void) {
	glClearColor(1.0, 1.0, 1.0, 1.0);
	glLineWidth(1);
	glPolygonMode(GL_FRONT, GL_LINE);
}

void Display(void) {
	switch (prevKey) {
	case '1':
		glClear(GL_COLOR_BUFFER_BIT);
		Display1();
		break;
	case '2':
		glClear(GL_COLOR_BUFFER_BIT);
		Display2();
		break;
	case '3':
		glClear(GL_COLOR_BUFFER_BIT);
		Display3();
		break;
	case '4':
		glClear(GL_COLOR_BUFFER_BIT);
		Display4();
		break;
	default:
		break;
	}
	glFlush();
}

void Reshape(int w, int h) {
	glViewport(0, 0, (GLsizei)w, (GLsizei)h);
}

void KeyboardFunc(unsigned char key, int x, int y) {
	prevKey = key;
	if (key == 27) exit(0); // escape
	glutPostRedisplay();
}

void MouseFunc(int button, int state, int x, int y) {}

int main(int argc, char** argv) {
	glutInit(&argc, argv);
	glutInitWindowSize(dim, dim);
	glutInitWindowPosition(100, 100);
	glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB);
	glutCreateWindow(argv[0]);
	Init();
	glutReshapeFunc(Reshape);
	glutKeyboardFunc(KeyboardFunc);
	glutMouseFunc(MouseFunc);
	glutDisplayFunc(Display);
	glutMainLoop();
	return 0;
}