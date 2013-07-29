package org.saarus.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.saarus.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class CommandParser {
	private Options options ;
	private CommandLine cmd ;
	private String helpMesg ;
	
	public CommandParser(String helpMesg) {
		options = new Options() ;
		addOption("help", false, "Print this help message") ;
		addOption("apphome", true, "The app home directory") ;
		this.helpMesg = helpMesg ;
	}
	
	public void addOption(String name, boolean hasArg, String desc) {
		Option op = new Option(name, hasArg, desc) ;
		options.addOption(op);
	}

	public void addMandatoryOption(String name, boolean hasArg, String desc) {
		Option op = new Option(name, hasArg, desc) ;
		op.setRequired(true) ;
		options.addOption(op);
	}
	
	public boolean hasOption(String name) { return cmd.hasOption(name) ; }
	
	public String getOption(String name, String dvalue) {
		return cmd.getOptionValue(name, dvalue) ;
	}
	
	public int getOptionAsInt(String name, int dvalue) {
		String val = cmd.getOptionValue(name, null) ;
		if(val == null) return dvalue ;
		return Integer.parseInt(val) ;
	}
	
	public String[] getOptionValues(String name, String[] dvalue) {
		String values = cmd.getOptionValue(name, null) ;
		if(values == null) return dvalue ;
		return StringUtil.toStringArray(values) ;
	}
	
	public int getOption(String name, int dvalue) {
		String value = cmd.getOptionValue(name, null) ;
		if(value == null) return dvalue ;
		return Integer.parseInt(value) ;
	}
	
	public boolean parse(String[] args)  {
		try {
		CommandLineParser parser = new PosixParser();
    this.cmd = parser.parse(options, args);
    if(cmd.hasOption("help")) {
    	printHelp() ;
    	return false ;
    }
		} catch(Throwable ex) {
			System.out.println("ERROR: " + ex.getMessage()) ;
			printHelp() ;
			return false ;
		}
    return true ;
	}

	public void printHelp()  {
    HelpFormatter formatter = new HelpFormatter();
  	formatter.printHelp(helpMesg, options );
	}
}