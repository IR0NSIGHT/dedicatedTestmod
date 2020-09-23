package Explorer;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 23.09.2020
 * TIME: 01:01
 * Data container for a block item and its probabilty values
 * range is used for a pointer to get items based on their weight.
 */
public class LootItem {
    /**
     * block ID
     */
    public int ID;
    /**
     * starting point of range
     */
    public int rangeMin;
    /**
     * end point of range
     */
    public int rangeMax;

    /**
     * Constructor
     * @param ID block ID
     * @param min startpoint of range
     * @param max endpoint of range
     */
    public LootItem(int ID, int min, int max) {
        this.ID = ID;
        this.rangeMin = min;
        this.rangeMax = max;
    }

}
