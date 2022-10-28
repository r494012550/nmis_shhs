package com.healta.plugin.activemq;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

public abstract class BaseJmsOrder implements Serializable {
	private static final long serialVersionUID = -2427617218391383019L;
    protected static long counter = 0;
    private String id;
    private int failureCount = 0;
    private Throwable throwable = null;  // Remember last exception happened
    private String origQueueName = null; // The original queue
    private Properties orderProperties = null;
    public static final char PROPERTY_DELIMITER = ';';
    public static final char PROPERTY_DELIMITER_ESCAPE = '\\';

    /**
     * Imposing a reasonable limit on the amount of information that is included in the 
     * context specific property collection. Note that a hard-limit of 65535 bytes exist
     * due to the internal use of {@link DataOutput#writeUTF(String)} when messages are
     * created. A character will be written using between one and three bytes depending
     * on the character. This means that a safe hard-limit value for MAX_PROPERTIES_SIZE
     * could be 21845 (65535 / 3) if desired.
     */
    private final int MAX_PROPERTIES_SIZE = 1000;
    private int propertiesSize = 0;
    
    public BaseJmsOrder()
    {
        id = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + counter++;
        orderProperties = new Properties();
    }
    
    public final int getFailureCount() {
        return failureCount;
    }

    public final void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
    	this.throwable = throwable;
    }
    
    public String toIdString()
    {
        return getClass().getName() + "@" + id + "@" + Integer.toHexString(hashCode());
    }

    protected String getOrderDetails() { return ""; };

    /**
     * Copies all context specific JMS Order properties or attributes to the supplied
     * {@code Properties} instance.
     */
    public void copyOrderPropertiesTo(Properties p) {
        if ( p != null )
        {
            p.putAll(orderProperties);
        }
    }
    
    /**
     * Allows each derived type to implement a strategy for processing context
     * specific properties or attributes that have relevant meaning to this JMS order.
     * The default behaviour provided by this base class is to process noting. 
     * @param properties generic set of (@code Object}s to be processed by a derived class. 
     */
    public void processOrderProperties(Object... properties) {};
    
    /**
     * Context specific properties or attributes that have relevant meaning to this
     * JMS order. The property is added if both the name and property supplied are
     * not null.
     * @param name the name to be placed into this property list
     * @param property the property corresponding to name
     * @return true if the entry was added, false otherwise
     */
    public boolean setOrderProperty(String name, String property) {
        if ( propertiesSize >= MAX_PROPERTIES_SIZE ) return false;
        
        final String propDelim = String.valueOf(PROPERTY_DELIMITER);
        final String propEscape = String.valueOf(PROPERTY_DELIMITER_ESCAPE);
        
        if ( name != null && property != null ) {
            // storing with delimiter surrounded values is a strategy that allows us to optimise the
            // message selector that finds an *exact* matching string within a larger string
            String delimitedProperty = propDelim + property.replace(propDelim, 
                    propEscape + propDelim) + propDelim;
            
            int size = name.length() + delimitedProperty.length();
            
            if ( propertiesSize + size >= MAX_PROPERTIES_SIZE ) {
                return false;
            }
            
            propertiesSize += size;
            
            orderProperties.setProperty(name, delimitedProperty);
            return true;
        }
        return false;
    }
    
    /**
     * Context specific properties or attributes that have relevant meaning to this
     * JMS order. The properties are added if both the name and properties supplied are
     * not null and the properties varargs is not empty. Properties are concatenated
     * using the ';' character. Any property that contains the ';' character, will be
     * modified to have that character escaped with the '\' character, so "My;Value"
     * will become "My\;Value".
     * @param name the name to be placed into this property list
     * @param property the array of properties corresponding to name
     * @return true if the entry was added, false otherwise
     */
    public boolean setOrderMultiProperty(String name, String... properties) {
        if ( propertiesSize >= MAX_PROPERTIES_SIZE ) return false;
        
        final String propDelim = String.valueOf(PROPERTY_DELIMITER);
        final String propEscape = String.valueOf(PROPERTY_DELIMITER_ESCAPE);
        
        if ( name != null && properties != null && properties.length > 0 ) {
            int combinedPropertiesSize = name.length() + 1; // 1 for initial delimiter
            
            StringBuffer sb = new StringBuffer();
            sb.append(PROPERTY_DELIMITER);
            for ( String property : properties ) {
                String delimitedProperty = property.replace(propDelim, 
                        propEscape + propDelim) + propDelim;
                
                combinedPropertiesSize += delimitedProperty.length();
                if ( propertiesSize + combinedPropertiesSize >= MAX_PROPERTIES_SIZE ) {
                    break;
                }
                
                sb.append(delimitedProperty);
            }
            
            propertiesSize += combinedPropertiesSize;
            
            // storing with delimiter surrounded values is a strategy that allows us to optimise the
            // message selector that finds an *exact* matching string within a larger string
            orderProperties.setProperty(name, sb.toString());
            return true;
        }
        return false;
    }
    
    /**
     * Set the original queue name, only the first time
     * 
     * @param queueName
     */
    public void setQueueName(String queueName) {		
        if(origQueueName == null)
            origQueueName = queueName;
    }

    public String getQueueName()
    {
        return origQueueName;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(toIdString() + "[");

        String orderDetails = getOrderDetails();
        if ( orderDetails.length() > 0) {
           sb.append(orderDetails);
        }
        sb.append(", failures=").append(failureCount);
        sb.append("]");

        return sb.toString();
    }

    public String toLongString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("\tInternal ID: ").append(toIdString()).append("\n");
        String orderDetails = getOrderDetails();
        if ( orderDetails.length() > 0) {
           sb.append("\tDetails: ").append(orderDetails).append("\n");
        }
        sb.append("\tOriginal queue name: ").append(origQueueName).append("\n");
        if ( orderProperties.size() > 0 ) {
            sb.append("\tOrder properties: ");
            for ( Enumeration<?> names = orderProperties.propertyNames(); names.hasMoreElements(); ) {
                String key = (String) names.nextElement();
                String property = orderProperties.getProperty(key);
                property = property.substring(1, property.length() - 1); // strip outer delimiters
                sb.append(key).append("=").append(property).append(", ");
            }
            sb.setCharAt(sb.length() - 2, '\n');
        }
        sb.append("\tFailure count: ").append(failureCount).append("\n");
        if(throwable != null)
        {
            StringWriter sw = new StringWriter(); 
            throwable.printStackTrace( new PrintWriter( sw ) ); 
            sb.append("\tException caught: ").append(sw.toString()).append("\n");
        }
        return sb.toString();
    }

    /*
     * Provide for deserialization of objects persisted against an earlier class definition.
     */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		if ( orderProperties == null ) {
			orderProperties = new Properties();
		}
	}
}
