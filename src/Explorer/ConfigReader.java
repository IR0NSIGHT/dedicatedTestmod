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
public class ConfigReader {
    public Mod instance;
    public ConfigReader(Mod instance) {
        this.instance = instance;
    }
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
