import os
import sys

lines_before = len(os.popen('find ./codestore').readlines())
os.popen('./syncer.sh ' + sys.argv[1]).readlines()
lines_after = len(os.popen('find ./codestore').readlines())

if lines_before != lines_after:
    os.popen('sudo reboot').readlines()
