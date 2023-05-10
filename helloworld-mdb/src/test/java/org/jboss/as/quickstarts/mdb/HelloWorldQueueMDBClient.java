package org.jboss.as.quickstarts.mdb;

import java.net.URISyntaxException;
import java.util.Optional;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class HelloWorldQueueMDBClient { 
	   
	   static String getBrokerUrl() {
		   final String host = Optional.ofNullable(System.getenv("amq.broker.host"))
					.orElse( Optional.ofNullable(System.getProperty("amq.broker.host"))
					.orElse("192.168.50.90"));
		   final String port = Optional.ofNullable(System.getenv("amq.broker.port"))
					.orElse( Optional.ofNullable(System.getProperty("amq.broker.port"))
					.orElse("61616"));
		    String template = "tcp://%s:%s";
		    return String.format(template, host,port);
	   }
	   public static void main(String[] args) throws URISyntaxException, Exception {
	        Connection connection = null;
	        try {
	            // Producer
	            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
	            		getBrokerUrl());
	            connection = connectionFactory.createConnection();
	            Session session = connection.createSession(false,
	                    Session.AUTO_ACKNOWLEDGE);
	            Queue queue = session.createQueue("jms.queue.testQueueRemoteArtemis");
	            MessageProducer producer = session.createProducer(queue);
	            String task = "Task";
	            for (int i = 0; i < 1000000000; i++) {
	                String payload = task + i;
	                Message msg = session.createTextMessage(payload);
	                System.out.println("Sending text '" + payload + "'");
	                producer.send(msg);
	            }
	            producer.send(session.createTextMessage("END"));
	            session.close();
	        } finally {
	            if (connection != null) {
	                connection.close();
	            }
	        }
	    }

}
