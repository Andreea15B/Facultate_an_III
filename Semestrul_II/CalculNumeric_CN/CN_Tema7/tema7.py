import numpy as np
import random
import math
import tkinter as tk

epsilon = 1e-8

def semn(number):
    if(number <= 0):
        return 1
    else:
        return -1

def interval(polinom):
    coeficienti = polinom.coefficients
    a_0 = coeficienti[0]
    A = max(coeficienti)
    R = int((abs(a_0) + A) / abs(a_0))
    return (-R, R)

def horner(polinom, x):
    coeficienti = polinom.coefficients
    suma = 0
    for coeficient in coeficienti:
        suma = suma * x + coeficient
    return suma

def H(n, polinom, x):
    derivata_1 = polinom.deriv()
    derivata_2 = polinom.deriv(m=2)
    result = ((n-1)**2) * (horner(derivata_1, x)**2)
    result = result - n*(n-1)*horner(polinom, x)*horner(derivata_2, x)
    return result

def delta_function(n, polinom, x):
    derivata_1 = polinom.deriv()
    result = n*horner(polinom, x)
    value = horner(derivata_1, x)
    result = int(result / (value + semn(value)* math.sqrt(H(n, polinom, x))))
    return result

def Laguerre(polinom):
    x = x0 = -10 + 20 * random.random()
    k = 0
    kmax = 10
    grad = len(polinom.coefficients) - 1
    delta = delta_function(grad, polinom, x)
    while abs(delta) >= epsilon and k <= kmax and abs(delta) <= 10**8:
        if H(grad, polinom, x) < 0:
            break
        derivata_1 = polinom.deriv()
        value = horner(derivata_1, x)
        aux = value + semn(value) * math.sqrt(H(grad, polinom, x))
        if abs(aux) <= epsilon:
            break
        delta = delta_function(grad, polinom, x)
        x = x - delta
        k = k + 1

    if abs(delta) < epsilon:
        return x
    else:
        return 'divergenta'


root = tk.Tk()

def display_polynomial(poly1d_obj):
    sup_line, peer_line = str(poly1d_obj).splitlines()
    peer_line = "Ecuatie:  " + peer_line
    tk.Label(root, text="{}\n{}".format(sup_line, peer_line)).grid(row=2, column=0)

def primul():
    print('\n')
    print('Exemplul 1')
    tk.Label(root, text="Exemplul 1: ").grid(row=1, column=0)
    exemplu_1 = [1, -6, 11, -6]

    p = np.poly1d(exemplu_1)
    print(p)
    display_polynomial(p)

    print('Solutii de aproximat [1,2,3]')
    tk.Label(root, text="Solutii de aproximat [1,2,3]").grid(row=3, column=0)

    print('Interval [-R,R]:', interval(np.poly1d(exemplu_1)))
    val = "Interval [-R,R]: " + str(interval(np.poly1d(exemplu_1)))
    tk.Label(root, text=val).grid(row=4, column=0)

    row_nr = 5
    for i in range(1, 20):
        result = Laguerre(np.poly1d(exemplu_1))
        f = open("results.txt","r")
        values_files = f.readlines()
        ok = True
        for line in values_files:
             if line != 'divergenta\n':
                if abs(float(line) - result) <= epsilon:
                    ok = False
                    break
        f = open("results.txt","w")
        if ok == True:
            f.write(str(result))
            f.write("\n")
        print(result)
        tk.Label(root, text=result).grid(row=row_nr, column=0)
        row_nr += 1

