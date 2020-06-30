# m = 1
# while 1 + pow(10, -m) != 1:
#     m += 1
#
# m -= 1
# print("Cel mai mic u este: ", pow(10, -m), ", cu m = ", m)
# u = pow(10, -m)
#
# x = 1.0
# y = u
# z = u
#
# first = (x + y) + z
# second = x + (y + z)
# print(first, second)
# if first != second:
#     print("Adunarea nu este asociativa :)")
#
# x = 1.0
# first = (x * y) * z
# second = x* (y * z)
# while first == second:
#     x += 1.0
#     first = (x * y) * z
#     second = x * (y * z)
#
# print(first, second)
# if first != second:
#     print("Inmultirea nu este asociativa pentru: x=", x, ", y=", y, ", z=", z)

import tkinter as tk

ex = input("Ex 1, 2 sau 3?\n")

if int(ex) == 1:
    master = tk.Tk()
    master.wm_title("Ex 1")
    tk.Label(master, text='m = ').grid(row=0)
    e1 = tk.Entry(master)
    e1.grid(row=0, column=1)

    def find_u():
        global e1
        m = int(e1.get())
        tk.Label(master, text='m = ').grid(row=3, column=0)
        tk.Label(master, text = m).grid(row=3, column=1)

        while 1 + pow(10, -m) != 1:
            m += 1
        m -= 1

        tk.Label(master, text='Cel mai mic u este: ').grid(row=4, column=0)
        tk.Label(master, text=pow(10, -m)).grid(row=4, column=1)

    button = tk.Button(master, text='Află precizia mașină', width=25, command=find_u)
    button.grid(row=1, column=1)

    tk.mainloop()

if int(ex) == 2:
    m = 1
    while 1 + pow(10, -m) != 1:
        m += 1
    m -= 1
    u = pow(10, -m)
    x = 1.0
    y = u
    z = u
    master = tk.Tk()
    master.wm_title("Ex 2")
    tk.Label(master, text='x = 1.0').grid(row=0)
    tk.Label(master, text='y = u').grid(row=1)
    tk.Label(master, text='z = u').grid(row=2)
    def asoc():
        global x
        first = (x + y) + z
        second = x + (y + z)
        if first != second:
            tk.Label(master, text='Adunarea nu este asociativă.').grid(row=4)

        first = (x * y) * z
        second = x* (y * z)
        while first == second:
            x += 1.0
            first = (x * y) * z
            second = x * (y * z)

        if first != second:
            tk.Label(master, text='Inmultirea nu este asociativă pentru x = ').grid(row=5)
            tk.Label(master, text=x).grid(row=5, column=1)

    button = tk.Button(master, text='Verifică asociativitatea', width=25, command=asoc)
    button.grid(row=3, column=0)
    tk.mainloop()

if int(ex) == 3:
    import math

    n = 6
    m = int(math.log2(n))
    print("m = ", m)
    p = int(n / m) + 1
    print("p = ", p)
    A = [[0, 1, 0, 1, 1, 1],
         [1, 0, 1, 1, 0, 1],
         [0, 1, 1, 0, 0, 0],
         [0, 1, 0, 0, 1, 1],
         [1, 0, 1, 0, 0, 1],
         [0, 1, 1, 1, 0, 1]]

    B = [[1, 1, 1, 1, 1, 1],
         [1, 0, 1, 1, 1, 1],
         [1, 1, 0, 0, 1, 0],
         [1, 1, 1, 1, 0, 1],
         [1, 0, 1, 0, 1, 1],
         [0, 1, 1, 0, 0, 0]]


    def submatrice_coloane(matrice):
        submatrici = []
        for i in range(1, p):
            submatrice = []
            l = m * (i - 1) + 1
            c = m * i
            for k in range(l - 1, c):
                value = []
                for i in range(0, n):
                    value.append(matrice[i][k])
                submatrice.append(value)
            submatrici.append(submatrice)
        if (len(submatrici) <= p):
            diff = p - len(submatrici) + 1
            values = []
            for k in range(0, diff):
                value = []
                for i in range(0, n):
                    value.append(0)
                values.append(value)
            submatrici.append(values)
        print("submatrici din A: ", submatrici)
        return submatrici

    def submatrice_linii(matrice):
        submatrici = []
        for i in range(1, p):
            submatrice = []
            l = m * (i - 1) + 1
            c = m * i
            for k in range(l - 1, c):
                value = []
                for i in range(0, n):
                    value.append(matrice[k][i])
                submatrice.append(value)
            submatrici.append(submatrice)
        if (len(submatrici) <= p):
            diff = p - len(submatrici) + 1
            values = []
            for k in range(0, diff):
                value = []
                for i in range(0, n):
                    value.append(0)
                values.append(value)
            submatrici.append(values)
        print("submatrici din B: ", submatrici)
        return submatrici

    def find_k(number):
        k = 0
        power = 2 ** k
        while power < number:
            k = k + 1
            power = 2 ** k
        if k == 0:
            return 0
        return k - 1

    def NUM(array):
        length = len(array)
        value = 0
        power = 0
        for i in range(length-1, -1, -1):
            value = value + (2 ** power) * array[i]
            power = power + 1
        return value

    def SUM(array1, array2):
        sum = []
        for i in range(0, len(array1)):
            sum.append(array1[i] ^ array2[i])
        return sum

    def matrix_product():
        matrix_C = []
        matrix_A = submatrice_coloane(A)
        matrix_B = submatrice_linii(B)
        for i in range(0, p):
            sum_linii = []
            value = []
            matrix_ci = []
            for _ in range(0, n):
                value.append(0)
            sum_linii.append(value)

            for j in range(1, int(2 ** m) - 1):
                k = find_k(j)
                aux = j - int(2 ** k)
                value = SUM(sum_linii[aux], matrix_B[i][k + 1])
                sum_linii.append(value)
            for r in range(0, n):
                line_A = [matrix_A[i][o][r] for o in range(0, m)]
                line_C = sum_linii[NUM(line_A) - 1]
                matrix_ci.append(line_C)
            matrix_C.append(matrix_ci)
        return matrix_C


    def final_program():
        matrix_C = matrix_product()
        length = len(matrix_C)
        final = []
        sum_inital = matrix_C[0]
        for i in range(1, length):
            value = []
            for j in range(0, len(matrix_C[i])):
                value.append(SUM(matrix_C[i][j], sum_inital[j]))
            final.append(value)
        return final[0]


    print(final_program())