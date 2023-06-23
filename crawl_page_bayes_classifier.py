import json

import nltk
import requests
import random
from bs4 import BeautifulSoup
from nltk.classify import NaiveBayesClassifier
from nltk.classify.util import accuracy
from nltk.corpus import stopwords

# uncomment it once and run - would be enough
# nltk.download('stopwords')

url = 'https://api.stackexchange.com/2.2/questions'
stop_words = set(stopwords.words('english'))
languages = ['java', 'python', 'go']
n = 25
docs = []


def document_classification(language):
    print('classifying... language=' + language)

    page_data = load_page_data(language)

    links = []
    for item in page_data['items']:
        link = item['link']
        links.append(link)

    i = 0
    for link in links:
        if i == n:
            break

        try:
            i += 1
            page = requests.get(link)
            bodies = BeautifulSoup(page.content, 'html.parser').find_all('body')
            text = ''
            for body in bodies:
                text += body.get_text()
            docs.append((text, language))
        except requests.exceptions.RequestException:
            i -= 1
            pass


def load_page_data(language):
    url_parameters = {'order': 'desc', 'sort': 'activity', 'tagged': language, 'site': 'stackoverflow'}
    server_response = requests.get(url, params=url_parameters)
    return json.loads(server_response.text)


def word_frequencies(document):
    features = {}
    for word in nltk.word_tokenize(document):
        if word not in stop_words:
            features[word] = features.get(word, 0) + 1
    return features


def convert_document_to_word_frequencies(documents):
    result_set = []
    for (document, language) in documents:
        word_freq = word_frequencies(document)
        result_set.append((word_freq, language))
    return result_set


# retrieve {tagged=language} pages
for lng in languages:
    document_classification(language=lng)

# shuffle documents
random.shuffle(docs)

# initialize train_set and test_set
train_set_percent = 0.8
border = int(len(docs) * train_set_percent)
train_documents = docs[0:border]
test_documents = docs[border:len(docs)]
train_set = convert_document_to_word_frequencies(train_documents)
test_set = convert_document_to_word_frequencies(test_documents)

# calculate naive_bayes classification and then the accuracy
print('accuracy measured:', round(accuracy(NaiveBayesClassifier.train(train_set), test_set), 1))
