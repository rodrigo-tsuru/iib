package br.com.tsuru.iib.monitor;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

import br.com.tsuru.iib.common.utils.ExceptionListParser;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.wmb.monitor.jaxb.ApplicationData.ComplexContent;
import com.ibm.wmb.monitor.jaxb.Event;

public class TransactionMonitor_Log4JImpl extends MbJavaComputeNode {

	private static Logger log = LogManager.getLogger(TransactionMonitor_Log4JImpl.class);
	private ExceptionListParser exParser = new ExceptionListParser();
	
	protected static JAXBContext jaxbContext = null;
	
	public void onInitialize() throws MbException {
		try {
			log.debug("Initializing JAXB...");
			jaxbContext = JAXBContext.newInstance("com.ibm.wmb.monitor.jaxb");
		} catch (JAXBException e) {
			log.error("Error while initializing JAXB!",e);
			throw new MbUserException(this, "onInitialize()", "", "", e.toString(), null);
		}
	}
	public void evaluate(MbMessageAssembly assembly) throws MbException {
		
		try {
			MbMessage message = assembly.getMessage();
			
			JAXBElement<Event> wrapper = (JAXBElement<Event>) jaxbContext.createUnmarshaller().unmarshal(message.getDOMDocument());
			Event event = wrapper.getValue();
			String payload = "";
			if(event.getBitstreamData() != null)
				payload = getStringFromBase64(event.getBitstreamData().getBitstream().getValue());
			
			// O nome do logger será o nome do message flow
			Logger logFlow = LogManager.getLogger(event.getEventPointData().getMessageFlowData().getMessageFlow().getName());
			String eventType = event.getEventPointData().getEventData().getEventSourceAddress();
						
			if(eventType.endsWith(".catch") || eventType.endsWith(".Rollback") || eventType.endsWith(".failure") || eventType.endsWith(".timeout") || eventType.endsWith(".fault")) {
				String stackTrace = null;
				if(event.getApplicationData() != null) {
					for(ComplexContent cc : event.getApplicationData().getComplexContent()) {
						if("ExceptionList".equals(cc.getElementName())) {
							
							stackTrace = exParser.getStackTrace((Element)cc.getAny().getLastChild());
							break;
						}
					}
				}
				logFlow.error(stackTrace);
			}
			else if (eventType.endsWith(".in") ||eventType.endsWith(".out") ||eventType.endsWith(".out1") ||eventType.endsWith(".out2") ||eventType.endsWith(".out3") ||eventType.endsWith(".start") ){
				logFlow.info(payload);
			} else {
				logFlow.warn("Evento nao tratado: " + eventType);
			}

			// End of user code
			// ----------------------------------------------------------
		} catch (Exception e) {
			log.error("Não foi possível realizar o log!",e);
		} 

	}
	/***
	 * Decodes a Base64 encoded string 
	 * @param base64String
	 * @return decoded string
	 */
	private String getStringFromBase64(String base64String) {
		byte[] str = DatatypeConverter.parseBase64Binary(base64String);
		return new String(str);
	}

}
