import numpy as np
import joblib
import random

EPOCI = 10
RATA_INVATARE = 0.5
MINI_BATCH_SIZE = 2


class ReteaNeuronala(object):
    def __init__(self, layer_dimensions):
        # nr de straturi
        self.nr_straturi = len(layer_dimensions)
        self.layer_dimensions = layer_dimensions
        # initializarea ponderilor si pragurilor cu valori aleatoare distribuite uniform:
        self.weights = [np.random.randn(
            y, x) / np.sqrt(x) for x, y in zip(layer_dimensions[:-1], layer_dimensions[1:])]

    def feedforward(self, a):
        for w in self.weights:
            a = sigmoid(np.dot(w, a))
        return a

    def train(self, dateAntrenare, epoci, rata_invatare=0.5, len_min_grupAntrenare=1):
        nr_grupAntrenare = len(dateAntrenare) // len_min_grupAntrenare
        for j in range(epoci):
            for i in range(0, nr_grupAntrenare):
                grup = dateAntrenare[i *
                                     len_min_grupAntrenare: (i + 1) * len_min_grupAntrenare]
                self.update_grupAntrenare(grup)

            # testare
            correct_cases = self.get_correct_cases(dateAntrenare)
            print(
                f"Epoca {j}: {correct_cases}/{len(dateAntrenare)}, cost: {self.calculare_cost(dateAntrenare)}")

    def get_predictii(self, X):
        return [np.argmax(self.feedforward(x)) for x in X]

    def update_grupAntrenare(self, grup):
        corectii_a = [np.zeros(w.shape) for w in self.weights]
        for x,y in grup:
            delta_corectii_w = self.back_propagation(x,y)
            corectii_a = [nw + dnw for nw,
                          dnw in zip(corectii_a, delta_corectii_w)]

        # Update weights
        self.weights = [w + vw for w, vw in zip(self.weights, corectii_a)]

    def back_propagation(self, x,y):
        corectii_w = [np.zeros(w.shape) for w in self.weights]
        # feedforward
        activare = x
        list_activari = [x]  # toate activarile, strat cu strat
        list_z = []
        for w in self.weights:
            print("w=",w)
            print("activare",activare)
            z = np.dot(w, activare)
            list_z.append(z)
            activare = sigmoid(z)
            list_activari.append(activare)

        delta = (y-list_activari[-1]) * derivata_sigmoid(list_z[-1])
        corectii_w[-1] = np.dot(delta, list_activari[-2].transpose())
        return corectii_w

    def get_correct_cases(self, testData):
        # returns how many cases are correct from the test_data
        test_results = [(np.argmax(self.feedforward(x)), np.argmax(y))
                        for (x, y) in testData]
        return sum(int(x == y) for (x, y) in test_results)

    def calculare_cost(self, dateAntrenare):
        t = [x[1] for x in dateAntrenare]
        iesiri = [self.feedforward(x[0]) for x in dateAntrenare]
        suma = 0
        for y, t in zip(iesiri, t):
            cost_i = (y - t)**2
            suma += sum(cost_i)
        return suma/(2 * len(iesiri))


def sigmoid(z):
    return 1.0/(1.0 + np.exp(-z))

def derivata_sigmoid(z):
    return sigmoid(z)*(1 - sigmoid(z))

def input_optiune():
    option = int(input('1 pentru antrenare, 2 pentru recunoastere: '))
    if option == 1:
        return 'antrenare'
    elif option == 2:
        return 'recunoastere'
    return None


def get_dateAntrenare():
    dateAntrenare = []
    with open('segments.data', 'r') as f:
        for index, line in enumerate(f):
            if index == 0:
                continue
            values = line.split(',')
            values = [int(x) for x in values]
            x = np.array(values[:7]).reshape((7, 1))  # 7 rows and 1 column
            y = np.array(values[7:]).reshape((10, 1))  # 10 rows and 1 column
            dateAntrenare.append((x, y))
    return dateAntrenare


def get_parametriStrat():
    f = open("segments.data", "r")
    first_line = f.readline().split()
    nr_intrari = first_line[0]
    nr_iesiri = first_line[1]
    nr_vectori = first_line[2]
    return (int(nr_intrari), int(nr_iesiri), int(nr_vectori))


def antrenare():
    dateAntrenare = get_dateAntrenare()
    (nr_intrari, nr_iesiri, nr_vectori) = get_parametriStrat()
    result = ReteaNeuronala((nr_intrari, nr_iesiri, nr_vectori))
    result.train(dateAntrenare, EPOCI, RATA_INVATARE, MINI_BATCH_SIZE)
    with open('result.data', 'wb') as f:
        joblib.dump(result, f)  # save results in file


def recunoastere():
    with open('result.data', 'rb') as f:
        model = joblib.load(f)
    x = input('Introdu cifra in binar (separare prin spatii): ')
    x = [int(val) for val in x.split(' ')]
    x = np.array(x).reshape((7, 1))
    predictions = model.get_predictii([x])
    print(f'Este cifra {predictions[0]}')


def main():
    option = input_optiune()
    if option == 'antrenare':
        antrenare()
    elif option == 'recunoastere':
        recunoastere()


if __name__ == '__main__':
    main()
