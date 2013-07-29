package sample.nlp.twitter.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
  static public void mkdirs(String path) throws IOException {
    File file = new File(path) ;
    if(!file.exists()) {
      if(!file.mkdirs()) {
        throw new IOException("Cannot create directory " + path) ;
      }
    }
  }

  static public void copyTo(InputStream src, String dest) throws IOException {
    File destFolder = new java.io.File(dest);
    if (destFolder.isFile()) {
      dest = destFolder.getParent();
    }
    OutputStream output = new FileOutputStream(dest) ;
    byte[] buff = new byte[8192] ;
    int len = 0 ;
    while ((len = src.read(buff)) > 0) {
      output.write(buff, 0, len);
    }
    src.close();
    output.close();
  }
}
