from utils import process_item

while 1:
    x = input("Number: ")
    if x == 'q':
        break
    x = int(x)
    print('Least prime number greater than %d is %d.' % (x, process_item(x)))