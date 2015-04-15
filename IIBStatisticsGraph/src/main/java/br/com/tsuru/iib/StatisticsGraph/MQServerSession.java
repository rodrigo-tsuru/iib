package br.com.tsuru.iib.StatisticsGraph;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.ServerSession;
import javax.jms.Session;

public class MQServerSession implements ServerSession
{
    private final Connection _conn;
    private       Session    _topicSession;
                                                                           
    MQServerSession(Connection conn)
    {
       _conn = conn;
    }
                                                                           
    // get or create the session for this server session
    // when creating a session a message listener is set
    public synchronized Session getSession() throws JMSException
    {
       if (_topicSession == null) {
          _topicSession = _conn.createSession(false,
              Session.AUTO_ACKNOWLEDGE);
          _topicSession.setMessageListener(new StatisticsTopicListener());
       }
                                                                          
       return _topicSession;
    }
                                                                           
    public void start() throws JMSException
    {
       Thread t = new Thread(_topicSession);
       t.start();
    }
                                                                           
    static int _msgCount = 0;
}