package net.disy.biggis.opensensemap.processor;

import static java.util.regex.Pattern.quote;
import static net.disy.biggis.opensensemap.config.OpensensemapTimeSeriesDownloadConfiguration.PATHVAR_SENSE_BOX_ID;
import static net.disy.biggis.opensensemap.config.OpensensemapTimeSeriesDownloadConfiguration.PATHVAR_SENSOR_ID;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import net.disy.biggis.opensensemap.config.OpensensemapTimeSeriesDownloadConfiguration;
import net.disy.biggis.opensensemap.model.LoadSensorDataCommand;

@Component
public class DownloadTimeseriesDataQueryCreator implements Processor {

  public static final String HEADER_COMMAND = "LoadSensorDataCommand";

  private static final Pattern REPLACE_SENSEBOX_ID = Pattern.compile(quote(PATHVAR_SENSE_BOX_ID));
  private static final Pattern REPLACE_SENSOR_ID = Pattern.compile(quote(PATHVAR_SENSOR_ID));

  @Autowired
  @Qualifier(OpensensemapTimeSeriesDownloadConfiguration.HTTPS_CLIENT_MEASUREMENTS)
  private String uriTemplate;

  @Override
  public void process(Exchange exchange) throws Exception {
    Message inMessage = exchange.getIn();
    LoadSensorDataCommand command = inMessage.getBody(LoadSensorDataCommand.class);
    inMessage.setHeader(HEADER_COMMAND, command);

    Map<String, String> pathVariables = new HashMap<>();
    pathVariables.put(PATHVAR_SENSE_BOX_ID, command.getSenseBoxId());
    pathVariables.put(PATHVAR_SENSOR_ID, command.getSensor().getId());

    String uri = REPLACE_SENSEBOX_ID.matcher(uriTemplate).replaceAll(command.getSenseBoxId());
    uri = REPLACE_SENSOR_ID.matcher(uri).replaceAll(command.getSensor().getId());

    String requestUrl = new StringBuilder(uri)
        .append("?from-date=")
        .append(command.getStart().toString())
        .append("&to-date=")
        .append(command.getEnd().toString())
        .append("&format=json")
        .toString();

    inMessage.setHeader(Exchange.HTTP_URI, requestUrl);
    inMessage.setBody(null);
  }

}
