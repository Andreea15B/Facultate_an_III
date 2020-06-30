/**********************************
  
  The reader of this program should not limit
  himself/herself to the comments of this 
  program.

  If one wants to read more about the syntax and 
  the semantics of OpenGL functions used in this
  program, one should read the beginning of the
  paragraph 2.6 "Begin/End Paradigm", the 
  paragraph 2.6.1 "Begin and End Objects" from the 
  file glspec15.pdf at page 25/333 and the index
  from the end of that file. One could also
  read the references to the GLUT functions from
  the file glut-3.spec.pdf.
  


  H O W  T O  R E A D  T H I S  P R O G R A M ?
  
  Start from the function "main" and follow the 
  instruction flow, paying attention to the fact that
  this program belongs to the event-driven programming
  paradigm. So pay attention to what happens when 
  the user presses a key, moves the mouse or presses a
  mouse button. There are also some special events: the
  redrawing of the application window, etc.
  Identify what happens in the program when one of these
  events occurs.

  **********************************/


/**********************************
  With respect to the include-file directives, see the 
  example program from homework 1; in the following it
  was made the assumption that you are using GLUT locally,
  in your project and you didn't install it as an
  Administrator
  **********************************/

#include <stdlib.h>
#include <stdio.h>
#include <math.h>

#include "glut.h"
//#include "GLU.h"

// the size of the window measured in pixels
#define dim 300

unsigned char prevKey;

// the graph of the Conchoid of Nicomedes
void Display1() {
   double xmax, ymax, xmin, ymin;
   double a = 1, b = 2;
   double pi = 4 * atan(1.0);
   double ratia = 0.05;
   double t;

   /**********************************
      Maximum/minimum values of x and y are computed.
      These values will be further used in scaling the
      original graph of the curve. 
      These values are stored in the variables
      xmax, ymax, xmin, ymin: e.g., xmax is initialized
      with a value smaller than any of possible values
      of x; if in doubt or you cannot estimate it, use 
      DBL_MIN instead (or DBL_MAX for minimum values).
      These DBL_* constants are found in <float.h> which
      need to be included.
      E.g., xmax is initialized with a - b - 1 because
      x(t) = a +/- b * cos(t) and for t in (-pi/2, pi/2),
      cos(t) is in (0, 1), so b * cos(t) is in (0, b),
      so +/- b * cos(t) is in (-b, b), so x(t) is in
      (a-b, a+b) and one can safely choose a-b-1 because
      a-b-1 < a-b. 
      For y(t) we see that in its expression appears
      tan(t) which varies in (-inf,+inf) when t
      varies in (-pi/2, pi/2).
     **********************************/
   xmax = a - b - 1; 
   xmin = a + b + 1;
   ymax = ymin = 0;
   for (t = - pi/2 + ratia; t < pi / 2; t += ratia) {
      double x1, y1, x2, y2;
      x1 = a + b * cos(t);
      xmax = (xmax < x1) ? x1 : xmax;
      xmin = (xmin > x1) ? x1 : xmin;

      x2 = a - b * cos(t);
      xmax = (xmax < x2) ? x2 : xmax;
      xmin = (xmin > x2) ? x2 : xmin;

      y1 = a * tan(t) + b * sin(t);
      ymax = (ymax < y1) ? y1 : ymax;
      ymin = (ymin > y1) ? y1 : ymin;

      y2 = a * tan(t) - b * sin(t);
      ymax = (ymax < y2) ? y2 : ymax;
      ymin = (ymin > y2) ? y2 : ymin;
   }

   /**********************************
      At this line, we have found that the graph of the Conchoid
      is included in the rectangle having the edges x = xmin,
      x = xmax, y = ymin and y = ymax. 
      We would like that the rectangle should be symmetric with
      respect to the Ox and Oy axes.
      We store in xmax and ymax the maximum of absolute values
      max(|xmax|,|xmin|) and max(|ymax|,|ymin|).
      Now we know that the graph of the Conchoid is included in
      the rectangle [-xmax, xmax] x [-ymax, ymax].
     **********************************/

   xmax = (fabs(xmax) > fabs(xmin)) ? fabs(xmax) : fabs(xmin);
   ymax = (fabs(ymax) > fabs(ymin)) ? fabs(ymax) : fabs(ymin);

   /**********************************
      At this line, we can perform the scaling. All the points
      scaled are visible (i.e., in the rectangle [-1,1]x[-1,1]).
      Because we have -xmax <= x <= xmax we get
      -1 <= x / xmax <= 1. Idem for y.
      In order to correctly display the graph of the Conchoid,
      one should use the exact same points that were used in
      the computation of the scaling factor.
     **********************************/

   glColor3f(1,0.1,0.1); // rosu
   glBegin(GL_LINE_STRIP); 
   for (t = - pi/2 + ratia; t < pi / 2; t += ratia) {
      double x1, y1, x2, y2;
      x1 = (a + b * cos(t)) / xmax;
      x2 = (a - b * cos(t)) / xmax;
      y1 = (a * tan(t) + b * sin(t)) / ymax;
      y2 = (a * tan(t) - b * sin(t)) / ymax;

      glVertex2f(x1,y1);
   }
   glEnd();

   glBegin(GL_LINE_STRIP); 
   for (t = - pi/2 + ratia; t < pi / 2; t += ratia) {
      double x1, y1, x2, y2;
      x1 = (a + b * cos(t)) / xmax;
      x2 = (a - b * cos(t)) / xmax;
      y1 = (a * tan(t) + b * sin(t)) / ymax;
      y2 = (a * tan(t) - b * sin(t)) / ymax;

      glVertex2f(x2,y2);
   }
   glEnd();
}

