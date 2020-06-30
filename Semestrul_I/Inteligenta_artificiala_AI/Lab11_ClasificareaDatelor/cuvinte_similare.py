# Dataset from https://www.kaggle.com/renanmav/imdb-movie-review-dataset

import pandas as pd
import re
import string
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords
import gensim

df = pd.DataFrame()
df = pd.read_csv('movie_data.csv')

# remove non-letter characters and keep only emoticons
def preprocessor(text):
    text = re.sub('<[^>]*>', '', text)
    emoticons = re.findall('[:;=](?:-)?[)(DP]', text)
    text = re.sub('[\W]+', ' ', text.lower())
    text = text + " ".join(emoticons).replace('-', '')
    return text

stop = stopwords.words('english')
df['review'] = df['review'].apply(preprocessor)

X_train = df.review.values
Y_train = df.sentiment.values

review_lines = list()
for line in X_train:
    tokens = word_tokenize(line)
    tokens = [w.lower() for w in tokens]
    table = str.maketrans('', '', string.punctuation)
    stripped = [w.translate(table) for w in tokens]
    words = [word for word in stripped if word.isalpha()]
    stop_words = set(stopwords.words('english')) # remove words that don't add useful information to the text
    words = [w for w in words if not w in stop_words]
    review_lines.append(words)

model = gensim.models.Word2Vec(sentences=review_lines, size=100, window=5, workers=4, min_count=1)
words = list(model.wv.vocab)
print('Vocabulary size: %d' % len(words))

wordsList = ['terrible', 'woman', 'cat', 'king', 'man', 'movie', 'queen', 'dog', 'chocolate', 'review']
for word in wordsList:
    print('Similar words for %s:' % word)
    print(model.wv.most_similar(word))

# save model
filename = 'imdb_word2vec.txt'
model.wv.save_word2vec_format(filename, binary=False)
