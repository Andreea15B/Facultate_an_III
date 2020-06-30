# 1.
def get_sorted_list(list_of_tuples):
    list_of_tuples.sort(key = lambda element : element[1])
    print(list_of_tuples)
get_sorted_list([('Bulboaca', 'Maria'), ('Bucataru', 'Andreea')])

# 2.
def check_first_name(list_of_tuples, first_name):
    for item in list_of_tuples:
        if item[1] == first_name:
            return True
    return False
print(check_first_name([('Bucataru', 'Andreea'), ('Bulboaca', 'Maria')], 'Ioana'))

# 3.
global_dictionary = {"+": lambda a, b: a + b, "*": lambda a, b: a * b, "/": lambda a, b: a / b, "%": lambda a, b: a % b,
                     "**": lambda a, b: a ** b}
def apply_operator(operator, a, b):
    try:
        operation = global_dictionary[operator]
        return operation(a, b)
    except:
        return "Eroare - operatorul nu exista!"
print(apply_operator("+", 1, 2))
print(apply_operator("*", 5, 2))
print(apply_operator("**", 5, 2))
print(apply_operator("!", 5, 2))

# 4.
global_dictionary = {"print_all": lambda *a, **k: print(a, k),
                     "print_args_commas": lambda *a, **k: print(a, k, sep=", "),
                     "print_only_args": lambda *a, **k: print(a),
                     "print_only_kwargs": lambda *a, **k: print(k)
                     }
def apply_function(operation_name, *a, **k):
    try:
        operation = global_dictionary[operation_name]
        operation(*a, **k)
    except:
        print("Eroare - operatia nu exista!")
apply_function("print_all", "a", 1, 5, a = 1, b = 2)
apply_function("print_args_commas", "a", 1, 5, a = 1, b = 2)
apply_function("print_only_args", "a", 1, 5, a = 1, b = 2)
apply_function("print_only_kwargs", "a", 1, 5, a = 1, b = 2)
apply_function("print_only", "a", 1, 5, a = 1, b = 2)

# 5.
def dictionary_function(*dictionaries):
    new_dict = {}
    for dictionary in dictionaries:
        for k in dictionary:
            if k in new_dict:
                if not isinstance(new_dict[k], list):
                    l = list()
                    l.append(new_dict[k])
                    l.append(dictionary[k])
                    new_dict[k] = l
                else:
                    new_dict[k].append(dictionary[k])
            else:
                new_dict[k] = dictionary[k]
    print(new_dict)

dictionary1 = {"a":1, "b":2, "c":3}
dictionary2 = {"c":9, "d":4, "e":5}
dictionary3 = {"c":10, "r":4, "y":5}
dictionary_function(dictionary1, dictionary2, dictionary3)