// The graph of the function 
// $f(x) = \bar sin(x) \bar \cdot e^{-sin(x)}, x \in \langle 0, 8 \cdot \pi \rangle$, 
void Display2() {
   double pi = 4 * atan(1.0);
   double xmax = 8 * pi;
   double ymax = exp(1.1);
   double ratia = 0.05;


   /**********************************
      For this example, the computation of the scaling factors
      is not needed. Because x is in the interval [0, 8pi]
      the scaling factor for x is 8pi because x/(8pi) is in
      [0,1]. In the case of the exponential function we know
      that if x is [0,8pi] then sin x is in [-1,1] so
      e^(-sin x) is in [1/e, e] and thus it is safe to use 
      e^(1.1) as a scaling factor.
     **********************************/
   glColor3f(1,0.1,0.1); // rosu
   glBegin(GL_LINE_STRIP); 
   for (double x = 0; x < xmax; x += ratia) {
      double x1, y1;
      x1 = x / xmax;
      y1 = (fabs(sin(x)) * exp(-sin(x))) / ymax;

      glVertex2f(x1,y1);
   }
   glEnd();
}

void Display3() {
    double xmax = 100, ymax = 1, xmin = 0, ymin = 1, ratia = 0.05, t, y, x;
    // distanta de la x la cel mai apropiat intreg este |x - round(x)|
    for (t = ratia; t <= 100; t += ratia) {
        y = fabs(t - roundf(t)) / t;
        ymax = (ymax < y) ? y : ymax;
        ymin = (ymin > y) ? y : ymin;
    }
    ymax = (fabs(ymax) > fabs(ymin)) ? fabs(ymax) : fabs(ymin);
    glColor3f(1, 0.1, 0.1);
    glBegin(GL_LINE_STRIP);
    glVertex2f(0, 1 - 0.1); // pentru cazul x=0
    for (t = ratia; t <= 100; t += ratia) {
        x = t / xmax;
        y = (fabs(t - roundf(t)) / t) / ymax - 0.1;
        glVertex2f(x, y);
    }
    glEnd();
}

