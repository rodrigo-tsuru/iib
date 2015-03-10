package br.com.tsuru.iib.admin.tools;

import java.sql.Connection;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbRecoverableException;
import com.ibm.broker.plugin.MbUserException;

public class JDBCTester_Test extends MbJavaComputeNode {

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
		try {
			String datasource = query.getFirstElementByPath("datasource").getValueAsString();
			MbElement data = outMessage.getRootElement().createElementAsLastChild(MbJSON.ROOT_ELEMENT_NAME).createElementAsLastChild(MbElement.TYPE_NAME, "Data", null);
			// ----------------------------------------------------------
			// Add user code below
			try {
				Connection conn = getJDBCType4Connection(datasource, JDBC_TransactionType.MB_TRANSACTION_AUTO);
				data.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"rc",0);
				data.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"msg","Success!");
			} catch (MbRecoverableException ex) {
				data.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"rc",ex.getMessageKey());
				data.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"msg",ex.getMessage());
			}

			out.propagate(outAssembly);
			
			// End of user code
			// ----------------------------------------------------------
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		}
		
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
