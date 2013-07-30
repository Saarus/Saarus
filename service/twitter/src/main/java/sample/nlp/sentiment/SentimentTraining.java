package sample.nlp.sentiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import sample.nlp.twitter.parsing.TweetParsing;
import sample.nlp.twitter.parsing.TweetToken;

import de.bwaldvogel.liblinear.Train;

public class SentimentTraining {
  static String fileCorpus = "d:/50kSentimentCorpus.csv";
  static String fileTraining = "target/models/fullSentiment.training";
  static String fileModel = "target/models/fullSentiment.model";
  static String fileWordlist = "target/models/fullSentiment.wordlist";

  public static FeatureGenerator<List<TweetToken>, String>[] mFeatureGenerators = new FeatureGenerator[] { 
    new SentimentBOWFeatureGenerator() 
  };

  static SentimentContextGenerator contextGenerator;
  static FeatureSet featureSet;
  static TweetParsing parsing;

  public static void init() throws Exception {
    contextGenerator = new SentimentContextGenerator(mFeatureGenerators);
    featureSet = new FeatureSet();
    parsing = new TweetParsing();
  }

  private static void createVectorTrainingFile() throws Exception {
    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileTraining)));
    CSVReader csvReader = new CSVReader(new FileReader(fileCorpus));

    String[] row = null;
    while((row = csvReader.readNext()) != null) {
      List<TweetToken> tokens = parsing.parsing(row[3]);
      String label = (row[1].equals("1")) ? "POSITIVE" : "NEGATIVE";
      String vector = featureSet.addprintVector(contextGenerator.getContext(tokens, row[3]), label, false);
      if (vector.equals("")) {
        continue;
      }
      writer.append(vector).append("\n");
    }
    csvReader.close();
    writer.close();

    FileOutputStream fileOut = new FileOutputStream(fileWordlist);
    ObjectOutputStream out = new ObjectOutputStream(fileOut);
    out.writeObject(featureSet);
    out.close();
    fileOut.close();
  }

  public static void main(String[] args) throws Exception {
    init();	
    long start = System.currentTimeMillis() ;
    System.out.println("Start creating vector") ;
    createVectorTrainingFile();
    System.out.println("Create vector in " + (System.currentTimeMillis() - start)) ;
    // Training
    start = System.currentTimeMillis() ;
    System.out.println("Start train") ;
    Train.main(new String[] { "-c", "0.5", "-s", "2", fileTraining, fileModel });
    // Cross-validation
    //Train.main(new String[] {"-v", "2", "-c", "0.5", "-s", "2", fileTraining, modelFile });
    System.out.println("Train in " + (System.currentTimeMillis() - start)) ;
  }
}
