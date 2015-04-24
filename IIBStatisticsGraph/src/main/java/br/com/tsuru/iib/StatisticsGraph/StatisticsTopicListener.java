package br.com.tsuru.iib.StatisticsGraph;

import java.io.StringReader;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.stats.WMQIStatisticsAccounting;

public class StatisticsTopicListener implements MessageListener {
	
	private static Logger log = LogManager.getLogger(StatisticsTopicListener.class);
	private static JAXBContext jc;
	private static Unmarshaller u;
	static {
		try {
			jc = JAXBContext.newInstance("com.ibm.stats");
			u = jc.createUnmarshaller();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			TextMessage tm = (TextMessage) message;
			try {
				
				log.info("Got message");
				WMQIStatisticsAccounting stat = (WMQIStatisticsAccounting) u.unmarshal(new StringReader(tm.getText()));
				StringBuffer metric = new StringBuffer();
				metric.append("com.ibm.iib.");
				metric.append(stat.getMessageFlow().getBrokerLabel());
				metric.append('.');
				metric.append(stat.getMessageFlow().getExecutionGroupName());
				metric.append('.');
				metric.append(stat.getMessageFlow().getApplicationName());
				metric.append('.');
				metric.append(stat.getMessageFlow().getMessageFlowName());
				metric.append('.');
				metric.append("tps");
				log.debug("Statistics for: " + metric);
				double elapsedSeconds = stat.getMessageFlow().getTotalElapsedTime() / 1000000.0; // TotalElapsedTime
				// is
				// in
				// microseconds
				double tps = 0;

				if (stat.getMessageFlow().getTotalInputMessages() > 0) {
					log.debug("Message flow has been used.");
					tps = stat.getMessageFlow().getTotalInputMessages()
							/ elapsedSeconds;

					XMLGregorianCalendar eventDate = stat.getMessageFlow().getStartDate();
					XMLGregorianCalendar eventTime = stat.getMessageFlow().getStartTime();
					eventDate.setHour(eventTime.getHour());
					eventDate.setMinute(eventTime.getMinute());
					eventDate.setSecond(eventTime.getSecond());
					CarbonClient.sendMetrics(metric.toString(), tps, eventDate.toGregorianCalendar().getTimeInMillis());
				}
			} catch (JAXBException e) {
				log.error("Error while parsing the message",e);
			} catch (JMSException e) {
				log.error("Error while handling the topic",e);
			}
		} else {
			log.warn("Message is not a TextMessage type!");
		}

	}

}
