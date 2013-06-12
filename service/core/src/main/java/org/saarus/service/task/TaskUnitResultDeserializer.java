package org.saarus.service.task;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import java.io.IOException;

public class TaskUnitResultDeserializer extends JsonDeserializer<Object> {

  @Override
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