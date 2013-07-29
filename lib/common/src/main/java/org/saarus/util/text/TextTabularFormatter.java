package org.saarus.util.text;

import java.io.StringWriter;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TextTabularFormatter extends TabularPrinter {
	private StringWriter writer  ;
	
	public TextTabularFormatter(int[] width) {
		writer = new StringWriter() ;
		init(writer, width) ;
	}
	
	public String getFormattedText() { return writer.toString() ; }
}
