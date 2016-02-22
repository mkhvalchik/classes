 # Import the required libraries.
import tweepy

consumerKey = 'q5GYLfFPhHHM6rcgl6R2fmS6y'
consumerSecret = 'wcNFgdRL8pH8Yfk0GWT1XEU5M8SAkrmy1F40BDYlZcJ1w8T5PR'

#Use tweepy.OAuthHandler to create an authentication using the given key and secret
auth = tweepy.OAuthHandler(consumer_key=consumerKey, consumer_secret=consumerSecret)

#Connect to the Twitter API using the authentication
api = tweepy.API(auth)
out = open('./data/20110207-000.out','w')

cnt = 0
with open("./data/20110207-000.dat", "r") as ins:
    for line in ins:
        cnt += 1
        words = line.split('\t')
        # Look for a tweet with a given ID
        result = api.statuses_lookup([int(words[0])])
        if (len(result) > 0):
            # Result is an aray of class Status where the tweet is in variable text
            tweet = result[0].text
            out.write(words[1] + '\t' + tweet.encode("utf-8") + '\n')
        if (cnt%50 == 0):
            print cnt
out.close()