// melcul lui Pascal
void Display4() {
    double xmax, ymax, xmin, ymin, ratia = 0.05, t, y, x;
    double a = 0.3, b = 0.2;
    double pi = 4 * atan(1.0);

    xmax = xmin = 2 * (a + b); // pentru ca cos(pi) = 1
    ymax = ymin = 0; // pentru ca sin(pi) = 0
    for (t = -pi + ratia; t < pi; t += ratia) {
        x = 2 * (a * cosf(t) + b) * cosf(t);
        y = 2 * (a * cosf(t) + b) * sinf(t);

        xmax = (xmax < x) ? x : xmax;
        xmin = (xmin > x) ? x : xmin;

        ymax = (ymax < y) ? y : ymax;
        ymin = (ymin > y) ? y : ymin;
    }

    ymax = (fabs(ymax) > fabs(ymin)) ? fabs(ymax) : fabs(ymin);
    xmax = (fabs(xmax) > fabs(xmin)) ? fabs(xmax) : fabs(xmin);

    ymax *= 1.5;
    xmax *= 1.2;

    glColor3f(1, 0.1, 0.1);
    glBegin(GL_LINE_LOOP);
    for (t = -pi + ratia; t < pi; t += ratia) {
        x = 2 * (a * cosf(t) + b) * cosf(t) / xmax;
        y = 2 * (a * cosf(t) + b) * sinf(t) / ymax;
        glVertex2f(x, y);
    }
    glEnd();
}

// trisectoarea lui Longchamps
void Display5() {
    double t;
    double a = 0.2;
    double pi = 3.14;
    double ratia = 0.005;
    int k = 0;
    double x_positions[250], y_positions[250];

    glColor3f(0.0, 0.0, 1.0);
    glPointSize(5);
    glBegin(GL_LINE_STRIP);


    for (t = -(pi / 2); t < -pi / 6; t += ratia) {
        double x = a / (4 * cos(t) * cos(t) - 3);
        double y = (a * tan(t)) / (4 * cos(t) * cos(t) - 3);

        x_positions[k] = x;
        y_positions[k] = y;
        k++;

        glVertex2f(x, y);
    }
    glEnd();

    glColor3f(1, 0.1, 0.1);
    glBegin(GL_TRIANGLES);
    for (int i = 1; i < k - 1; i += 3) {
        if (i <= 50 || i >= 150) {
            glVertex2f(-1.0, 1.0);
            glVertex2f(x_positions[i], y_positions[i]);
            glVertex2f(x_positions[i + 1], y_positions[i + 1]);
        }
    }
    glEnd();

}

// cicloida
void Display6() {
    double xmax, ymax, xmin, ymin, ratia = 0.05, t, y, x;
    double a = 0.1, b = 0.2;
    double pi = 4 * atan(1.0);

    xmax = xmin = 0;
    ymax = ymin = a - b;
    for (t = -10; t < 10; t += ratia) {
        x = a * t - b * sinf(t);
        y = a - b * cosf(t);

        xmax = (xmax < x) ? x : xmax;
        xmin = (xmin > x) ? x : xmin;

        ymax = (ymax < y) ? y : ymax;
        ymin = (ymin > y) ? y : ymin;
    }

    xmax = (fabs(xmax) > fabs(xmin)) ? fabs(xmax) : fabs(xmin);
    ymax = (fabs(ymax) > fabs(ymin)) ? fabs(ymax) : fabs(ymin);

    ymax *= 2.5;

    glColor3f(1, 0.1, 0.1);
    glBegin(GL_LINE_STRIP);
    for (t = -10; t < 10; t += ratia) {
        x = a * t - b * sinf(t);
        y = a - b * cosf(t);
        glVertex2f(x/xmax, y/ymax);
    }
    glEnd();
}

