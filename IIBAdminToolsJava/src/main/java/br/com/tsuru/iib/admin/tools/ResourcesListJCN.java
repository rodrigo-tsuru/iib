package br.com.tsuru.iib.admin.tools;

import java.util.Properties;

import com.ibm.broker.config.proxy.BrokerProxy;
import com.ibm.broker.config.proxy.ConfigurableService;
import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class ResourcesListJCN extends MbJavaComputeNode {
	
	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		//MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			//MbElement query = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("HTTP/Input/QueryString");
			MbMessage outMessage = new MbMessage();
			outMessage.getRootElement().addAsFirstChild(
					inMessage.getRootElement().getFirstChild().copy()); // Copy
																		// Properties
																		// folder
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			
			// create new message as a copy of the input
			
			MbElement outArray = outMessage
					.getRootElement()
					.createElementAsLastChild(MbJSON.ROOT_ELEMENT_NAME)
					.createElementAsLastChild(MbJSON.ARRAY, MbJSON.DATA_ELEMENT_NAME, null);
			
			// ----------------------------------------------------------
			// Add user code below

			BrokerProxy b = BrokerProxy.getLocalInstance();
			while (!b.hasBeenPopulatedByBroker()) {
				Thread.sleep(100);
			}
			for (String type : b.getConfigurableServiceTypes()) {
				ConfigurableService resources[] = b.getConfigurableServices(type);
				MbElement typeOut = outArray.createElementAsLastChild(MbElement.TYPE_NAME, MbJSON.ARRAY_ITEM_NAME, null);
				typeOut.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "type", type);
				MbElement resArray = typeOut.createElementAsLastChild(	MbJSON.ARRAY, "resource", null);
				for (ConfigurableService cs : resources) {
					MbElement dsOut = resArray.createElementAsLastChild(MbElement.TYPE_NAME, MbJSON.ARRAY_ITEM_NAME, null);
					dsOut.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"name", cs.getName());
					Properties props = cs.getProperties();
					for (Object key : props.keySet()) {
						dsOut.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, key.toString(),props.get(key));
					}
				}				
			}

			out.propagate(outAssembly,true);

			// End of user code
			// ----------------------------------------------------------
		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			throw e;
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be
			// handled in the flow
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		}

	}

}
