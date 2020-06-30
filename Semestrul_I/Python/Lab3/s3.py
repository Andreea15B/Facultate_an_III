def problema1(n):
    suma = 0
    for i in range(1, n+1):
        suma += i
    return suma

def problema5(n):
    nr = int(n, 8)
    if str(nr) == str(nr)[::-1]:
        return True
    else:
        return False

# print(problema1(199))
# print(problema5('1010'))