#!/bin/bash
# Description: A script to disable SSL v3 as documented at http://www-01.ibm.com/support/docview.wss?uid=swg21687678
BROKERS=$(mqsilist | grep manager | awk '{print $3}' | sed "s/'//g")
for brk in $BROKERS ; do
	mqsichangeproperties $brk -b httplistener -o HTTPSConnector -n sslProtocol -v TLS
	echo "$brk Broker-wide HTTP Input Listener will use TLS only"
	mqsichangeproperties $brk -b httplistener -o HTTPSConnector -n sslProtocol -v TLS
	echo "$brk Broker-wide SOAP Input Listener will use TLS only"
	mqsichangeproperties $brk -b webadmin -o HTTPSConnector -n sslProtocol -v TLS
	echo "$brk Web Admin will use TLS only"
	EGS=$(mqsilist $brk | grep BIP1286I | awk '{print $4}' | sed "s/'//g")	
	for eg in $EGS ; do
		# SOAP Input Node / HTTP Input Node (eg-wide)	
		echo "SOAP Input Nodes and HTTP Input Nodes from Integration Server $eg ,node $brk will use TLS only"
		mqsichangeproperties $brk -e $eg -o HTTPSConnector -n sslProtocol -v TLS
	done
	TCPIPSERVICES=$(mqsireportproperties $brk -c TCPIPServer -o AllReportableEntityNames -a | grep ^\ \ | sed "s/^ *//g")
	for tcp in $TCPIPSERVICES ; do
		echo "TCP/IP Server Node with configurable service $tcp will use TLS only"
		mqsichangeproperties $brk -c TCPIPServer -o $tcp -n SSLProtocol -v TLS 		
	done
done
