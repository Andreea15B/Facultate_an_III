from itertools import combinations

# 1.
def fibonacci(n):
    if n <= 0:
        return []
    if n == 1:
        return [1]
    numbers = [0, 1]
    for i in range(2, n):
        numbers.append(numbers[i-2] + numbers[i-1])
    return numbers

# 2.
def isPrime(nr):
    if nr < 1:
        return False
    for i in range(2, nr//2+1):
        if (nr % i) == 0:
            return False
    return True

def nr_prime(aList):
    bList = list()
    for i in range(len(aList)):
        if isPrime(aList[i]):
            bList.append(aList[i])
    return bList

# 5.
def combinatii(xList, k):
    listComb = list()
    comb = combinations(xList, k)
    for i in list(comb):
        listComb.append(i)
    return listComb
print(combinatii([1,2,3,4], 3))

# 6.
def elements(x, *lists):
    newList = []
    for l in lists:
        newList += l
    return [e for e in set(newList) if newList.count(e) == x]
print(elements(2, [1, 2, 3], [2, 3, 4], [4, 5, 6], [4, 1, "test"]))