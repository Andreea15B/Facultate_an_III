#include <cstdlib>
#include <list>
#include <glut.h>
#include <complex>

using namespace std;
unsigned char prevKey;

#define numar 15
#define DIM 400
#define DIST 0.5

class Point {
private:
	int X, Y;
public:
	Point(int x, int y) {
		this->X = x;
		this->Y = y;
	}
};

class Segment {
private:
	int fromX, fromY, toX, toY;
	float m, n;
	int dX, dY, a, b, c;

public:
	Segment(int fromX, int fromY, int toX, int toY) {
		this->fromX = fromX;
		this->fromY = fromY;
		this->toX = toX;
		this->toY = toY;
		dX = abs(toX - fromX);
		dY = abs(toY - fromY);
		a = dY;
		b = -dX;
		c = dX * fromY - dY * fromX;
		m = dY / (dX + 0.0f);
		n = fromY - (dY * fromX) / (dX + 0.0f);
	}
};

class GrilaCarteziana {
private:
	int nr;
	float nrCelule;
	float distantaMargine;
public:
	GrilaCarteziana(const int nr) { this->nr = nr; }

	void drawGrid() {
		nrCelule = static_cast<float>(numar) / 2;
		distantaMargine = 1 - (nrCelule / (nrCelule + DIST));
		const auto startXPoint = -1 + distantaMargine;
		const auto endXPoint = 1 - distantaMargine;

		glLineWidth(1.0);
		glColor3f(0.1, 0.1, 0.1);

		// trasare linii orizontale
		for (float i = -nrCelule; i <= nrCelule; i++) {
			const auto commonYPoint = static_cast<float>(i / (nrCelule + DIST));
			glBegin(GL_LINES);
			glVertex2f(startXPoint, commonYPoint);
			glVertex2f(endXPoint, commonYPoint);
			glEnd();
		}

		// trasare linii verticale
		for (auto i = -nrCelule; i <= numar; i++) {
			const auto commonXPoint = static_cast<float>(i / (nrCelule + DIST));
			glBegin(GL_LINES);
			glVertex2f(commonXPoint, endXPoint);
			glVertex2f(commonXPoint, startXPoint);
			glEnd();
		}
	}

	void drawLine() {
		const auto negXPoint = -1 + distantaMargine;
		const auto pozXPoint = 1 - distantaMargine;

		glLineWidth(4.0);
		glColor3f(1.0, 0.0, 0.0);

		// trasare prima linie rosie
		glBegin(GL_LINES);
		glVertex2f(negXPoint, pozXPoint);
		glVertex2f(pozXPoint, 2.5 / (nrCelule + DIST));
		glEnd();

		// trasare a doua linie rosie
		glColor3f(1.0, 0.0, 0.0);
		glBegin(GL_LINES);
		glVertex2f(negXPoint, negXPoint);
		glVertex2f(pozXPoint, (-0.5 / (nrCelule + DIST)));
		glEnd();

		// trasare puncte prima linie
		AfisareSegmentDreapta3(0, numar, numar, 10, 2);

		// trasare puncte a doua linie
		AfisareSegmentDreapta3(0, 0, numar, 7, 0);
	}

	void pixels(int x, int y, int length) {
		length = length / 2;
		for (auto i = -length; i <= length; i++)
			writePixel(x, y + i);
	}

	void writePixel(int i, int j) {
		glColor3f(0.3, 0.3, 0.3);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

		const auto point_x_position = i + -nrCelule;
		const auto point_y_position = j + -nrCelule;

		glBegin(GL_POLYGON);
		for (float k = 0; k <= 10; k += 0.5) {
			const float radius = 0.35;
			const auto output_x = static_cast<float>(point_x_position + radius * sin(k)) / (nrCelule + DIST);
			const auto output_y = static_cast<float>(point_y_position + radius * cos(k)) / (nrCelule + DIST);
			glVertex2f(output_x, output_y);
		}
		glEnd();
	}

	void AfisareSegmentDreapta3(int from_x, int from_y, int to_x, int to_y, const unsigned int bold = 0) {
		int dx = to_x - from_x;
		int x = from_x, y = from_y;
		float m = ((to_y - from_y) / static_cast<float>(to_x - from_x));
		int i = m > 0 ? 1 : -1;
		int dy = i * (to_y - from_y);
		auto d = 2 * dy - dx;
		auto dE = 2 * dy;
		auto dNE = 2 * (dy - dx);

		pixels(x, y, bold);
		while (x < to_x) {
			if (d <= 0) {
				d += dE;
				x++;
			}
			else {
				d += dNE;
				x++;
				y += i;
			}
			pixels(x, y, bold);
		}
	}
};

void Init(void) {
	glClearColor(1.0, 1.0, 1.0, 1.0);
	glLineWidth(1);
	glPolygonMode(GL_FRONT, GL_LINE);
	glMatrixMode(GL_PROJECTION);
}

auto grid = new GrilaCarteziana(numar);

void Display(void) {
	glClear(GL_COLOR_BUFFER_BIT);
	if (prevKey == '1') {
		grid->drawGrid();
		grid->drawLine();
	}
	glFlush();
}

void Reshape(const int width, const int height) {
	glViewport(0, 0, static_cast<GLsizei>(width), static_cast<GLsizei>(height));
}

void KeyboardFunc(unsigned char key, int x, int y) {
	prevKey = key;
	if (key == 27) exit(0);
	glutPostRedisplay();
}

int main(int argc, char** argv) {
	glutInit(&argc, argv);
	glutInitWindowSize(DIM, DIM);
	glutInitWindowPosition(100, 100);
	glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB);
	glutCreateWindow(argv[0]);
	Init();
	glutReshapeFunc(Reshape);
	glutKeyboardFunc(KeyboardFunc);
	glutDisplayFunc(Display);
	glutMainLoop();
	return 0;
}