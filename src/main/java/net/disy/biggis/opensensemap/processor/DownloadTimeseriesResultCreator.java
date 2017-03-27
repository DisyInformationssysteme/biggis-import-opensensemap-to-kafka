package net.disy.biggis.opensensemap.processor;

import java.io.IOException;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import net.disy.biggis.opensensemap.model.LoadSensorDataCommand;
import net.disy.biggis.opensensemap.model.Measurement;
import net.disy.biggis.opensensemap.model.Sensor;
import net.disy.biggis.opensensemap.model.TimeSeries;

@Component
public class DownloadTimeseriesResultCreator implements Processor {

  private static final ObjectMapper OM = new ObjectMapper();
  private static final CollectionType MEASUREMENTS_TYPE = OM
      .getTypeFactory()
      .constructCollectionType(List.class, Measurement.class);

  @Override
  public void process(Exchange exchange) throws Exception {
    Message inMessage = exchange.getIn();

    TimeSeries timeSeries = new TimeSeries();
    timeSeries.setSensor(getSensor(inMessage));
    timeSeries.setMeasurements(getMeasurements(inMessage));

    inMessage.setBody(timeSeries);
  }

  private Sensor getSensor(Message inMessage) {
    LoadSensorDataCommand command = inMessage
        .getHeader(DownloadTimeseriesDataQueryCreator.HEADER_COMMAND, LoadSensorDataCommand.class);
    return command.getSensor();
  }

  private List<Measurement> getMeasurements(Message inMessage)
      throws IOException,
      JsonParseException,
      JsonMappingException {
    final String body = inMessage.getBody(String.class);
    return OM.readValue(body, MEASUREMENTS_TYPE);
  }

}
