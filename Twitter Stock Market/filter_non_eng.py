# filters out tweets in non-english language

__author__ = 'Marie'

from guess_language import guess_language

filename = 'C:\\Users\Marie\\Documents\\Project\\Data\\tweets2009-06.txt\\processed_tweets2009-06.txt'

f = open(filename, 'r')
out = open(filename + "_filtered", 'w')
i = 0
for s in f:
    ssplitted = s.split(",.||")
    if (len(ssplitted) > 3 ):
        try:
            # if tweet is in eng, we output it to the final file.
            if (guess_language(unicode(ssplitted[3])) == 'en'):
                out.write(s + "\n")
        except Exception:
            pass
    i = i + 1
    # track progress
    if (i % 10000 == 0):
        print i
f.close()
out.close()
