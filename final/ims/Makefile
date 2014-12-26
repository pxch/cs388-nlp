JAVAC = javac
JAVAFLAGS = -O -d bin -encoding utf-8
SRCPATH = src/sg/edu/nus/comp/nlp/ims
CLASSPATH = "lib/liblinear-1.33-with-deps.jar;lib/jwnl.jar;lib/commons-logging.jar;lib/jdom.jar;lib/trove.jar;lib/maxent-2.4.0.jar;lib/opennlp-tools-1.3.0.jar;lib/weka-3.2.3.jar;lib/libsvm.jar;lib/edu.mit.jwi_2.1.4.jar;lib/edu.sussex.nlp.jws.05090156.jar"
default:
#	mkdir -p bin
	$(JAVAC) -classpath $(CLASSPATH) $(JAVAFLAGS) $(SRCPATH)/classifiers/*.java $(SRCPATH)/corpus/*.java $(SRCPATH)/feature/*.java $(SRCPATH)/implement/*.java $(SRCPATH)/instance/*.java $(SRCPATH)/io/*.java $(SRCPATH)/lexelt/*.java $(SRCPATH)/util/*.java
#	cd bin && jar cvf ../ims.jar sg
#	cd bin;	pwd; jar cvf ../ims-"date +%Y-%m-%d".jar sg; cd ..
#	cp ims-"date +%Y-%m-%d".jar ims.jar
