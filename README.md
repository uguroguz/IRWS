# IRWS
IRWS project, ImplementatingVector Space Model of Information Retreival using java.

File is exported.
contains Stopword.txt,cranfield.txt
Stemmer implemented as .java

Project consist two part Prepration and Query

Executing Preperation part returns TermWeightLength.txt and
SimilarityFile.txt at project folder.

Executing Query part request input from user returns result
as query(Number).txt at project folder.


+Preperation: 
	Reading cranfied field line by line 
	removing stopwords
	lowercasing words and removing punctuation marks

	stemming words and adding to HashMap (hh) -> TF
	-HashMap Architecture:
		Term,<DocumentId,Frequency>
	
	Calculating IDF
	Changing hashmap values TF -> TW
	Creating TwLength hashmap without square root
	Writing TwLength result with square root to TermWeightLength.txt file
	
	Calculating Normalization from Tw hashmap and TwLength hashmap
	Writing results in SimilarityFile.txt 
	
+Query:

	Reading stopword file,SimilarityFile
	-Requesting input from user
	 -1 to terminate search
	Getting search input from user
	removing stopwords in search input
	stemmig search input
	-Checking SimilarityFile if search inputs exists in file
	 existing words documentId and Similarity value stored in Ranked hashmap
	 
	Ordering Ranked hashmap in Ordered treemap
	Writing orderedRanked treemap in query(number).txt file
