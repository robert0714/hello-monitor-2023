package org.jboss.as.quickstarts.common;

import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;

import java.util.function.Function;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

public class JMS11Sender {
	private static final Logger logger = getLogger(JMS11Sender.class.getName());

	 

	// https://docs.payara.fish/community/docs/documentation/user-guides/mdb-in-payara-micro.html
	public void sendMsgByJMS11(final ConnectionFactory cf, final Queue queue,
			final Function<Session, Message> message) {
		try (Connection connection = cf.createConnection()) {
			Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			session.createProducer(queue).send(message.apply(session));
		} catch (JMSException ex) {
			logger.log(SEVERE, "Exception sending msg", ex);
		}
	}

	public TextMessage createTextMessage(Session session, String message) {
		try {
			return session.createTextMessage(message);
		} catch (JMSException e) {
			throw new IllegalStateException(e);
		}
	}
}
