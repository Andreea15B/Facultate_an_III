def problema1(numar, cifra):
    count = 0
    while(numar > 0):
        if numar % 10 == cifra:
            count += 1
        numar = numar // 10
    return count

# print(problema1(123151, 1))

# def problema2(*arg_poz, *kw):
#     print(arg_poz)
#
# problema2(1,2,3,4)

def problema3(matrice):
    for i in range(0, len(matrice)):
        for j in range(0, len(matrice)):
            if i > j :
                matrice[i][j] = 0
    return matrice

# print(problema3(	[[1, 2, 3], [4, 5, 6], [7, 8, 9]]))

def problema4(notes, moves, start):
    new_list = []
    new_list.append(notes[start])
    for i in moves:
        new_list.append(notes[(start + i) % len(notes)])
        start = (start + i) % len(notes)
    return new_list

# print(problema4(["do", "re", "mi", "fa", "sol"], [1, -3, 4, 2], 2))