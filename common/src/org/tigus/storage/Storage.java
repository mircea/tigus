package org.tigus.storage;

import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.w3c.dom.Node;

/**
 * @author Mircea Bardac
 * 
 *         Abstract class defining basic methods for handling a results data
 *         store
 */
public abstract class Storage extends Observable {

    public static final int STATUS_COMPLETED = 0;
    public static final int STATUS_PENDING = 1;
    public static final int STATUS_FAILED = 2;

    public Map<String, String> mapEntryIdStudent;

    /**
     * Function for retrieving the name of the plugin
     * 
     * @return name of the storage plug-in
     */
    public abstract String getName();

    /**
     * Function for retrieving the version of the plugin
     * 
     * @return version of the plug-in
     * 
     *         format "x.y", where x and y are increasing integers
     */
    public abstract String getVersion();

    /**
     * Function for retrieving the license of the plugin
     * 
     * @return array of Strings describing the license
     * 
     *         The license strings should include license information for the
     *         dependency classes.
     */
    public abstract String[] getLicense();

    /**
     * Function for retrieving the XML namespace for of the configuration node
     * used for storing plugin-specific configuration options
     * 
     * @return String containing XML namespace for the configuration node
     */
    public abstract String getXmlNS();

    /**
     * Function for loading the configuration from an XML node
     * 
     * @param xmlConfigurationNode
     *        node in the configuration file holding plugin-specific settings
     * @return true if configuration was successful
     * 
     *         The XML configuration node is unique per plug-in instance. There
     *         is no such thing as global settings for all plug-in instances.
     * 
     *         The XML configuration node can be null. In this situation, a
     *         default configuration can be assumed.
     */
    public abstract boolean loadConfiguration(Node xmlConfigurationNode);

    /**
     * Function for loading the configuration from an XML node
     * 
     * @param xmlConfigurationNode
     *        node in the configuration file holding plugin-specific settings
     * @return true if configuration was successful
     * 
     *         The XML configuration node is unique per plug-in instance. There
     *         is no such thing as global settings for all plug-in instances.
     *         The node for the saved configuration might be different from the
     *         node where the configuration was loaded from.
     */
    public abstract boolean saveConfiguration(Node xmlConfigurationNode);

    /**
     * Function for writing an entry to the data store.
     * 
     * @param entry
     *        containing information about
     * 
     *        This function returns immediately. Writing to the data store is
     *        done asynchronously. The Storage implementation should maintain a
     *        queue of not yet written entries and try writing them from time to
     *        time.
     * 
     *        Status string should be reported through getStatusString()
     *        function, number of pending writes should be reported through
     *        getPendingWritesCount() function.
     */
    public abstract void write(StudentGradesEntry entry);

    /**
     * Function for reading all entries from the data store
     * 
     * @return vector with all entries in the data storage
     */
    public abstract List<StudentGradesEntry> readAll();

    /**
     * Function for reading an entry from the data store based on the entry ID
     * 
     * @param entryId
     *        identifies the entry in the data storage
     * @return entry that was read or null if entry does not exist
     */
    public abstract StudentGradesEntry read(String entryId);

    /**
     * Function for deleting an entry from the data store based on the entry ID
     * 
     * @param entryId
     *        identifies the entry in the data storage
     */
    public abstract void delete(String entryId);

    /**
     * Function for determining the status for the actions requested from the
     * data store. Returns STATUS_* values
     * 
     * @return status integer (STATUS_*)
     */
    public abstract int getStatus();

    /**
     * Function for getting the status string.
     * 
     * @return status string
     * 
     *         This function is being called when the status of the data store
     *         changes to something different than STATUS_COMPLETED. It should
     *         return a string describing the reasons for the new status.
     */
    public abstract String getStatusString();

    /**
     * Function for getting the number of pending writes
     * 
     * @return number of pending writes
     */
    public abstract int getPendingWritesCount();

    /**
     * Function for drawing the plugin configuration on a panel
     * 
     * @param parentPanel
     *        panel used for drawing the configuration interface
     */
    public abstract void drawConfigurationPanel(javax.swing.JPanel parentPanel);

    /**
     * Function called when closing the configuration panel This function should
     * save the parameters from the panel into local plugin variables
     * 
     * @param parentPanel
     *        panel used for drawing the configuration interface
     */
    public abstract void closeConfigurationPanel(javax.swing.JPanel parentPanel);

    /**
     * Function for storing locally information about an entry The cache is
     * being used for looking up conflicts. It should maintain enough
     * information for determining conflicts and identifying conflicting entries
     * 
     * @param entry
     *        to be saved to local cache
     */
    public void saveToLocalCache(StudentGradesEntry entry) {
        // TODO: function body
    }

    /**
     * Function for testing if the data store has conflicts with the entry
     * passed as parameter
     * 
     * @param entry
     *        to be checked
     * @return true if there are conflicts
     */
    public boolean hasConflict(StudentGradesEntry entry) {
        // TODO: function body
        return false;
    }

    /**
     * Function for retrieving the conflicting entries
     * 
     * @param entry
     *        for which the list of conflicts is wanted
     * @return vector of entries
     */
    public List<StudentGradesEntry> getConflictEntries(StudentGradesEntry entry) {
        if (!hasConflict(entry)) {
            return null;
        }
        // TODO: function body
        return null;
    }

}
