# Takes a list of queries and extracts top tweets relevant to the queries.
# Example running:
# python ./indri_queries_input.py ./queries/adhoc/2012.topics.MB51-110.txt /home/marie/sfsu/indstudy/index_small 10
# first arg is the topic file, second one - is the index, third one is max topics per query
from subprocess import Popen, PIPE
from langdetect import detect
import langdetect
import sys
import enchant
import wikipedia
import warnings
from stemming.porter2 import stem


# Convert tweet id to Indri's inner id.
def tweetIdToInnerId(tweet_id):
    process = Popen(["dumpindex", "/data/common/tweets_index/",
                    "di", "docno", str(tweet_id)], stdout=PIPE)
    (output, err) = process.communicate()
    exit_code = process.wait()
    return output

# Extract tweet text using inner id.
def innerIdtoTweetText(inner_id):
    process = Popen(["dumpindex", "/data/common/tweets_index/",
                    "dt", str(inner_id)], stdout=PIPE)
    (output, err) = process.communicate()
    exit_code = process.wait()
    return output.split("<text>")[1].split("</text>")[0]

# Open a file with qrels and add them all to the map<query,tweet_id,relevance>
def createRelevanceDictionary(file_name):
    f = open(file_name)
    relevance_dictionary = {}
    for line in f:
        columns = line.split(" ")
        if(len(columns) != 4):
            continue
        query = columns[0]
        tweet_id = columns[2]
        relevance = columns[3]
        # Filling out the map of relevances - maps query and tweet_id to
        # relevance
        if query not in relevance_dictionary:
            relevance_dictionary[query] = {}
        relevance_dictionary[query][tweet_id] = relevance
    return relevance_dictionary

# Find all non-english words.
def findNonEnglish(query):
    nonen_words = []
    d = enchant.Dict("en_US")
    for word in query.split(" "):
        # Ignore weird words and characters (e.g. space)
        if (len(word) < 2):
            continue
        if(not d.check(word.lower())):
            nonen_words.append(word.lower())
    return nonen_words

# Extract popular categories from wikipedia article.
def popularCategories(categories, words):
    #print "categories"
    #print categories
    stop_words = ['clarif', 'edit', 'date', 'unit', 'be', 'wikidata', 'refer', 'lack', 'januar', 'februar', 'contain', 'march', 'april', 'may', 'june', 'jul', 'august', 'septemb', 'octob', 'novemb', 'decemb', 'verif', 'merg', 'cleanup', 'and', 'in', 'with', 'to', 'text', 'establish', 'the', 'of', 'articl', 'wikipedia', 'identifi', 'from', 'page', 'all', 'use', 'page', 'link', 'unsourc', 'dead', 'extern', 'peopl', 'citat', 'need', 'statement']
    stop_words = [stem(word.lower()) for word in words] + stop_words
    if len(categories) < 19:
        return []
    word_frequency = {}
    for categorie in categories:
        categorie_list = categorie.split(" ")
        for word in categorie_list:
            word = stem(word.lower())
            word = word.replace(".", "")
            word = word.replace("'", "")
            word = word.replace("\"", "")
            word = word.replace(",", "")
            word = word.replace(":", "")
            word = word.replace("(", "")
            word = word.replace(")", "")
            if(word.isdigit()):
                continue
            if word in stop_words:
                continue
            if word in word_frequency:
                word_frequency[word] += 1
            else:
                word_frequency[word] = 1
    if len(word_frequency) < 4:
        return []
    max_word1 = max(word_frequency.iterkeys(), key=lambda k: word_frequency[k])
    word_frequency.pop(max_word1, 0)
    max_word2 = max(word_frequency.iterkeys(), key=lambda k: word_frequency[k])
    word_frequency.pop(max_word2, 0)
    max_word3 = max(word_frequency.iterkeys(), key=lambda k: word_frequency[k])
    word_frequency.pop(max_word3, 0)
    max_word4 = max(word_frequency.iterkeys(), key=lambda k: word_frequency[k])
    #print [max_word1, max_word2]
    return [max_word1, max_word2, max_word3, max_word4]

