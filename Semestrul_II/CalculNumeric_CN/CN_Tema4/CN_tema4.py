import numpy as np
import tkinter as tk

epsilon = 10 ** -9
kMax = 10000


def citire_a(filename):
    with open(filename, 'r') as f:
        n = int(f.readline())
        A = [[] for _ in range(n)]
        for line in f.readlines():
            if line != '\n':
                val = float(line.split(',')[0])
                i = int(line.split(',')[1])
                j = int(line.split(',')[2])
                nuExista = True
                for l in range(len(A[i])):
                    if A[i][l][1] == j:
                        A[i][l][0] += val
                        nuExista = False
                        break
                if nuExista:
                    A[i].append([val, j])
    return A

def citire_b(filename):
    with open(filename, 'r') as f:
        n = int(f.readline())
        b = []
        for line in f.readlines():
            val = float(line.split(' ')[0])
            b.append(val)
    return b

def solve_Gauss_Sidel(matrix, vector):
    length = len(matrix)
    solution = np.zeros((length, ))
    aux = vector
    k = 0
    norma = np.linalg.norm(aux - solution)
    while k <= kMax and norma >= epsilon and norma <= 10**8:
        for i in range(length):
            bi = vector[i]
            suma = 0
            aii = 0
            for j in range(len(matrix[i])):
                if matrix[i][j][1] == i:
                    aii = matrix[i][j][0]
                else:
                    suma += matrix[i][j][0] * solution[matrix[i][j][1]]

            solution[i] = (bi - suma) / aii
        norma = np.linalg.norm(aux - solution)
        aux = np.array(solution)
        k = k+1
    if norma < epsilon:
        return solution
    else:
        return 'divergenta'

def multiply_vector(matrix, vector):
    result = np.array([])
    for line in matrix:
        s = 0
        for element in line:
            s += element[0] * vector[element[1]]
        result = np.append(result, s)
    return result

if __name__ == "__main__":
    root = tk.Tk()
    file_a = "a_2.txt"
    file_b = "b_2.txt"
    matrix_A = citire_a(file_a)
    vector_B = citire_b(file_b)
    
    for i in range(len(matrix_A)):
        ok = False
        for j in range(len(matrix_A[i])):
            if i == matrix_A[i][j][1] and abs(matrix_A[i][j][0]) > epsilon:
                ok = True
                break
        if not ok:
            raise "Matricea are 0 pe diagonala."

    solution = solve_Gauss_Sidel(matrix_A, vector_B)
    print(solution)
    tk.Label(root, text='Using files ' + file_a + " and " + file_b).grid(row=0, column=0)
    tk.Label(root, text='Solution: ' + format(solution)).grid(row=1, column=0)

    if (str(solution) != "divergenta"):
        norma = np.linalg.norm(multiply_vector(matrix_A, solution) - vector_B)
        print(norma)
        tk.Label(root, text='Norma: ' + format(norma)).grid(row=2, column=0)
    tk.mainloop()
