package nlp.lm;

import java.io.*;
import java.util.*;

/** 
 * @author Ray Mooney
 * A simple bigram language model that uses simple fixed-weight interpolation
 * with a unigram model for smoothing.
*/

public class BidirectionalBigramModel {

    /** Forward Bigram Model */
    BigramModel forward = null; 

    /** BackwardBigram Model */
    BackwardBigramModel backward = null;

    /** Total count of tokens in training data */
    public double tokenCount = 0;

    /** Interpolation weight for forward model */
    public double lambda1 = 0.5;

    /** Interpolation weight for backward model */
    public double lambda2 = 0.5;

    /** Initialize model with empty hashmaps with initial
     *  unigram entries for setence start (<S>), sentence end (</S>)
     *  and unknown tokens */
    public BidirectionalBigramModel() {
	forward = new BigramModel();
	backward = new BackwardBigramModel();
    }

    /** Train the model on a List of sentences represented as
     *  Lists of String tokens */
    public void train (List<List<String>> sentences) {
	// Accumulate unigram and bigram counts in maps
	forward.train(sentences);
	backward.train(sentences);
	// Compure final unigram and bigram probs from counts
    }


    /** Return bigram string as two tokens separated by a newline */
    public String bigram (String prevToken, String token) {
	return prevToken + "\n" + token;
    }

    /** Return fist token of bigram (substring before newline) */
    public String bigramToken1 (String bigram) {
	int newlinePos = bigram.indexOf("\n");
	return bigram.substring(0,newlinePos);
    }

    /** Return second token of bigram (substring after newline) */
    public String bigramToken2 (String bigram) {
	int newlinePos = bigram.indexOf("\n");
	return bigram.substring(newlinePos + 1, bigram.length());
    }

    /** Use sentences as a test set to evaluate the model. Print out perplexity
     *  of the model for this test data */
    public void test (List<List<String>> sentences) {
	// Compute log probability of sentence to avoid underflow
	double totalLogProb = 0;
	// Keep count of total number of tokens predicted
	double totalNumTokens = 0;
	// Accumulate log prob of all test sentences
	for (List<String> sentence : sentences) {
	    // Num of tokens in sentence plus 1 for predicting </S>
	    totalNumTokens += sentence.size() + 1;
	    // Compute log prob of sentence
	    double sentenceLogProb = sentenceLogProb(sentence);
	    //	    System.out.println(sentenceLogProb + " : " + sentence);
	    // Add to total log prob (since add logs to multiply probs)
	    totalLogProb += sentenceLogProb;
	}
	// Given log prob compute perplexity
	double perplexity = Math.exp(-totalLogProb / totalNumTokens);
	System.out.println("Perplexity = " + perplexity );
    }
    
    /* Compute log probability of sentence given current model */
    public double sentenceLogProb (List<String> sentence) {
	// Calculate vector of probabilities of predicting each token in the sentence by forward and backward model
	double[] forwardProbs = forward.sentenceTokenProbs(sentence);
	double[] backwardProbs = backward.sentenceTokenProbs(sentence);
	
	// Maintain total sentence prob as sum of individual token
	// log probs (since adding logs is same as multiplying probs)
	double sentenceLogProb = 0;
	// Check prediction of each token in sentence
	double logProb = 0;
	for (int i = 0; i <= sentence.size() + 1; i++) {
		if (i == 0) {
			logProb = Math.log(backwardProbs[sentence.size()]);}
		else if (i == sentence.size() + 1) {
			logProb = Math.log(forwardProbs[sentence.size()]);}
		else {
			logProb = Math.log(interpolatedProb(forwardProbs[i-1], backwardProbs[sentence.size() - i]));}
	    // Add token log prob to sentence log prob
	    sentenceLogProb += logProb;
	    // update previous token and move to next token
	}
	return sentenceLogProb;
    }

