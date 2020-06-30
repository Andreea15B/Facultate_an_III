import math
import tkinter as tk
import numpy as np
import copy
from numpy.linalg import inv


def displayMatrix(m, size):
    for i in range(size):
        for j in range(size):
            print(str(m[i][j]), end=" ")
        print()
    print()

# splits the matrix into upper and lower triangular matrices
def descompunereMatrix(A, size):
    for i in range(0, size):
        computeRow(i, A, size)

# computes elements from a specific row for the upper matrix
def computeRow(row, A, size):
    for j in range(0, size):
        if j < row:
            A[row][j] = (A[row][j] - computeSum1(j, row)) / A[j][j]
        else:
            A[row][j] -= computeSum2(row, j)

# calculates the sum of (elements from columns from L)*(elements from rows from U)

def computeSum1(column, row):
    result = 0.0
    for k in range(0, column):
        if row == column:
            result += A[k][column]
        else:
            result += A[row][k] * A[k][column]
    return result

# calculates the sum of (elements from the columns of L)*(elements from the rows of U)


def computeSum2(row, column):
    result = 0.0
    for k in range(0, row):
        result += A[row][k] * A[k][column]
    return result


def read_matrix():
    with open("input_matrix.txt", "r") as f:
        first_line = f.readline().split(" ")
        size = int(first_line[0])
        epsilon = int(first_line[1])
        the_input = [list(map(float, line.split(" "))) for line in f]
        A = []
        b = []
        for i in range(0, size):
            A.append(the_input[i])
            b.append(the_input[len(the_input)-i-1])
        b = [val for sublist in b for val in sublist]  # un-nest list
        A_init = copy.deepcopy(A)
        return (size, A, A_init, b, epsilon)


def splitLU(A, size):
    L = [[0.0 for i in range(size)] for j in range(size)]
    U = [[0.0 for i in range(size)] for j in range(size)]
    for i in range(0, size):
        for j in range(0, size):
            if i == j:
                L[i][j] = 1.0
            if i > j:
                L[i][j] = A[i][j]
    for i in range(0, size):
        for j in range(0, size):
            if i <= j:
                U[i][j] = A[i][j]
            else:
                U[i][j] = 0.0
    return (L, U)


def computeDeterminant(matrix, size):
    result = 1.0
    for i in range(0, size):
        result *= matrix[i][i]
    return result

# computes equations for the i-th row, for direct substitution method
def computeSumDirect(row, A, X):
     result = 0.0
     for j in range(0, row):
        result += A[row][j] * X[j]
     return result

# implements the "direct substitution method" on lower triangular matrix
def computeDirectSubstitution(A, X, b, size):
     for i in range(0, size):
        X[i] = b[i] - computeSumDirect(i, A, X)

# computes equations for the i-th row, for direct substitution method


def computeSumReverse(row, A, X, size):
    result = 0.0
    for j in range(row+1, size):
        result += A[row][j] * X[j]
    return result

# implements the "reverse substitution method" on upper triangular matrix
def computeReverseSubstitution(A, X, b, size):
     for i in range(size-1, -1, -1):
        X[i] = (b[i] - computeSumReverse(i, A, X, size))/A[i][i]

# verify the solution by computing euclidian norm of the error of the vector A_init * X - b
def verify(A_init, X, b, size):
    error = 0.0
    for i in range(0, size):
        aux = 0.0
        for j in range(0, size):
            aux += A_init[i][j] * X[j]
        error += math.pow(aux - b[i], 2.0)
    error = math.sqrt(error)

    return error < math.pow(10, -8)

def norm(x,y):
     value = 0.0
     for i in range(0,len(x)):
          value = value + pow(x[i]-y[i],2)
     return math.sqrt(value)