# Open wiki and extract popular categories.
def searchWikiN(words, n):
    weighted_query = ""
    for i in range(0, len(words) - (n - 1)):
        #print i
        #print words
	title = ""
        empty_word = False
        for j in range (i, i + n):
	    title += words[j] + " " 
            if (words[j] == ""):
                empty_word = True
                break
        title = title[0:len(title) - 1]
        if (empty_word):
            continue
        try:
            article = wikipedia.page(title, auto_suggest=False)
            weighted_query += "0.45 #uw2(" + title + ") "
            #print article.categories
            for pop_category in popularCategories(article.categories, words):
                weighted_query += "0.15 " + pop_category + " "
            if (wikipedia.suggest(title) != title.lower()):
                weighted_query += "0.45 #uw2(" + wikipedia.suggest(title) + ") "
        except:
            continue
    #print weighted_query
    return weighted_query

# Build weighted query for Indri.
def buildWeightedQuery(query):
    non_eng_words = findNonEnglish(query)

    weighted_query = " #weight[text]("
    words = query.split(" ")
    weighted_query += searchWikiN(words, 2)
    weighted_query += searchWikiN(words, 3)
    i = 0
    for word in words:
        if (len(word) == 0):
            continue
        if word.lower() in non_eng_words:
            weighted_query += "1.0 " + word + " "
        else:
            if(word.isdigit()):
                weighted_query+= "0.85 " + word + " "
            else:
                # last word given a higher weight
                if (i == len(words) - 1):
                    weighted_query+= "1.0 " + word + " "
                else:
                    weighted_query+= "0.7 " + word + " "
        i += 1
    weighted_query += "0.2 http )"
    #print weighted_query
    return weighted_query

warnings.filterwarnings("ignore")
f = open(sys.argv[1])
nums = []
queries = []
time = []
for line in f:
    if(line.find('<num>') != -1):
        nums.append(int(line[16:line.find('</') - 1]))
    if(line.find('<query>') != -1):
        queries.append(line[8:line.find('</') - 1])
    if(line.find('<title>') != -1):
        queries.append(line[8:line.find('</') - 1])
    if(line.find('<querytweettime>') != -1):
        time.append(int(line[17:line.find('</') - 1]))


if(len(sys.argv) > 4):
    reldict = createRelevanceDictionary(sys.argv[4])

# Running over all queries and looking each one in Indri
for i in range(len(queries)):
    query = queries[i]
    #Filtering out unneccesary punctuation
    query = query.replace(".", " ")
    query = query.replace("'", " ")
    query = query.replace("\"", " ")
    query = query.replace(",", " ")
    #Search only in text extent (eg ignore author's name)
    process = Popen(["/usr/local/bin/IndriRunQuery", "-query=" +
                    buildWeightedQuery(query), "-index=" + sys.argv[2]], stdout=PIPE)
    (output, err) = process.communicate()
    exit_code = process.wait()
    j = 1
    #Each element of the output is a tweet number with the score
    for line in output.splitlines():
        score = line.split("\t")[0]
        tweet_id = line.split("\t")[1]
        if not(time[i] >= int(tweet_id)):
            continue
        tweet_text = innerIdtoTweetText(tweetIdToInnerId(tweet_id))
        #Detecting non-English tweets
        try:
            if(detect(tweet_text.decode('utf-8')) != 'en'):
                continue
        except langdetect.lang_detect_exception.LangDetectException:
            continue
        #removing RTs
        if(tweet_text.find("RT @") != -1):
            continue
        #Printing relevance if user specified that
        if(len(sys.argv) > 4):
            relevance = "-3"
            if (tweet_id in reldict[str(nums[i])]):
                relevance = reldict[str(nums[i])][tweet_id]
            print str(nums[i]) + " Q0 " + tweet_id + " " + str(j) + " " + score + " coef " + relevance.split('\n')[0]
        else:
            print str(nums[i]) + " Q0 " + tweet_id + " " + str(j) + " " + score + " coef"
        j += 1
        if (j > int(sys.argv[3])):
            break
