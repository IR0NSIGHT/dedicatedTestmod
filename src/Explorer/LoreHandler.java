package Explorer;

import Events.Mod;
import api.ModPlayground;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


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
    String path = "lore.json";
    public LoreHandler(Mod instance) {
        this.instance = instance;
        instance.ChatDebug("created LoreHandler");

        LoreShort ls = new LoreShort("hello i was created by lorehandler",new int[] {1,2,3},new int[] {5,6,7});
        ls.addEntityType(5);
        ls.addFactionID(5);
        instance.ChatDebug(ls.StringIt());
        String jsonS = LoreShort.SerializeToJson(ls); //turns java object into json string
        instance.ChatDebug(jsonS);
        LoreShort ls2 = LoreShort.UnserializeFromJSON(jsonS);
        instance.ChatDebug("unserialized loreshort: " + ls2.StringIt());

        instance.ChatDebug("trying to write new loreshort to file");
        WriteFile();
    }

    public void ReadFile() {
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader(path)
            );
            Gson gson = new Gson();
            instance.ChatDebug("trying to retrieve arraylist of loreshort from json file");
            LoreShort[] list = gson.fromJson(br, LoreShort[].class);
            loreList = new ArrayList<>();
            for (LoreShort ls: list) {
                loreList.add(ls); //add to list
                instance.ChatDebug("retrieved ls " + ls.StringIt());
                ModPlayground.broadcastMessage("ls file: " + ls.StringIt());
            }

        } catch (Exception e) {
            e.printStackTrace();
            instance.ChatDebug("exception during read file " + e.toString());
            ModPlayground.broadcastMessage("file reading failed!");
        }


    }
    public void WriteFile() {
   /*     instance.ChatDebug("trying to write file");
        LoreShort ls = new LoreShort("hello i was created by lorehandler",new int[] {1,2,3},new int[] {5,6,7});
        String json = LoreShort.SerializeToJson(ls);
        instance.ChatDebug("java object: " + ls.StringIt());
        instance.ChatDebug("json string: " + json);

        try {
            FileWriter writer = new FileWriter(path);
            writer.write(json);
            writer.close();
            instance.ChatDebug("no errors occured");
        } catch (Exception e) {
            e.printStackTrace();
            instance.ChatDebug("LoreHandler failed reading config file: " + e.toString());
        }
        instance.ChatDebug("finished writing file");
    */
    }

}
