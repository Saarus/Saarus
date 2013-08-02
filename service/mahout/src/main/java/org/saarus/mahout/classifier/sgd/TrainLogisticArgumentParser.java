package org.saarus.mahout.classifier.sgd;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.util.HelpFormatter;
import org.saarus.service.hadoop.util.FSResource;
import org.saarus.service.sql.CacheDataReader;
import org.saarus.service.sql.CvsFileDataReader;
import org.saarus.service.sql.DataReader;
import org.saarus.service.sql.HiveTableDataReader;
import org.saarus.service.sql.SQLService;

public final class TrainLogisticArgumentParser {
  String targetVariable ;
  int maxTargetCategories ;
  int numFeatures ;
  boolean useBias ;
  String inputUrl;
  String modelUri;
  double lambda ;
  double learningRate ;
  List<PredictorType> predictorTypes ;
  
  int passes;
  boolean scores;
  
  private SQLService hservice ;
  
  public TrainLogisticArgumentParser setHiveService(SQLService hservice) {
    this.hservice = hservice ;
    return this ;
  }
  
  public boolean parse(String[] args) {
    DefaultOptionBuilder builder = new DefaultOptionBuilder();

    Option help = builder.withLongName("help").withDescription("print this list").create();

    Option quiet = builder.withLongName("quiet").withDescription("be extra quiet").create();
    Option scores = builder.withLongName("scores").withDescription("output score diagnostics during training").create();

    ArgumentBuilder argumentBuilder = new ArgumentBuilder();
    Option inputFile = builder.withLongName("input")
            .withRequired(true)
            .withArgument(argumentBuilder.withName("input").withMaximum(1).create())
            .withDescription("where to get training data")
            .create();

    Option outputFile = builder.withLongName("output")
            .withRequired(true)
            .withArgument(argumentBuilder.withName("output").withMaximum(1).create())
            .withDescription("where to get training data")
            .create();

    Option predictors = builder.withLongName("predictors")
            .withRequired(true)
            .withArgument(argumentBuilder.withName("p").create())
            .withDescription("a list of predictor variables. The predictor has to prefix with type: numeric, word, or text")
            .create();

    Option target = builder.withLongName("target")
            .withRequired(true)
            .withArgument(argumentBuilder.withName("target").withMaximum(1).create())
            .withDescription("the name of the target variable")
            .create();

    Option features = builder.withLongName("features")
            .withArgument(
                    argumentBuilder.withName("numFeatures")
                            .withDefault("1000")
                            .withMaximum(1).create())
            .withDescription("the number of internal hashed features to use")
            .create();

    Option passes = builder.withLongName("passes")
            .withArgument(
                    argumentBuilder.withName("passes")
                            .withDefault("2")
                            .withMaximum(1).create())
            .withDescription("the number of times to pass over the input data")
            .create();

    Option lambda = builder.withLongName("lambda")
            .withArgument(argumentBuilder.withName("lambda").withDefault("1e-4").withMaximum(1).create())
            .withDescription("the amount of coefficient decay to use")
            .create();

    Option rate = builder.withLongName("rate")
            .withArgument(argumentBuilder.withName("learningRate").withDefault("1e-3").withMaximum(1).create())
            .withDescription("the learning rate")
            .create();

    Option noBias = builder.withLongName("noBias")
            .withDescription("don't include a bias term")
            .create();

    Option targetCategories = builder.withLongName("categories")
            .withRequired(true)
            .withArgument(argumentBuilder.withName("number").withMaximum(1).create())
            .withDescription("the number of target categories to be considered")
            .create();

    Group normalArgs = new GroupBuilder()
            .withOption(help)
            .withOption(quiet)
            .withOption(inputFile)
            .withOption(outputFile)
            .withOption(target)
            .withOption(targetCategories)
            .withOption(predictors)
           // .withOption(types)
            .withOption(passes)
            .withOption(lambda)
            .withOption(rate)
            .withOption(noBias)
            .withOption(features)
            .create();

    Parser parser = new Parser();
    parser.setHelpOption(help);
    parser.setHelpTrigger("--help");
    parser.setGroup(normalArgs);
    parser.setHelpFormatter(new HelpFormatter(" ", "", " ", 130));
    CommandLine cmdLine = parser.parseAndHelp(args);

    if (cmdLine == null) {
      throw new RuntimeException("Invalid input parameters") ;
    }

    this.inputUrl = getStringArgument(cmdLine, inputFile);
    this.modelUri = getStringArgument(cmdLine, outputFile);
    this.targetVariable = getStringArgument(cmdLine, target) ;
    this.maxTargetCategories = getIntegerArgument(cmdLine, targetCategories) ;
    this.numFeatures = getIntegerArgument(cmdLine, features) ;
    this.useBias = !getBooleanArgument(cmdLine, noBias) ;
    this.lambda = getDoubleArgument(cmdLine, lambda) ;
    this.learningRate = getDoubleArgument(cmdLine, rate) ;
    
    this.predictorTypes = new ArrayList<PredictorType>() ;
    String predictorNames = getStringArgument(cmdLine, predictors) ;
    for (String name : predictorNames.split("\\|")) {
      predictorTypes.add(new PredictorType(name.trim()));
    }

    this.scores = getBooleanArgument(cmdLine, scores);
    this.passes = getIntegerArgument(cmdLine, passes);
    return true;
  }

  public String getInputFile() { return this.inputUrl  ; }
    
  public LogisticModelParameters getLogisticModelParameters() {
    LogisticModelParameters lmp = new LogisticModelParameters();
    lmp.setTargetVariable(targetVariable);
    lmp.setMaxTargetCategories(maxTargetCategories);
    lmp.setNumFeatures(numFeatures);
    lmp.setUseBias(useBias);
    lmp.setTypeMap(predictorTypes);

    lmp.setLambda(lambda);
    lmp.setLearningRate(learningRate);
    return lmp ;
  }
  
  public DataReader getDataReader() throws Exception {
    if(inputUrl.startsWith("hive://")) {
      String table = inputUrl.substring("hive://".length()) ;
      List<String> fholder = new ArrayList<String>() ;
      for(int i = 0; i < predictorTypes.size(); i++) {
        PredictorType ptype = predictorTypes.get(i) ;
        fholder.add(ptype.getName()) ;
      }
      fholder.add(targetVariable) ;
      return new CacheDataReader(new HiveTableDataReader(hservice, table, fholder.toArray(new String[fholder.size()]))) ;
    } else {
      DataReader dataReader = new CacheDataReader(new CvsFileDataReader(inputUrl, true)) ;
      return dataReader ;
    }
  }
  
  public FSResource getModelFSResource() { return FSResource.get(modelUri) ; }
  
  private static String getStringArgument(CommandLine cmdLine, Option inputFile) {
    return (String) cmdLine.getValue(inputFile);
  }

  private static boolean getBooleanArgument(CommandLine cmdLine, Option option) {
    return cmdLine.hasOption(option);
  }

  private static int getIntegerArgument(CommandLine cmdLine, Option features) {
    return Integer.parseInt((String) cmdLine.getValue(features));
  }

  private static double getDoubleArgument(CommandLine cmdLine, Option op) {
    return Double.parseDouble((String) cmdLine.getValue(op));
  }
}
