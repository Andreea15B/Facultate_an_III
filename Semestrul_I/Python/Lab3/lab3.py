# 1.
def function1(aList, bList):
    finalSet = set()
    set1 = frozenset(aList).union(bList)
    finalSet.add(set1)
    set2 = frozenset(aList).intersection(bList)
    finalSet.add(set2)
    set3 = frozenset(set(aList)-set(bList))
    finalSet.add(set3)
    set4 = frozenset(set(bList)-set(aList))
    finalSet.add(set4)
    return finalSet

lst1 = [ 4, 9, 1, 17, 11, 26, 28, 28, 26, 66, 91]
lst2 = [9, 9, 74, 21, 45, 11, 63]
print(function1(lst1, lst2))

# 2.
def function2(str):
    dictionary = {}
    for letter in str:
        dictionary[letter] = str.count(letter)
    return dictionary

print(function2('buuna'))

# 6.
def function6(aSet):
    a = len(aSet)
    b = 0
    return (a,b)

a = [1,2,3,4,4]
print(function6(a))