package org.tigus.storage;

import java.util.Map;

/**
 * @author Mircea Bardac
 * 
 *         By default, at least the question_pos and the question_grade mappings
 *         are passed to a storage plugin. This way the plugin can store the
 *         question grades on the same positions as in the tests.
 * 
 *         Using the available mappings, the storage plugins can decide whether
 *         to store: - the grade for each question (sorted by question position)
 *         - the answer id for each question (sorted by question position)
 */
public class StudentGradesEntry {

    /** 
     * ID of the entry - UUID will be used for this
     */
    public String id;

    /**
     * Author of the entry (who corrected the test)
     */
    public String author;

    /**
     * String containing the test serial number
     * 
     * This string identifies a test version. The same test version can be used
     * by multiple students.
     */
    public String testSerialNumber;

    /**
     * String containing the student's name
     * 
     * Note: should be unique, but we should not consider this true
     */
    public String studentName;

    /**
     * String containing the student's group
     * 
     * format: nnnAA (n = number, A = character)
     */
    public String studentGroup;

    /**
     * Map between question id and position in the test
     * 
     * The position in the test represents the question number. A question is
     * uniquely identified in all tests by a question ID.
     */
    public Map<Integer, Integer> mapQuestionPosition;

    /**
     * Map between question id and question value
     * 
     * This mapping is used the storage plugins which save the grade for the
     * test in the data store.
     */
    public Map<Integer, Float> mapQuestionGrade;

    /**
     * Map between question id and answer id
     * 
     * This map can be used by storage plugins which do not save the grade for a
     * test. This mapping can later be used to recalculate grades if the value
     * for an answer changes
     */
    public Map<Integer, Integer> mapQuestionAnswer;

    /**
     * Grade for the total test
     * 
     * This can be ignored by plugins which do not store this
     */
    public Float total;

}
