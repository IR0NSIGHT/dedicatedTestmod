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

public class EntityLoadEventLoop{
    /*
       create a loop checking for newly loaded entities
       fires Events.EntityLoadedEvent
       TODO will fire Events.EntityUnloadedEvent
     */
    private Mod modInstance;
    public EntityLoadEventLoop(Mod modinstance){
        this.modInstance = modinstance;
        SegmentControllerCheckLoop();
    }

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
                        chatDebug("new segement controller was loaded.");
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

    public void chatDebug(String s) {
        if (true) {
            SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            String timeStamp = formatter.format(date);
            DebugFile.log((timeStamp + " -- entityeventloop -- " + s), modInstance);
        }
    }
}
