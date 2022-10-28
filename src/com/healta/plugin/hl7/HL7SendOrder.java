package com.healta.plugin.hl7;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.healta.plugin.activemq.BaseJmsOrder;

public class HL7SendOrder extends BaseJmsOrder implements Serializable {
	private static final long serialVersionUID = 3257003259104147767L;

	private final byte[] hl7msg;

	private final String receiving;
	
	private final String id;
	
	private final String value;
	
	private final String logid;

	public HL7SendOrder(byte[] hl7msg, String receiving) {
		if (hl7msg == null)
			throw new NullPointerException();
		if (receiving == null)
			throw new NullPointerException();
		this.hl7msg = hl7msg;
		this.receiving = receiving;
		this.id=UUID.randomUUID().toString();
		
		this.value=null;
		this.logid=null;
	}
	
	public HL7SendOrder(byte[] hl7msg, String receiving,String value,String logid) {
            if (hl7msg == null)
                    throw new NullPointerException();
            if (receiving == null)
                    throw new NullPointerException();
            this.hl7msg = hl7msg;
            this.receiving = receiving;
            
            this.id=UUID.randomUUID().toString();
            this.value=value;
            this.logid=logid;
    }

	

    public String getLogid() {
        return logid;
    }

    public String getValue() {
        return value;
    }

    public String getId() {
        return id;
    }

    public final byte[] getHL7Message() {
        return hl7msg;
    }

	public final String getReceiving() {
		return receiving;
	}

    public String getOrderDetails() {
        return "receiving=" + receiving;
    }
    
    /**
     * Processes order attributes based on the HL7 message set in the {@code ctor}.
     * @see BaseJmsOrder#processOrderProperties(Object...)
     */
    @Override
    public void processOrderProperties(Object... properties) {
//        try {
//            List<?> segments = HL7Factory.getInstance().parse(hl7msg).segments();
//            for ( int i = 0; i < segments.size(); i++ ) {
//                HL7Segment seg = (HL7Segment) segments.get(i);
//                if ( seg.id().equals("PID") ) {
//                    // the sequence we want from the PID segment looks like "1002^^^THE_ISSUER"
//                    String patientID = seg.get(HL7.PIDPatientIDInternalID);
//                    String id, issuer = null;
//                    if ( patientID.contains("^") ) {
//                        id = patientID.substring(0, patientID.indexOf('^'));
//                        issuer = patientID.substring(patientID.lastIndexOf('^') + 1, patientID.length());
//                    }
//                    else {
//                        id = patientID;
//                    }
//                    this.setOrderProperty(JmsOrderProperties.ISSUER_OF_PATIENT_ID, issuer);
//                    this.setOrderProperty(JmsOrderProperties.PATIENT_ID, id);
//                    break;
//                }
//            }
//            
//            if ( properties != null && properties.length == 1 && properties[0] instanceof MSH ) {
//                MSH msh = (MSH) properties[0];
//                this.setOrderProperty(JmsOrderProperties.MESSAGE_TYPE, msh.messageType);
//                this.setOrderProperty(JmsOrderProperties.TRIGGER_EVENT, msh.triggerEvent);
//            }
//        }
//        catch (Hl7Exception e) {
//            throw new RuntimeException("Failed to parse PID segment of message", e);
//        }
    }
}
