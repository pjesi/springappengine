package is.hax.google.mail;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.*;
import java.util.logging.Logger;
import java.util.Properties;
import java.util.Arrays;

/**
 * @author Peter Backlund
 * Borrowed from http://code.google.com/p/feeling-lucky-pictures
 */
public class GoogleMailSender extends JavaMailSenderImpl {

	Logger logger = Logger.getLogger(GoogleMailSender.class.getName());

	@Override
	public synchronized Session getSession() {
		return Session.getDefaultInstance(new Properties(), null);
	}

	@Override
	protected Transport getTransport(Session session) throws NoSuchProviderException {
		return new Transport(session, null) {
			@Override
			public void connect(String host, int port, String username, String password) throws MessagingException {
				// Noop
			}
			@Override
			public void close() throws MessagingException {
				// Noop
			}
			@Override
			public void sendMessage(Message message, Address[] recipients) throws MessagingException {
				logger.info("Sending message '" + message.getSubject() + "' to " + Arrays.toString(recipients));
				Transport.send(message, recipients);
			}
		};
	}
}
