/**
 * IMS (It Makes Sense) -- NUS WSD System
 * Copyright (c) 2010 National University of Singapore.
 * All Rights Reserved.
 */
package sg.edu.nus.comp.nlp.ims.feature;

import java.util.ArrayList;

import sg.edu.nus.comp.nlp.ims.corpus.AItem;
import sg.edu.nus.comp.nlp.ims.corpus.ICorpus;
import sg.edu.nus.comp.nlp.ims.corpus.IItem;
import sg.edu.nus.comp.nlp.ims.corpus.ISentence;
import sg.edu.nus.comp.nlp.ims.util.*;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TreeMap;
import java.util.List;
import java.util.Set;
import java.text.*;
import edu.sussex.nlp.jws.*;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import edu.mit.jwi.*;


/**
 * POS feature extractor.
 *
 * @author zhongzhi
 *
 */
public class CWSFeatureExtractor implements IFeatureExtractor {
	// the positions of neighbor words
	protected ArrayList<Integer> m_POSs = new ArrayList<Integer>();

	// corpus to be extracted
	protected ICorpus m_Corpus = null;

	// index of current instance
	protected int m_Index = -1;

	// current sentence to process
	protected ISentence m_Sentence = null;

	// item index in current sentence
	protected int m_IndexInSentence;

	// item length
	protected int m_InstanceLength;

	// index of feature
	protected int m_featIndex = -1;

	public String[] m_wordLists = null;
	public String[] m_POSLists = null;
	public int[] m_synsetsNumLists = null;
		
	public JWS jws = null;
	
	public IDictionary dict = null;
	public URL url = null;
	
	protected double[] m_senseProb = null;
	
	// current feature
	protected IFeature m_CurrentFeature = null;

	protected static int g_PIDX = AItem.Features.POS.ordinal();
	protected static int g_TIDX = AItem.Features.TOKEN.ordinal();
	protected static int g_LIDX = AItem.Features.LEMMA.ordinal();

	protected ILemmatizer m_Lemmatizer;
	/**
	 * constructor
	 *
	 * @param p_Indice
	 *            pos tag interested
	 */
	public CWSFeatureExtractor(ArrayList<Integer> p_Indice) {
		if (p_Indice == null) {
			throw new IllegalArgumentException();
		}
		this.m_POSs.addAll(p_Indice);
		this.m_wordLists = new String[this.m_POSs.size()];
		this.m_POSLists = new String[this.m_POSs.size()];
		this.m_synsetsNumLists = new int[this.m_POSs.size()];
		this.jws = new JWS("/u/pxcheng/WordNet", "1.7.1");
		this.m_Lemmatizer = new CPTBWNLemmatizer();
	}

