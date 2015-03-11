package br.com.tsuru.iib.admin.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ibm.broker.config.proxy.BrokerProxy;
import com.ibm.broker.config.proxy.ConfigManagerProxyLoggedException;
import com.ibm.broker.config.proxy.ConfigManagerProxyPropertyNotInitializedException;
import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.javastartparameters.JavaStartParameters;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class PasswordsListJCN extends MbJavaComputeNode {

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

		try {
						
			// ----------------------------------------------------------
			// Add user code below
			
			MbElement data = outMessage.getRootElement().createElementAsLastChild(MbJSON.ROOT_ELEMENT_NAME).createElementAsLastChild(MbElement.TYPE_NAME, "Data", null);
			
			List<String> aliases = getPasswordAliases();
			for (String alias : aliases) {
				
				alias = alias.replace("@FWSL@", "/");
				
				String type = "";
				String name = alias;
				if(alias.contains("::")) {
					String[] aux = alias.split("::");
					type = aux[0] + "::";
					name = aux[1];
				}
				System.out.println("type: " + type + ",name:" + name);
				String[] credential = JavaStartParameters.getResourceUserAndPassword(type, "", name);
				
				MbElement pwd = data.createElementAsLastChild(MbElement.TYPE_NAME,"pwd",null);
				pwd.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"alias",alias);
				if(credential != null) {
					pwd.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"user",credential[0]);
					pwd.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"password",credential[1]);
				}
			}
			// The following should only be changed
			// if not propagating message to the 'out' terminal
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

	private List<String> getPasswordAliases() {
		
		List<String> list = new ArrayList<String>();
		String brokerName = "";
		try {
			brokerName = BrokerProxy.getLocalInstance().getName();
		} catch (ConfigManagerProxyPropertyNotInitializedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigManagerProxyLoggedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String workpath = (String) System.getenv("MQSI_WORKPATH");
		String path = workpath + "/registry/" + brokerName + "/CurrentVersion/DSN";
		System.out.println("Path for aliases: " + path);
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
		    if (file.isDirectory()) {
		        list.add(file.getName());
		    }
		}
		return list;
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