// epicicloida
void Display7() {
    double xmax, ymax, xmin, ymin, t, y, x;
    double ratia = 0.05, R = 0.1, r = 0.3, pi = 4 * atan(1.0);

    xmax = xmin = R + r;
    ymax = ymin = R;
    for (t = 0; t <= 2 * pi; t += ratia) {
        x = (R + r) * cosf(r * t / R) - r * cosf(t + r * t / R);
        y = (R + r) * sinf(r * t / R) - r * sinf(t + r * t / R);

        ymax = (ymax < y) ? y : ymax;
        ymin = (ymin > y) ? y : ymin;

        xmax = (xmax < x) ? x : xmax;
        xmin = (xmin > x) ? x : xmin;
    }

    ymax = (fabs(ymax) > fabs(ymin)) ? fabs(ymax) : fabs(ymin);
    xmax = (fabs(xmax) > fabs(xmin)) ? fabs(xmax) : fabs(xmin);

    xmax *= 1.5;
    ymax *= 1.4;

    glColor3f(1, 0.1, 0.1); // rosu
    glBegin(GL_LINE_STRIP);
    for (t = 0; t <= 2 * pi; t += ratia) {
        x = (R + r) * cosf(r * t / R) - r * cosf(t + r * t / R);
        y = (R + r) * sinf(r * t / R) - r * sinf(t + r * t / R);

        glVertex2f(x / xmax, y / ymax);
    }
    glEnd();
}

// hipocicloida
void Display8() {
    double xmax, ymax, xmin, ymin, t, y, x;
    double ratia = 0.05, R = 0.1, r = 0.3;
    double pi = 4 * atan(1.0);

    xmax = xmin = R - 2 * r;
    ymax = ymin = 0;
    for (t = 0; t <= 2 * pi; t += ratia) {
        x = (R - r) * cosf(r * t / R) - r * cosf(t - r * t / R);
        y = (R - r) * sinf(r * t / R) - r * sinf(t - r * t / R);

        ymax = (ymax < y) ? y : ymax;
        ymin = (ymin > y) ? y : ymin;

        xmax = (xmax < x) ? x : xmax;
        xmin = (xmin > x) ? x : xmin;
    }

    ymax = (fabs(ymax) > fabs(ymin)) ? fabs(ymax) : fabs(ymin);
    xmax = (fabs(xmax) > fabs(xmin)) ? fabs(xmax) : fabs(xmin);

    ymax *= 1.6;
    xmax *= 1.6;

    glColor3f(1, 0.1, 0.1); // rosu
    glBegin(GL_LINE_STRIP);
    for (t = 0; t <= 2 * pi; t += ratia) {
        x = (R - r) * cosf(r * t / R) - r * cosf(t - r * t / R);
        y = (R - r) * sinf(r * t / R) - r * sinf(t - r * t / R);

        glVertex2f(x / xmax, y / ymax);
    }
    glEnd();
}

// lemniscata lui Bernoulli
void Display9() {
    double xmax, ymax, xmin, ymin, t, y1, x1, x2, y2, r1, r2;
    double ratia = 0.01, a = 0.4, pi = 4 * atan(1.0);

    xmax = xmin = a * sqrt(2);
    ymax = ymin = 0;

    for (t = -pi / 4 + ratia; t < pi / 4; t += ratia) {
        r1 = a * sqrt(2 * cosf(2 * t));
        r2 = -a * sqrt(2 * cosf(2 * t));

        x1 = r1 * cosf(t);
        y1 = r1 * sinf(t);

        x2 = r2 * cosf(t);
        y2 = r2 * sinf(t);

        ymax = (ymax < y1) ? y1 : ymax;
        ymin = (ymin > y1) ? y1 : ymin;

        xmax = (xmax < x1) ? x1 : xmax;
        xmin = (xmin > x1) ? x1 : xmin;

        ymax = (ymax < y2) ? y2 : ymax;
        ymin = (ymin > y2) ? y2 : ymin;

        xmax = (xmax < x2) ? x2 : xmax;
        xmin = (xmin > x2) ? x2 : xmin;
    }

    ymax = (fabs(ymax) > fabs(ymin)) ? fabs(ymax) : fabs(ymin);
    xmax = (fabs(xmax) > fabs(xmin)) ? fabs(xmax) : fabs(xmin);

    ymax *= 3;
    xmax *= 1.5;

    glColor3f(1, 0.1, 0.1); // rosu
    glBegin(GL_LINE_STRIP);
    for (t = -pi / 4 + ratia; t < pi / 4; t += ratia) {
        r2 = -a * sqrt(2 * cosf(2 * t));

        x2 = r2 * cosf(t);
        y2 = r2 * sinf(t);

        glVertex2f(x2 / xmax, y2 / ymax);
    }
    glEnd();

    glBegin(GL_LINE_STRIP);
    for (t = -pi / 4 + ratia; t < pi / 4; t += ratia) {
        r1 = a * sqrt(2 * cosf(2 * t));

        x1 = r1 * cosf(t);
        y1 = r1 * sinf(t);

        glVertex2f(x1 / xmax, y1 / ymax);
    }
    glVertex2f(x2 / xmax, y2 / ymax);
    glEnd();
}

