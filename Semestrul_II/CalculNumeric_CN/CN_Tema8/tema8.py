import math
import random
import tkinter as tk

epsilon = 1e-8

def exemplu_1(x):
    return x**2 - 4*x + 3

def exemplu_2(x):
    return x**2 + math.exp(x)

def exemplu_3(x):
    return x**4 - 6*(x**3) + 13*(x**2) - 12*x + 4

def G_1(functie, x, h=1e-6):
    result = 3 * functie(x) - 4 * functie(x-h) + functie(x - 2*h)
    return result/(2*h)

def G_2(functie, x, h=1e-6):
    result = functie(x-2*h)-functie(x+2*h) + 8*(functie(x+h) - functie(x-h))
    return result/(12*h)

def Yun_Petcovic_Algorithm(functie):
    row_nr = 2
    for i in [1, 2]:
        kmax = 30
        x0 = -10 + 20 * random.random()
        x1 = -10 + 20 * random.random()
        x = x1
        h = x1 - x0
        if i == 1:
            derivata = G_1
        else:
            derivata = G_2
        delta = (h * derivata(functie, x)) / \
            (derivata(functie, x) - (derivata(functie, x-h)))
        k = 0
        while (abs(delta) >= epsilon and k <= kmax and abs(delta) <= 10**8):
            g_x = derivata(functie, x)
            g_x_h = derivata(functie, x-h)
            if(abs(g_x - g_x_h) <= epsilon):
                x0 = -10 + 20 * random.random()
                x1 = -10 + 20 * random.random()
            delta = (h * derivata(functie, x)) / \
                (derivata(functie, x) - (derivata(functie, x-h)))
            x1 = x
            x = x - delta
            h = -delta
            k = k + 1
        if(abs(derivata(functie,x)) < epsilon):
            print('Mininum gasit este', x, ' folosind derivata ', i, ' dupa ', k, ' iteratii')
            result = "Minimul gasit este " + str(x) + " folosind derivata " + str(i) + " dupa " + str(k) + " iteratii."
        else:
            print('divergenta')
            result = "Divergenta"
        tk.Label(root, text=result).grid(row=row_nr, column=0)
        row_nr += 1

def primul():
    Yun_Petcovic_Algorithm(exemplu_1)
    tk.Label(root, text="Primul exemplu:").grid(row=1, column=0)

def doilea():
    Yun_Petcovic_Algorithm(exemplu_2)
    tk.Label(root, text="Al doilea exemplu:").grid(row=1, column=0)

def treilea():
    Yun_Petcovic_Algorithm(exemplu_3)
    tk.Label(root, text="Al treilea exemplu:").grid(row=1, column=0)

root = tk.Tk()
button = tk.Button(root, text="Primul exemplu", width=25, command=primul)
button.grid(row=0, column=0)
button = tk.Button(root, text="Al doilea exemplu", width=25, command=doilea)
button.grid(row=0, column=1)
button = tk.Button(root, text="Al treilea exemplu", width=25, command=treilea)
button.grid(row=0, column=2)

tk.mainloop()


# print('Primul exemplu')
# Yun_Petcovic_Algorithm(exemplu_1)
# print('\n')
# print('Al doilea exemplu')
# Yun_Petcovic_Algorithm(exemplu_2)
# print('\n')
# print('Al treilea exemplu')
# Yun_Petcovic_Algorithm(exemplu_3)