package br.com.tsuru.iib.common.utils;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.broker.plugin.MbConfigurationException;
import com.ibm.broker.plugin.MbConversionException;
import com.ibm.broker.plugin.MbDatabaseException;
import com.ibm.broker.plugin.MbDate;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbFatalException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbParserException;
import com.ibm.broker.plugin.MbRecoverableException;
import com.ibm.broker.plugin.MbSecurityException;
import com.ibm.broker.plugin.MbTime;

public class ExceptionListParser {
	
	private static Logger log = LogManager.getLogger(ExceptionListParser.class);
 
	public String getStackTrace(MbMessage exceptionList) throws MbException {
        MbElement currException = exceptionList.getRootElement().getFirstChild();
        StringBuffer sb = new StringBuffer();
        while (currException != null && currException.getName().endsWith("Exception")) {
                MbException currMBE = convert2MbException(currException);
                sb.append(currMBE.getMessage());
                sb.append("\n");
                // Processa o próximo nó de exceção
                currException = currException.getLastChild();
        }
        return sb.toString();
}
	/***
	 * Converts an Exception Node from a ExceptionList tree to a Java Exception
	 * @param exceptionNode
	 * @return exception object
	 * @throws MbException
	 */
	private MbException convert2MbException(MbElement exceptionNode) {
		MbException eRet = null;
		String name;
		try {
			name = exceptionNode.getName();

			String source, methodName, messageSource, messageKey, traceText;
			source 			= exceptionNode.getFirstElementByPath("File").getValueAsString();
			methodName 		= exceptionNode.getFirstElementByPath("Function").getValueAsString();
			messageSource 	= exceptionNode.getFirstElementByPath("Catalog").getValueAsString();
			messageKey 		= exceptionNode.getFirstElementByPath("Number").getValueAsString();
			traceText 		= exceptionNode.getFirstElementByPath("Text").getValueAsString();
			Object[] inserts = getInserts(exceptionNode);
			if(name.equals("RecoverableException")) {
				eRet = new MbRecoverableException(source, methodName, messageSource, messageKey, traceText, inserts);
			} else if(name.equals("UserException")) {
				eRet = new MbRecoverableException(source, methodName, messageSource, messageKey, traceText, inserts);
			} else if(name.equals("DatabaseException")) {
				eRet = new MbDatabaseException(source, methodName, messageSource, messageKey, traceText, inserts);			
			} else if(name.equals("ConversionException")) {
				eRet = new MbConversionException(source, methodName, messageSource, messageKey, traceText, inserts);				
			} else if(name.equals("ParserException")) {
				eRet = new MbParserException(source, methodName, messageKey, messageSource, traceText, inserts);						
			} else if(name.equals("CastException")) {
				eRet = new MbConversionException(source, methodName, messageSource, messageKey, traceText, inserts);			
			} else if(name.equals("MessageException")) {
				// MbReadOnlyMessageException?
				eRet = new MbRecoverableException(source, methodName, messageSource, messageKey, traceText, inserts);			
			} else if(name.equals("SqlException")) {
				eRet = new MbDatabaseException(source, methodName, messageSource, messageKey, traceText, inserts);		
			} else if(name.equals("SocketException")) {
				eRet = new MbRecoverableException(source, methodName, messageSource, messageKey, traceText, inserts);				
			} else if(name.equals("SocketTimeoutException")) {
				eRet = new MbRecoverableException(source, methodName, messageSource, messageKey, traceText, inserts);				
			} else if(name.equals("UnknownException")) {
				eRet = new MbRecoverableException(source, methodName, messageSource, messageKey, traceText, inserts);				
			} else if(name.equals("FatalException")) {
				eRet = new MbFatalException(source, methodName, messageSource, messageKey, traceText, inserts);				
			} else if(name.equals("ConfigurationException")) {
				eRet = new MbConfigurationException(source, methodName, messageSource, messageKey, traceText, inserts);				
			} else if(name.equals("SecurityException")) {
				eRet = new MbSecurityException(source, methodName, messageSource, messageKey, traceText, inserts);				
			} else {
				throw new AssertionError("see http://publib.boulder.ibm.com/infocenter/wmbhelp/v8r0m0/topic/com.ibm.etools.mft.doc/ac00490_.htm");
			}
		} catch (MbException e) {
			log.error("Cannot convert exception node to a java exception object!",e);
		}
		return eRet;
	}

	/***
	 * Transforms Inserts nodes to java objects array
	 * @param exceptionNode
	 * @return
	 * @throws MbException
	 */
	private Object[] getInserts(MbElement exceptionNode) throws MbException {
		Object[] oRet = null;
		MbElement[] inserts = (MbElement[]) exceptionNode.evaluateXPath("Insert");
		if(inserts != null && inserts.length > 0) {
			oRet = new Object[inserts.length];
			int index = 0;
			for (MbElement insert : inserts) {
				int type = Integer.parseInt(insert.getFirstElementByPath("Type").getValueAsString());
				String value = insert.getFirstElementByPath("Text").getValueAsString();
				switch (type) {
//				The data type of the value:
//					0 = Unknown 
//					1 = Boolean 
//					2 = Integer
//					3 = Float
//					4 = Decimal 
//					5 = Character
//					6 = Time
//					7 = GMT Time
//					8 = Date
//					9 = Timestamp
//					10 = GMT Timestamp 
//					11 = Interval
//					12 = BLOB
//					13 = Bit Array
//					14 = Pointer
				case 0:
					oRet[index] = value;
					break;
				case 1:
					oRet[index] = Boolean.valueOf(value);
					break;
				case 2:
					oRet[index] = Integer.valueOf(value);
					break;
				case 3:
					oRet[index] = Float.valueOf(value);
					break;
				case 4:
					oRet[index] = new BigDecimal(value);
					break;
				case 5:
					oRet[index] = value;
					break;
				case 6:
					oRet[index] = new MbTime(Integer.parseInt(value.substring(0, 2)), Integer.parseInt(value.substring(3, 5)), Integer.parseInt(value.substring(6, 8)));
					break;
				case 7:
					oRet[index] = new MbTime(Integer.parseInt(value.substring(0, 2)), Integer.parseInt(value.substring(3, 5)), Integer.parseInt(value.substring(6, 8)));					
					break;
				case 8:
					oRet[index] = new MbDate(Integer.parseInt(value.substring(0, 4)), Integer.parseInt(value.substring(5, 7)), Integer.parseInt(value.substring(8, 10)));
					break;
				case 9:
					oRet[index] = value;
					//oRet[index] = new MbTimestamp(arg0, arg1, arg2, arg3, arg4, arg5);
					break;
				case 10:
					oRet[index] = value;
					//oRet[index] = new MbTimestamp(arg0, arg1, arg2, arg3, arg4, arg5);
					break;
				case 11:
					oRet[index] = value;
					break;
				case 12:
					oRet[index] = new String(hexStringToByteArray(value));
					break;
				case 13:
					oRet[index] = value;
					break;
				case 14:
					oRet[index] = value;
					break;

				default:
					oRet[index] = value;
					log.error("type " + type + " not supported!");
					break;
				}
				index++;
			}
		} else {
			// put just node label in the array 
			oRet = new Object[1];
			oRet[0] = exceptionNode.getFirstElementByPath("Label").getValueAsString();
		}
		return oRet;
	}
	private static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
}
