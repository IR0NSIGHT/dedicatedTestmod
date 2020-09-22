package Explorer;

import Events.Mod;
import api.ModPlayground;
import com.google.gson.Gson;
import org.lwjgl.Sys;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * STARMADE MOD
 * CREATOR: Max1M
 * DATE: 16.09.2020
 * TIME: 13:29
 */
public class LoreHandler {
    /**
     *  this class reads the lore config files and turns them into LoreShort class instances
     *  it saves all of these instances to a list
     *  it can be called from outside, takes in specifications for the type of lore you want and returns the fitting loreshort
     */
    private final Mod instance;
    public List<LoreShort> loreList = new ArrayList<>();
    public String defaultLore = " AUTOMATED SYSTEM LOG /n -- ALL SYSTEMS OFFLINE --";
    String path = "lore.json";
    public LoreHandler(Mod instance) {
        this.instance = instance;
        instance.ChatDebug("created LoreHandler");
        ReadFile();
    }
    private int i = 0;

    /**
     * Reads the loreconfig.json from the game install
     * if the file does not exist, it will create a config from the hardcoded LoreHolder.json from the jar and read that one.
     * will attempt to create a new config 2 times before stopping.
     * @param path the file path in the game install from where to read the lore config file
     */
    public void ReadFile(String path) {
        instance.ChatDebug("trying to read file from: " + path);
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader(path)
            );
            Gson gson = new Gson();
            instance.ChatDebug("trying to retrieve arraylist of loreshort from json file");
            LoreShort[] list = gson.fromJson(br, LoreShort[].class);
            instance.ChatDebug("retrieved loreshort list from json has " + list.length + " entries");
            if (list.length == 0) {
                return;
            }
            loreList = new ArrayList<>();
            for (LoreShort ls: list) {
                loreList.add(ls); //add to list
                instance.ChatDebug("retrieved ls " + ls.StringIt());
                //ModPlayground.broadcastMessage("ls file: " + ls.StringIt());
            }

        } catch (FileNotFoundException e) {
            instance.ChatDebug("lorehandler could not find config file");
            instance.ChatDebug("i is at " + i);
            i ++;
            if (i < 2) {
              try {
                  // WriteFile();
                  InputStream is = LoreShort.class.getResourceAsStream("LoreHolder.json");
                  if (is == null) {
                      instance.ChatDebug("input stream is null");
                  }
                  //instance.ChatDebug("input stream: " + is.toString());
                  File f = new File(path);
                  if (f.exists() == false) {
                      instance.ChatDebug("output file does not exist, creating one");
                      f.createNewFile();
                  }
                  OutputStream os = new FileOutputStream(path);
                  byte[] buffer = new byte[1024];
                  int length;
                  while ((length = is.read(buffer))> 0) {
                      os.write(buffer,0,length);
                  }
                  is.close();
                  os.close();
                  instance.ChatDebug("finished writing new config.");
                  ReadFile();
                  if (is == null) {
                      instance.ChatDebug("input stream is null");
                  }
              } catch (Exception ex) {
                  ex.printStackTrace();
                  instance.ChatDebug("url to file failed: " + ex.toString());
              }

            }

        }
        catch (Exception e) {
            e.printStackTrace();
            instance.ChatDebug("exception during read file " + e.toString());
            //ModPlayground.broadcastMessage("file reading failed!");
        }


    }

    /**
     *  ReadFile overload that does not require a specific path. will use the default path instead.
     */
    public void ReadFile() {
        try {
            ReadFile(path);
        }catch (Exception e) {
            instance.ChatDebug("failed to read file with default path");
        }

    }
    public static void main(String[] args){
        String text = "hello world";
    }
    public void WriteFile() {
        //copy the hardcoded default textfile into the starmade install folder as the new configfile
   /*     InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream()
        }*/

    }

    /**
     * Will search through all available Loreshorts, create a list fitting the specified faction and entitytype and choose one randomly.
     * @param factionID Vanilla faction ID (int). Intended to be trading guild, traders, pirates, derelict etc.
     * @param entityType Entity type, unused parameter atm. -1 as input will ignore entityType.
     * @return randomly selected, fitting LoreShort. Default Loreshort if no matches were found.
     */
    public String GetLoreText(int factionID, int entityType) {
        instance.ChatDebug("GetLoreText was called");
        if (loreList.size() == 0) {
            instance.ChatDebug("lore list is zero, no text available");
            return "list not yet read";
        }
        List<LoreShort> allowedList = new ArrayList<>();
        instance.ChatDebug("creating allowed list entries");
        for (LoreShort ls: loreList) {
            if (
                    (ls.factionIDList.contains(factionID) || factionID == 10001) //TODO remove 101 playerfaction
                            && (ls.entityTypeList.contains(entityType) || entityType == -1
                    )) {
                //this loreshort is allowed for given faction and entity type
                //add to list
                allowedList.add(ls);
            }
        }
        instance.ChatDebug("allowed list has " + allowedList.size() + " entries");
        if (allowedList.size() == 0) {
            return defaultLore;
        }
        Random r = new Random();
        LoreShort randLS = allowedList.get((r.nextInt(allowedList.size())));
        instance.ChatDebug("random loreshort from list was created.");
        return randLS.getText();
    }
    public String GetLoreText(int factionID) {
       String text = GetLoreText(factionID, -1);
       return text;
    }

}
