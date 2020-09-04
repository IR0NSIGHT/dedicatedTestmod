import api.common.GameClient;
import api.common.GameServer;
import api.utils.StarRunnable;
import org.schema.schine.network.client.ClientState;
import org.schema.schine.network.server.ServerState;

/**
 * STARMADE MOD
 * CREATOR: IR0NSIGHT
 * DATE: 04.09.2020
 * TIME: 17:24
 */
public class ServerCheck {
    private Mod instance;
    private int timeSinceCreation = 0;
    public ServerCheck(Mod instance) {
        this.instance = instance;
        instance.ChatDebug("server check loop created");
        //DebugNow();
        StartLoop();
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
    private void StartLoop() {
        new StarRunnable() {
            @Override
            public void run() {
                timeSinceCreation ++;
                instance.ChatDebug("Check loop iterating");
                if (GameServer.getServerState() != null) { //wait until server was created, otherwise everything is null
                    if (GameClient.getClientState() == null) {
                        instance.ChatDebug("client state is null ---------> dedicated server");
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
        }.runTimer(25 * 1);
    }
}
