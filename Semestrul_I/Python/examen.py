import os
import re

def problema1(my_list):
    max = 0
    for sir in my_list:
        if len(sir) > max:
            max = len(sir)
            cuvant = sir
    return cuvant

# print(problema1(['abc', 'ab', 'asdfgh', 'a', 'aaaaaaa']))

def problema2(the_path):
    fisiereOk = []
    for file in os.listdir(the_path):
        fle = os.path.join(the_path, file)
        if os.path.isfile(fle):
            data = open(fle).read()
            ok = 1
            for i in range(0, len(data)):
                if data[i] != data[len(data)-i-1]:
                    ok = 0
                    break
            if ok == 1:
                fisiereOk.append(fle)
    return fisiereOk

# print(problema2(".\\tst"))

# def problema3(dir_path):
#     all = []
#     for (root, directories, files) in os.walk(dir_path):
#         for dir in directories:
#             r = re.compile(".*([0-9][0-9][0-9])+")
#             if r.test(dir):
#                 os.path.join(dir_path,)
