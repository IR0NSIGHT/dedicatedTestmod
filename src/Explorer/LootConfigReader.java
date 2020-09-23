package Explorer;

import Events.Mod;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * STARMADE MOD
 * CREATOR: IR0NSIGHT
 * DATE: 08.09.2020
 * TIME: 12:00
 */

/**
 * Class that contains methods to read a .properties configfile where block item IDs and their weights for spawning in storages are defined.
 */
//TODO make static methods.
public class LootConfigReader {
    /**
     * reference to main class. Used for writing to debug file
     */
    public Mod instance;

    /**
     * constructor
     * @param instance main mod class
     */
    public LootConfigReader(Mod instance) {
        this.instance = instance;
    }

    /**
     * Reads a .properties configfile. Creates Hashmap of blockID : weight
     * @return HashMap that maps BlockID to probability weight
     */
    //TODO change so it takes path as input param instead of hardcoded path
    //TODO change to json (?)
    public HashMap<Integer,Integer> ReadConfig() {
        Properties prop = new Properties();
        HashMap<Integer,Integer> list = new HashMap<>();
        try {
            FileInputStream ip = new FileInputStream("loottable.properties");
            prop.load(ip);
            ip.close();
        } catch (Exception e) {
            instance.ChatDebug("file reading failed. e: " + e.toString());
        }
        instance.ChatDebug("properties: " + prop);
        for (Map.Entry me: prop.entrySet()) {
            try {
                Integer k = Integer.parseInt((String) me.getKey());
                Integer v = Integer.parseInt((String) me.getValue());
                list.put(k,v);
                instance.ChatDebug("key: " + k + "value: " + v);
            } catch (Exception e) {
                instance.ChatDebug("failed getting entry set: " + e.toString());
            }
        }
        return list;
    }
}
