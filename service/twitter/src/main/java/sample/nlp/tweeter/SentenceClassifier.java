package sample.nlp.tweeter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import sample.nlp.sentiment.SentimentClassifier;

public class SentenceClassifier {
  static public void main(String[] args) throws Exception {
//    if(args == null || args.length == 0) {
//      args = new String[] {
//          "-f", "src/app/data/SmallExample.txt"
//      };
//    }
    Options options = new Options();
    options.addOption("s", true,
        "A sentence to classify. The sentense should be in \"...\"");
    options.addOption("f", true,
        "The input file. Each line in the file will be consider as a tweet");
    CommandLineParser parser = new PosixParser();
    CommandLine cmd = parser.parse(options, args);
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("Usage", options);
    System.out.println("\n\n");
    
    SentimentClassifier classifier = new SentimentClassifier();
    
    if (cmd.hasOption("s")) {
      String sentence = cmd.getOptionValue("s");
      dump(classifier, sentence) ;
    } else if (cmd.hasOption("f")) {
      String inputFile = cmd.getOptionValue("f");
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
      String line = "";
      while ((line = reader.readLine()) != null) {
        if (line.trim().equals("")) continue;
        dump(classifier, line) ;
      }
      reader.close();
    }
  }

  static void dump(SentimentClassifier classifier, String sentence) {
	double output =  classifier.classify(sentence) ;
	if (output != -1) {
		System.out.println("Sentence       : " + sentence);
	    String label = classifier.getFeatureSet().getLabels().get((int) output);
	    System.out.println("  Predict      : " + label);
	    System.out.println();	
	}    
  }
}
