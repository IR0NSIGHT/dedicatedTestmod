package Events;

import api.listener.events.Event;
import org.schema.game.common.controller.SegmentController;
/**
 *  Eventclass for new loaded segment controllers.
 fired from EntityLoadEventload whenever a new segment controller is detected.
 loop runs once a second: event is not instant
 Only has getter methods, can not be used to directly modify something.
 */
public class EntityLoadedEvent extends Event{
   private final String entityUID;
   private final SegmentController entitySegmentController;

    /**
     * Constructor
     * Gets called by the Eventloop for every new detected segmentcontroller
     * @param UID UID of the segmentcontroller (string)
     * @param sc Segmentcontroller
     */
        public EntityLoadedEvent(String UID, SegmentController sc) {
            this.entitySegmentController = sc;
            this.entityUID = UID;
            //ModPlayground.broadcastMessage("entity loaded event fired for " + this.entityUID);
        }

    /**
     * Get the new entities UID
     * @return entities UID string
     */
    public String getEntityUID() {
            return this.entityUID;
        }

    /**
     * Get the new entities segment controller
     * @return new entites segment controller
     */
    public SegmentController getSegmentController() {
            return this.entitySegmentController;
        }

}
