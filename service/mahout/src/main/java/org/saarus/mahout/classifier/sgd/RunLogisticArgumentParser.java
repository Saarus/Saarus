package org.saarus.mahout.classifier.sgd;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.util.HelpFormatter;

public final class RunLogisticArgumentParser {
  private String inputFile;
  private String modelFile;
  private boolean showAuc;
  private boolean showScores;
  private boolean showConfusion;

  public void  parseArgs(String[] args) {
    DefaultOptionBuilder builder = new DefaultOptionBuilder();
    Option help = builder.withLongName("help").withDescription("print this list").create();

    Option quiet = builder.withLongName("quiet").withDescription("be extra quiet").create();

    Option auc = builder.withLongName("auc").withDescription("print AUC").create();
    Option confusion = builder.withLongName("confusion").withDescription("print confusion matrix").create();

    Option scores = builder.withLongName("scores").withDescription("print scores").create();

    ArgumentBuilder argumentBuilder = new ArgumentBuilder();
    Option inputFileOption = builder.withLongName("input")
            .withRequired(true)
            .withArgument(argumentBuilder.withName("input").withMaximum(1).create())
            .withDescription("where to get training data")
            .create();

    Option modelFileOption = builder.withLongName("model")
            .withRequired(true)
            .withArgument(argumentBuilder.withName("model").withMaximum(1).create())
            .withDescription("where to get a model")
            .create();

    Group normalArgs = new GroupBuilder()
            .withOption(help)
            .withOption(quiet)
            .withOption(auc)
            .withOption(scores)
            .withOption(confusion)
            .withOption(inputFileOption)
            .withOption(modelFileOption)
            .create();

    Parser parser = new Parser();
    parser.setHelpOption(help);
    parser.setHelpTrigger("--help");
    parser.setGroup(normalArgs);
    parser.setHelpFormatter(new HelpFormatter(" ", "", " ", 130));
    CommandLine cmdLine = parser.parseAndHelp(args);

    if (cmdLine == null) {
      throw new RuntimeException("Invalid parameters!!!") ;
    }

    inputFile = getStringArgument(cmdLine, inputFileOption);
    modelFile = getStringArgument(cmdLine, modelFileOption);
    showAuc = getBooleanArgument(cmdLine, auc);
    showScores = getBooleanArgument(cmdLine, scores);
    showConfusion = getBooleanArgument(cmdLine, confusion);
  }

  public String getInputFile() { return this.inputFile ; }
  
  public String getModelFile() { return this.modelFile ; }
  
  public boolean getShowAuc() { return this.showAuc ; }
  
  public boolean getShowScores() { return this.showScores ; }
  
  public boolean getShowConfusion() { return this.showConfusion ; }
  
  public DataReader getDataReader() throws Exception {
    DataReader dataReader = new CvsFileDataReader(inputFile, true) ;
    return dataReader ;
  }
  
  private static boolean getBooleanArgument(CommandLine cmdLine, Option option) {
    return cmdLine.hasOption(option);
  }

  private static String getStringArgument(CommandLine cmdLine, Option inputFile) {
    return (String) cmdLine.getValue(inputFile);
  }
}