package br.com.tsuru.iib.StatisticsGraph;

import java.util.Hashtable;

import javax.jms.ConnectionConsumer;
import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.directory.InitialDirContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Hello world!
 * 
 */
public class App {

	private static Logger log = LogManager.getLogger(App.class);
	//public final static String DEFAULT_CONTEXT_FACTORY = "com.ibm.mq.jms.context.WMQInitialContextFactory"; // deprecated ME01
	//public final static String DEFAULT_CONTEXT_FACTORY = "com.ibm.mq.jms.Nojndi"; // supports queues only
	public final static String DEFAULT_CONTEXT_FACTORY = "com.sun.jndi.fscontext.RefFSContextFactory"; // filesystem JNDI implementation
	
	
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Shutdown Hook is running !");
			}
		});
		TopicConnectionFactory factory = null;
		TopicConnection connection = null;
		TopicSession session = null;
		Topic topic = null;

		Context ctx = null;
		
		Hashtable environment = new Hashtable();
		environment.put(Context.INITIAL_CONTEXT_FACTORY,
				App.DEFAULT_CONTEXT_FACTORY);
		environment.put(Context.PROVIDER_URL,"file:/tmp/");
		try {
			log.info("Getting the context factory.");
			ctx = new InitialDirContext(environment);
		} catch (Exception e) {
			log.error("Error during lookup of Context Factory ",e);
			return;
		}
		try {
			log.info("Getting the connection factory.");
			factory = (TopicConnectionFactory) ctx.lookup("Local_CF");
		} catch (Exception e) {
			log.error("Error during lookup of Queue Connection Factory ",e);
			return;
		}
		try {
			// http://www-01.ibm.com/support/knowledgecenter/SSKM8N_8.0.0/com.ibm.etools.mft.doc/aq20080_.htm
			log.info("Getting the topic.");
			topic = (Topic) ctx.lookup("StatisticsTopic");
		} catch (Exception e) {
			log.error("Error during lookup of topic",e);
			return;
		}

		/***********************************************/
		/* Create our objects, get selector from user. */
		/***********************************************/
		try {
			connection = factory.createTopicConnection();
			// create a server session pool
			MQServerSessionPool ssPool = new MQServerSessionPool(connection);
			connection.setExceptionListener(ssPool);
			// create a topic connection consumer
			ConnectionConsumer connConsumer = connection.createConnectionConsumer(topic, null, ssPool, 10);
			log.info("Connecting to JMS Provider");
			connection.start();

			// wait for connection consumer
			while (true) {
				Thread.sleep(10000);
			}

		} catch (JMSException je) {
			log.error("JMSException: ",je);
			Exception le = je.getLinkedException();
			if (le != null)
				log.error("Linked exception: " + le);

		} catch (Exception e) {
			/*******************************************/
			/* Catch and display exception information */
			/*******************************************/
			log.error("Exception: ",e);
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (JMSException e) {
				log.error("Error while closing the connection",e);
			}
		}

		log.info("Finished");

	}

}
