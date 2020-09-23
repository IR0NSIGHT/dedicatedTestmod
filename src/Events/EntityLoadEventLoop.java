package Events;

import api.DebugFile;
import api.common.GameServer;

import api.mod.StarLoader;

import api.utils.StarRunnable;
import org.schema.game.common.controller.SegmentController;
import org.schema.schine.network.server.ServerState;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * a loop detecting newly loaded segment controllers
 * stores all currently loaded entities and on the next check compares if new ones were added or old ones unloaded.
 * Fires according eventy: EntityLoadedEvent + EntityUnloadedEvent
 */
//TODO will fire Events.EntityUnloadedEvent
public class EntityLoadEventLoop{
    /**
     * reference to mod instance for debug logging
     */
    private Mod modInstance;

    /**
     * constructor
     * Starts checkloop
     * @param modinstance reference to main mod for debug logging
     */
    public EntityLoadEventLoop(Mod modinstance){
        this.modInstance = modinstance;
        SegmentControllerCheckLoop();
    }

    /**
     * creates a starrunnable that runs once a second
     * Logs entities to a list (oldLoadedEnts)
     * compares to newLoadedEnts
     */
    private void SegmentControllerCheckLoop() {
        new StarRunnable() {
            private ArrayList<EntityWrapper> oldLoadedEnts;
            @Override
            public void run() {
                //chatDebug("loop running");
                if (ServerState.isShutdown() || ServerState.isFlagShutdown()) {
                    chatDebug("loop stopping bc server shutdown");
                    this.cancel();
                }
                if (GameServer.getServerState() != null) { //only start running when server is loaded.
                    ArrayList<EntityWrapper> newEnts = new ArrayList<>();
                    ArrayList<EntityWrapper> unloadedEnts = new ArrayList<>();
                    if (oldLoadedEnts == null || oldLoadedEnts.size() == 0) { //first time init, all entites are new.
                        oldLoadedEnts = new ArrayList<>();

                    }

                    ArrayList<EntityWrapper> newLoadedEnts = getoldLoadedEnts();
                    //check currenty loaded entities against old newLoadedEnts of loaded entities to get newly loaded ones.
                    for (int i = 0; i < newLoadedEnts.size(); i++) {
                        //foreach currently loaded entity
                        EntityWrapper newEntity = newLoadedEnts.get(i);
                        //check if its in old newLoadedEnts
                        boolean alreadyListed = IsInList(oldLoadedEnts, newEntity.getUID());
                        if (!alreadyListed) {
                            newEnts.add(newEntity);
                        }
                    }

                    for (int i = 0; i < newEnts.size(); i++) {
                        EntityLoadedEvent event = new EntityLoadedEvent( newEnts.get(i).getUID(),newEnts.get(i).getInternalEntity() );
                        StarLoader.fireEvent(EntityLoadedEvent.class, event, true);
                        SegmentController sc = newEnts.get(i).getInternalEntity();
                        int fID = sc.getFactionId();
                        chatDebug("new sc loaded: " + sc.getName() + " faction: " + fID);
                    }

                    for (int i = 0; i < oldLoadedEnts.size(); i++) {
                        //if old entity not in new newLoadedEnts -> was unloaded
                        EntityWrapper oldEnt = oldLoadedEnts.get(i);
                        boolean inNewList = IsInList(newLoadedEnts,oldEnt.getUID());
                        if (!inNewList) {
                            unloadedEnts.add(oldEnt);
                        }
                    }

                    for (int i = 0; i < unloadedEnts.size(); i++) {
                        //TODO get unloaded Entities UID -> star api merge request
                        //Events.EntityUnloadedEvent event = new Events.EntityUnloadedEvent(unloadedEnts.get(i).getUID());
                        //StarLoader.fireEvent(Events.EntityUnloadedEvent.class, event, true);
                    }
                    //ModPlayground.broadcastMessage("updating entities loaded. new: " + newEnts.size() + " unloaded:" + unloadedEnts.size() +" total:" + newLoadedEnts.size() +   "----------------------------" + System.currentTimeMillis()/1000);
                    oldLoadedEnts = newLoadedEnts; //replace old loaded entity newLoadedEnts with new one.
                }
                //chatDebug("loop completed");
            }

            /**
             * Check given List for this UID.
             * @param newLoadedEnts
             * @param UID
             * @return true if in list, false if not.
             */
            private boolean IsInList(ArrayList<EntityWrapper> newLoadedEnts, String UID) {
                for (int j = 0; j < newLoadedEnts.size(); j++) {
                    //compare UIDs
                    //break out at first matching instance
                    if (newLoadedEnts.get(j).getUID().equals(UID)) {
                        //already in loaded Entities.
                        return true;
                    }
                }
                //looped through complete newLoadedEnts, didnt find a match, entity is not in newLoadedEnts
                return false;
            }

            /**
             * get all currently loaded segmentcontrollers in a EntityWrapper list
             * @return Arraylist with all loaded segmentcontrollers as EntityWrapper objects
             */
            private ArrayList<EntityWrapper> getoldLoadedEnts() {
                ArrayList<EntityWrapper> newLoadedEnts = new ArrayList<>();
                for (SegmentController sc: GameServer.getServerState().getSegmentControllersByName().values()) {
                    newLoadedEnts.add(
                            new EntityWrapper( //create a new entity wrapper from segmentcontroller
                                sc.getUniqueIdentifier(),
                                sc
                    ));
                }
                return newLoadedEnts;
            }
        }.runTimer(25);
    }

    /**
     * Debug method for logging a string to the starloader debug file
     * Adds a timestamp and modorigin to log
     * @param s String to log
     */
    public void chatDebug(String s) {
        if (true) {
            SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            String timeStamp = formatter.format(date);
            DebugFile.log((timeStamp + " -- entityeventloop -- " + s), modInstance);
        }
    }
}
