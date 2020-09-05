package Explorer;

import Events.Mod;
import api.ModPlayground;
import api.listener.Listener;
import api.listener.events.SegmentControllerSpawnEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
/**
 * STARMADE MOD
 * CREATOR: IR0NSIGHT
 * DATE: 05.09.2020
 * TIME: 14:54
 */
public class DerelictController {
    /**
     * this class handles the SegmentController loaded event and checks if it is a derelict station.
     * if so, it fills loot into the stations cargo holds
     */
    private final StarMod instance;
    public DerelictController(StarMod instance) {
        this.instance = instance;
        //TODO find out if derelic stations can be caught spawning -- yes, derelicts spawn with faction set to zero
        //TODO otherwise fall back to loading sc event

        StarLoader.registerListener(SegmentControllerSpawnEvent.class, new Listener<SegmentControllerSpawnEvent>() {
            @Override
            public void onEvent(SegmentControllerSpawnEvent e) {



                ModPlayground.broadcastMessage("segment controller spawned: " + e.getController().getName());
                try {
                    int fID = e.getController().getFactionId();
                    ModPlayground.broadcastMessage(("faction ID is: " + fID));
                    int cID = e.getController().getCreatorId();
                    ModPlayground.broadcastMessage("creator ID is: " + cID);
                    if (fID == 0 && cID == 1) {
                        ModPlayground.broadcastMessage("DERELICT STATION");
                    }
                    if (fID == -1 && cID == 1) {
                        ModPlayground.broadcastMessage("PIRATE STATION");
                    }
                } catch (Exception ex) {
                    ModPlayground.broadcastMessage("failed getting faction ID. ex: " + ex.toString());
                }
            }
        });

    }
}
