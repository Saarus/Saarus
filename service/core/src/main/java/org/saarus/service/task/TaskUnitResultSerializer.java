package org.saarus.service.task;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

public class TaskUnitResultSerializer extends JsonSerializer<Object> {
  @Override
  public  void serialize(Object resutl, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeObjectField("type", resutl.getClass().getName());
    jsonGenerator.writeObjectField("data", resutl);
    jsonGenerator.writeEndObject();
  }
}