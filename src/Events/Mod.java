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
public class Mod extends StarMod {
    public DerelictController dc;
    public static void main(String[] args) {
    }
    @Override
    public void onGameStart() {
        super.onGameStart();
        setModAuthor("IR0NSIGHT");
        setModName("testmode dedicated");
        setModVersion("0.1");
        this.forceEnable = true;
   //     this.flagEnabled(true); //last edit: added !!THIS WILL BREAK THE MOD
    }

    @Override
    public void onEnable() {
        ChatDebug("hi im testmod and im running on a dedicated server");

        ServerCheck checkloop = new ServerCheck(this);
        ChatDebug("new checkloop being created by Mod class");
    }
    public void ChatDebug(String s) {
        SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        String timeStamp = formatter.format(date);
        DebugFile.log(timeStamp + " -- TESTMOD FOR DEDICATED -- " + s);
    }

}
