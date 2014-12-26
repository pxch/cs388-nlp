from os import walk
import sys

inputdir = str(sys.argv[1])
inputfiles = []
for (dirpath, dirnames, filenames) in walk(inputdir):
    inputfiles.extend(filenames)
    break

fout = open("test.answer", "w")
for file in inputfiles:
	fin = open(inputdir + file);
	for line in fin:
		id = file.split(".")[0]
		if id == line.split(" ")[0].split(".")[0]:
			fout.write(id + line)
		else:
			fout.write(id + " " + line.split(" ", 1)[1])
	fin.close()
fout.close()