    /** Like test1 but excludes predicting end-of-sentence when computing perplexity */
    public void test2 (List<List<String>> sentences) {
	double totalLogProb = 0;
	double totalNumTokens = 0;
	for (List<String> sentence : sentences) {
	    totalNumTokens += sentence.size();
	    double sentenceLogProb = sentenceLogProb2(sentence);
	    //	    System.out.println(sentenceLogProb + " : " + sentence);
	    totalLogProb += sentenceLogProb;
	}
	double perplexity = Math.exp(-totalLogProb / totalNumTokens);
	System.out.println("Word Perplexity = " + perplexity );
    }
    
    /** Like sentenceLogProb but excludes predicting end-of-sentence when computing prob */
    public double sentenceLogProb2 (List<String> sentence) {
	// Calculate vector of probabilities of predicting each token in the sentence by forward and backward model
	double[] forwardProbs = forward.sentenceTokenProbs(sentence);
	double[] backwardProbs = backward.sentenceTokenProbs(sentence);
	
	// Maintain total sentence prob as sum of individual token
	// log probs (since adding logs is same as multiplying probs)
	double sentenceLogProb = 0;
	// Check prediction of each token in sentence
	for (int i = 0; i < sentence.size(); i++) {
		double logProb = Math.log(interpolatedProb(forwardProbs[i], backwardProbs[sentence.size() - i - 1]));
	    // Add token log prob to sentence log prob
	    sentenceLogProb += logProb;
	    // update previous token and move to next token
	}
	return sentenceLogProb;
    }

    /** Interpolate bigram prob using bigram and unigram model predictions */	 
    public double interpolatedProb(double forwardVal, double backwardVal) {
	// Linearly combine weighted forward and backward probs
	return lambda1 * forwardVal + lambda2 * backwardVal;
    }

    public static int wordCount (List<List<String>> sentences) {
	int wordCount = 0;
	for (List<String> sentence : sentences) {
	    wordCount += sentence.size();
	}
	return wordCount;
    }

    /** Train and test a bigram model.
     *  Command format: "nlp.lm.BigramModel [DIR]* [TestFrac]" where DIR 
     *  is the name of a file or directory whose LDC POS Tagged files should be 
     *  used for input data; and TestFrac is the fraction of the sentences
     *  in this data that should be used for testing, the rest for training.
     *  0 < TestFrac < 1
     *  Uses the last fraction of the data for testing and the first part
     *  for training.
     */
    public static void main(String[] args) throws IOException {
	// All but last arg is a file/directory of LDC tagged input data
	File[] files = new File[args.length - 1];
	for (int i = 0; i < files.length; i++) 
	    files[i] = new File(args[i]);
	// Last arg is the TestFrac
	double testFraction = Double.valueOf(args[args.length -1]);
	// Get list of sentences from the LDC POS tagged input files
	List<List<String>> sentences = 	POSTaggedFile.convertToTokenLists(files);
	int numSentences = sentences.size();
	// Compute number of test sentences based on TestFrac
	int numTest = (int)Math.round(numSentences * testFraction);
	// Take test sentences from end of data
	List<List<String>> testSentences = sentences.subList(numSentences - numTest, numSentences);
	// Take training sentences from start of data
	List<List<String>> trainSentences = sentences.subList(0, numSentences - numTest);
	System.out.println("# Train Sentences = " + trainSentences.size() + 
			   " (# words = " + wordCount(trainSentences) + 
			   ") \n# Test Sentences = " + testSentences.size() +
			   " (# words = " + wordCount(testSentences) + ")");
	// Create a bigram model and train it.
	BidirectionalBigramModel model = new BidirectionalBigramModel();
	System.out.println("Training...");
	model.train(trainSentences);
	// Test on training data using test and test2
//	model.test(trainSentences);
	model.test2(trainSentences);
	System.out.println("Testing...");
	// Test on test data using test and test2
//	model.test(testSentences);
	model.test2(testSentences);
    }

}
