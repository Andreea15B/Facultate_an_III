import numpy as np
import random

labirint = [
    ['-', '-', '-', '-','LOSE'],
    ['-', 'O', '-', '-','-'],
    ['-', '-', 'O', 'WIN','-'],
    ['-', 'O', '-', '-','-'],
    ['TD', '-', '-', '-','-'],
]

numar_epoci = 100
eta = 0.1
gamma = 0.1
rata_explorare = 0.1


valori_linie = [-1, 0, 1, 0]
valori_coloana = [0, 1, 0, -1]

n = len(labirint[0])

def verificareObstacoleVecini(linie, coloana):
    vecini_linie = [-1, -1, 0, 1, 1, 1, 0, -1]
    vecini_coloana = [0, 1, 1, 1, 0, -1, -1, -1]
    for k in range(0, 8):
        if linie + vecini_linie[k] < 0 or linie + vecini_linie[k] >= n:
            continue
        if coloana + vecini_coloana[k] < 0 or coloana + vecini_coloana[k] >= n:
            continue
        if labirint[linie + vecini_linie[k]][coloana + vecini_coloana[k]] == 'O':
            return False
    return True

R = np.zeros((n,n),dtype=int)
Q = np.zeros((n,n,4))

# initializare recompense
for i in range(n):
    for j in range(n):
        if labirint[i][j] == '-':
            R[i][j] = -0.4
        elif labirint[i][j] == 'O':
            R[i][j] = -99999
        elif labirint[i][j] == 'WIN':
            R[i][j] = 99999
        else:
            R[i][j] = -100

def generare_actiune_random(pozitie):
    posibile_actiuni = []
    for i in range(4):
        if mutareCorecta((pozitie[0] + valori_linie[i],pozitie[1] + valori_coloana[i])):
            posibile_actiuni.append(i)
    return random.choice(posibile_actiuni)

def mutareCorecta(stare):
    if stare[0] < 0 or stare[0] >= n:
        return False
    if stare[1] < 0 or stare[1] >= n:
        return False
    if labirint[stare[0]][stare[1]] == 'O':
        return False
    return True

def generare_actiune_maxima(pozitie):
    valoriQ = []
    for k in range(0,4):
        if mutareCorecta((pozitie[0] + valori_linie[k],pozitie[1] + valori_coloana[k])):
            valoriQ.append(Q[pozitie][k])
        else:
            valoriQ.append(-9999)
    maxim  = max(valoriQ)
    posibile_actiuni = [index for index,value in enumerate(valoriQ) if value == maxim]

    return random.choice(posibile_actiuni)


def antrenare():
    for nr_epoca in range(numar_epoci):
            stare_start = (4,0)
            while True:
                if random.uniform(0,1) <= rata_explorare:
                    actiune = generare_actiune_random(stare_start)
                else:
                    actiune = generare_actiune_maxima(stare_start)
            urmatoarea_stare =(stare_start[0] + valori_linie[actiune],stare_start[1] + valori_coloana[actiune]) 
            recompensa = R[urmatoarea_stare]

            maximQ = max(Q[urmatoarea_stare][actiune] + eta*(recompensa + gamma*maximQ - Q[stare_start]))
            stare_start = urmatoarea_stare

            rezultat = labirint[stare_start[0]][stare_start[1]]
            if rezultat == 'WIN' or rezultat == 'Lose':
                break
            
def rezolvare():
    print(labirint)
    stare = (4,0)
    i = 1
    while True:
        print('Stare: ',i, ':',stare)
        valoare = labirint[stare[0]][stare[1]]
        if valoare == 'WIN' or valoare == 'LOSE':
            print(R[stare])
            print(valoare)
            break
        actiune = generare_actiune_maxima(stare)
        urmatoarea_stare =(stare[0] + valori_linie[actiune],stare[1] + valori_coloana[actiune]) 
        stare = urmatoarea_stare
        i = i + 1


def TEMA():
    # antrenare()
    rezolvare()

TEMA()

