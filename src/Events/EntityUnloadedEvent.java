package Events;

import api.listener.events.Event;

/**
 * event that fires whenever a segmentcontroller is unloaded
 * event is not instant, bc its fired from a 1 second loop
 */
public class EntityUnloadedEvent extends Event {
    /**
     * UID of the unloaded entity
     */
    private final String entityUID;

    /**
     * constructor
     * @param UID UID of Unloaded entity
     */
    public  EntityUnloadedEvent(String UID) {
        this.entityUID = UID;
    }

    /**
     * get UID of the unloaded entity
     * @return UID of unloaded entity (string)
     */
    public String getEntityUID() {
        return this.entityUID;
    }
}
