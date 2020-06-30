import nltk.data
from nltk.corpus import wordnet

def semantic_distance():
    tokenizer = nltk.data.load('tokenizers/punkt/english.pickle')
    fp = open("Text_input.txt")
    data = fp.read()
    sentences = tokenizer.tokenize(data) # get sentences

    for sentence in sentences:
        words = sentence.split(' ')

        delimiter_list = [".", ";", ","]
        dist_max = 0
        for word1 in words:
            if word1[len(word1) - 1] in delimiter_list:
                word1 = word1[:-1]
            for word2 in words:
                if word2[len(word2) - 1] in delimiter_list:
                    word2 = word2[:-1]
                if word2 == word1:
                    continue
                syns1 = wordnet.synsets(word1)
                syns2 = wordnet.synsets(word2)
                if syns1 and syns2:
                    distance = syns1[0].shortest_path_distance(syns2[0])
                    if distance != None and distance > dist_max:
                        dist_max = distance

        print("\n" + sentence)
        print("Max distance for this sentence: ", dist_max)

def replace_nouns():
    file_in = open("Text_input.txt", "r")
    text = file_in.read()
    print(text)
    txt = text.split(' ')
    file_out = open("Text_rezultat.txt", "w")

    for i in range(0, len(txt)):
        word = txt[i]
        print("Word: ", word)

        delimiter_list = [".", ";", ","]
        delimiter = 0
        if word[len(word)-1] in delimiter_list:
            delimiter = word[len(word)-1]
            word = word[:-1]

        syns = wordnet.synsets(word)
        print("Synset: ", syns)

        if syns:
            parte_de_vorbire = syns[0].name().split('.')[1]
            if parte_de_vorbire == 'n':
                print("The word is a noun!")
                first_hypernym = syns[0].hypernyms()[0]
                print("Hypernym: ", first_hypernym)
                result = first_hypernym.name().split('.')[0]
                print("Replacing with: ", result)

                file_out.write(result)
                if delimiter != 0:
                    file_out.write(delimiter + ' ')
                else:
                    file_out.write(' ')
            else:
                file_out.write(word)
                if delimiter != 0:
                    file_out.write(delimiter + ' ')
                else:
                    file_out.write(' ')
        else:
            file_out.write(word)
            if delimiter != 0:
                file_out.write(delimiter + ' ')
            else:
                file_out.write(' ')
        print("\n")

def main():
   # replace_nouns()
    semantic_distance()

if __name__ == '__main__':
    main()