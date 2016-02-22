# Transforms tweets from one format into another

__author__ = 'Marie'

from os import listdir
from os.path import isfile, join, basename

mypath = 'C:\\Users\Marie\\Documents\\Project\\Data\\tweets'
onlyfiles = [ f for f in listdir(mypath) if isfile(join(mypath,f)) ]

outfile = open('C:\\Users\Marie\\Documents\\Project\\Data\\new_data', 'w')
i = 0
for f in onlyfiles:
    myfile = open(mypath + "\\" + f, 'r')
    in_block = False
    date = ""
    tweet = ""
    # parsing each line in the original file and exracting tweet itself, date and author.
    for s in myfile:
        if (in_block):
            if (s[0:7] == "Origin:"):
                tweet = s[8:]
            if (s[0:5] == "Time:"):
                ssplitted = s.split()
                if (len(ssplitted) < 4):
                    continue
                if ssplitted[3].isdigit():
                  date = "2011-07-" + ssplitted[3]
        if (s.find("***") != -1):
            if (in_block and date != "" and tweet != "") :
                outfile.write("T:\t" + date + "\n")
                outfile.write("U:\thttp://twitter.com/" + basename(f) + "\n")
                outfile.write("W:\t" + tweet + "\n")
                outfile.write("\n")
                date = ""
                tweet = ""
            in_block = not in_block

    myfile.close()
    i = i + 1
    # show progress
    if (i % 100 == 0):
        print str(i) + "//" + str(len(onlyfiles))

outfile.close()
