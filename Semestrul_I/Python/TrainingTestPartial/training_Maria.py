def problema1():
    s=0
    for i in range(1,11):
        s = s+i
    return s

def problema2(n,m):
    s3 = 1
    if n>m:
        s1=n 
        s2 =m 
    else: 
        s1= m 
        s2= n
    print(s1,s2)
    while(s3<=s2 and s1>2*s2 and s3>=1):
        s3 = s1 - (s2*2)
        if(s3<s2 and s1>s2 ):
            s1 = s2
            s2 = s3
        else:
            break
    thistuplet =(s2,s1)
    return thistuplet

def prim(x):
    ok = 1
    if x == 0 or x == 1:
        ok = 0
    else:
        for i in range(2,x//2+1):
            if x % i == 0:
                ok = 0
    return ok
def problema3(m):
    list=[]
    for i in range(0,m):
        if(prim(i)==1):
            list.append(i)
    return list

def problema4(my_list):
    list_ceva=[]
    for i in my_list:
        if isinstance(i,int):
            list_ceva.append(i)
    list_ceva.sort(reverse=True)
    return list_ceva

def problema5(n):
    n = n[::-1]
    baza_8 = 0
    putere = 0
    for i in n:
        baza_8 = baza_8 + (8**putere * int(i))
        putere = putere + 1
    if baza_8 < 10:
        return True
    copie = baza_8
    numar = 0
    copie1 = 0
    while copie != 0:
        numar = int(copie % 10)
        copie = copie // 10
        copie1 = copie1 * 10 + numar
    if copie1 == baza_8:
        return True
    else:
        return False

# def problema7(matrix):
#     my_list=[]
#     j = 0
#     print(len(matrix[0]))
#     if len(matrix[0])>2:
#         while j < len(matrix[0])+1:
#             ok = 1
#             i = 0
#             while i < len(matrix):
#                 if matrix[i][j] == matrix[i+1][j]:
#                     ok = 0
#                 i=i+1
#             my_list.append(ok)
#             j = j+1
#     else:
#         while j < len(matrix):
#             ok = 1
#             if matrix[0][j] == matrix[1][j]:
#                 ok = 0
#             my_list.append(ok)
#             j = j+1

#     return my_list

def problema7(matrix):
    my_list =[]
    for j in range(len(matrix[0])):
        ok = 1
        for i in range(len(matrix)-1):
            print(i,j)
            if matrix[i][j] == matrix[i+1][j]:
                ok = 0
        my_list.append(ok)
    return my_list

if __name__ =="__main__":
    #print(problema1())
    print(problema2(99,41))
