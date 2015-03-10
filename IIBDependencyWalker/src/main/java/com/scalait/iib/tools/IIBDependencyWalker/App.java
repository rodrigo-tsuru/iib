package com.scalait.iib.tools.IIBDependencyWalker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMILoadImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLHelperImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Hello world!
 * 
 */
public class App {
	
	private static Logger log = LogManager.getLogger(App.class);
	private HashSet<String> ODBC = new HashSet<String>();
	private HashSet<String> JDBC = new HashSet<String>();
	
	public static void main(String[] args) {
		// System.out.println("Select the root folder for source files: ");
		// Scanner scan = new Scanner(System.in);
		// String root = scan.nextLine();
		App app = new App();
		File dir = new File("/home/tsuru/GIT/amil/esb");
		String[] SUFFIX = { "msgflow" };
		Collection<File> files = FileUtils.listFiles(dir, SUFFIX, true);
//		for (File file : files) {
//			log.debug("found: " + file.getAbsolutePath());
//			app.getDependentResources(file);
//		}
		
		SUFFIX[0] = "java";
		files = FileUtils.listFiles(dir, SUFFIX, true);
		for (File file : files) {
			log.debug("found: " + file.getAbsolutePath());
			app.getDependentResourcesJava(file);
		}
		app.printResult();
		// }
		// Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		// Map<String, Object> m = reg.getExtensionToFactoryMap();
		// m.put("msgflow", new XMIResourceFactoryImpl());
		//
		// EPackage.Registry.INSTANCE
		// .put("http://www.ibm.com/wbi/2005/eflow", EflowPackage.eINSTANCE);

		// // Obtain a new resource set
		// ResourceSet resSet = new ResourceSetImpl();
		//
		// EflowFactory factory = EflowFactoryImpl.eINSTANCE;

		//
		//
		// // Get the resource
		// Resource resource = resSet.getResource(
		// URI.createURI("/home/tsuru/IBM/dependency/asdf/asdf.msgflow"),
		// true);
		// // Get the first model element and cast it to the right type, in my
		// // example everything is hierarchical included in this first node
		// // MyWeb myWeb = (MyWeb) resource.getContents().get(0);
		// System.out.println(resource.getContents().get(0).getClass());

		// MFTResourceFactoryImpl factory = new MFTResourceFactoryImpl();
		// Resource resource =
		// factory.createResource(URI.createURI("/home/tsuru/IBM/dependency/asdf/asdf.msgflow"));
		// System.out.println(resource.getContents());

		// XMLHelper helper = new XMLHelperImpl();
		// XMIResource resource = new
		// MFTResourceImpl(URI.createURI("/home/tsuru/IBM/dependency/asdf/asdf.msgflow"));
		//
		// try {
		// resource.load(Collections.EMPTY_MAP);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println(resource.getContents());

		// try {
		// loader.load(resource, new
		// FileInputStream("/home/tsuru/IBM/dependency/asdf/asdf.msgflow"),
		// Collections.EMPTY_MAP);
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	private void getDependentResources(File file) {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		FileInputStream fis = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			fis = new FileInputStream(file);
			Document doc = builder.parse(fis);
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			xpath.setNamespaceContext(new DefaultNamespaceContext());
			XPathExpression expr = xpath.compile("/ecore:EPackage/eClassifiers/composition/nodes");
			NodeList result = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for(int i=0; i < result.getLength(); i++) {
				Node node = result.item(i);
				String nodeType = node.getAttributes().getNamedItemNS("http://www.omg.org/XMI", "type").getNodeValue().split(":")[0];
				if(nodeType.equals("ComIbmCompute.msgnode")) {
					Node dsn = node.getAttributes().getNamedItem("dataSource");
					if(dsn!=null) {
						ODBC.add(dsn.getNodeValue());
					}
				} else if(nodeType.equals("ComIbmJavaCompute.msgnode")) {
					Node javaClass = node.getAttributes().getNamedItem("javaClass");
					if(javaClass!=null) {
						ODBC.add(javaClass.getNodeValue());
					}
				}
			}
			expr = xpath.compile("/ecore:EPackage/eClassifiers/propertyOrganizer/propertyDescriptor");
			result = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for(int i=0; i < result.getLength(); i++) {
				String attr = result.item(i).getAttributes().getNamedItem("describedAttribute").getNodeValue();
				System.out.println(attr);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(fis!=null)
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}
	
	public void printResult() {
		System.out.println("ODBC data sources: ");
		for (Object dsn : ODBC.toArray()) {
			System.out.println(dsn);
		}
		System.out.println("JDBC data sources: ");
		for (Object dsn : JDBC.toArray()) {
			System.out.println(dsn);
		}
	}

	private void getDependentResourcesJava(File file) {
		
		final String jdbcMethod = "getJDBCType4Connection";
		try {
			String content = FileUtils.readFileToString(file);
			int idx = content.indexOf(jdbcMethod);
			if(idx > 0) {
				idx = idx + jdbcMethod.length() + 1;
				int idx2 = content.indexOf(")",idx);
				String dsn = content.substring(idx, idx2).split(",")[0].replace("\"", "");
				JDBC.add(dsn);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Resource loadFromXML(String xml, ResourceSet rsSet)
			throws IOException {
		final Resource res;
		res = new XMIResourceImpl();
		final XMLHelper xmlHelper = new XMLHelperImpl();
		FileInputStream stringreader = new FileInputStream(xml);
		XMILoadImpl xmiload = new XMILoadImpl(xmlHelper);
		xmiload.load((XMLResource) res, stringreader, Collections.EMPTY_MAP);
		if (res.getURI() == null) {
			res.setURI(URI.createURI(""));
		}
		return res;
	}
}
