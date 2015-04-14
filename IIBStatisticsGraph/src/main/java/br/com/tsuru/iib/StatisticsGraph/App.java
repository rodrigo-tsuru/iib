package br.com.tsuru.iib.StatisticsGraph;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss.SSSSSS");
		MQEnvironment.disableTracing();
		MQQueueManager qmgr = null;
		MQQueue q = null;
		MQMessage message;
		try {
			qmgr = new MQQueueManager("IB9NODE");
			int openOptions = CMQC.MQOO_INQUIRE
					+ CMQC.MQOO_FAIL_IF_QUIESCING + CMQC.MQOO_INPUT_SHARED;
			q = qmgr.accessQueue("ESTATISTICAS", openOptions);
			
			MQGetMessageOptions gmo = new MQGetMessageOptions();
			gmo.options = MQC.MQGMO_WAIT;
			gmo.waitInterval = 5000;
		} catch (MQException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		boolean hasMessages = true;
		while (hasMessages) {
			try {
				message = new MQMessage();
				q.get(message);
				byte[] content = new byte[message.getDataLength()];
				message.readFully(content);
				System.out.println(new String(content));
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(new ByteArrayInputStream(content));
				Element messageFlow = (Element) doc.getDocumentElement().getFirstChild();
				String egName = messageFlow.getAttribute("ExecutionGroupName");
				String flowName = messageFlow.getAttribute("MessageFlowName");
				String qtd = messageFlow.getAttribute("TotalInputMessages");
				double elapsedSeconds = Double.valueOf(messageFlow.getAttribute("TotalElapsedTime")) / 1000000.0; // TotalElapsedTime
																		// is in
																		// microseconds
				//System.out.println("Elapsed seconds: " + elapsedSeconds);
				double tps = 0;

				if (Integer.valueOf(qtd) > 0) {
					System.out.println(qtd);
					tps = Integer.valueOf(qtd) / elapsedSeconds;
					String time = messageFlow.getAttribute("StartDate")	+ messageFlow.getAttribute("StartTime");
					Date eventDate = sdf.parse(time);
				}

				System.out.println("Transaction per second: " + tps);

			} catch (MQException e) {
				if (e.reasonCode == 2033) {
					System.out.println("Nenhuma mensagem encontrada!");
					hasMessages = false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
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
}
