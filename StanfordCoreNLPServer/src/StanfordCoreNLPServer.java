/*
	Stanford CoreNLP Server
	Copyright (C) 2015 Daniel Theiss

    originally based on:
    Stanford CoreNLP XML Server
    Copyright (C) 2013 Niels Lohmann

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class StanfordCoreNLPServer implements Container {
	
	private static StanfordCoreNLP pipeline;
	
	private static int port = 8080; //default port
	
	private static final Logger log = Logger
			.getLogger(StanfordCoreNLPServer.class.getName());
	
	private static int total_requests = 0;

	// an interface to the Stanford Core NLP
	public String parse(String text, String outputMode)
			throws java.io.IOException {
		// annotate input text
		Annotation annotation = new Annotation(text);
		pipeline.annotate(annotation);

		StringWriter sb = new StringWriter();

		// Print pipeline in format of outputMode
		if (outputMode.equals("json")) {
//			pipeline.jsonPrint(annotation, sb);
			JSONOutputter.jsonPrint(annotation, sb, pipeline);
		}
		else if (outputMode.equals("xml"))
			pipeline.xmlPrint(annotation, sb);

		return sb.toString();
	}

	public void handle(Request request, Response response) {
		try {
			int request_number = ++total_requests;
			log.info("Request " + request_number + " from "
					+ request.getClientAddress().getHostName());
			long time = System.currentTimeMillis();

			response.setValue("Server",
					"Stanford CoreNLP Server/1.0 (Simple 5.1.6)");
			response.setDate("Date", time);
			response.setDate("Last-Modified", time);
			
			// Check for POST Query
			if(!request.getMethod().equals("POST")) {
				responseWithErrorStatus(response, Status.BAD_REQUEST, "Please use POST for querys!");
				log.info("Request " + request_number + " did not use POST, was "+request.getMethod());
				return;
			}
			
			// Set outputMode
			String outputMode = "xml"; // default outputMode
			if (request.getQuery().containsKey("outputMode")) {
				outputMode = request.getQuery().get("outputMode");
			}

			// Check "outputMode" and set Content-Type of response
			if (outputMode.equals("xml")) {
				response.setContentType("application/xml");
			} else if (outputMode.equals("json")) {
				response.setContentType("application/json");
			} else {
				responseWithErrorStatus(response, Status.BAD_REQUEST,
						"The given outputMode is not allowed! Must be 'xml' or 'json'.");
				log.info("Request " + request_number
						+ " with invalid outputMode (" + outputMode + ").");
				return;
			}

			// Check if input "text" is there
			if (!request.getQuery().containsKey("text")) {
				responseWithErrorStatus(response, Status.BAD_REQUEST,
						"Input 'text' is missing!");
				log.info("Request " + request_number + " with missing text.");
				return;
			}

			// pass "text" POST query to Stanford Core NLP parser
			String text = request.getQuery().get("text");
			PrintStream body = response.getPrintStream();
			body.println(parse(text, outputMode));
			body.close();

			long time2 = System.currentTimeMillis();
			log.info("Request " + request_number + " done (" + (time2 - time)
					+ " ms)");
		} catch (Exception e) {
			try {
				responseWithErrorStatus(response, Status.INTERNAL_SERVER_ERROR,
						"An internal server has occured. Please try again.");
			} catch (IOException e1) {
				log.log(Level.SEVERE, "Exception", e1);
			}
			log.log(Level.SEVERE, "Exception", e);
		}
	}

	private static void responseWithErrorStatus(Response response,
			Status status, String msg) throws IOException {
		response.setStatus(status);
		response.setContentType("text/plain");
		PrintStream body = response.getPrintStream();
		body.println(msg);
		body.close();
	}

	public static void main(String args[]) throws Exception {
		// use port if given
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			// silently keep port at 8080
		}

		Properties props = new Properties();
		props.setProperty("annotators",
				"tokenize, ssplit, pos, lemma, ner, regexner"); // entitymentions, parse, relation");
	    props.setProperty("regexner.mapping", "res/simpsons-regexner.txt");

		// initialize the Stanford Core NLP
		pipeline = new StanfordCoreNLP(props); // optionally set props as
												// parameter

		// start the server
		Container container = new StanfordCoreNLPServer();
		Server server = new ContainerServer(container);
		Connection connection = new SocketConnection(server);
		SocketAddress address = new InetSocketAddress(port);
		connection.connect(address);

		log.info("Initialized server at port " + port + ".");
	}
}
