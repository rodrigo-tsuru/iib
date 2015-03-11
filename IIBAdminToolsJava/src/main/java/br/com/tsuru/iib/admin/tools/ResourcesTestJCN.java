package br.com.tsuru.iib.admin.tools;

import java.sql.Connection;

import com.ibm.broker.config.proxy.BrokerProxy;
import com.ibm.broker.config.proxy.ConfigurableService;
import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbRecoverableException;

public class ResourcesTestJCN extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();

		// create new empty message
		MbMessage outMessage = new MbMessage();
		outMessage.getRootElement().addAsFirstChild(
				inMessage.getRootElement().getFirstChild().copy()); // Copy
																	// Properties
																	// folder
		MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly,
				outMessage);
		MbElement query = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("HTTP/Input/QueryString");
		MbElement data = outMessage.getRootElement().createElementAsLastChild(MbJSON.ROOT_ELEMENT_NAME).createElementAsLastChild(MbElement.TYPE_NAME, "Data", null);
		try {
			String type = query.getFirstElementByPath("type").getValueAsString();
			String name = query.getFirstElementByPath("name").getValueAsString();
			if(type==null || type.length() == 0) {
				throw new IllegalArgumentException("resource type parameter (type) not found!");
			}
			if(name==null || name.length() == 0 ) {
				throw new IllegalArgumentException("resource name parameter (name) not found!");
			}
			BrokerProxy b = BrokerProxy.getLocalInstance();
			ConfigurableService cs = b.getConfigurableService(type, name);
			
			if(cs == null) {
				throw new IllegalArgumentException("resource not found!");
			}
			
			
			if(type.equals("JDBCProviders")) {
				try {
					Connection conn = getJDBCType4Connection(name, JDBC_TransactionType.MB_TRANSACTION_AUTO);
					data.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"rc",0);
					data.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"msg","Success!");
				} catch (MbRecoverableException ex) {
					data.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"rc",ex.getMessageKey());
					data.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"msg",ex.getMessage());
				}
			} else if(type.equals("FTPServer")) {
				//outAssembly.getLocalEnvironment().getRootElement().createElementAsLastChild(MbElement.TYPE_NAME,"Destination",null).createElementAsLastChild(MbElement.TYPE_NAME,"File",null).createElementAsLastChild(MbElement.TYPE_NAME,"Remote",null).createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"Server",name);
				try {
					
					data.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"rc",0);
					data.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"msg","Success!");
				} catch(MbException mbe) {
					data.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"rc",mbe.getMessageKey());
					data.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"msg",mbe.getMessage());
				}
			} else {
				throw new UnsupportedOperationException("Testing of resource type " + type + " not supported!");
			}
			
		} catch (Exception e) {
			data.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"rc",999);
			data.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"msg",e.getMessage());
		}
		out.propagate(outAssembly,true);
	}

	public void copyMessageHeaders(MbMessage inMessage, MbMessage outMessage)
			throws MbException {
		MbElement outRoot = outMessage.getRootElement();

		// iterate though the headers starting with the first child of the root
		// element
		MbElement header = inMessage.getRootElement().getFirstChild();
		while (header != null && header.getNextSibling() != null) // stop before
																	// the last
																	// child
																	// (body)
		{
			// copy the header and add it to the out message
			outRoot.addAsLastChild(header.copy());
			// move along to next header
			header = header.getNextSibling();
		}
	}

}
