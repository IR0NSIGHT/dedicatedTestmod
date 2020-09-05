package Explorer;

import Events.Mod;
import api.ModPlayground;
import api.common.GameServer;
import api.listener.Listener;
import api.listener.events.SegmentControllerSpawnEvent;
import api.listener.events.player.PlayerChatEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.SpaceStation;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.ManagedSegmentController;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;

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
    public DerelictController(final StarMod instance) {
        this.instance = instance;
        //TODO find out if derelic stations can be caught spawning -- yes, derelicts spawn with faction set to zero
        //TODO otherwise fall back to loading sc event

        StarLoader.registerListener(SegmentControllerSpawnEvent.class, new Listener<SegmentControllerSpawnEvent>() {
            @Override
            public void onEvent(SegmentControllerSpawnEvent e) {



                ModPlayground.broadcastMessage("segment controller spawned: " + e.getController().getName());
                try {
                    int fID = e.getController().getFactionId();
                    SegmentController sc = e.getController();
                    int cID = e.getController().getCreatorId();


                    if (fID == 0 && cID == 1 && sc.getType() == SimpleTransformableSendableObject.EntityType.SPACE_STATION) {
                        ModPlayground.broadcastMessage("DERELICT STATION");
                        if (addToEntitiesCargo(sc,(short) 1008,1000)) {
                           ModPlayground.broadcastMessage("success adding to derelict");
                        } else {
                            ModPlayground.broadcastMessage("failed adding to derelict");
                        }
                    }
                    if (fID == -1 && cID == 1) {
                        ModPlayground.broadcastMessage("PIRATE STATION");
                    }
                    ModPlayground.broadcastMessage("creator ID is: " + cID);
                    ModPlayground.broadcastMessage(("faction ID is: " + fID));
                } catch (Exception ex) {
                    ModPlayground.broadcastMessage("failed getting faction ID. ex: " + ex.toString());
                }
            }

        });
        StarLoader.registerListener(PlayerChatEvent.class, new Listener<PlayerChatEvent>() {
            @Override
            public void onEvent(PlayerChatEvent e) {
                //TODO put casting into try catch
                SegmentController ship;
                String sender = e.getMessage().sender;
                PlayerState player = GameServer.getServerState().getPlayerFromNameIgnoreCaseWOException(sender); //get player
                if (player != null) {
                    ship = (SegmentController) player.getFirstControlledTransformableWOExc(); //get ship
                    if (ship != null) {
                        ModPlayground.broadcastMessage("adding to cargo");

                        if (addToEntitiesCargo(ship,(short)1008, 1000)) {//add to ships cargo
                            ModPlayground.broadcastMessage("success");
                        } else {
                            ModPlayground.broadcastMessage("failed");
                        }
                    }
                }

            }
        });

    }

    public boolean addToEntitiesCargo(SegmentController sc, short blockID, int blockAmount) {
        try {
            if (sc.getType() != SimpleTransformableSendableObject.EntityType.SHIP && sc.getType() != SimpleTransformableSendableObject.EntityType.SPACE_STATION) {
                return false;
            }
            ManagedSegmentController msc = (ManagedSegmentController)sc; //cast segmentcontroller into managed segmentcontroller -> get access to method for manager container
            ManagerContainer mc = msc.getManagerContainer(); //get managercontainer
            ObjectArrayList<Inventory> list = mc.getInventories().inventoriesList; //get all cargo inventories for that segmentcontroller
            if (list.size() == 0) {
                return false;
            }
            Inventory biggestInv = null;
            for (int i = 0; i < list.size(); i++) {
                Inventory inv = list.get(i); //inventory class
                if (biggestInv == null) {
                    biggestInv = inv;
                } else {
                    //check if capacity is bigger
                    if (biggestInv.getCapacity() < inv.getCapacity()) {
                        biggestInv = inv;   //set this inventory as new biggest inventory
                    }
                }
            }

            int sendThisNumber = biggestInv.incExistingOrNextFreeSlot(blockID,blockAmount); // seems to be always zero
            biggestInv.sendInventoryModification(sendThisNumber); //what does this method exactly do? maybe input var int is a "countdown in seconds"?
            return true;
        } catch (Exception e) {
            ModPlayground.broadcastMessage("adding to entities cargo failed: ");
            ModPlayground.broadcastMessage(e.toString());
            return false;
        }
    }
}
