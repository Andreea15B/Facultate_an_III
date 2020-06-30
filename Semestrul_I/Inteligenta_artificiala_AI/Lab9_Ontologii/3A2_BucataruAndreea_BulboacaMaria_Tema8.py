import csv

input_word = input('Introdu cuvantul: \n')

with open('CSO.3.1.csv') as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=',')
    line_count = 0
    for row in csv_reader:
        if line_count == 0:
            line_count += 1
        else:
            if input_word in row[0]:
                print('cuvantul ',input_word, ' are intrarea ', row[0])
                break
            line_count += 1