if __name__ == '__main__':
    (size, A, A_init, b, epsilon) = read_matrix()
    print("Matricea A:")
    displayMatrix(A, size)

    # descompunere
    descompunereMatrix(A, size)
    print("Descompunerea matricei A:")
    displayMatrix(A, size)
    (L, U) = splitLU(A, size)
    print("Matricea L:")
    displayMatrix(L, size)
    print("Matricea U:")
    displayMatrix(U, size)

    # determinant
    det_A = computeDeterminant(A, size)
    print("det(A) = ", det_A)
    det_L = computeDeterminant(L, size)
    print("det(L) = ", det_L)
    det_U = computeDeterminant(U, size)
    print("det(U) = ", det_U)
    print("det(A) = det(L)*det(U) = ", det_L*det_U)

    # metoda substitutiei directe
    X_lu = [None] * size
    print("\nVectorul termenilor liberi: ", b)
    Y = [None] * size
    computeDirectSubstitution(L, Y, b, size)
    # metoda substitutiei inverse
    computeReverseSubstitution(U, X_lu, Y, size)
    print("X = ", X_lu)

    # verificare solutie
    print("Verificare norma: ", verify(A_init, X_lu, b, size))

    X_lib = np.linalg.solve(A_init, b)
    print("Library solution: \n", X_lib)

    Ainv_Lu = inv(A_init)
    print("Inversa A din librarie:", Ainv_Lu)

    print("||x_lu - x_lib|| : ", norm(X_lu, X_lib))

    prod =  Ainv_Lu.dot(b)
    print("||x_lu - Ainv*b||: ", norm(X_lu, prod))

    #A^-1 = U^-1 * L^-1
    Uinv = inv(U)
    Linv = inv(L)

    Ainv_lib = np.dot(Uinv, Linv)
    print("Ainv folosind LU: ", Ainv_lib)

    C = Ainv_Lu - Ainv_lib

    max = 0
    for i in range(0, size):
        suma = 0
        for j in range(0, size):
            suma += abs(C[i][j])
        if suma > max:
            max = suma
    print("C = ", max)


root = tk.Tk()

def descompunere():
    global A
    tk.Label(root, text="Matricea A este: ").grid(row=3, column=0)
    j = 4
    for i in range(0, size):
        tk.Label(root, text=A[i]).grid(row=j, column=0)
        j += 1

    tk.Label(root, text="Matricea L este: ").grid(row=j+1, column=0)
    j = j+2
    for i in range(0, size):
        tk.Label(root, text=L[i]).grid(row=j, column=0)
        j += 1

    tk.Label(root, text="Matricea U este: ").grid(row=j+1, column=0)
    j = j+2
    for i in range(0, size):
        tk.Label(root, text=U[i]).grid(row=j, column=0)
        j += 1


def determinant():
    tk.Label(root, text="det(A) = det(L)*det(U) = ").grid(row=2, column=0)
    tk.Label(root, text=det_A).grid(row=2, column=1)

def inversa():
    tk.Label(root, text="Inversa A cu libraria este: ").grid(row=2, column=0)
    j = 3
    for i in range(0, size):
        tk.Label(root, text=Ainv_lib[i]).grid(row=j, column=0)
        j += 1

def inversaLU():
    tk.Label(root, text="Inversa A cu LU este: ").grid(row=2, column=0)
    j = 3
    for i in range(0, size):
        tk.Label(root, text=Ainv_Lu[i]).grid(row=j, column=0)
        j += 1

def solX():
    tk.Label(root, text="Solutia X cu LU este: ").grid(row=2, column=0)
    tk.Label(root, text=X_lu).grid(row=2, column=1)
    tk.Label(root, text="Solutia X cu libraria este: ").grid(row=3, column=0)
    tk.Label(root, text=X_lib).grid(row=3, column=1)

button = tk.Button(root, text="Descompunere matrice",
                   width=25, command=descompunere)
button.grid(row=1, column=0)
button = tk.Button(root, text="Determinantul lui A",
                   width=25, command=determinant)
button.grid(row=1, column=1)
button = tk.Button(root, text="Inversa A cu libraria",
                   width=25, command=inversa)
button.grid(row=1, column=2)
button = tk.Button(root, text="Inversa A cu LU",
                   width=25, command=inversaLU)
button.grid(row=1, column=3)
button = tk.Button(root, text="Solutia X",
                   width=25, command=solX)
button.grid(row=1, column=4)
tk.mainloop()
