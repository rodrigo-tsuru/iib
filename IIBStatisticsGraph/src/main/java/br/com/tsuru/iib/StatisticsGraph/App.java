package br.com.tsuru.iib.StatisticsGraph;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.MQHeaderIterator;

/**
 * Hello world!
 * 
 */
public class App {
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
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
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
				System.out.println(new String(content));
				System.exit(1);
				Document doc = builder.parse(new ByteArrayInputStream(content));
				Element messageFlow = (Element) doc.getDocumentElement().getFirstChild();
				String egName = messageFlow.getAttribute("ExecutionGroupName");
				String flowName = messageFlow.getAttribute("MessageFlowName");
				String qtd = messageFlow.getAttribute("TotalInputMessages");
				double elapsedMicroseconds = Double.valueOf(messageFlow
						.getAttribute("TotalElapsedTime"));
				double elapsedSeconds = elapsedMicroseconds / 1000000.0; // TotalElapsedTime
																			// is
																			// in
																			// microseconds
				double tps = 0;

				if (Integer.valueOf(qtd) > 0) {
					System.out.println(qtd);
					tps = Integer.valueOf(qtd) / elapsedSeconds;
					String time = messageFlow.getAttribute("StartDate")
							+ messageFlow.getAttribute("StartTime");
					Date eventDate = sdf.parse(time);
					sendMetrics(flowName, tps, eventDate.getTime());
					System.out.println("Transaction per second: " + tps);
				}

			} catch (MQException e) {
				if (e.reasonCode == 2033) {
					System.out.println("Nenhuma mensagem encontrada!");
					hasMessages = false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MQDataException e) {
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
