import random
import numpy as np
import math

dl = [-1, 0, 1, 0]
dc = [0, 1, 0, -1]

def generare_matrice(n_lins, n_cols):
    A = [None] * n_lins
    for i in range(0, n_lins):
        A[i] = [0] * n_cols
    return A

class Labirint:
    def __init__(self, dimensiune=10, nrObstacole=3):
        self.dimensiune = dimensiune
        self.nrObstacole = nrObstacole
        self.generare_labirint()

    def __str__(self):
        result = ''
        for i in range(0, self.dimensiune):
            for j in range(0, self.dimensiune):
                result += str(self.table[i][j]) + ' '
            result += '\n'
        return result

    def generare_labirint(self):
        self.table = [None] * self.dimensiune
        for i in range(0, self.dimensiune):
            self.table[i] = ['0'] * self.dimensiune
        # generare obstacole
        self.obstacole = []
        for i in range(0, self.nrObstacole):
            while True:
                lin = random.randint(0, self.dimensiune - 1)
                col = random.randint(0, self.dimensiune - 1)
                if self.nuAreObstacolVecin(lin, col) and self.table[lin][col] == '0':
                    self.table[lin][col] = 'X'
                    self.obstacole.append((lin, col))
                    break
        # generare start, win si lose
        values = ['S', 'W', 'L']
        for value in values:
            while True:
                lin = random.randint(0, self.dimensiune - 1)
                col = random.randint(0, self.dimensiune - 1)
                if self.nuAreObstacolVecin(lin, col) and self.table[lin][col] == '0':
                    self.table[lin][col] = value
                    if value is 'S':
                        self.start = (lin, col)
                    elif value is 'W':
                        self.win = (lin, col)
                    else:
                        self.lose = (lin, col)
                    break

    def nuAreObstacolVecin(self, lin, col):
        dl = [-1, -1, 0, 1, 1, 1, 0, -1]
        dc = [0, 1, 1, 1, 0, -1, -1, -1]
        for k in range(0, 8):
            if lin + dl[k] < 0 or lin + dl[k] >= self.dimensiune:
                continue
            if col + dc[k] < 0 or col + dc[k] >= self.dimensiune:
                continue
            if self.table[lin + dl[k]][col + dc[k]] is 'X':
                return False
        return True


class QLearning:
    def __init__(self, labirint: Labirint):
        self.R = generare_matrice(labirint.dimensiune, labirint.dimensiune)
        self.Q = np.zeros((labirint.dimensiune, labirint.dimensiune, 4))
        self.labirint = labirint
        for i in range(0, labirint.dimensiune):
            for j in range(0, labirint.dimensiune):
                if labirint.table[i][j] is '0' or labirint.table[i][j] is 'S':
                    self.R[i][j] = -0.4
                elif labirint.table[i][j] is 'X':
                    self.R[i][j] = -math.inf
                elif labirint.table[i][j] is 'W':
                    self.R[i][j] = 1000.0
                else:
                    self.R[i][j] = -1000.0
        self.R = np.asarray(self.R)

    def antrenare(self, epoci=100, alfa=0.1, gamma=0.2, rata_explorare=0.1):
        for nr_epoca in range(0, epoci):
            self.stare = tuple(self.labirint.start)
            # cat timp nu exista stare de win/lose
            while True:
                if random.uniform(0, 1) <= rata_explorare:
                    # explorare
                    actiune = self.get_actiune_random(self.stare)
                else:
                    actiune = self.get_BestAction(self.stare)
                nextState, recompensa = self.doAction(actiune)

                max_next_Q = max([self.Q[nextState][k] for k in range(0, 4)])
                # update Q
                self.Q[self.stare][actiune] = self.Q[self.stare][actiune] + alfa * \
                                              (recompensa + gamma * max_next_Q - self.Q[self.stare][actiune])

                self.stare = nextState
                value = self.labirint.table[self.stare[0]][self.stare[1]]
                if value is 'W' or value is 'L':
                    break

            # print(f'Finished epoch {nr_epoca}')

    def rezolva(self):
        print('\n\n')
        print(self.labirint)
        print('Prezicere drum:')
        self.stare = self.labirint.start
        while True:
            print(self.stare)
            value = self.labirint.table[self.stare[0]][self.stare[1]]
            if value is 'W' or value is 'L':
                break
            actiune = self.get_BestAction(self.stare)
            nextState, _ = self.doAction(actiune)
            self.stare = nextState

    def doAction(self, actiune):
        nextState = (self.stare[0] + dl[actiune], self.stare[1] + dc[actiune])
        recompensa = self.R[nextState]
        return nextState, recompensa

    def get_BestAction(self, stare):
        q_values = []
        actiuni = []
        lin, col = stare
        for k in range(0, 4):
            if self.estePosibilaStarea((lin + dl[k], col + dc[k])):
                q_values.append(self.Q[stare][k])
            else:
                q_values.append(-math.inf)
        max_q = max(q_values)
        for index, value in enumerate(q_values):
            if value == max_q:
                actiuni.append(index)
        return random.choice(actiuni)

    def get_actiune_random(self, stare):
        actiuni = []
        lin, col = stare
        for k in range(0, 4):
            if self.estePosibilaStarea((lin + dl[k], col + dc[k])):
                actiuni.append(k)
        return random.choice(actiuni)

    def estePosibilaStarea(self, stare):
        lin, col = stare
        if lin < 0 or lin >= self.labirint.dimensiune:
            return False
        if col < 0 or col >= self.labirint.dimensiune:
            return False
        if self.labirint.table[lin][col] == 'X':
            return False
        return True

    def get_nextState(self, stare):
        return random.choice(self.get_next_states(stare))

    def get_next_states(self, stare):
        posibilitati = []
        lin, col = stare
        for k in range(0, 4):
            if lin + dl[k] < 0 or lin + dl[k] >= self.labirint.dimensiune:
                continue
            if col + dc[k] < 0 or col + dc[k] >= self.labirint.dimensiune:
                continue
            if self.labirint[lin + dl[k]][col + dc[k]] != 'X':
                nextState = (lin+dl[k], col + dc[k])
                nextAction = k
                posibilitati.append(nextState, nextAction)
        return posibilitati


if __name__ == '__main__':
    labirint = Labirint(dimensiune=10, nrObstacole=5)
    q_learning = QLearning(labirint)
    q_learning.antrenare(epoci=1000, alfa=1.5, gamma=0.1, rata_explorare=0.1)
    q_learning.rezolva()