package br.com.tsuru.iib.StatisticsGraph;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSession;
import javax.jms.ServerSessionPool;

public class MQServerSessionPool implements ServerSessionPool,
		ExceptionListener {
	private final Connection _conn;

	MQServerSessionPool(Connection conn) {
		_conn = conn;
	}

	public ServerSession getServerSession() {
		return new MQServerSession(_conn);
	}

	public void onException(JMSException ex) {
		ex.printStackTrace();
	}
}
