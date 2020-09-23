package Explorer;

import com.google.gson.Gson;
import org.luaj.vm2.ast.Str;

import java.util.ArrayList;
import java.util.List;

/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 16.09.2020
 * TIME: 13:00
 * this class is built from config files for each lore text entry
 * it holds info about in what ship/station etc it can appear.
 */

/**
 * data container for lore short texts
 * read from config file
 * Lists the allowed factions for this text
 * Lists the allowed entity types for this text
 */
public class LoreShort {
    /**
     * lore text.
     * Gets filled on creation
     */
    private String text = "";
    /**
     * list of allowed factions that can use this lore text
     */
    public List<Integer> factionIDList = new ArrayList<>(); //for which faction the text can appear
    /**
     * list of allowed entity types that can use this lore text
     */
    public List<Integer> entityTypeList = new ArrayList<>();; //ship, station, etc

    /**
     * constructor (most basic, takes one faction and one entity type)
     * @param text Loretext
     * @param faction faction ID
     * @param type entity type
     */
    public LoreShort(String text, int faction, int type) {
        this.text = text;
        addFactionID(faction);
        addEntityType(type);
    };

    /**
     * constructor (advanced, takes lists of factions and entity types)
     * @param text Loretext
     * @param factionIDs list of faction IDS
     * @param types list of entity types
     */
    public LoreShort(String text, int[] factionIDs, int[] types) {
        this.text = text;
        for (Integer i: factionIDs) {
            addFactionID(i);
        }
        for (Integer i: types) {
            addEntityType(i);
        }
    };

    /**
     * set text of LoreShort object that will appear in logbooks
     * @param text text to use in logbook. maximum of 512 character, 99 lines
     */
    //TODO trim text to 99 lines and 512 characters or give warning
    public void setText(String text) {
        this.text = text;
    }

    /**
     * get lore text
     * @return lore text
     */
    public String getText() {
        return this.text;
    }

    /**
     * Add new faction to list of allowed factions
     * Autochecks for duplicates
     * @param ID faction ID to add
     */
    public void addFactionID(int ID) { //add new faction ID if not already present
        if (FindAt(factionIDList,ID) == -1) {
            this.factionIDList.add(ID);
        }
    }

    /**
     * add new entity type to list of allowed entity types
     * automatically checks for double entries
     * @param type entity type to add
     */
    public void addEntityType(int type) { //add new entity type if not already present
        if (FindAt(entityTypeList,type) == -1) {
            entityTypeList.add(type);
        }
    }

    /**
     * search given list for the entry, return entry
     * @param list list to search for entry
     * @param entry entry to find in list
     * @return index of entry in list, -1 if not found
     */
    private int FindAt(List<Integer> list, int entry) {
        int idx = 0;
        if (list == null || list.size() == 0) {
            return -1;
        }
        for (Integer x: list) {
            if (x == entry) {
               return idx;
            }
            idx ++;
        }
        return -1;
    }; //find index of entry, return -1 if not present
    /**
     * Turns loreshort instance into a loggable string (for debugging)
     * to lazy to find out how to overwrite of toString() works.
     * @return String containing all relevant info about this loreshort
     */
    public String StringIt() {
        String s = "LoreShort: ";
        s += text;
        s += " || for factions: ";
        for(Integer faction: factionIDList) {
            s+= (faction + ", ");
        }
        s+= " || for types: ";
        for (Integer ent: entityTypeList) {
          s += (ent + ", ");
        }
        return s;
    }
}
