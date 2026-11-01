"""The syncs of GrowlingManModVariables once per server tick (run after port_variables_common.py; idempotent).
MCreator syncs after every write of a variable: up to several times per tick for one player, every tick for the spawn
timers (to every player). The syncs asked for during a tick are sent at its end with the latest values; port/PerfScene
counts them (syncs)."""
import sys

p = sys.argv[1] if len(sys.argv) > 1 else 'common/src/main/java/net/voxla/growlingman/network/GrowlingManModVariables.java'
s = open(p).read()
if 'PENDING_PLAYERS' not in s:
    s = s.replace('public class GrowlingManModVariables {', '''public class GrowlingManModVariables {
	/** port/PerfScene: sync packets sent */
	public static int syncs;

	// MCreator syncs after every write of a variable, up to several times per tick for one player and every tick for
	// the spawn timers: the syncs asked for during a server tick are sent once, at its end, with the latest values
	private static final Set<ServerPlayer> PENDING_PLAYERS = new LinkedHashSet<>();
	private static final Set<ServerLevel> PENDING_WORLDS = new LinkedHashSet<>();
	private static MapVariables pendingMap;

	private static void flushSyncs(ServerTickEvent.Post event) {
		for (ServerPlayer player : PENDING_PLAYERS)
			if (!player.isRemoved() && !player.hasDisconnected()) {
				syncs++;
				Platform.INSTANCE.sendToPlayer(player, new PlayerVariablesSyncMessage(of(player)));
			}
		PENDING_PLAYERS.clear();
		for (ServerLevel level : PENDING_WORLDS) {
			syncs++;
			Platform.INSTANCE.sendToPlayersInDimension(level, new SavedDataSyncMessage(1, WorldVariables.get(level)));
		}
		PENDING_WORLDS.clear();
		if (pendingMap != null) {
			syncs++;
			Platform.INSTANCE.sendToAllPlayers(new SavedDataSyncMessage(0, pendingMap));
			pendingMap = null;
		}
	}
''', 1)
    for old, new in (('Platform.INSTANCE.sendToPlayersInDimension(level, new SavedDataSyncMessage(1, this));', 'PENDING_WORLDS.add(level);'),
                     ('Platform.INSTANCE.sendToAllPlayers(new SavedDataSyncMessage(0, this));', 'pendingMap = this;'),
                     ('Platform.INSTANCE.sendToPlayer(serverPlayer, new PlayerVariablesSyncMessage(this));', 'PENDING_PLAYERS.add(serverPlayer);')):
        assert old in s, old
        s = s.replace(old, new)
    reg = 'Platform.INSTANCE.registerClientbound(PlayerVariablesSyncMessage.TYPE, PlayerVariablesSyncMessage.STREAM_CODEC, PlayerVariablesSyncMessage::handleData);'
    assert reg in s
    s = s.replace(reg, reg + '\n\t\tGMBus.add(ServerTickEvent.Post.class, EventPriority.LOWEST, GrowlingManModVariables::flushSyncs);')
    s = s.replace('\nimport ', '\nimport java.util.LinkedHashSet;\nimport java.util.Set;\nimport net.voxla.growlingman.port.bus.EventPriority;\n'
                  'import net.voxla.growlingman.port.bus.GMBus;\nimport net.voxla.growlingman.port.bus.ServerTickEvent;\nimport ', 1)
    open(p, 'w').write(s)
    print('deferred syncs added')
