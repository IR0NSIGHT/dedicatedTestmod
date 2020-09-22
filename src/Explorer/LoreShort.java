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
public class LoreShort {
    private String text = "";
    public List<Integer> factionIDList = new ArrayList<>(); //for which faction the text can appear
    public List<Integer> entityTypeList = new ArrayList<>();; //ship, station, etc

    public LoreShort(String text, int faction, int type) {
        this.text = text;
        addFactionID(faction);
        addEntityType(type);
    };
    public LoreShort(String text, int[] factionIDs, int[] types) {
        this.text = text;
        for (Integer i: factionIDs) {
            addFactionID(i);
        }
        for (Integer i: types) {
            addEntityType(i);
        }
    };
    public void setText(String text) {
        this.text = text;
    }
    public String getText() {
        return this.text;
    }
    public void addFactionID(int ID) { //add new faction ID if not already present
        if (FindAt(factionIDList,ID) == -1) {
            this.factionIDList.add(ID);
        }
    }
    public void addEntityType(int type) { //add new entity type if not already present
        if (FindAt(entityTypeList,type) == -1) {
            entityTypeList.add(type);
        }
    }

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
    public static String SerializeToJson(LoreShort ls) {
        Gson gson = new Gson();
        String json = gson.toJson(ls);
        return json;
    }
    public static LoreShort UnserializeFromJSON(String json) {
        Gson gson = new Gson();
        LoreShort ls = gson.fromJson(json, LoreShort.class);
        return ls;
    }

}
