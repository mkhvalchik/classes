# Calculate number of sad and happy tweets per given day.
__author__ = 'Marie'

from os import listdir
from os.path import isfile, join, basename
import collections

mypath = 'C:\\Users\Marie\\Documents\\Project\\Data\\all tweets\proc'
onlyfiles = [ f for f in listdir(mypath) if isfile(join(mypath,f)) ]
outfile = open('C:\\Users\Marie\\Documents\\Project\\Data\\final', 'w')
sad_dict = {}
happy_dict = {}

i = 0

# Going over all files in the directory with processed tweets
for f in onlyfiles:
    myfile = open(mypath + "\\" + f, 'r')
    for s in myfile:
        ssplitted = s.split(",.||")
        i += 1
        # parsing each line and extracting: day, prob that tweet is happy or sad.
        if (len(ssplitted) > 1):
            if (not (ssplitted[0] in sad_dict)):
                sad_dict[ssplitted[0]] = 0;
                happy_dict[ssplitted[0]] = 0;

            prob = ssplitted[2].split(',')
            prob_happy = float(prob[0][1:])
            prob_sad = float(prob[1][:(len(prob[1]) - 1)])
            # if tweet is happy/sad with > 0.6 probability, we consider it as happy/sad.
            if (prob_happy > 0.6) :
                happy_dict[ssplitted[0]] += 1
            if (prob_sad > 0.6) :
                sad_dict[ssplitted[0]] += 1
        # track the progress.
        if (i % 1000000 == 0):
            print i
    myfile.close()

ordered_sad = collections.OrderedDict(sorted(sad_dict.items()))
ordered_happy = collections.OrderedDict(sorted(happy_dict.items()))

# final output of data - percentage of happy/sad tweets for each day.
for key in ordered_sad:
    total = ordered_sad[key] + ordered_happy[key]
    percent_happy = float(ordered_happy[key])/total
    percent_sad = float(ordered_sad[key])/total
    print key, percent_happy, percent_sad

outfile.close()