	/**
	 * constructor
	 */
	public CWSFeatureExtractor() {
//		this.m_POSs.add(-6);
//		this.m_POSs.add(-5);
//		this.m_POSs.add(-4);
		this.m_POSs.add(-3);
		this.m_POSs.add(-2);
		this.m_POSs.add(-1);
		this.m_POSs.add(0);
		this.m_POSs.add(1);
		this.m_POSs.add(2);
		this.m_POSs.add(3);
//		this.m_POSs.add(4);
//		this.m_POSs.add(5);
//		this.m_POSs.add(6);
		this.m_wordLists = new String[this.m_POSs.size()];
		this.m_POSLists = new String[this.m_POSs.size()];
		this.m_synsetsNumLists = new int[this.m_POSs.size()];
		this.jws = new JWS("/u/pxcheng/WordNet", "1.7.1");
		this.m_Lemmatizer = new CPTBWNLemmatizer();
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor#getCurrentInstanceID()
	 */
	@Override
	public String getCurrentInstanceID() {
		if (this.validIndex(this.m_Index)) {
			return this.m_Corpus.getValue(this.m_Index, "id");
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (this.m_CurrentFeature != null) {
			return true;
		}
		if (this.validIndex(this.m_Index)) {
			this.m_CurrentFeature = this.getNext();
			if (this.m_CurrentFeature != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * get the next feature of current instance
	 *
	 * @return feature
	 */
	protected IFeature getNext() {
		IFeature feature = null;
		if (this.m_featIndex >= 0 && this.m_featIndex < this.m_senseProb.length) {
			feature = new CWSFeature();
			int senseIdx = this.m_featIndex + 1;
			feature.setKey(this.m_wordLists[3]+"#"+this.m_POSLists[3]+"#"+senseIdx);
			feature.setValue(Double.toString(this.m_senseProb[this.m_featIndex]));
			this.m_featIndex++;
		}
		return feature;
	}

	protected void getWordsAndPOSs() {
		for(int i = 0; i < this.m_POSs.size(); i++) {
			String lemma = this.getLemma(this.m_POSs.get(i));
			String pos = tranformPOS(this.getPOS(this.m_POSs.get(i)));
//			this.m_wordLists[i] = this.m_Lemmatizer.lemmatize(new String[] { word.toLowerCase(), pos });
			this.m_wordLists[i] = lemma;
			this.m_POSLists[i] = pos;
			this.m_synsetsNumLists[i] = getSynsetsNum(lemma, pos);
		}
	}
	
	protected String tranformPOS(String POS) {
		if (POS.startsWith("NN"))
			return "n";
		else if (POS.startsWith("VB"))
			return "v";
		else if (POS.startsWith("JJ"))
			return "a";
		else if (POS.startsWith("RB"))
			return "r";
		else
			return "-";
	}
	
	protected int getSynsetsNum(String word, String pos) {
		IIndexWord word_idx = null;
		if(pos.equals("-"))
			return -1;
		else if(pos.equalsIgnoreCase("n"))
			word_idx = dict.getIndexWord(word, POS.NOUN);
		else if(pos.equalsIgnoreCase("v"))
			word_idx = dict.getIndexWord(word, POS.VERB);
		else if(pos.equalsIgnoreCase("a"))
			word_idx = dict.getIndexWord(word, POS.ADJECTIVE);
		else
			word_idx = dict.getIndexWord(word, POS.ADVERB);
		
		if(word_idx == null)
			return 0;
		else {
			List<IWordID> wordIDs = word_idx.getWordIDs();
			return wordIDs.size();
		}
	}
	
	protected void getFeatures() {
		JiangAndConrath jcn = this.jws.getJiangAndConrath();
		LeacockAndChodorow lch = this.jws.getLeacockAndChodorow();
		AdaptedLeskPOS lesk = this.jws.getAdaptedLeskPOS();
		
		int wordIndex = 3;
		int numSenses = this.m_synsetsNumLists[wordIndex];
		if (numSenses <= 0)
			return;
		
		this.m_senseProb = new double[numSenses];
		for (int s = 0; s < numSenses; s++)
			this.m_senseProb[s] = 0.0;
		
		for (int neighIndex = 0; neighIndex < this.m_POSs.size(); neighIndex++) {
			int flag = -1;
			if (neighIndex == wordIndex)
				break;
			if (this.m_synsetsNumLists[wordIndex] <= 0)
				break;
			for (int s = 0; s < numSenses; s++) {
				ArrayList<Double> scores = null;
				if (this.m_POSLists[neighIndex].equals("n") && this.m_POSLists[wordIndex].equals("n")) {
					scores = jcn.jcn(this.m_wordLists[wordIndex], s + 1, this.m_wordLists[neighIndex], "n");
					flag = 1;
				}
				else if (this.m_POSLists[neighIndex].equals("v") && this.m_POSLists[wordIndex].equals("v")) {
					scores = lch.lch(this.m_wordLists[wordIndex], s + 1, this.m_wordLists[neighIndex], "v");
					flag = 2;
				}
				else {
					scores = lesk.lesk(this.m_wordLists[wordIndex], s + 1, this.m_POSLists[wordIndex], this.m_wordLists[neighIndex], this.m_POSLists[neighIndex]);
					flag = 3;
				}
				for(double weight : scores) {
					if (flag == 1) {
						weight = (weight - 0.04) / (0.2 - 0.04);
						if (weight < 0)
							weight = 0;
						if (weight > 1)
							weight = 1;
					}
					else if (flag == 2) {
						weight = (weight - 0.34) / (3.33 - 0.34);
						if (weight < 0)
							weight = 0;
						if (weight > 1)
							weight = 1;
					}
					else {
						weight = weight / 480;
						if (weight < 0)
							weight = 0;
						if (weight > 1)
							weight = 1;
					}
					this.m_senseProb[s] += weight;
				}
			}
		}
	}
	
	/**
	 * get the part-of-speech of item p_Index + m_IndexInSentence
	 *
	 * @param p_Index
	 *            index
	 * @return feature value
	 */
	protected String getPOS(int p_Index) {
		if (p_Index > 0) {
			p_Index += this.m_InstanceLength - 1;
		}
		p_Index += this.m_IndexInSentence;
		if (p_Index >= 0 && p_Index < this.m_Sentence.size()) {
			IItem item = this.m_Sentence.getItem(p_Index);
			return item.get(g_PIDX);
		}
		return "NULL";
	}

	protected String getLemma(int p_Index) {
		if (p_Index > 0) {
			p_Index += this.m_InstanceLength - 1;
		}
		p_Index += this.m_IndexInSentence;
		if (p_Index >= 0 && p_Index < this.m_Sentence.size()) {
			IItem item = this.m_Sentence.getItem(p_Index);
			return item.get(g_LIDX);
		}
		return "NULL";
	}

	protected String getToken(int p_Index) {
		if (p_Index > 0) {
			p_Index += this.m_InstanceLength - 1;
		}
		p_Index += this.m_IndexInSentence;
		if (p_Index >= 0 && p_Index < this.m_Sentence.size()) {
			IItem item = this.m_Sentence.getItem(p_Index);
			return item.get(g_TIDX);
		}
		return "NULL";
	}
	
	/**
	 * check the validity of index
	 *
	 * @param p_Index
	 *            index
	 * @return valid or not
	 */
	protected boolean validIndex(int p_Index) {
		if (this.m_Corpus != null && this.m_Corpus.size() > p_Index
				&& p_Index >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * form POS feature name
	 *
	 * @param p_Index
	 *            index
	 * @return feature name
	 */
	protected String formPOSName(int p_Index) {
		if (p_Index < 0) {
			return "POS_" + -p_Index;
		}
		return "POS" + p_Index;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor#next()
	 */
	@Override
	public IFeature next() {
		IFeature feature = null;
		if (this.hasNext()) {
			feature = this.m_CurrentFeature;
			this.m_CurrentFeature = null;
		}
		return feature;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor#restart()
	 */
	@Override
	public boolean restart() {
		this.m_featIndex = 0;
		this.m_CurrentFeature = null;
		
		this.m_wordLists = new String[this.m_POSs.size()];
		this.m_POSLists = new String[this.m_POSs.size()];		
		this.m_synsetsNumLists = new int[this.m_POSs.size()];
		
		try
		{
		  this.url = new URL("file", null, "/u/pxcheng/WordNet/1.7.1/dict");
		}
		catch (MalformedURLException localMalformedURLException)
		{
		  localMalformedURLException.printStackTrace();
		}
		if (this.url == null) {
		  return false;
		}
		this.dict = new Dictionary(this.url);
		this.dict.open();
		
		this.getWordsAndPOSs();
		this.getFeatures();
		
		return this.validIndex(this.m_Index);
	}

	public boolean restart1() {
		this.m_featIndex = 0;
		this.m_CurrentFeature = null;
		
		return this.validIndex(this.m_Index);
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor#setCorpus(sg.edu.nus.comp.nlp.ims.corpus.ICorpus)
	 */
	@Override
	public boolean setCorpus(ICorpus p_Corpus) {
		if (p_Corpus == null) {
			return false;
		}
		this.m_Corpus = p_Corpus;
		this.m_Index = 0;
		this.restart1();
		this.m_Index = -1;
		this.m_IndexInSentence = -1;
		this.m_InstanceLength = -1;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see sg.edu.nus.comp.nlp.ims.feature.IFeatureExtractor#setCurrentInstance(int)
	 */
	@Override
	public boolean setCurrentInstance(int p_Index) {
		if (this.validIndex(p_Index)) {
			this.m_Index = p_Index;
			this.m_IndexInSentence = this.m_Corpus.getIndexInSentence(p_Index);
			this.m_InstanceLength = this.m_Corpus.getLength(p_Index);
			this.m_Sentence = this.m_Corpus.getSentence(this.m_Corpus
					.getSentenceID(p_Index));
			this.restart1();
			return true;
		}
		return false;
	}

}
