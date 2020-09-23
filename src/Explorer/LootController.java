package Explorer;

import Events.Mod;

import java.util.*;


/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 07.09.2020
 * TIME: 21:15
 */

/**
 * Collection of methods used for creating lists of loot which are put into entities inventories.
 */
public class LootController {
    /**
     * reference to the mods main class. Used for writing to debug file
     */
    private Mod instance;

    /**
     * Constructor
     * @param instance
     */
    public LootController(Mod instance) {
        this.instance = instance;
    }

    /**
     * Will use items + weights to create a map of items + numbers
     * 0.5 slots to 1.5 slots are created and foreach slot, an item is selected from the weighted list.
     * The total amount of items is distributed equally between the slots.
     * each slot is then filled 0..100% with its item.
     * @param weights Hashmap that maps Item ID and item weight
     * @param totalItems total number of items
     * @param slots number of slots for which items are selected
     * @return Hashmap that maps Item ID and item amount
     */
    public HashMap< Integer, Integer> GetLoot(HashMap <Integer, Integer> weights, int totalItems, int slots) {
        instance.ChatDebug("GetLoot called.");
        HashMap<Integer, Integer> loot = new HashMap<>();
        List<LootItem> selectList = GetWeighted(weights);
        //instance.ChatDebug("range list calculated");
        //select random amount of slots, max is 10
        float slotAmount = (float) getRandomRange((int) (slots * 0.5), (int) (slots * 1.5)); //randomly selects from range 0.5 - 1.5 * slots
        float slotSize = totalItems/slotAmount;
        instance.ChatDebug(slotAmount + "slots created with size " + slotSize);
        //instance.ChatDebug("slot amount is " + slotAmount);
        //instance.ChatDebug("slot size is " + slotSize);
        //put items to the slots (weighted)
        int idx = (selectList.size() - 1);
        //instance.ChatDebug("last index for selectlist is "+ idx);
        int totalWeight = selectList.get(idx).rangeMax;
        int itemID = 0;

        for (int i = 0; i < slotAmount; i++) {
            int amount =(int) Math.ceil( Math.random() * slotSize); //actual amount of items in this slot
            //get random from 0 to totalweight
            double point = Math.random() * totalWeight;
            //get point position of item range
            for(LootItem o: selectList) {
                if (point > o.rangeMin && point <= o.rangeMax) { //check if pointer is inside the weighted range of the item
                    itemID = o.ID;
                   // instance.ChatDebug("point is at item " + itemID);
                    break;
                }
            }
            if (amount <= 0) {
                continue;
            }
            //check if item is already present in loot list
            if (loot.containsKey(itemID) ) {
                amount += loot.get(itemID);
               // instance.ChatDebug("item already present, updating amount");
            }
            loot.put(itemID,amount);
           // instance.ChatDebug("added item " + itemID + " with amount " + amount + " to loot list");
        }
      //  instance.ChatDebug("loot list returned " + loot);
        instance.ChatDebug("loot list (itemID, amount) is: ");
        for (Map.Entry<Integer,Integer> entry: loot.entrySet()) {
            instance.ChatDebug("item " + entry.getKey() + " amount " + entry.getValue());
        }
        return loot;
    }

    /**
     *  Creates a range by adding all weights, where each item takes up space according to its weight
     *  Is used to select an item while respecting the items weight.
     *  example:
     *  gold: 1, silver: 2, copper: 5 returns
     *  gold 0 - 1, silver  1 - 3, copper 3 - 8
     *  can now to Random(0,totalWeight) to select an item, respecting weight
     * @param weightedList Hashmap that maps Item ID to item weight
     * @return List of LootItems.
     */
    private List<LootItem> GetWeighted(HashMap <Integer, Integer> weightedList) {

        instance.ChatDebug("GetWeighted called. Creating range list");
        List<LootItem> items = new ArrayList<>();
        int totalWeight = 0;
        for (Map.Entry me : weightedList.entrySet()) {
            /*
             *  get every entry of the weights list, define the min and max range for the pointer, add to items list
             */
           int min = totalWeight;
           int weight = (int) me.getValue();
           totalWeight += weight;
           int max = totalWeight;
           LootItem item = new LootItem((int)me.getKey(),min,max);
           items.add(item);
           instance.ChatDebug("item " + item.ID + " range " + item.rangeMin + "-" + item.rangeMax+ "weight " + weight);
        }
        instance.ChatDebug("weighted range list: " + items);
        return items;
    }

    /**
     * Get a random number between min and max
     * @param min
     * @param max
     * @return random number between min and max
     */
    public int getRandomRange (int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
