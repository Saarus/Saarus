package sample.nlp.tweeter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import sample.nlp.twitter.parsing.TweetParsing;
import sample.nlp.twitter.parsing.TweetToken;

public class SentenceNormalizer {
  static public void main(String[] args) throws Exception {
//    if(args == null || args.length == 0) {
//      args = new String[] {
//          "-f", "src/app/data/SmallExample.txt"
//      };
//    }
    Options options = new Options();
    options.addOption("s", true,
        "A sentence to normalize. The sentense should be in \"...\"");
    options.addOption("f", true,
        "The input file. Each line in the file will be consider as a tweet");
    CommandLineParser parser = new PosixParser();
    CommandLine cmd = parser.parse(options, args);
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("Usage", options);
    System.out.println("\n\n");
    
    TweetParsing parsing = new TweetParsing();
    
    if (cmd.hasOption("s")) {
      String sentence = cmd.getOptionValue("s");
      dump(parsing, sentence) ;
    } else if (cmd.hasOption("f")) {
      String inputFile = cmd.getOptionValue("f");
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
      String line = "";
      while ((line = reader.readLine()) != null) {
        if (line.trim().equals("")) continue;
        dump(parsing, line) ;
      }
      reader.close();
    }
  }

  static void dump(TweetParsing parsing, String sentence) {
    List<TweetToken> tokens = parsing.parsing(sentence) ;
    System.out.println("Sentence       : " + sentence);
    String output = "";
    for (TweetToken token : tokens)
      if (token.getTokenNormalized().size() == 0)
        output += token.getToken() + " ";
      else
        output += token.getTokenNormalized().get("Lemma") + " ";
    System.out.println("  Normalized   : " + output);
    output = "";
    for (TweetToken token : tokens)
      if (token.getTokenNormalized().size() == 0)
        output += token.getToken() + "[" + token.getTokenAnnotations().get("POS") + "] ";
      else
        output += token.getTokenNormalized().get("Lemma") + "[" + token.getTokenAnnotations().get("POS") + "] ";
    System.out.println("  POS Tagged   : " + output) ; 
    System.out.println();
  }
}
