package Events;

import Explorer.DerelictController;
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
/**
 * Detects once the server or client has finished loading. Will initialize rest of mod after that
 * Can detect if mod is client or serverside (or local SP).
 * this class contains 2 loops
 * start loop runs until it detects either a server or a client state.
 * second loop will only run for a server state
 * second loop will start the entity load event loop which checks all entities existing and fires the events when they are loaded and unloaded
 */
public class ServerCheck {

    private final Mod instance;
    private EntityLoadEventLoop eventLoop;

    public ServerCheck(Mod instance) {
        this.instance = instance;
        instance.ChatDebug("server check loop created");
        StartLoop();
    }


    /**
     * loop that detects if the serverstate was created. will start second loop if server or abort if client
     */
    private void StartLoop() {
        //TODO check if runs for local SP
        new StarRunnable() {
            boolean stop = false;
            @Override
            public void run() {
                //instance.ChatDebug("Check loop iterating");
                if (GameServer.getServerState() != null) { //wait until server was created, otherwise everything is null
                    instance.ChatDebug("server state detected");
                    SecondLoop();
                    stop = true;
                }
                if (GameClient.getClientState() != null) {
                    instance.ChatDebug("client state detected");
                }
                if (GameClient.getClientState() != null && GameServer.getServerState() != null) {
                    instance.ChatDebug("Singleplayer");
                }
                if(stop) {
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
                    instance.ChatDebug("second loop was canceled");
                    cancel();
                }
                if (eventLoop == null) {
                    eventLoop = new EntityLoadEventLoop(instance);
                    instance.ChatDebug("creating eventloop");
                }
                if (instance.dc == null && eventLoop != null) {
                    instance.dc = new DerelictController(instance); //create the class that controls the derelict station stuff -> loot filling etc
                    instance.ChatDebug("creating derelict controller");
                }
                //ChatDebug("running from the server");
                //ModPlayground.broadcastMessage("im running");
                //instance.ChatDebug("second loop is running");
            }
        }.runTimer(25 * 5);
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
