package br.com.tsuru.iib.StatisticsGraph;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.MQHeaderIterator;
import com.ibm.stats.WMQIStatisticsAccounting;

/**
 * Hello world!
 * 
 */
public class App {
	private static JAXBContext jc; 
	private static Unmarshaller u;
	static {
		try {
			jc = JAXBContext.newInstance( "com.ibm.stats" );
			u = jc.createUnmarshaller();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss.SSSSSS");
		MQEnvironment.disableTracing();
		MQQueueManager qmgr = null;
		MQQueue q = null;
		MQMessage message = new MQMessage();
		MQGetMessageOptions gmo = new MQGetMessageOptions();
		try {
			qmgr = new MQQueueManager("IB9NODE");
			int openOptions = CMQC.MQOO_BROWSE;
			q = qmgr.accessQueue("ESTATISTICAS", openOptions);

			gmo.options = CMQC.MQGMO_BROWSE_NEXT;
			gmo.waitInterval = 5000;
		} catch (MQException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		boolean hasMessages = true;
		while (hasMessages) {
			try {
				message.clearMessage();
				message.correlationId = CMQC.MQCI_NONE;
				message.messageId = CMQC.MQMI_NONE;
				q.get(message, gmo);
				MQHeaderIterator it = new MQHeaderIterator(message);
				it.skipHeaders();
				byte[] content = new byte[message.getDataLength()];
				message.readFully(content);
				
				WMQIStatisticsAccounting stat = (WMQIStatisticsAccounting) u.unmarshal(new ByteArrayInputStream(content));
				double elapsedSeconds = stat.getMessageFlow().getTotalElapsedTime() / 1000000.0; // TotalElapsedTime
																			// is
																			// in
																			// microseconds
				double tps = 0;

				if (stat.getMessageFlow().getTotalInputMessages() > 0) {
					tps = stat.getMessageFlow().getTotalInputMessages() / elapsedSeconds;
					
					XMLGregorianCalendar eventDate = stat.getMessageFlow().getStartDate();
					XMLGregorianCalendar eventTime = stat.getMessageFlow().getStartTime();
					eventDate.setHour(eventTime.getHour());
					eventDate.setMinute(eventTime.getMinute());
					eventDate.setSecond(eventTime.getSecond());
					sendMetrics(stat.getMessageFlow().getMessageFlowName(), tps, eventDate.toGregorianCalendar().getTimeInMillis());
				}

			} catch (MQException e) {
				if (e.reasonCode == MQConstants.MQRC_NO_MSG_AVAILABLE) {
					System.out.println("No more messages available!");
					hasMessages = false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MQDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			q.close();
			qmgr.disconnect();
		} catch (MQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void sendMetrics(String metric, Double value, long timeStampInMillis) {
		try (Socket socket = new Socket("localhost", 2003);
				OutputStream s = socket.getOutputStream();) {

			PrintWriter out = new PrintWriter(s, true);

			out.printf("%s %f %d%n", metric, value, timeStampInMillis / 1000l);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