def doilea():
    print('\n')
    print('Exemplul 2')
    tk.Label(root, text="Exemplul 2: ").grid(row=1, column=0)
    exemplu_2 = [44, -55, -42, 49, -6]

    p = np.poly1d(exemplu_2)
    print(p)
    display_polynomial(p)

    print('Solutii de aproximat [2/3, 1/7, -1, 1.5]')
    tk.Label(root, text="Solutii de aproximat [2/3, 1/7, -1, 1.5]").grid(row=3, column=0)

    print('Interval [-R,R]:', interval(np.poly1d(exemplu_2)))
    val = "Interval [-R,R]: " + str(interval(np.poly1d(exemplu_2)))
    tk.Label(root, text=val).grid(row=4, column=0)

    row_nr = 5
    for i in range(1, 20):
        result = Laguerre(np.poly1d(exemplu_2))
        f = open("results.txt","r")
        values_files = f.readlines()
        ok = True
        for line in values_files:
            if line != 'divergenta\n':
                if abs(float(line) - result) <= epsilon:
                    ok = False
                    break
        f = open("results.txt","w")
        if ok == True:
            f.write(str(result))
            f.write("\n")
        print(result)
        tk.Label(root, text=result).grid(row=row_nr, column=0)
        row_nr += 1

def treilea():
    print('\n')
    print('Exemplul 3')
    tk.Label(root, text="Exemplul 3: ").grid(row=1, column=0)
    exemplu_3 = [8, -38, 49, -22, 3]

    p = np.poly1d(exemplu_3)
    print(p)
    display_polynomial(p)

    print('Solutii de aproximat [1, 0.5, 3, 0.25]')
    tk.Label(root, text="Solutii de aproximat [1, 0.5, 3, 0.25]").grid(row=3, column=0)

    print('Interval [-R,R]:', interval(np.poly1d(exemplu_3)))
    val = "Interval [-R,R]: " + str(interval(np.poly1d(exemplu_3)))
    tk.Label(root, text=val).grid(row=4, column=0)

    row_nr = 5
    for i in range(1, 20):
        result = Laguerre(np.poly1d(exemplu_3))
        f = open("results.txt","r")
        values_files = f.readlines()
        ok = True
        for line in values_files:
            if line != 'divergenta\n':
                if abs(float(line) - result) <= epsilon:
                    ok = False
                    break
        f = open("results.txt","w")
        if ok == True:
            f.write(str(result))
            f.write("\n")
        print(result)
        tk.Label(root, text=result).grid(row=row_nr, column=0)
        row_nr += 1
    
def patrulea():
    print('\n')
    print('Exemplu 4')
    tk.Label(root, text="Exemplul 4: ").grid(row=1, column=0)
    exemplu_4 = [1, -6, 13, -12, 4]

    p = np.poly1d(exemplu_4)
    print(p)
    display_polynomial(p)

    print('Solutii de aproximat [1, 2]')
    tk.Label(root, text="Solutii de aproximat [1, 2]").grid(row=3, column=0)

    print('Interval [-R,R]:', interval(np.poly1d(exemplu_4)))
    val = "Interval [-R,R]: " + str(interval(np.poly1d(exemplu_4)))
    tk.Label(root, text=val).grid(row=4, column=0)

    row_nr = 5
    for i in range(1, 20):
        result = Laguerre(np.poly1d(exemplu_4))
        f = open("results.txt","r")
        values_files = f.readlines()
        ok = True
        for line in values_files:
           if line != 'divergenta\n':
                if abs(float(line) - result) <= epsilon:
                    ok = False
                    break
        f = open("results.txt","w")
        if ok == True:
            f.write(str(result))
            f.write("\n")
        print(result)
        tk.Label(root, text=result).grid(row=row_nr, column=0)
        row_nr += 1


button = tk.Button(root, text="Exemplul 1", width=25, command=primul)
button.grid(row=0, column=0)
button = tk.Button(root, text="Exemplul 2", width=25, command=doilea)
button.grid(row=0, column=1)
button = tk.Button(root, text="Exemplul 3", width=25, command=treilea)
button.grid(row=0, column=2)
button = tk.Button(root, text="Exemplul 4", width=25, command=patrulea)
button.grid(row=0, column=3)
tk.mainloop()
