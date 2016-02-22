# Filters out tweets not having any feeling-related word

__author__ = 'Marie'

feelings = ['feel', ':)', '=)', ':(', '=(', '=\'(', 'Happy', 'Well', 'OK', 'Very well', 'Clean', 'Strong', 'Beautiful', 'Excited', 'Attracted', 'Handsome', 'Gorgeous', 'Attractive', 'Empowered', 'Intelligent', 'Smart', 'Better', 'Agreeable', 'Brave', 'Calm', 'Delightful', 'Eager', 'Gentle', 'Jolly', 'Joyful', 'Kind', 'Nice', 'Sweet', 'Proud', 'Relieved', 'Hope', 'Faith', 'Optimistic', 'Loving', 'Open', 'Understanding', 'Reliable', 'Amazed', 'Great', 'Lucky', 'Fortunate', 'Festive', 'Cheerful', 'Moody', 'Playful', 'Animated', 'Wonderful', 'Thrilled', 'Supportive', 'Serene', 'Free', 'Engrossed', 'Involved', 'Angry', 'Sad', 'Hungry', 'Cold', 'Hot', 'Warm', 'Chilly', 'Thirsty', 'Sick', 'Tired', 'Weak', 'Disgruntled', 'Terrible', 'Dreadful', 'Dirty', 'Ugly', 'Defeated', 'Embarrassed', 'Stupid', 'Dumb', 'Foolish', 'Silly', 'Hurt', 'Nervous', 'Scared', 'Evil', 'Bashful', 'Pessimistic', 'Sadistic', 'Hating', 'Taken aback']


feelings_new = [x.lower() for x in feelings]

print feelings_new


filename = 'C:\\Users\Marie\\Documents\\Project\\Data\\all tweets\\tweets10_proc.txt'

f = open(filename, 'r')
out = open(filename + "_feelings", 'w')
i = 0
# going over the tweets and searching for the ones not having any feeling-related words.
for s in f:
    ssplitted = s.split(",.||")
    if (len(ssplitted) > 3 ):
        bfound = False

        for feel in feelings:

            if (ssplitted[3].lower().find(feel) != -1):
                bfound = True
                break

        if bfound:
            out.write(s + "\n")
    i = i + 1
    # tracking progress.
    if (i % 100000 == 0):
        print i
f.close()
out.close()
