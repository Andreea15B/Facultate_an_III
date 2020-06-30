
import pandas as pd
import re
import string
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords
import gensim


import numpy as np
import pandas as pd
from matplotlib import pyplot as plt
from sklearn.datasets import load_breast_cancer
from sklearn.metrics import confusion_matrix
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import train_test_split


# from math import sqrt
# import numpy

from sklearn import preprocessing

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

import numpy as np 
from sklearn import preprocessing, svm 
from sklearn.model_selection import train_test_split 
from sklearn.linear_model import LinearRegression 

def function(review,value):
    for i in range(len(review)):
        if review[i] == value:
            return i

X_train_ = df.review.values
Y_train_ = df.sentiment.values

le = preprocessing.LabelEncoder()
review_encoded=le.fit_transform(X_train_)

X = np.array(review_encoded).reshape(-1, 1) 
y = np.array(Y_train_).reshape(-1, 1) 

X_train, X_test, y_train, y_test = train_test_split(X, y, random_state=1)

# for i in X_test:
#     print(X_train_[function(review_encoded,i)])

knn = KNeighborsClassifier(n_neighbors=1, metric='euclidean')
knn.fit(X_train, y_train)

y_pred = knn.predict(X_test)
for i in range(len(y_test)):
    print(y_test[i],' -->',y_pred[i])
# print(y_pred)