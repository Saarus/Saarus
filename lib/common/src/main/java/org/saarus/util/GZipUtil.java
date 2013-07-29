package org.saarus.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtil {
  static public byte[] compress(ByteBuffer buffer) throws Exception {
    return compress(buffer.array()) ;
  }
  
  static public byte[] compress(byte[] bytes) throws IOException {
    return compress(bytes, 0, bytes.length) ;
  }
  
  static public byte[] compress(byte[] bytes, int fromPos, int length) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length/4) ;
    GZIPOutputStream gzipOut = new GZIPOutputStream(bos);
    gzipOut.write(bytes, fromPos, length);
    gzipOut.close() ;
    return bos.toByteArray() ;
  }
  
  static public byte[] decompress(ByteBuffer buffer) throws IOException {
    return decompress(buffer.array()) ;
  }
  
  static public byte[] decompress(byte[] bytes)  throws IOException {
    ByteArrayInputStream bin = new ByteArrayInputStream(bytes) ;
    GZIPInputStream gzipIn = new GZIPInputStream(bin);
    ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
    byte[] buf = new byte[1024];
    int len;
    while ((len = gzipIn.read(buf)) > 0) bos.write(buf, 0, len);
    return bos.toByteArray() ;
  }
}
