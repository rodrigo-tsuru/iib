package br.com.tsuru.iib;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.broker.config.proxy.BrokerProxy;
import com.ibm.broker.config.proxy.ConfigManagerProxyLoggedException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class GenerateWASPluginTest 
    extends TestCase
{
	
	private static Logger log = LogManager.getLogger(GenerateWASPluginTest.class);
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public GenerateWASPluginTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( GenerateWASPluginTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
		GenerateWASPlugin app = new GenerateWASPlugin();
		BrokerProxy bp = app.connect("192.168.1.26", 4414);

		if (bp != null) {

			String[] memberhostNames = { "esbsrv01","esbsrv02" };

			Map<BrokerProxy, String[]> brokerHostMap = new HashMap<BrokerProxy, String[]>();

			brokerHostMap.put(bp, memberhostNames);

			try {
				String pluginCfgXml = BrokerProxy.generateWASPlugin(
						brokerHostMap, 120*1000);

				log.info(pluginCfgXml);
			} catch (ConfigManagerProxyLoggedException e) {
				log.error("Plugin generation error!",e);
			}
			
			bp.disconnect();
		}
        
    }
}
