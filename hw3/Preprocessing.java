import java.io.*;
import java.util.*;

public class Preprocessing {
	protected static void Concatenate(File input, List<List<String>> tree_bank) {
		try {
			if (!input.isDirectory()) {
				BufferedReader in = null;
				try {
					List<String> current_tree = new ArrayList<String>();

					in = new BufferedReader(new FileReader(input));
					String line = in.readLine();
					while (line.startsWith("*x*")) {
						line = in.readLine();
					}

					while ((line = in.readLine()) != null) {
							if (line.startsWith("( (")) {
								if (current_tree.size() != 0)
									tree_bank.add(current_tree);
								current_tree = new ArrayList<String>();
							}
							current_tree.add(line);
					}
					tree_bank.add(current_tree);
				} finally {
					if (in != null) {
						in.close();
					}
				}
			}
			else {
				File[] dirFiles = input.listFiles();
				BufferedReader in = null;
				for (File file : dirFiles) {
					try {
						List<String> current_tree = new ArrayList<String>();

						in = new BufferedReader(new FileReader(file));
						String line = in.readLine();
						while (line.startsWith("*x*")) {
							line = in.readLine();
						}

						while ((line = in.readLine()) != null) {
								if (line.startsWith("( (")) {
									if (current_tree.size() != 0)
										tree_bank.add(current_tree);
									current_tree = new ArrayList<String>();
								}
								current_tree.add(line);
						}
						tree_bank.add(current_tree);
					} finally {
						if (in != null) {
							in.close();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void wsjProcessing(String input_file, String output_file,
			int[] size_set) {
		List<List<String>> tree_bank_train = new ArrayList<List<String>>();
		List<List<String>> tree_bank_test = new ArrayList<List<String>>();

		String train_file_name;
		for (int i = 2; i < 23; i ++) {
			train_file_name = input_file + "\\" + String.format("%02d", i);
			File train_file = new File(train_file_name);
			Concatenate(train_file, tree_bank_train);
		}

		String test_file_name = input_file + "\\23";
		File test_file = new File(test_file_name);
		Concatenate(test_file, tree_bank_test);

		try {
			BufferedWriter out = null;
			String output_filename = output_file + "_test.mrg";
			try {
				out = new BufferedWriter(new FileWriter(output_filename));
				out.newLine();
				for (List<String> tree : tree_bank_test) {
					for (String line : tree) {
						out.write(line);
						out.newLine();
					}
				}
			} finally {
				if (out != null)
					out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			BufferedWriter out = null;
			if (size_set != null) {
				for (int i = 0; i < size_set.length; i++) {
					String output_filename = output_file + "_train_"
							+ Integer.toString(size_set[i]) + ".mrg";
					try {
						out = new BufferedWriter(
								new FileWriter(output_filename));
						out.newLine();
						for (int j = 0; j < size_set[i]; j++) {
							List<String> tree = tree_bank_train.get(j);
							for (String line : tree) {
								out.write(line);
								out.newLine();
							}
						}
					} finally {
						if (out != null)
							out.close();
					}
				}
			} else {
				String output_filename = output_file + "_train.mrg";
				try {
					out = new BufferedWriter(new FileWriter(output_filename));
					out.newLine();
					for (List<String> tree : tree_bank_train) {
						for (String line : tree) {
							out.write(line);
							out.newLine();
						}
					}
				} finally {
					if (out != null)
						out.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void brownProcessing(String input_file, String output_file, float percentage, 
			int[] size_set) {
		File input_folder = new File(input_file);
		String[] genreDirName = input_folder.list();
		int num_genres = genreDirName.length;
		
		List<List<String>> tree_bank_train = new ArrayList<List<String>>();
		List<List<String>> tree_bank_test = new ArrayList<List<String>>();

		File[] genreDir = new File[num_genres];
		for (int i = 0; i < num_genres; i++) {
			List<List<String>> tree_bank_by_genre = new ArrayList<List<String>>();
			genreDir[i] = new File(input_file + "\\" + genreDirName[i]);
			
			Concatenate(genreDir[i], tree_bank_by_genre);
			
			int genre_size = tree_bank_by_genre.size();
			int train_size = Math.round(genre_size * percentage);
			int count = 0;
			
			for (List<String> tree : tree_bank_by_genre) {
				count ++;
				if (count <= train_size)
					tree_bank_train.add(tree);
				else
					tree_bank_test.add(tree);
			}
		}

		try {
			BufferedWriter out = null;
			String output_filename = output_file + "_test.mrg";
			try {
				out = new BufferedWriter(new FileWriter(output_filename));
				out.newLine();
				for (List<String> tree : tree_bank_test) {
					for (String line : tree) {
						out.write(line);
						out.newLine();
					}
				}
			} finally {
				if (out != null)
					out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			BufferedWriter out = null;
			if (size_set != null) {
				for (int i = 0; i < size_set.length; i++) {
					String output_filename = output_file + "_train_"
							+ Integer.toString(size_set[i]) + ".mrg";
					try {
						out = new BufferedWriter(
								new FileWriter(output_filename));
						out.newLine();
						for (int j = 0; j < size_set[i]; j++) {
							List<String> tree = tree_bank_train.get(j);
							for (String line : tree) {
								out.write(line);
								out.newLine();
							}
						}
					} finally {
						if (out != null)
							out.close();
					}
				}
			} else {
				String output_filename = output_file + "_train.mrg";
				try {
					out = new BufferedWriter(new FileWriter(output_filename));
					out.newLine();
					for (List<String> tree : tree_bank_train) {
						for (String line : tree) {
							out.write(line);
							out.newLine();
						}
					}
				} finally {
					if (out != null)
						out.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		String corpus = args[0];
		String input_filename = args[1];
		String output_filename = args[2];
		float percentage = Float.valueOf(args[3]);
		
		int[] size_set = null;
		if (args.length > 4) {
			size_set = new int[args.length - 4];
			for (int i = 0; i < size_set.length; i++) {
				size_set[i] = Integer.valueOf(args[i + 4]);
			}
		}

		if (corpus.equals("wsj")) {
			wsjProcessing(input_filename, output_filename, size_set);
		}
		else if (corpus.equals("brown")) {
			brownProcessing(input_filename, output_filename, percentage, size_set);
		}
		else {
			System.err.println("First param must be wsj or brown!");
			System.exit(0);
		}
	}
}
