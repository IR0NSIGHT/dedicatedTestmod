package Explorer;

import Events.Mod;
import api.ModPlayground;
import api.common.GameServer;
import api.listener.Listener;
import api.listener.events.SegmentControllerSpawnEvent;
import api.listener.events.player.PlayerChatEvent;
import api.mod.StarLoader;
import api.utils.StarRunnable;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.ManagedSegmentController;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.meta.Logbook;
import org.schema.game.common.data.element.meta.MetaObjectManager;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import org.schema.game.server.data.Galaxy;
import org.schema.game.server.data.GameServerState;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * STARMADE MOD
 * CREATOR: IR0NSIGHT
 * DATE: 05.09.2020
 * TIME: 14:54
 */
/**
 * this class handles the SegmentController loaded event and checks if it is a derelict station.
 * if so, it fills loot into the stations cargo holds
 */
public class DerelictController {
    /**
     * reference to main mod class, used for writing to debug file
     */
    private final Mod instance;
    private final LootController lc;
    private final LootConfigReader cr;
    private final LoreHandler lh;
    /**
     * HashMap that maps ItemID to item weight. (direct copy of config file)
     */
    private HashMap<Integer,Integer> derelictLoot;
    /**
     * number of items put into a new derelict station.
     */
    private int lootAmount = 5000;

