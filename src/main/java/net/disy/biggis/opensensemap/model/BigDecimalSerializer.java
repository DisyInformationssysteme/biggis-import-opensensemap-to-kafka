package net.disy.biggis.opensensemap.model;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

  @Override
  public void serialize(BigDecimal value, JsonGenerator generator, SerializerProvider serializers)
      throws IOException,
      JsonProcessingException {
    if (value == null) {
      generator.writeNull();
    } else {
      generator.writeString(value.toString());
    }

  }

}
