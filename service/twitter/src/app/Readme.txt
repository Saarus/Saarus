1. Requirement: 
===================

You need to have java install on your computer and the JAVA_HOME variable available.

To set the JAVA_HOME variable on unix/linux/mac
JAVA_HOME=/path/to/java-jdk && export JAVA_HOME

To set the JAVA_HOME variable on window
set JAVA_HOME=/path/to/java-jdk

Check http://docs.oracle.com/cd/E19182-01/820-7851/inst_cli_jdk_javahome_t/index.html for instruction how to install java and set JAVA_HOME

2. Run Program
================

This program will run and analyze a sentence or a file, depending on the arguments you enter in the command.

To analyse a sentence, go to the TwitterSample directory.

#On unix/linux/mac use the command
For tweet normalization:
./normalizer.sh -s "He loves his new iphone"
For sentiment analysis:
./classifier.sh -s "He loves his new iphone"

Or You can use the java command
For tweet normalization:
java -cp lib/* sample.nlp.tweeter.SentenceNormalizer -s "He loves his new iphone"
For sentiment analysis:
java -cp lib/* sample.nlp.tweeter.SentenceClassifier -s "He loves his new iphone"

#On window use the command
For tweet normalization:
java -cp lib/* sample.nlp.tweeter.SentenceNormalizer -s "He loves his new iphone"
For sentiment analysis:
java -cp lib/* sample.nlp.tweeter.SentenceClassifier -s "He loves his new iphone"

(You may need to change / to \ on the old window system)

You should have the output look like:
For tweet normalization:
---------------------------------------------------------
Sentence       : He loves his new iphone
  Normalized   : he love his new iphone
  POS Tagged   : he[O] love[V] his[D] new[A] iphone[^]
---------------------------------------------------------
For sentiment analysis:
---------------------------------------------------------
Sentence       : He loves his new iphone
  Predict      : POSITIVE  
---------------------------------------------------------

Where the 25 tags are:

N - common noun
O - pronoun (personal/WH; not possessive)
^ - proper noun
S - nominal + possessive
Z - proper noun + possessive
V - verb incl. copula, auxiliaries
A - adjective
R - adverb
! - interjection
D - determiner
P - pre- or postposition, or subordinating conjunction
& - coordinating conjunction
T - verb particle
X - existential there, predeterminers
# - hashtag (indicates topic/category for tweet)
@ - at-mention (indicates another user as a recipient of a tweet)
~ - discourse marker, indications of continuation of a message across multiple tweets
U - URL or email address
E - emoticon
$ - numeral
, - punctuation
G - other abbreviations, foreign words, posses-sive endings, symbols, garbage
L - nominal + verbal (e.g. i’m), verbal + nominal(let’s, lemme)
M - proper noun + verbal
Y - X + verbal

To run and check more examples , you can have many sentences in a file and use the file input option

#On unix/linux/mac use the command
For tweet normalization:
./normalizer.sh -f data/SmallExample.txt
For sentiment analysis:
./classifier.sh -f data/SmallExample.txt

Or
For tweet normalization:
java -cp lib/* sample.nlp.tweeter.SentenceNormalizer -f data/SmallExample.txt
For sentiment analysis:
java -cp lib/* sample.nlp.tweeter.SentenceClassifier -f data/SmallExample.txt

#On window use the command
For tweet normalization:
java -cp lib/* sample.nlp.tweeter.SentenceNormalizer -f data/SmallExample.txt
For sentiment analysis:
java -cp lib/* sample.nlp.tweeter.SentenceClassifier -f data/SmallExample.txt
