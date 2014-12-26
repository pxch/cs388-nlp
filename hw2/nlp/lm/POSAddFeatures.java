package nlp.lm;

import java.io.*;
import java.util.*;

/** 
 *
 * @author Ray Mooney
 * Methods for processing Linguistic Data Consortium (LDC,www.ldc.upenn.edu) 
 * data files that are tagged for Part Of Speech (POS). Converts tagged files
 * into simple untagged Lists of sentences which are Lists of String tokens.
*/

/*
s					plural
ing					gerund
er					comparative
est					superlative
hyphen
capitalized
*/

public class POSAddFeatures {

    /** The name of the LDC POS file */
    public File file = null;
    /** The I/O reader for accessing the file */
//    protected BufferedReader reader = null;
	
	protected static boolean plural = false;
	protected static boolean gerund = false;
	protected static boolean comparative = false;
	protected static boolean superlative = false;
	protected static boolean hyphen = false;
	protected static boolean capitalized = false;

    /** Create an object for a given LDC POS tagged file */
    public POSAddFeatures(File file) {
	this.file = file;
/*	try {
	    this.reader = new BufferedReader(new FileReader(file));
	}
	catch (IOException e) {
	    System.out.println("\nCould not open POSTaggedFile: " + file);
	    System.exit(1);
	}*/
    }

	public static void setParameter(String param) {
		if (param.equals("plural"))
			plural = true;
		else if (param.equals("gerund"))
			gerund = true;
		else if (param.equals("comparative"))
			comparative = true;
		else if (param.equals("superlative"))
			superlative = true;
		else if (param.equals("hyphen"))
			hyphen = true;
		else if (param.equals("capitalized"))
			capitalized = true;
	}

	public static void writeExtraFeatures(File input_files, File output_file) {	
	try {
		BufferedReader reader = new BufferedReader(new FileReader(input_files));
		BufferedWriter output = new BufferedWriter(new FileWriter(output_file));
		String line;
	    while((line = reader.readLine()) != null) {
		// Read a line from the file
		if (!line.equals("")) {
			int slash = line.lastIndexOf(" ");
			String word = line.substring(0,slash);
			String POS = line.substring(slash+1,line.length());
			String feature = "";
			
			if (plural) {
				if (word.endsWith("s"))
					feature = feature + " s";
			}
			if (gerund) {
				if (word.endsWith("ing"))
					feature = feature + " ing";
			}
			if (comparative) {
				if (word.endsWith("er"))
					feature = feature + " er";
			}
			if (superlative) {
				if (word.endsWith("est"))
					feature = feature + " est";
			}
			if (hyphen) {
				if (word.contains("-"))
					feature = feature + " hyph";
			}
			if (capitalized) {
				if (Character.isUpperCase(word.charAt(0)))
					feature = feature + " caps";
			}
			String newline = word + feature + " " + POS;
			output.write(newline);
			output.newLine();
		}
		else {
			output.newLine();
		}
		
	    }
		reader.close();
		output.close();
	}
	catch (IOException e) {
		e.printStackTrace();
	}
	
	}
	
    /** Convert LDC POS tagged files to just lists of tokens for each setences 
     *  and print them out. */
    public static void main(String[] args) throws IOException {
	File files = new File(args[0]);
	for (int i = 1; i < args.length; i++) {
	    setParameter(args[i]);
	}
	
	String output_file_name = args[0].substring(0, args[0].length()-4);
	for (int i = 1; i < args.length; i++) {
		output_file_name = output_file_name + "_" + args[i];
	}
	output_file_name = output_file_name + ".pos";
	File output_file = new File(output_file_name);
//	List<List<String>> sentences = 	convertToTokenLists(files);	
	writeExtraFeatures(files, output_file);	

//	System.out.println("# Sentences=" + sentences.size());	
//	System.out.println(sentences);	
    }

}
