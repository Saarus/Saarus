package org.saarus.util.json;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.saarus.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class JSONSerializer {
  final static public JSONSerializer JSON_SERIALIZER = new JSONSerializer() ;

  private ObjectMapper mapper ;

  public JSONSerializer() {
    mapper = new ObjectMapper(); // can reuse, share globally
    mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false) ;
    mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL) ;
    DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter() ;
    prettyPrinter.indentArraysWith(new DefaultPrettyPrinter.Lf2SpacesIndenter()) ;
    mapper.prettyPrintingWriter(prettyPrinter) ;
    mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true) ;
  }

  public void setIgnoreUnknownProperty(boolean b) {
    mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, !b);
  }

  public <T> byte[] toBytes(T idoc) throws IOException {
    StringWriter w = new StringWriter() ;
    mapper.writeValue(w, idoc);
    w.close() ;
    return w.getBuffer().toString().getBytes(StringUtil.UTF8) ;
  }

  public <T> T fromBytes(byte[] data, Class<T> type) throws IOException {
    StringReader reader = new StringReader(new String(data, StringUtil.UTF8)) ;
    return mapper.readValue(reader , type);
  }

  public <T> String toString(T idoc) throws IOException {
    StringWriter writer = new StringWriter() ;
    mapper.writeValue(writer, idoc);
    return writer.toString() ;
  }

  public  String toString(JsonNode node) throws IOException {
    StringWriter writer = new StringWriter() ;
    mapper.writeValue(writer, node);
    return writer.toString() ;
  }

  public <T> T fromString(String data, Class<T> type) throws IOException {
    StringReader reader = new StringReader(data) ;
    return mapper.readValue(reader , type);
  }

  public JsonNode fromString(String data) throws IOException {
    StringReader reader = new StringReader(data) ;
    return mapper.readTree(reader);
  }
  
  static public class GenericTypeDeserializer extends JsonDeserializer<Object> {
    public Object deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
      ObjectCodec oc = jsonParser.getCodec();
      JsonNode node = oc.readTree(jsonParser);
      String resultType = node.get("type").getTextValue();
      try {
        Class type = Class.forName(resultType)  ;
        JsonNode rnode = node.get("data") ;
        Object val = oc.treeToValue(rnode, type);
        return val ;
      } catch (ClassNotFoundException e) {
        throw new IOException(e) ;
      }
    }
  }

  static public class GenericTypeSerializer extends JsonSerializer<Object> {
    public  void serialize(Object resutl, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
      jsonGenerator.writeStartObject();
      jsonGenerator.writeObjectField("type", resutl.getClass().getName());
      jsonGenerator.writeObjectField("data", resutl);
      jsonGenerator.writeEndObject();
    }
  }
}