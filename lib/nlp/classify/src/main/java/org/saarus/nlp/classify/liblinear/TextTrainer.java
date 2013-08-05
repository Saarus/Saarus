package org.saarus.nlp.classify.liblinear;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;

import org.saarus.nlp.classify.liblinear.TextClassifyDataReader.Record;
import org.saarus.nlp.token.IToken;
import org.saarus.nlp.token.TextSegmenter;
import org.saarus.nlp.token.analyzer.CommonTokenAnalyzer;
import org.saarus.nlp.token.analyzer.GroupTokenMergerAnalyzer;
import org.saarus.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.saarus.util.FileUtil;

import de.bwaldvogel.liblinear.Train;

public class TextTrainer {
  static  TextSegmenter TEXT_TOKENIZER = new TextSegmenter(
    new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(), new GroupTokenMergerAnalyzer()
  );
  
  static FeatureGenerators FEATURE_GENERATORS = new FeatureGenerators(new BagOfWordFeatureGenerator());
  
  FeatureGenerators featureGenerators ;
  TextSegmenter textTokenizer ;
  String trainingDataFile, dictFile, modelFile  ;

  public TextTrainer() throws Exception {
    this(FEATURE_GENERATORS, TEXT_TOKENIZER) ;
  }
  
  public TextTrainer(FeatureGenerators featureGenerators, TextSegmenter textTokenizer) throws Exception {
    this.featureGenerators = featureGenerators ;
    this.textTokenizer = textTokenizer ;
  }
  
  private FeatureSet createVectorTrainingFile(TextClassifyDataReader reader, String trainingDataFile) throws Exception {
    FeatureSet featureSet = new FeatureSet(); 
    
    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(trainingDataFile)));
    Record record = null;
    while((record = reader.next()) != null) {
      String text  = record.getText() ;
      String label = record.getLabel();
      IToken[] tokens = textTokenizer.segment(text) ;
      if(text == null || label == null) continue ;
      String vector = featureSet.addprintVector(featureGenerators.getContext(tokens, text), label, false);
      if (vector.length() > 0) writer.append(vector).append("\n");
    }
    reader.close() ;
    writer.close();
    return featureSet ;
  }
 
  public void prepareTrain(TextClassifyDataReader reader, String outdir) throws Exception {
    System.out.println("Preparing........") ;
    
    trainingDataFile = outdir + "/text-classify.train" ;
    dictFile         = outdir + "/text-classify.dict" ;
    modelFile        = outdir + "/text-classify.model" ;
    
    long start = System.currentTimeMillis() ;
    System.out.println("Start creating vector") ;
    FeatureSet featureSet = createVectorTrainingFile(reader, trainingDataFile);
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dictFile));
    out.writeObject(featureSet);
    out.close();
    
    System.out.println("Create the training file in " + (System.currentTimeMillis() - start) + "ms") ;
  }
  
  public void train(TextClassifyDataReader reader, String outdir) throws Exception {
    FileUtil.mkdirs(outdir) ;
    prepareTrain(reader, outdir) ;
    // Training
    System.out.println("Training........") ;
    Train.main(new String[] {"-c", "0.5", "-s", "2", trainingDataFile, modelFile });
    FileUtil.removeIfExist(trainingDataFile) ;
  }
 
  public void crossValidation(TextClassifyDataReader reader, String outdir) throws Exception {
    prepareTrain(reader, outdir) ;
    // Training
    System.out.println("Start running cross validation") ;
    Train.main(new String[] {"-v", "2", "-c", "0.5", "-s", "2", trainingDataFile, modelFile });
  }
  
  public static void main(String[] args) throws Exception {
  //static String corpusFile = "d:/fullSentimentCorpus.csv";
    TextClassifyDataReader reader = new TextClassifyDataReader.CSVDataReader("d:/50kSentimentCorpus.csv") {
      protected Record createRecord(String[] cell) {
        String label = (cell[1].equals("1")) ? "POSITIVE" : "NEGATIVE";
        return new Record(label, cell[3]) ;
      }
    };
    FileUtil.mkdirs("target/classify") ;
    new TextTrainer().train(reader, "target/classify" );
    //new TextTrainer().crossValidation(reader, "target/classify" );
  }
}