    /**
     * Constructor
     * Creates 2 Eventhandlers:
     * new entity spawned
     * chatevent (for debug purposes)
     * @param instance reference to mods main class for debug logging
     */
    public DerelictController(final Mod instance) {
        this.instance = instance;
        lc = new LootController(instance);
        cr = new LootConfigReader(instance);
        derelictLoot = cr.ReadConfig();
        lh = new LoreHandler(instance);

        StarLoader.registerListener(SegmentControllerSpawnEvent.class, new Listener<SegmentControllerSpawnEvent>() {
            @Override
            public void onEvent(SegmentControllerSpawnEvent e) {
                ModPlayground.broadcastMessage("segment controller spawned: " + e.getController().getName());
                try {
                    int fID = e.getController().getFactionId();
                    SegmentController sc = e.getController();
                    int cID = e.getController().getCreatorId();
                    instance.ChatDebug("new sc created: " + sc.getName() + " faction: " + fID);
                    if (fID == 0 && cID == 1 && sc.getType() == SimpleTransformableSendableObject.EntityType.SPACE_STATION) {
                        ModPlayground.broadcastMessage("DERELICT STATION");
                    }
                    if (fID == -1 && cID == 1) {
                        ModPlayground.broadcastMessage("PIRATE STATION");
                    }
                    if (sc != null) {
                        final SegmentController segCon = sc;
                        new StarRunnable() {
                            @Override
                            public void run() {
                                AddLore(segCon, null);
                                FillEntityLoot(segCon);
                            }
                        }.runLater(50);
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

                if (e.getMessage().text.equals("config")) {
                    ReadConfig();
                }
                if (e.getMessage().text.equals("amount")) {
                    DebugLootAmount();
                }
                if (e.getMessage().text.equals("loreread")) {
                    lh.ReadFile();
                }
                if (e.getMessage().text.equals("server")) {
                    String s = " server has universe: " + GameServer.getUniverse().getName() + "Gameserverstate path " + GameServerState.DATABASE_PATH;
                    ModPlayground.broadcastMessage(s);
                    instance.ChatDebug(s);
                    //server has universe: nullGameserverstate path .\server-database\world_zero\
                }
                try {
                    if (player != null) {
                        ship = (SegmentController) player.getFirstControlledTransformableWOExc(); //get ship
                        if (ship != null) {
                            ModPlayground.broadcastMessage("adding to cargo");
                            if (e.getMessage().text.equals("book")) {
                                AddLore(ship,null);
                            }
                        }
                    }
                } catch (Exception ex) {
                    instance.ChatDebug("chat event failed: " + ex);
                }



            }
        });

    }

    /**
     * Adds a logbook to the specified inventory of the ship. Selects fitting lore text based on ship faction
     * @param ship
     * @param inv
     */
    //TODO add check for entity type
    private void AddLore(SegmentController ship, Inventory inv) {
        try {
            if (inv == null) {
                inv = GetRndInv(ship); //get random inventory of segment controller
            }
            if (inv == null) {
                instance.ChatDebug("inventory is null");
                return;
            }
            instance.ChatDebug("gotten inv: capacity " + inv.getCapacity());
            //get free inventory slot
            int index = inv.getFreeSlot();
            instance.ChatDebug("free slot of inventory is : " + index);
            //add logbook
            int faction = ship.getFactionId();
            instance.ChatDebug("faction ID is " + faction);
            AddLogbook(inv, index, faction); //chooses a lore text fitting for the given faction and creates a logbook with it. adds to inventory
            inv.clientUpdate();
            instance.ChatDebug("added logbook");
            inv.sendAll();
            instance.ChatDebug("sent all");
        }catch (Exception e) {
            e.printStackTrace();
            instance.ChatDebug(e.toString());
        }
    }

    /**
     * returns loot map
     * @param loottable
     * @param amount
     * @param slots
     * @return
     */
    private HashMap<Integer, Integer> GetLoot(HashMap<Integer,Integer> loottable, int amount, int slots) {
        return lc.GetLoot(loottable,amount,slots);
    }

    /**
     * fills every inventory in the ship with basic loot (100 items, ~5 slots), independent from mainloot itemnumber.
     * fills every inventory in the ship with a logbook with random loretext
     * the inventory list is shuffeled.
     * each inventory gets its own distribution for loot: "lootset"
     * this is used for basic loot and main loot
     * the method calculates how many lootsets fit into the inventory
     * the inventory is then filled to the brim with multiple lootsets until the total number of loot is zero, or no more inventory space is left.
     * @param sc
     */
    //TODO split up into sub methods of basic loot, actual loot, logbook
    private void FillEntityLoot (SegmentController sc) {
        //foreach cargo module do loot
        if (sc.getType() != SimpleTransformableSendableObject.EntityType.SHIP && sc.getType() != SimpleTransformableSendableObject.EntityType.SPACE_STATION) {
            instance.ChatDebug("entity is neither ship nor station.");
            return;
        }
        ManagedSegmentController msc = (ManagedSegmentController)sc; //cast segmentcontroller into managed segmentcontroller -> get access to method for manager container
        ManagerContainer mc = msc.getManagerContainer(); //get managercontainer
        final ObjectArrayList<Inventory> list = mc.getInventories().inventoriesList; //get all cargo inventories for that segmentcontroller
        if (list.size() == 0) {
            instance.ChatDebug("inventory list is zero");
            return;
        }
        //randomize list
        Collections.shuffle(list);

        double totalCap = 0;
        int totalItems = lootAmount;
        instance.ChatDebug(" total items before filling: " + totalItems);
        for (Inventory inv: list) { //foreach inventory, create loot and fill it
            AddLore(sc,inv); //TODO remove add lore to every inv for final builds

            int baseSlots = (int) (Math.random() * 5);
            HashMap<Integer,Integer> loot = GetLoot(derelictLoot, 100,baseSlots); //basic loot, low amount + slots, just to keep chests not empty and interesting
            //TODO get smallest common denominator to get smallest possible size of lootset
            float lootVol = 0;
            float lootSetItems = 0;
            for (Map.Entry me: loot.entrySet()) { //foreach item type, add to cargo

                short ID = (short)((int) me.getKey());
                int meta = -1;
                int amount = (int) me.getValue();
                ElementInformation elInfo = ElementKeyMap.getInfoFast(ID);
                float itemVol =  elInfo.getVolume();
                float setVol = itemVol * amount;
                lootVol += setVol;
                lootSetItems += amount;
                if (inv.canPutIn(ID,amount)) {
                    int sendThisNumber = inv.incExistingOrNextFreeSlot(ID,amount); // seems to be always zero
                    inv.sendInventoryModification(sendThisNumber); //what does this method exactly do? maybe input var int is a "countdown in seconds"?, might be slot array pos?
                } else {
                    instance.ChatDebug("cant add basic " + ID + "x" + amount + " to inventory.");
                }
            }
            if (totalItems <= 0) {
                instance.ChatDebug("total items for this entity loot is below zero, skip");
                continue;
            }
            instance.ChatDebug("total items at " + totalItems);
            //calculate number of lootsets which fit into inventory
            int setAmount = (short) Math.round(inv.getCapacity() / lootVol);
            instance.ChatDebug("inventory has volume " + inv.getCapacity() + " loot set has volume " + lootVol);
            instance.ChatDebug("inventory can hold " + setAmount + " lootsets");
            int allowedSets = (int)(totalItems/lootSetItems); //allowed number of sets in this inventory
            setAmount = Math.min(setAmount,allowedSets); //cap number of sets to total items
            instance.ChatDebug("set amount after minmax at " + setAmount);
            if (setAmount < 1) {
                instance.ChatDebug("allowed sets below one, skipping");
                continue;
            }
            for (Map.Entry me: loot.entrySet()) {
                short ID = (short)((int) me.getKey());
                int amount = (((int) me.getValue()) * setAmount);
                //TODO caninputhowmuch -> fill instead of skip
                if (inv.canPutIn(ID,amount)) {
                    int sendThisNumber = inv.incExistingOrNextFreeSlot(ID,amount); // seems to be always zero
                    inv.sendInventoryModification(sendThisNumber); //what does this method exactly do? maybe input var int is a "countdown in seconds"?, might be slot array pos?
                    totalItems -= amount;
                    instance.ChatDebug("added " + amount + " of ID " + ID);
                } else {
                    instance.ChatDebug("cant add " + ID + "x" + amount + " to inventory.");
                }
            }
            totalCap += inv.getCapacity();
        }

        instance.ChatDebug("total capacity is " + totalCap);
    }
    /**
     * creates a MetaObject logbook and puts it at specified index of the inventorymap into the specified inventory.
     * @param inv Inventory which the logbook gets put into
     * @param mapidx Inventory slot index
     * @param factionID faction ID for which fitting lore is used
     */
    //TODO add check for entity type
    //TODO move "getlore" out of method, use input string instead
    private void AddLogbook(Inventory inv, int mapidx, int factionID) {

        try {
            lh.ReadFile(); //read file again in case of change TODO disable for final build.
            instance.ChatDebug("trying to create new logbook with ID");
            Logbook lb = (Logbook) MetaObjectManager.instantiate(MetaObjectManager.MetaObjectType.LOG_BOOK,(short) 1,true); //var0 = type, var1 = weapon subtype (ignored if not weapon), var2 bool true => generate new ID automatically.
            instance.ChatDebug("new logbook has ID: " + lb.getId());

            //TODO entity type check/pass into lorehandler
            lb.setTxt(lh.GetLoreText(factionID)); //set random text from file
            inv.put(mapidx,lb); //put logbook into inventory at slot !!overwrites existing slots

            instance.ChatDebug("put logbook into inv");
        }catch (Exception e) {
            e.printStackTrace();
            instance.ChatDebug("logbook creation failed: " + e.toString());
        }
        ModPlayground.broadcastMessage("done creating logbook");
    }

    /**
     * selects a random inventory of the SegmentController. returns null if none are available
     * @param sc SegmentController
     * @return
     */
    //TODO check for free space in inv
    private Inventory GetRndInv(SegmentController sc) {
        instance.ChatDebug("trying to getrandom entities cargo: " + sc.getName());
        try {
            if (sc.getType() != SimpleTransformableSendableObject.EntityType.SHIP && sc.getType() != SimpleTransformableSendableObject.EntityType.SPACE_STATION) {
                instance.ChatDebug("entity is neither ship nor station.");
                return null;
            }
            ManagedSegmentController msc = (ManagedSegmentController) sc; //cast segmentcontroller into managed segmentcontroller -> get access to method for manager container
            ManagerContainer mc = msc.getManagerContainer(); //get managercontainer
            ObjectArrayList<Inventory> list = mc.getInventories().inventoriesList; //get all cargo inventories for that segmentcontroller
            if (list.size() == 0) {
                instance.ChatDebug("inventory list is zero");
                return null;
            }
            instance.ChatDebug("inventories list is " + list.size());
            int idx = (int) Math.floor(Math.random() * list.size());
            return list.get(idx);
        }
        catch (Exception e) {
            e.printStackTrace();
            instance.ChatDebug("GetRndInv failed: " + e.toString());
        }
        return null;
    }

    /**
     * change number of items for lootcreation
     * called through chatevent
     * cycles through 5k, 15k, 45k
     */
    private void DebugLootAmount() {
        //cycle through 5k, 15k, 45k
        int old = lootAmount;
        lootAmount *= 3;
        if (lootAmount > 5000 * 3 * 3) {
            lootAmount = 5000;
        }
        instance.ChatDebug("set loot amount from " + old + " to " + lootAmount);
        ModPlayground.broadcastMessage("loot item number set to " + lootAmount);
    }

    /**
     * Deprecated method
     * Adds specified items to biggest inventory of segmentcontroller
     * @param sc segmentcontroller
     * @param blockID blockID
     * @param blockAmount number of blocks
     * @return true if was able to add blocks, false if not.
     */
    public boolean addToEntitiesCargo(SegmentController sc, short blockID, int blockAmount) {
        instance.ChatDebug("trying to add to entities cargo: " + sc.getName());
        try {
            if (sc.getType() != SimpleTransformableSendableObject.EntityType.SHIP && sc.getType() != SimpleTransformableSendableObject.EntityType.SPACE_STATION) {
                instance.ChatDebug("entity is neither ship nor station.");
                return false;

            }
            ManagedSegmentController msc = (ManagedSegmentController)sc; //cast segmentcontroller into managed segmentcontroller -> get access to method for manager container
            ManagerContainer mc = msc.getManagerContainer(); //get managercontainer
            ObjectArrayList<Inventory> list = mc.getInventories().inventoriesList; //get all cargo inventories for that segmentcontroller
            if (list.size() == 0) {
                instance.ChatDebug("inventory list is zero");
                return false;
            }
            instance.ChatDebug("inventories list is " + list.size());
            Inventory biggestInv = null;
            for (int i = 0; i < list.size(); i++) {
                Inventory inv = list.get(i); //inventory class
                instance.ChatDebug("inventory x has capacity " + inv.getCapacity());
                if (biggestInv == null) {
                    biggestInv = inv;
                } else {
                    //check if capacity is bigger

                    if (biggestInv.getCapacity() < inv.getCapacity()) {
                        biggestInv = inv;   //set this inventory as new biggest inventory
                    }
                }
                //TODO check canputin
                int sendThisNumber = inv.incExistingOrNextFreeSlot(blockID,blockAmount); // seems to be always zero
                inv.sendInventoryModification(sendThisNumber); //what does this method exactly do? maybe input var int is a "countdown in seconds"?
            }
            instance.ChatDebug("finished looping through inventories");
            instance.ChatDebug("biggest inentory has capacity " + biggestInv.getCapacity());

            instance.ChatDebug("sent inventory update modification");
            return true;

        } catch (Exception e) {
            ModPlayground.broadcastMessage("adding to entities cargo failed: ");
            ModPlayground.broadcastMessage(e.toString());
            instance.ChatDebug(e.getMessage());
            instance.ChatDebug(e.toString());
            return false;
        }
    }

    /**
     * Debug method
     * Will log every single stellar system of spawn galaxy with coordinates + name to debug file
     * @return
     */
    public boolean WriteAllSystems() {
        boolean success = false;
        try {
            int voids = 0;
            //get galaxy
            for (int x = 0; x < 128; x ++) {
                for (int y = 0; y < 128; y ++) {
                    for (int z = 0; z < 128; z++) {
                        Vector3i sy = new Vector3i(x,y,z);
                        Galaxy gl = GameServer.getUniverse().getGalaxyFromSystemPos(sy); //get galaxy from system pos (spawn galaxy seems to be pos 0,0,0
                        String sysName = gl.getName(sy);
                        if (sysName.equals("Void")) {
                            voids ++;
                        } else {
                            instance.ChatDebug("system " + sy.toString() + ", name " + sysName);
                        }

                    }
                }
            }
            ModPlayground.broadcastMessage("galaxy has " + voids + " voids");

        } catch (Exception e) {
            instance.ChatDebug("setNameBySystem has failed with error: " + e.toString());
            return false;
        }
        return success;
    }

    /**
     * Reads lootitem configfile
     */
    //TODO call directly
    public void ReadConfig() {
        instance.ChatDebug("trying to call ReadConfig of configreader");
        cr.ReadConfig();
    }
}
