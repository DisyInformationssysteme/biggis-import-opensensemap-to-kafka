package net.disy.biggis.opensensemap.model;

import java.io.IOException;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class ZonedDateTimeDeserializer extends StdDeserializer<ZonedDateTime> {

  public ZonedDateTimeDeserializer() {
    this(null);
  }

  public ZonedDateTimeDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public ZonedDateTime deserialize(JsonParser parser, DeserializationContext ctxt)
      throws IOException,
      JsonProcessingException {
    String text = parser.getText();
    return ZonedDateTime.parse(text);
  }

}
