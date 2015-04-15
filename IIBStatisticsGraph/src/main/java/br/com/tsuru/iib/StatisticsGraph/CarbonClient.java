package br.com.tsuru.iib.StatisticsGraph;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/***
 * 
 * @author Rodrigo Tsuru
 *
 */
public class CarbonClient {
	private static Logger log = LogManager.getLogger();
	private static String CARBON_CACHE_HOST = "localhost";
	private static int CARBON_CACHE_PORT = 2003;
	
	public static void sendMetrics(String metric, Double value, long timeStampInMillis) {
		try (Socket socket = new Socket(CARBON_CACHE_HOST, CARBON_CACHE_PORT);
			OutputStream s = socket.getOutputStream();) {

			PrintWriter out = new PrintWriter(s, true);

			out.printf("%s %f %d%n", metric, value, timeStampInMillis / 1000l);

		} catch (UnknownHostException e) {
			log.error("Cannot connect to Carbon Cache! Please check the host", e);
		} catch (IOException e) {
			log.error("Cannot connect to Carbon Cache! Please check your configurations", e);
		}
	}

}
