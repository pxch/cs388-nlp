*** Commands for preprocessing: (Assuming that the wsj and brown treebank are in the './penn_treebank3' folder)

(parameters in brackets are optional)

java Preprocessing wsj ./penn_treebank3/wsj ./penn_treebank3/wsj 1.0 [1000 2000 3000 4000 5000 7000 10000 13000 16000 20000 25000 30000 35000]

java Preprocessing brown ./penn_treebank3/brown ./penn_treebank3/brown 0.9 [1000 2000 3000 4000 5000 7000 10000 13000 17000 21000]


*** Commands for experiment:

java -mx1500m UDA seed_training_file_path self_training_file_path target_testing_file_path source_testing_file_path

	parameters:
		--seed_training_file_path: path for seed file from source domain used for original training
		--self_training_file_path: path for self training file from target domain used for automatic annotation and retraining parser
		--target_testing_file_path: path for testing file from target domain to evaluate out-of-domain performance
		--source_testing_file_path: path for testing file from source domain to evaluate in-domain performance

example:
	java -mx1500m UDA data\wsj_train_1000.mrg data\brown_train.mrg data\brown_test.mrg data\wsj_test.mrg 
