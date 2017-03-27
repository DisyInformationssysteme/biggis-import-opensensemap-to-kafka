package net.disy.biggis.opensensemap.processor;

import static java.text.MessageFormat.format;
import static net.disy.biggis.opensensemap.config.OpensensemapTimeSeriesDownloadConfiguration.QUERY_START_TIME;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import net.disy.biggis.opensensemap.model.LoadSensorDataCommand;
import net.disy.biggis.opensensemap.model.Sensor;

@Component
public class LoadSensorDataCommandFactory {

  public static final String SENSEBOX_ID = "OpensensemapSenseboxId";
  public static final String SENSEBOX_CREATE_DATE = "OpensensemapSenseboxCreateDate";

  private static final Logger LOG = LoggerFactory.getLogger(LoadSensorDataCommandFactory.class);

  private final ConcurrentHashMap<String, LoadSensorDataCommand> lastCommands = new ConcurrentHashMap<>();

  @Autowired
  @Qualifier(QUERY_START_TIME)
  private ZonedDateTime defaultStartDate;

  public void createInitialCommand(Exchange exchange) {
    Message inMessage = exchange.getIn();
    String senseBoxId = inMessage.getHeader(SENSEBOX_ID, String.class);
    Sensor sensor = inMessage.getBody(Sensor.class);
    ZonedDateTime defaultStartDate = getStartDate(inMessage);
    ZonedDateTime startDate = Optional
        .ofNullable(sensor.getId())
        .map(lastCommands::get)
        .map(LoadSensorDataCommand::getEnd)
        .filter(defaultStartDate::isBefore)
        .orElse(defaultStartDate);

    LoadSensorDataCommand command = createCommand(senseBoxId, sensor, startDate);
    inMessage.setBody(command);
  }

  private LoadSensorDataCommand createCommand(
      String senseBoxId,
      Sensor sensor,
      ZonedDateTime startDate) {
    LoadSensorDataCommand command = new LoadSensorDataCommand();
    command.setSensor(sensor);
    command.setSenseBoxId(senseBoxId);
    command.setStart(startDate);
    command.setEnd(addQueryInterval(startDate));
    return command;
  }

  private ZonedDateTime addQueryInterval(ZonedDateTime startDate) {
    return startDate.plusDays(1L);
  }

  private ZonedDateTime getStartDate(Message message) {
    String createdAt = message.getHeader(SENSEBOX_CREATE_DATE, String.class);
    if (createdAt == null) {
      return defaultStartDate;
    }
    try {
      return ZonedDateTime.parse(createdAt);
    } catch (DateTimeParseException logged) {
      LOG.info(format("Unable to parse ''createdAt'' field with value ''{0}''", createdAt));
      return defaultStartDate;
    }
  }

  public void createFollowUpCommand(Exchange exchange) {
    Message inMessage = exchange.getIn();
    LoadSensorDataCommand command = inMessage
        .getHeader(DownloadTimeseriesDataQueryCreator.HEADER_COMMAND, LoadSensorDataCommand.class);
    Sensor sensor = command.getSensor();
    ZonedDateTime endDate = lastCommands
        .compute(sensor.getId(), (k, v) -> refresh(k, v, command))
        .getEnd();
    if (endDate.isBefore(sensor.getLastMeasurement().getCreatedAt())) {
      LoadSensorDataCommand followUp = createCommand(command.getSenseBoxId(), sensor, endDate);
      inMessage.setBody(followUp);
      inMessage.setHeader(DownloadTimeseriesDataQueryCreator.HEADER_COMMAND, null);
    } else {
      inMessage.setBody(null);
      LOG.info(format("Polling finished on sensor ''{0}''", sensor.getId()));
    }
  }

  private LoadSensorDataCommand refresh(
      String id,
      LoadSensorDataCommand storedCommand,
      LoadSensorDataCommand currentCommand) {
    if (storedCommand == null) {
      return currentCommand;
    }
    ZonedDateTime storedDate = storedCommand.getEnd();
    ZonedDateTime newDate = currentCommand.getEnd();
    return newDate.isAfter(storedDate) ? currentCommand : storedCommand;
  }
}
