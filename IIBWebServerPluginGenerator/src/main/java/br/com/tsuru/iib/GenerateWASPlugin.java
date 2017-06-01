package br.com.tsuru.iib;

import iapi.common.ResourcesHandler;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.broker.config.proxy.BrokerConnectionParameters;
import com.ibm.broker.config.proxy.BrokerProxy;
import com.ibm.broker.config.proxy.ConfigManagerProxyLoggedException;
import com.ibm.broker.config.proxy.IntegrationNodeConnectionParameters;

/**
 * @author tsuru
 * @see https
 *      ://www.ibm.com/support/knowledgecenter/SSMKHH_10.0.0/com.ibm.etools.
 *      mft.doc/be55230_.htm
 * @see https
 *      ://www.ibm.com/support/knowledgecenter/pt-br/SSMKHH_10.0.0/com.ibm.etools
 *      .mft.doc/ae33102_.htm
 */
public class GenerateWASPlugin {
	/**
	 * The default name of the integration node if it is not supplied
	 */
	private final static String DEFAULT_BROKER_NAME = "TESTNODE_"
			+ System.getProperty("user.name");

	/**
	 * The default host name of the integration node if it is not supplied
	 */
	private final static String DEFAULT_BROKER_HOSTNAME = "localhost";

	/**
	 * The default port of the integration node if it is not supplied
	 */
	private final static int DEFAULT_BROKER_PORT = 4414;

	private final static int DEFAULT_TIMEOUT = 120 * 1000;

	private static Logger log = LogManager.getLogger(GenerateWASPlugin.class);


	/**
	 * Connects, using the parameters to the web administration port on which an
	 * integration node is listening on the named host.
	 * 
	 * @param brokerHost
	 *            The host name of the integration node May be null, in which
	 *            case the default parameters will be used.
	 * @param brokerPort
	 *            The web administration port number of the integration node
	 * @return BrokerProxy connected instance. If the connection could not be
	 *         established, null is returned.
	 */
	public BrokerProxy connect(String brokerHost, int brokerPort) {
		BrokerProxy b = null;

		String brokerHostName = brokerHost;
		int brokerPortNumber = brokerPort;
		if (brokerHostName == null) {
			brokerHostName = DEFAULT_BROKER_HOSTNAME;
		}
		if (brokerPortNumber == 0) {
			brokerPortNumber = DEFAULT_BROKER_PORT;
		}

		BrokerConnectionParameters bcp = new IntegrationNodeConnectionParameters(
				brokerHostName, brokerPortNumber);

		try {
			log.info(ResourcesHandler
					.getNLSResource(ResourcesHandler.CONNECTING));
			b = BrokerProxy.getInstance(bcp);
			log.info(ResourcesHandler
					.getNLSResource(ResourcesHandler.CONNECTED_TO_BROKER));

			// Ensure the integration node is actually talking to us.
			// (This step isn't necessary - although it does allow us to
			// catch comms failures early.)
			boolean brokerIsResponding = b.hasBeenPopulatedByBroker(true);

			if (brokerIsResponding) {
				log.info(ResourcesHandler
						.getNLSResource(ResourcesHandler.CONNECTED_TO_BROKER));
			} else {
				log.info(ResourcesHandler
						.getNLSResource(ResourcesHandler.NO_RESPONSE_FROM_BROKER));
				b.disconnect();
				b = null;
			}

		} catch (ConfigManagerProxyLoggedException e) {
			log.error(ResourcesHandler
					.getNLSResource(ResourcesHandler.CONNECT_FAILED), e);
		}
		return b;
	}

	/**
	 * Connects to a local integration node
	 * 
	 * @param localBrokerName
	 *            The host name of the integration node May be null, in which
	 *            case the default parameters will be used.
	 * @return BrokerProxy connected instance. If the connection could not be
	 *         established, null is returned.
	 */
	public BrokerProxy connect(String localBrokerName) {
		BrokerProxy b = null;

		String brokerName = localBrokerName;
		if (brokerName == null) {
			brokerName = DEFAULT_BROKER_NAME;
		}

		try {
			log.info(ResourcesHandler
					.getNLSResource(ResourcesHandler.CONNECTING));
			b = BrokerProxy.getLocalInstance(brokerName);
			log.info(ResourcesHandler
					.getNLSResource(ResourcesHandler.CONNECTED_TO_BROKER));

			// Ensure the integration node is actually talking to us.
			// (This step isn't necessary - although it does allow us to
			// catch comms failures early.)
			boolean brokerIsResponding = b.hasBeenPopulatedByBroker(true);

			if (brokerIsResponding) {
				log.info(ResourcesHandler
						.getNLSResource(ResourcesHandler.CONNECTED_TO_BROKER));
			} else {
				log.info(ResourcesHandler
						.getNLSResource(ResourcesHandler.NO_RESPONSE_FROM_BROKER));
				b.disconnect();
				b = null;
			}

		} catch (ConfigManagerProxyLoggedException e) {
			log.error(ResourcesHandler
					.getNLSResource(ResourcesHandler.CONNECT_FAILED),e);
		}
		return b;
	}
}
