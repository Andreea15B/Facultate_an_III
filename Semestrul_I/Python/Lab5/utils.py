def isPrime(nr):
    if nr > 1:
        for i in range(2, nr // 2):
            if nr%i == 0:
                return False
        return True
    return True

def process_item(x):
    i = x+1
    while i < x*x:
        if isPrime(i):
            return i
        i += 1
    return 0
