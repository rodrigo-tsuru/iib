package com.scalait.iib.tools.IIBDependencyWalker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

public class DefaultNamespaceContext implements NamespaceContext {

	private static final Map<String, String> PREF_MAP = new HashMap<String, String>();
	
	static {
		PREF_MAP.put("ecore", "http://www.eclipse.org/emf/2002/Ecore");
		PREF_MAP.put("xmi", "http://www.omg.org/XMI");
	}
	
	public String getNamespaceURI(String prefix) {
		return PREF_MAP.get(prefix);
	}

	public String getPrefix(String arg0) {
		throw new UnsupportedOperationException();
	}

	public Iterator getPrefixes(String arg0) {
		throw new UnsupportedOperationException();
	}

}
