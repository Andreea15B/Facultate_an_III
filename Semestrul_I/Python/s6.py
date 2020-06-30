def fibo(n):
    fibonacci_numbers = [0, 1]
    for i in range(2, n+1):
        fibonacci_numbers.append(fibonacci_numbers[i - 1] + fibonacci_numbers[i - 2])
    return fibonacci_numbers[n]

def is_prime(number):
    if number > 1:
        for i in range(2, (int)(number ** (1/2))+1):
            if (number % i) == 0:
                return False
        return True
    return False

def custom_filter(my_list):
    new_list = sorted(my_list)
    final = []
    for i in range(0, len(new_list)):
        if is_prime(new_list[i]):
            for j in range(1, 100):
                if fibo(j) == new_list[i]:
                    final.append(fibo(j))
    return final