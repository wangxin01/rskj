package co.rsk.net.sync;

import javax.annotation.Nonnull;
import java.time.Duration;

public class DecidingSyncState implements SyncState {
    private Duration timeElapsed = Duration.ZERO;
    private SyncConfiguration syncConfiguration;
    private SyncEventsHandler syncEventsHandler;
    private SyncInformation syncInformation;
    private PeersInformation knownPeers;

    public DecidingSyncState(SyncConfiguration syncConfiguration, SyncEventsHandler syncEventsHandler, SyncInformation syncInformation, PeersInformation knownPeers) {
        this.syncConfiguration = syncConfiguration;
        this.syncEventsHandler = syncEventsHandler;
        this.syncInformation = syncInformation;
        this.knownPeers = knownPeers;
    }

    @Nonnull
    @Override
    public SyncStatesIds getId() {
        return SyncStatesIds.DECIDING;
    }

    @Override
    public void newPeerStatus() {
        if (knownPeers.count() >= syncConfiguration.getExpectedPeers()) {
            canStartSyncing();
        }
    }

    @Override
    public void tick(Duration duration) {
        timeElapsed = timeElapsed.plus(duration);
        if (knownPeers.countIf(s -> !s.isExpired(syncConfiguration.getExpirationTimePeerStatus())) > 0 &&
                timeElapsed.compareTo(syncConfiguration.getTimeoutWaitingPeers()) >= 0) {

            canStartSyncing();
        } else {
            knownPeers.cleanExpired(syncConfiguration.getExpirationTimePeerStatus());
        }
    }

    private void canStartSyncing() {
        knownPeers.getBestPeer()
                .filter(syncInformation::hasLowerDifficulty)
                .ifPresent(syncEventsHandler::startSyncing);
    }
}