// spirala logaritmica
void Display10() {
    double xmax, ymax, xmin, ymin, t, y, x, r;
    double ratia = 0.05, a = 0.02, pi = 4 * atan(1.0);

    xmax = xmin = a * exp(1);
    ymax = ymin = 0;
    for (t = ratia; t <= 17.2; t += ratia) {
        r = a * exp(1 + t);
        x = r * cosf(t);
        y = r * sinf(t);

        ymax = (ymax < y) ? y : ymax;
        ymin = (ymin > y) ? y : ymin;

        xmax = (xmax < x) ? x : xmax;
        xmin = (xmin > x) ? x : xmin;
    }

    ymax = (fabs(ymax) > fabs(ymin)) ? fabs(ymax) : fabs(ymin);
    xmax = (fabs(xmax) > fabs(xmin)) ? fabs(xmax) : fabs(xmin);

    xmax *= 0.2;
    ymax *= 0.2;

    glColor3f(1, 0.1, 0.1); // rosu
    glBegin(GL_LINE_STRIP);
    for (t = ratia; t <= 100; t += ratia) {
        r = a * exp(1 + t);
        x = r * cosf(t);
        y = r * sinf(t);

        glVertex2f(x, y);
    }
    glEnd();
}

void Init(void) {
   glClearColor(1.0,1.0,1.0,1.0);
   glLineWidth(1);
   glPointSize(4);
   glPolygonMode(GL_FRONT, GL_LINE);
}

void Display(void) {
   glClear(GL_COLOR_BUFFER_BIT);
   switch(prevKey) {
   case '1':
      Display1();
      break;
   case '2':
      Display2();
      break;
   case '3':
      Display3();
      break;
   case '4':
      Display4();
      break;
   case '5':
      Display5();
      break;
   case '6':
      Display6();
      break;
   case '7':
      Display7();
      break;
   case '8':
      Display8();
      break;
   case '9':
      Display9();
      break;
   case '0':
      Display10();
      break;
   default:
      break;
   }
   glFlush();
}

void Reshape(int w, int h) {
   glViewport(0, 0, (GLsizei) w, (GLsizei) h);
}

void KeyboardFunc(unsigned char key, int x, int y) {
   prevKey = key;
   if (key == 27) // escape
      exit(0);
   glutPostRedisplay();
}

void MouseFunc(int button, int state, int x, int y) {
}

int main(int argc, char** argv) {
   glutInit(&argc, argv);
   glutInitWindowSize(dim, dim);
   glutInitWindowPosition(100, 100);
   glutInitDisplayMode (GLUT_SINGLE | GLUT_RGB);
   glutCreateWindow (argv[0]);
   Init();
   glutReshapeFunc(Reshape);
   glutKeyboardFunc(KeyboardFunc);
   glutMouseFunc(MouseFunc);
   glutDisplayFunc(Display);
   glutMainLoop();
   return 0;
}
