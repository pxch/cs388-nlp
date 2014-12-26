
import java.io.*;
import java.util.*;

public class ConcatenateTreeBank {
	
	public static void Concatenate(File[] input, File output) {
	try
	{
	  BufferedWriter out = null;
	  BufferedReader in = null;
	  for (File file : input)
	  {
	    if (!file.isDirectory()) {
		  try {
		  	out = new BufferedWriter(new FileWriter(output, true));
		    in = new BufferedReader(new FileReader(file));
		    String line = in.readLine();
			while (line.startsWith("*x*")) {
			  line = in.readLine();
			}

		    do {
			  line = in.readLine();
			  if (line != null){
			    out.write(line);
				out.newLine();
              }
			  else {
			    break;
		      }
			} while(true);
          }
          finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
		  }
		}
		else {
		  File[] dirFiles = file.listFiles();
		  Concatenate(dirFiles, output);
		}
	  }
	}
	catch (Exception e)
	{
	  e.printStackTrace();
	}
	}

    public static void main(String[] args) throws IOException {
	File[] input = new File[args.length - 1];
	for (int i = 0; i < input.length; i++)
	    input[i] = new File(args[i]);
	File output = new File(args[args.length - 1]);
	if (output.exists()) {
	  if (output.delete()) {
		  System.out.println(output.getName() + " already exists. Delete original file and create an empty file!");
		  output = new File(args[args.length - 1]);
	  }
	  else {
		  System.out.println(output.getName() + " already exists. Deleting operation failed! System exit!");
		  System.exit(0);
	  }
	}
	Concatenate(input, output);	
    }

}
