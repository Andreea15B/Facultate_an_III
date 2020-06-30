# 1. cmmdc
# x = input('Enter numbers separated by a space:')
# alist = [int(i) for i in x.split()]
# print(alist)
# cmmdc = alist[0]
# for i in (1, len(alist)-1):
#     b = alist[i]
#     while cmmdc != b:
#         if cmmdc > b:
#             cmmdc = cmmdc - b
#         else:
#             b = b - cmmdc
# print(cmmdc)

# 2. nr vocale
# sir = input('Sir: ')
# nr_vocale = 0
# for letter in sir:
#     if letter in "aeiouAEIOU":
#         nr_vocale = nr_vocale + 1
# print(nr_vocale)

# 3. nr occurrences of first string in a second
# sir1 = input("Sir1: ")
# sir2 = input("Sir2: ")
# count = sir1.count(sir2)
# print("Sirul1 se afla in sirul2 de", count, "ori.")

# 4. convert UpperCamelCase into lowercase_with_underscores
# sir = input("Sir: ")
# new_sir = sir[0].lower()
# for i in range(1, len(sir)):
#     if sir[i] >= 'A' and sir[i] <= 'Z':
#         if i == 0:
#             new_sir += sir[i].lower()
#         else:
#             new_sir += '_'
#             new_sir += sir[i].lower()
#     else:
#         new_sir += sir[i]
# print(new_sir)

# 5. matrix in spiral order
def spiralPrint(m, n, a):
    k = 0
    l = 0

    ''' k - starting row index 
        m - ending row index 
        l - starting column index 
        n - ending column index 
        i - iterator '''

    while (k < m and l < n):
        # Print the first row from
        # the remaining rows
        for i in range(l, n):
            print(a[k][i], end="")
        k += 1

        # Print the last column from
        # the remaining columns
        for i in range(k, m):
            print(a[i][n - 1], end="")
        n -= 1

        # Print the last row from
        # the remaining rows
        if (k < m):
            for i in range(n - 1, (l - 1), -1):
                print(a[m - 1][i], end="")
            m -= 1

        # Print the first column from
        # the remaining columns
        if (l < n):
            for i in range(m - 1, k - 1, -1):
                print(a[i][l], end="")
            l += 1

a = [['f', 'i', 'r', 's'],
     ['n', '_', 'l', 't'],
     ['o', 'b', 'a', '_'],
     ['h', 't', 'y', 'p']]

R = 4
C = 4
spiralPrint(R, C, a)