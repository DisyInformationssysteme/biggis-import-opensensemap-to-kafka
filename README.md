# biggis-import-opensensemap-to-kafka
Import of sensor data from opensensemap.org to Apache Kafka

This repository contains a Kafka producer for the stream processing prototype in [https://github.com/DisyInformationssysteme/biggis-streaming-vector-data](https://github.com/DisyInformationssysteme/biggis-streaming-vector-data).
It requires the [BigGIS Infrastructure](https://github.com/DisyInformationssysteme/biggis-infrastructure) to be present and running, in particular Kafka and Zookeeper.
Data is read via the REST API from https://opensensemap.org.

The producer is implemented using Apache Camel and Spring Boot. It polls the openSenseMap API periodically for sensor information which is then posted to the Kakfa topic _opensensemap-sensors_. For any sensor a download command is posted to _opensensemap-timeseries-download-commands_ if the last measurement of the sensors is after a configurable start date. The client features a number of workers subscribe to the command topic. For each command a http request is send to openSenseBox to retrieve all measurements for a period of one day. The results are posted to the topic _opensensemap-timeseires-download-measurements_. Successful commands are logged to _opensensemap-timeseires-download-log_. After writing the log a new download command ist posted for the next day if the command starts before the last measurement retrieved in the periodical polling.

The client keeps track of the last measurement for each sensor that has been logged. When the next polling run is started the download commands will be issued starting with this date. A possible extension is to read the Kafka log topic to initialize this command store on startup of the client.

This work was done for the [BigGIS project](http://biggis-project.eu/) funded by the [German Federal Ministry of Education and Research (BMBF)](https://www.bmbf.de).
