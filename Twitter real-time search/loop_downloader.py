# Downloads twitter dataset by running provided Java tool on a given file with tweet ids and writing
# its output into json.gz file
# Example usage:
# ./loop_downloader.py ./tweets.tar.gz
import tarfile,sys
import os
import subprocess
import sys

tar = tarfile.open(sys.argv[1] + '.tar.gz', 'r:gz')
tar.extractall(sys.argv[1])
tar.close()

dat_dir_name = sys.argv[1] + '/' + os.listdir(sys.argv[1])[0]
file_list = os.listdir(dat_dir_name)

# Going over all extracted tar files and processing it using the download Jave tool.
# Each file contains a list of tweet IDs which are being downloaded by Java tool.
for filename in file_list:
    command = 'sh twitter-tools/twitter-tools-core/target/appassembler/bin/AsyncHTMLStatusBlockCrawler  -data ' + dat_dir_name + '/' + filename + ' -output ' + sys.argv[1] + '/' + filename + '.json.gz'
    print command
    process = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE)
    # This will be false when process terminates
    while True:
      out = process.stdout.read(1)
      if out == '' and process.poll() != None:
        break
      if out != '':
        sys.stdout.write(out)
        sys.stdout.flush()
    process.wait()
