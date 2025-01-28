package suites.execution;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

public class GmailTest {

	private static Properties getServerProperties(String protocol, String host, String port) {
		Properties properties = new Properties();

		// server setting
		properties.put(String.format("mail.%s.host", protocol), host);
		properties.put(String.format("mail.%s.port", protocol), port);

		// SSL setting
		properties.setProperty(String.format("mail.%s.socketFactory.class", protocol),
				"javax.net.ssl.SSLSocketFactory");
		properties.setProperty(String.format("mail.%s.socketFactory.fallback", protocol), "false");
		properties.setProperty(String.format("mail.%s.socketFactory.port", protocol), String.valueOf(port));

		return properties;
	}

	public static void check(String host, String user, String password) {
		try {

			// create properties field

			Properties properties = getServerProperties("imap", host, "993");
			Session session = Session.getDefaultInstance(properties);

//			properties.put("mail.pop3.host", host);
//			properties.put("mail.pop3.port", "993");
//			properties.put("mail.pop3.starttls.enable", "true");
//			Session emailSession = Session.getDefaultInstance(properties);

			// create the POP3 store object and connect with the pop server
//			Store store = emailSession.getStore("pop3s");
			Store store = session.getStore("imap");

			store.connect(host, user, password);

			// create the folder object and open it
			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.getUnreadMessageCount();
			System.out.println("unread messages.length---" + emailFolder.getUnreadMessageCount());
			emailFolder.open(Folder.READ_WRITE);

			Flags seen = new Flags(Flags.Flag.SEEN);
			FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
			Message[] messages = emailFolder.search(unseenFlagTerm);

			// retrieve the messages from the folder in an array and print it
//			Message[] messages = emailFolder.getMessages();
//			ArrayUtils.reverse(messages);
//			messages = Arrays.asList(messages).stream().filter(message -> message.getReceivedDate().toString().contains("Oct 14")).collect(Collectors.toList());
			System.out.println("messages.length---" + messages.length);

			for (int i = 0, n = messages.length; i < n - 1; i++) {
				Message message = messages[i];
				if (!message.getReceivedDate().toString().contains("Oct 14")) {
					continue;
				}
				String contentType = message.getContentType();
				String messageContent = "";
//				System.out.println("contentType: " + contentType);
				if (contentType.toLowerCase().contains("text/plain")
						|| contentType.toLowerCase().contains("text/html")) {
					try {
						Object content = message.getContent();
						if (content != null) {
							messageContent = content.toString();
						}

					} catch (Exception ex) {
						messageContent = "[Error downloading content]";
						ex.printStackTrace();
					}
				}

				if (message.getContent() instanceof Multipart) {
					Multipart mime = (Multipart) message.getContent();

					for (int j = 0; j < mime.getCount(); j++) {
						BodyPart part = mime.getBodyPart(i);
						messageContent += part.getContent().toString();
					}
				}
				System.out.println("---------------------------------");
				System.out.println("Email Number " + (i + 1));
				System.out.println("Subject: " + message.getSubject());
				System.out.println("From: " + message.getFrom()[0]);
				System.out.println("date - " + message.getReceivedDate());
//				System.out.println("Text: " + messageContent);
			}

			// close the store and folder objects
			emailFolder.close(false);
			store.close();

		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void read() {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(new File("C:\\smtp.properties")));
			Session session = Session.getDefaultInstance(props, null);

			Store store = session.getStore("imaps");
			store.connect("smtp.gmail.com", "*************@gmail.com", "your_password");

			Folder inbox = store.getFolder("inbox");
			inbox.open(Folder.READ_ONLY);
			int messageCount = inbox.getMessageCount();

			System.out.println("Total Messages:- " + messageCount);

			Message[] messages = inbox.getMessages();
			System.out.println("------------------------------");
			for (int i = 0; i < 10; i++) {
				System.out.println("Mail Subject:- " + messages[i].getSubject());
			}
			inbox.close(true);
			store.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {

		String host = "imap.gmail.com";
		String username = "syssolutions1979@gmail.com";// change accordingly
//		String password = "SYS@2022";// change accordingly
		String password = "lzquscrxwasnyqkd";

		check(host, username, password);

	}

}
