import re
import os
import urllib
from urllib import request
import hashlib
import zipfile
import socket
import json


def problema1(s):
    words = re.findall('[02468]+', s)
    words.sort(reverse=True)
    return words


print(problema1('abav124 hbfbf34tbjhdx5bhjgds12'))

def problema2(url, cheie):
    content = urllib.request.urlopen(url).read().decode()
    dictionary = json.loads(content)

    list = dictionary[cheie]
    last_element = list[-1]

    return last_element


def problema3(path):
    def md5(filePath):
        try:
            m = hashlib.md5()
            f = open(filePath, "rb")
            while True:
                data = f.read(4096)
                if len(data) is 0:
                    break
                m.update(data)
            f.close()
            return m.hexdigest()
        except:
            return ""

    dictionary = {}
    for root, dirs, files in os.walk(path):
        for filename in files:
            full_path = os.path.join(root, filename)
            if os.path.isfile(full_path):
                dictionary[filename] = md5(full_path)

    return dictionary


def problema4(lista_arhive):
    file_set = set()
    for archive in lista_arhive:
        z = zipfile.ZipFile(archive)
        for i in z.infolist():
            name = os.path.basename(i.filename)
            file_set.add(name)

    return file_set


def problema5(url):
    content = urllib.request.urlopen(url).read().decode()
    dictionary = json.loads(content)

    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((dictionary['ip'], dictionary['port']))

    info = dictionary['info']
    first_32 = info[:32]
    s.send(first_32.encode())

    received_from_server = s.recv(10).decode()
    count = received_from_server.count('A')
    s.close()

    return count

