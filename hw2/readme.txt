## Preprocessing:
java -cp ./ nlp.lm.POSMalletFormat input_file(directory)_name output_file_name

## Sequence Labeling:
HMM on atis:
java -cp "mallet-2.0.7/class;mallet-2.0.7/lib/mallet-deps.jar" cc.mallet.fst.HMMSimpleTagger --train true --model-file model_file --training-proportion 0.8 --test lab atis_file_path

CRF on atis:
java -cp "mallet-2.0.7/class;mallet-2.0.7/lib/mallet-deps.jar" cc.mallet.fst.SimpleTagger --train true --model-file model_file --training-proportion 0.8 --test lab atis_file_path

HMM on wsj:
java -cp "mallet-2.0.7/class;mallet-2.0.7/lib/mallet-deps.jar" cc.mallet.fst.HMMSimpleTagger --train true --model-file model_file --training-proportion 1 --test lab wsj_00_file_path wsj_01_file_path

CRF on wsj:
java -cp "mallet-2.0.7/class;mallet-2.0.7/lib/mallet-deps.jar" cc.mallet.fst.SimpleTagger --train true --model-file model_file --training-proportion 1 --test lab wsj_00_file_path wsj_01_file_path

## Adding Orthographic Features:
java -cp ./ nlp.lm.POSAddFeatures input_file [feature 1] [feature 2] ...
	valid feature args:
		capitalized
		plural
		hyphen
		gerund
		comparative
		superlative
		
## Changes to Mallet:
Only modified the "TokensAccuracyEvaluator.java" file.
Add two variables:
	int numCorrectOOVTokens
	int totalOOVTokens
Add a HashSet variable to store seen tokens:
	HashSet<String> lexicon
Add input to lexicon or inference whether or not lexicon contains input by judging the variable "description" equals "training" or "testing".
Calculate OOV percentage and OOV accuracy, and output them to logger.
