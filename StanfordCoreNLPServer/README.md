# Stanford CoreNLP Server

## About

This server component is use for Natural Language Processing. The HTTP server implementation is
based on [Stanford CoreNLP XML Server](https://github.com/nlohmann/StanfordCoreNLPXMLServer) which offers the functionality of the [Stanford CoreNLP Toolkit](http://nlp.stanford.edu/software/corenlp.shtml)
as HTTP-XML-Server. This version was extended by a JSON-Outputter (from a newer version of Stanford
CoreNLP) that allows to respond the annotated text in JSON format to the requesting client (additinally to XML).

## Usage

The server will be listening by default on Port *8080*.
The text you want to analyze needs to be POSTed as field `text`.
Response format can be defined with parameter `outputMode` to *xml* (default) or *json*.

## Prerequisites
- [Oracle JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or [OpenJDK](http://openjdk.java.net/install/) version 8 or later
- [Apache Ant](http://ant.apache.org) (for building)

## Installation

1. Download and install the third party libraries:
	```shell
	cd StanfordCoreNLPServer
	ant libs
	```

2. Compile the JAR file:
	```shell
	ant jar
	```
3. Run the server (default on Port *8080*):
	```shell
	ant run
	```
	(optionally append *-Dport=<PortNo>*)

	or just execute the jar (from *build/jar*) with other port number as optional argument.

For Simpsons-related Named Entity Recognition the file *res/simpsons-regexner.txt* is needed!

Logging options and default port can be changed in the StanfordCoreNLPServer class.

