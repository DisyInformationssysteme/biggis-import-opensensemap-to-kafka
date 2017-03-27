package net.disy.biggis.opensensemap.model;

import java.io.IOException;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {

  @Override
  public void serialize(
      ZonedDateTime value,
      JsonGenerator generator,
      SerializerProvider serializers)
      throws IOException,
      JsonProcessingException {
    if (value == null) {
      generator.writeNull();
    } else {
      generator.writeString(value.toString());
    }

  }

}
