package com.healta.plugin.dcm;

import org.dcm4che3.data.Tag;

public class Tags extends Tag {

	/** (0008,1150) VR=UI Referenced SOP Class UID */
    public static final int RefSOPClassUID = 0x00081150;

    /** (0008,1155) VR=UI Referenced SOP Instance UID */
    public static final int RefSOPInstanceUID = 0x00081155;
    
    /** (0040,0001) VR=AE Scheduled Station AE Title */
    public static final int ScheduledStationAET = 0x00400001;
    
    /** (0040,0002) VR=DA Scheduled Procedure Step Start Date */
    public static final int SPSStartDate = 0x00400002;

    /** (0040,0003) VR=TM Scheduled Procedure Step Start Time */
    public static final int SPSStartTime = 0x00400003;

    /** (0040,0009) VR=SH Scheduled Procedure Step ID */
    public static final int SPSID = 0x00400009;

    /** (0040,0020) VR=CS Scheduled Procedure Step Status */
    public static final int SPSStatus = 0x00400020;

    /** (0040,0100) VR=SQ Scheduled Procedure Step Sequence */
    public static final int SPSSeq = 0x00400100;
    
}
