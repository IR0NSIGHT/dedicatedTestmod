package Events;

import org.luaj.vm2.ast.Str;
import org.schema.game.common.controller.SegmentController;

/**
 * STARMADE MOD
 * CREATOR: IR0NSIGHT
 * DATE: 05.09.2020
 * TIME: 14:16
 */
public class EntityWrapper {
    private SegmentController internalEntity;
    private String UID;
    public EntityWrapper(String UID, SegmentController internalEntity) {
        this.internalEntity = internalEntity;
        this.UID = UID;
    }

    public SegmentController getInternalEntity() {
        try {
            return internalEntity;
        } catch (Exception e) {
            return null;
        }
    }
    public String getUID() {
        return UID;
    }
}
