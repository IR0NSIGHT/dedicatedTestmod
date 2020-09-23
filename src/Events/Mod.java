package Events;

import Explorer.DerelictController;
import api.DebugFile;
import api.mod.StarMod;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * STARMADE MOD
 * CREATOR: IR0NSIGHT
 * DATE: 25.08.2020
 * TIME: 20:01
 */
/**
 *  core class of the complete mod.
 *  Starts first loop which checks for server creation
 *  is used to write to debug file
 */
public class Mod extends StarMod {
    /**
     * instance that houses the eventhandlers
     */
    public DerelictController dc;

    /**
     * empty method required to build jar correctly.
     * @param args
     */
    public static void main(String[] args) {
    }
    @Override
    /**
     * run when the game starts.
     */
    public void onGameStart() {
        super.onGameStart();
        setModAuthor("IR0NSIGHT");
        setModName("testmode dedicated");
        setModVersion("0.1");
        this.forceEnable = true;
    }

    @Override
    /**
     * run when the mod gets enabled (player joins on server or serverworld is loaded)
     */
    public void onEnable() {
        ChatDebug("hi im testmod and im running on a dedicated server");

        ServerCheck checkloop = new ServerCheck(this); //loop that waits till server is done loading until further stuff is done.
        ChatDebug("new checkloop being created by Mod class");
    }

    /**
     * Debug method to write standardized messages to starloader debug file
     * Adds a timestamp and the mods name to the log string
     * @param s String to write to debug file
     */
    public void ChatDebug(String s) {
        SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        String timeStamp = formatter.format(date);
        DebugFile.log(timeStamp + " -- TESTMOD FOR DEDICATED -- " + s);
    }

}
