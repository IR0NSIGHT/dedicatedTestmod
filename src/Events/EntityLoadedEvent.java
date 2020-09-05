package Events;

import api.listener.events.Event;
import org.schema.game.common.controller.SegmentController;

public class EntityLoadedEvent extends Event{
        /**
            fired from EntityLoadEventload whenever a new segment controller is detected.
            loop runs once a second -> event is not instant.
        */
        private final String entityUID;
        private final SegmentController entitySegmentController;

        public EntityLoadedEvent(String UID, SegmentController sc) {
            this.entitySegmentController = sc;
            this.entityUID = UID;
            //ModPlayground.broadcastMessage("entity loaded event fired for " + this.entityUID);
        }
        public String getEntityUID() {
            return this.entityUID;
        }
        public SegmentController getSegmentController() {
            return this.entitySegmentController;
        }

}
