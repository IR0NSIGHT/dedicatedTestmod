package Events;

import api.ModPlayground;
import api.common.GameClient;
import api.common.GameServer;
import api.utils.StarRunnable;
import org.schema.schine.network.server.ServerState;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * STARMADE MOD
 * CREATOR: IR0NSIGHT
 * DATE: 04.09.2020
 * TIME: 17:24
 */
public class ServerCheck {
    /**
     * this class contains 2 loops
     * start loop runs until it detects either a server or a client state.
     * second loop will only run for a server state
     * second loop will start the entity load event loop which checks all entities existing and fires the events when they are loaded and unloaded
     */
    private final Mod instance;
    private EntityLoadEventLoop eventLoop;
    public ServerCheck(Mod instance) {
        this.instance = instance;
        instance.ChatDebug("server check loop created");
        //DebugNow();
        StartLoop();
    }



    private void StartLoop() {
        new StarRunnable() {
            @Override
            public void run() {
                instance.ChatDebug("Check loop iterating");
                if (GameServer.getServerState() != null) { //wait until server was created, otherwise everything is null
                    if (GameClient.getClientState() == null) {
                        instance.ChatDebug("client state is null ---------> dedicated server");
                        SecondLoop();
                    } else {
                        instance.ChatDebug("client state is not null ----> client");

                    }
                    instance.ChatDebug("killing checkloop --------------");
                    cancel();
                }
                if (GameClient.getClientState() != null && GameServer.getServerState() == null) {
                    instance.ChatDebug("client state is not null ----> client");
                    cancel();
                }
            }

            @Override
            public void cancel() { //start the second loop when the first one is finished (bc server is done loading)
                instance.ChatDebug("first loop canceled");

                super.cancel();
            }
        }.runTimer(25);
    }
    private void SecondLoop() {
        new StarRunnable() {
            @Override
            public void run() {
                if (ServerState.isShutdown() || ServerState.isFlagShutdown()) {
                    cancel();
                }
                if (eventLoop == null) {
                    eventLoop = new EntityLoadEventLoop(instance);
                }
                //ChatDebug("running from the server");
            }
        }.runTimer(25 * 5);
    }

    private void ChatDebug(String s) {
        SimpleDateFormat formatter = new SimpleDateFormat ("dd-MM-yyyy 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        String timeStamp = formatter.format(date);
        ModPlayground.broadcastMessage(timeStamp + "-----" + s);
    }
    private void DebugNow() {
        if (GameServer.getServerState() == null) {
            instance.ChatDebug(("Server state is null"));
        } else {
            instance.ChatDebug("Server state exists");
        }
        if (GameClient.getClientState() == null) {
            instance.ChatDebug("Client state is null");
        } else {
            instance.ChatDebug("Client state exists");
        }
        if (ServerState.isFlagShutdown() || ServerState.isShutdown()) {
            instance.ChatDebug("serverstate is shutdown");
        }
        if (ServerState.isCreated()) {
            instance.ChatDebug("server state is created");
        }
    }
}
