import copy
import tkinter as tk

class MatriceEconomica:
    def __init__(self, length, other_array, data=None, columns=False):
        self.length = length
        self.other = other_array
        self.data = [[] for _ in range(self.length)]
        if data:
            if columns:
                self.__store_as_columns__(data)
            else:
                self.__store__(data)

    # store data from file in self
    def __store__(self, a):
        for line in a:
            self.append_element(line[1], [line[0], line[2]])

    def __store_as_columns__(self, a):
        for line in a:
            self.append_element(line[2], [line[0], line[1]])

    def append_element(self, line, element):
        pos = self.search_index(self.data[line], element[1])
        if pos != -1:
            self.data[line][pos][0] += element[0]
        else:
            self.data[line].insert(self.get_index_of_insertion(self.data[line], element[1]), copy.deepcopy(element))

    def get_index_of_insertion(self, line, column):
        length = len(line)
        if length == 0:
            return 0
        left = 0
        right = length - 1
        while left < right:
            mid = (left + right) // 2
            if line[mid][1] == column:
                return mid + 1
            if column < line[mid][1]:
                right = mid - 1
            else:
                left = mid + 1
        return left + 1 if column > line[left][1] else left

    def search_index(self, line, column):
        length = len(line)
        left = 0
        right = length - 1
        while left <= right:
            mid = (left + right) // 2
            if line[mid][1] == column:
                return mid
            if column < line[mid][1]:
                right = mid - 1
            else:
                left = mid + 1
        return -1

    def verifyLineLength(self):
        for i in range(len(self)):
            if len(self.data[i]) > 10:
                # print("More than 10 elements on line " + str(i), "  Removing some elements...")
                while len(self.data[i]) > 10:
                    self.data[i].pop()

    def dot_product(self, i, j, other):
        result = self.merge_dot_product(i, j, other)
        return result

    def merge_dot_product(self, i, j, other):
        result = 0
        line1 = self.data[i]
        line2 = other.data[j]
        i = 0
        j = 0
        n = len(line1)
        m = len(line2)
        while i < n and j < m:
            c1 = line1[i][1]
            c2 = line2[j][1]
            if c1 == c2:
                result += line1[i][0] * line2[j][0]
                i += 1
                j += 1
            elif c1 > c2:
                j += 1
            else:
                i += 1
        return result

    def __len__(self):
        return len(self.data)

    def __str__(self):
        str1 = ""
        for i in range(len(self.data)):
            for element in self.data[i]:
                str1 += str((element[0], i, element[1])) + "\n"
        return str1

    def __add__(self, second_array):
        result = MatriceEconomica(self.length, [0 for _ in range(self.length)])
        result.other = [x + y for x, y in zip(self.other, second_array.other)]
        for i in range(len(self)):
            for element in self.data[i]:
                result.append_element(i, element)
        for i in range(len(second_array)):
            for element in second_array.data[i]:
                result.append_element(i, element)
        return result

    def __mul__(self, other):
        result = MatriceEconomica(self.length, [0 for i in range(self.length)])
        for i in range(len(self)):
            for j in range(len(other)):
                product = self.dot_product(i, j, other)
                if product != 0:
                    result.append_element(i, [product, j])
        return result

def line_mapper(l):
    return [float(l[0]), int(l[1]), int(l[2])]

def read_file(filename):
    with open(filename, "r") as f:
        lines = f.readlines()
        length = int(lines[0][:-1])
        data = list(map(line_mapper, list(map(lambda x: x[:-1].split(","), lines[1:]))))
        return length, data

if __name__ == "__main__":

    ### suma
    other_array = []
    (length_a, data_a) = read_file("a.txt")
    a = MatriceEconomica(length_a, other_array, data_a)
    a.verifyLineLength()

    (length_b, data_b) = read_file("b.txt")
    b = MatriceEconomica(length_b, other_array, data_b)
    b.verifyLineLength()

    (length_aplusb, data_aplusb) = read_file("aplusb.txt")
    aplusb = MatriceEconomica(length_aplusb, other_array, data_aplusb)

    result = a + b
    # print("\na+b = \n", result)
    if result == aplusb:
        print("a+b este egal cu suma matricelor din fisierul aplusb.")
    else:
        print("a+b nu este egal cu suma matricelor din fisierul aplusb.")


    ### produsul
    b = MatriceEconomica(length_b, other_array, data_b, True)

    (length_aorib, data_aplusb) = read_file("aorib.txt")
    aorib = MatriceEconomica(length_aorib, other_array, data_aplusb)

    result = a * b
    print("\na*b = \n", result)
    if result == aorib:
        print("a*b este egal cu produsul matricelor din fisierul aorib.")
    else:
        print("a*b nu este egal cu produsul matricelor din fisierul aorib.")

    def suma():
        if result == aplusb:
            tk.Label(root, text='a+b este egal cu suma matricelor din fisierul aplusb.').grid(row=3, column=0)
        else:
            tk.Label(root, text='a+b nu este egal cu suma matricelor din fisierul aplusb.').grid(row=3, column=0)

    def produs():
        if result == aorib:
            tk.Label(root, text='a*b este egal cu produsul matricelor din fisierul aorib.').grid(row=3, column=0)
        else:
            tk.Label(root, text='a*b nu este egal cu produsul matricelor din fisierul aorib.').grid(row=3, column=0)

    root = tk.Tk()
    button = tk.Button(root, text='Suma', width=25, command=suma)
    button.grid(row=1, column=0)
    button = tk.Button(root, text='Produs', width=25, command=produs)
    button.grid(row=1, column=1)
    tk.mainloop()


