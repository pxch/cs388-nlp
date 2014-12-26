
import java.util.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.*;
import edu.stanford.nlp.util.*;

public class UDA {

	private static Treebank makeTreebank(String treebankPath, Options op)
	{
	System.err.println("Training a parser from treebank dir: " + treebankPath);
	Treebank trainTreebank = op.tlpParams.diskTreebank();
	System.err.print("Reading trees...");
	trainTreebank.loadPath(treebankPath);
	Timing.tick("done [read " + trainTreebank.size() + " trees].");
	return trainTreebank;
	}

	private static List<CoreLabel> getInputSentence(Tree t)
	{
		return Sentence.toCoreLabelList(t.yieldWords());
	}
	
	public static void main(String[] args) {
		
		Options op = new Options();
		op.doDep = false;
		op.doPCFG = true;
		op.setOptions("-goodPCFG", "-evals", "tsv", "-writeOutputFiles");
		
		LexicalizedParser lp;
		
		Treebank seedTreebank = makeTreebank(args[0], op);
		Treebank seedTestTreebank = makeTreebank(args[3], op);
		Treebank selfTrainingTreebank = makeTreebank(args[1], op);
		Treebank testingTreebank = makeTreebank(args[2], op);
		
		lp = LexicalizedParser.trainFromTreebank(seedTreebank, op);
		
		EvaluateTreebank evaluator = new EvaluateTreebank(lp);
		evaluator.testOnTreebank(seedTestTreebank);
		evaluator.testOnTreebank(testingTreebank);
		
		MemoryTreebank retrainTreebank = new MemoryTreebank();
		
		for (Tree t : seedTreebank)
			retrainTreebank.add(t);
		
		for (Tree t : selfTrainingTreebank) {
			LexicalizedParserQuery parserQuery = lp.lexicalizedParserQuery();
			if(parserQuery.parse(getInputSentence(t))){
				Tree labelled = parserQuery.getBestPCFGParse();
				retrainTreebank.add(labelled);
			}
		}
		
		lp = LexicalizedParser.trainFromTreebank(retrainTreebank, op);
		
		evaluator = new EvaluateTreebank(lp);
		evaluator.testOnTreebank(testingTreebank);
	}
}
