/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at http://sourceforge.net/projects/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * TIANI Medgraph AG.
 * Portions created by the Initial Developer are Copyright (C) 2002-2005
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * Gunter Zeilinger <gunter.zeilinger@tiani.com>
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package com.healta.server.dicom;

import java.util.*;

import org.dcm4che.data.*;

import org.apache.log4j.*;


/**
 * Implements the StorageServiceEvent.
 * 
 * @author Thomas Hacklaender
 * @version 2006-06-21
 */
public class StorageServiceEvent extends java.util.EventObject {
    
    /** Initialize logger */
    private static Logger log = Logger.getLogger(StorageServiceEvent.class);

    /** The Dataset send as content of the event. */
    private Dataset dataset = null;
 
    
    /**
     * Creates a new instance of StorageServiceEvent.
     * 
     * @param source the object which fires the event.
     */
    public StorageServiceEvent(Object source) {
        // pass the source object to the supercalss
        super(source);
    }

    
    /**
     * Creates a new instance of StorageServiceEvent.
     * 
     * @param source the object which fires the event.
     * @param ds the Dataset as content of the event.
     */
    public StorageServiceEvent(Object source, Dataset ds) {
        // pass the source object to the supercalss
        super(source);
        setSelectedFiles(ds);
    }
    
    
    /**
     * Sets the content of the event.
     * @param ds the Dataset as content of the event.
     */
    public void setSelectedFiles(Dataset ds) {
         dataset = ds;
   }
    
    
    /**
     * Gets the content of the event.
     * @return the content as a Dataset.
     */
    public Dataset getDataset() {
        return dataset;
    }
 
}
