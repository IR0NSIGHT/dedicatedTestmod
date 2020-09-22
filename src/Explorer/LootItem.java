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

    public int ID;
    public int rangeMin;
    public int rangeMax;
    public LootItem(int ID, int min, int max) {
        this.ID = ID;
        this.rangeMin = min;
        this.rangeMax = max;
    }

}
