all: compile-files run-DBApp

compile-files:
	javac src/APlusTree/*.java src/BPTree/*.java src/RTree/*.java -d classes/

run-DBApp:
	java -classpath classes APlusTree.DBApp

clean:
	rm -r classes/
