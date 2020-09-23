package Events;

import org.schema.game.common.controller.SegmentController;

/**
 * STARMADE MOD
 * CREATOR: IR0NSIGHT
 * DATE: 05.09.2020
 * TIME: 14:16
 */

/**
 * data container wrapper for segment controller
 * used because segment controllers get destroyed when unloaded: survives unloading of the segmentcontroller
 */
public class EntityWrapper {
    /**
     * segmentcontroller of the object
     */
    private SegmentController internalEntity;
    /**
     * UID of the object
     */
    private String UID;

    /**
     * constructor
     * @param UID UID of object
     * @param internalEntity segmentcontroller of object
     */
    public EntityWrapper(String UID, SegmentController internalEntity) {
        this.internalEntity = internalEntity;
        this.UID = UID;
    }

    /**
     * Get the segmentcontroller of the object
     * @return segmentcontroller of object, null if not existant.
     */
    public SegmentController getInternalEntity() {
        try {
            return internalEntity;
        } catch (Exception e) {
            internalEntity = null; //kill reference to segment controller (could otherwise maybe cause leak?)
            return null;
        }
    }

    /**
     * Get UID of entity
     * @return UID of entity
     */
    public String getUID() {
        return UID;
    }
}
