def problema1():
    s = 0
    for i in range(1, 11):
        s += i
    return s

def problema2(n, m):
    while 1:
        c = m - 2*n
        if c <= 0 or c==n:
            break
        m = n
        n = c
    return (n, m)
    # recursiv: ??
    # if n <= 0 or n == m or n == 1:
    #     return (n, m)
    # (a, b) = problema2(m-2*n, n)
    # return (a, b)

def problema3(m):
    list = []
    for i in range(2, m):
        flag = 1
        for j in range(2, i//2+1):
            if i%j == 0:
                flag = 0
                break
        if flag == 1:
            list.append(i)
    return list

def problema4(my_list):
    new_list = []
    for i in my_list:
        if isinstance(i, int):
            new_list.append(i)
    new_list.sort(reverse=True)
    return new_list

# print(problema4([1, 2, 'trei', 4, [5, 6]]))

def problema5(n):
    n = int(n, 8)
    reversed = str(n)[::-1]
    if int(reversed) == n:
        return True
    return False

# problema5("0o171")

def problema7(matrix):
    new_list = []
    for i in range(0, len(matrix[0])):
        nr = matrix[0][i]
        flag = 0
        for j in range(1, len(matrix)):
            if matrix[j][i] != nr:
                flag = 1
        if flag == 0:
            new_list.append(0)
        else:
            new_list.append(1)
    return new_list

print(problema7([[1, 2, 1], [1, 3, 1], [1, 3, 1], [1, 3, 1]]))