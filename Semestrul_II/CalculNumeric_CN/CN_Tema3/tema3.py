import tkinter as tk

def citire(filename):
    with open(filename, 'r') as f:
        n = int(f.readline())
        b = []
        a = [[] for _ in range(n)]
        for line in f.readlines():
            if line == '\n':
                continue
            val = float(line.split(',')[0])
            if len(line.split(',')) > 1:
                i = int(line.split(',')[1])
                j = int(line.split(',')[2])
                nuExista = True
                for l in range(len(a[i])):
                    if a[i][l][1] == j:
                        a[i][l][0] += val
                        nuExista = False
                        break
                if nuExista:
                    a[i].append([val, j])
            else:
                b.append(val)
    return a, b

def egale(a, b):
    esp = 1e-16
    for i in range(len(a)):
        for val, j in b[i]:
            for l in range(len(a[i])):
                if a[i][l][1] == j:
                    dif = a[i][l][0] - val
                    if dif > esp:
                        return False
    return True

def aduna(a, b):
    n = len(a)
    rez = a
    for i in range(n):
        for val, j in b[i]:
            nuExista = True
            for l in range(len(rez[i])):
                if rez[i][l][1] == j:
                    rez[i][l][0] += val
                    nuExista = False
                    break
            if nuExista:
                rez[i].append([val, j])
    return rez

def inmultesteVector(a, x):
    vec = []
    for i in range(len(a)):
        sum = 0
        for l in range(len(a[i])):
            sum += a[i][l][0] * x[a[i][l][1]]
        vec.append(sum)
    return vec

def testInmultesteVector(a, b):
    x = [len(b) - i for i in range(len(b))]
    x = inmultesteVector(a, x)
    if x == b:
        return True
    return False

def inmultesteMatrici(a, b):
    mat = []
    for i in range(len(a)):
        linie = []
        for j in range(len(b)):
            sum = 0
            for l in range(len(a[i])):
                bb = 0
                for x in range(len(b[a[i][l][1]])):
                    if b[a[i][l][1]][x][1] == j % (len(b)):
                        bb = b[a[i][l][1]][x][0]
                        break
                sum = sum + bb * a[i][l][0]
            if sum != 0:
                linie.append([sum, j])
        mat.append(linie)
    return mat

def start():
    # adunare
    a = citire('a.txt')[0]
    b = citire('b.txt')[0]
    aplusb = citire('aplusb.txt')[0]
    a = aduna(a, b)
    print("A + B = ", a)
    esteEgal = egale(a, aplusb)
    print("A + B = AplusB: {}".format(egale(a, aplusb)))

    # inmultireVector
    # a, b = citire('a.txt')
    # print("[i] A * x = AoriX: {}".format(testInmultesteVector(a, b)))

    # inmultireMatrice
    a = citire('a.txt')[0]
    b = citire('b.txt')[0]
    aorib = citire('aorib.txt')[0]
    a = inmultesteMatrici(a, b)
    print("A * B = ", a)
    esteEgal = egale(a, aorib)
    print("A * B = AoriB: {}".format(esteEgal))

    def suma():
        if esteEgal == True:
            tk.Label(root, text='a+b este egal cu suma matricelor din fisierul aplusb.').grid(row=3, column=0)
        else:
            tk.Label(root, text='a+b nu este egal cu suma matricelor din fisierul aplusb.').grid(row=3, column=0)

    def produs():
        if esteEgal == True:
            tk.Label(root, text='a*b este egal cu produsul matricelor din fisierul aorib.').grid(row=3, column=0)
        else:
            tk.Label(root, text='a*b nu este egal cu produsul matricelor din fisierul aorib.').grid(row=3, column=0)

    root = tk.Tk()
    button = tk.Button(root, text='Suma', width=25, command=suma)
    button.grid(row=1, column=0)
    button = tk.Button(root, text='Produs', width=25, command=produs)
    button.grid(row=1, column=1)
    tk.mainloop()

if __name__ == '__main__':
    start()