#!/bin/sh
# Multiplayer check (LOADER=neoforge|fabric, max 15 min): starts the dev server (<loader>/run-net, the dev player "Dev" is
# an operator), waits for it, runs the client scene port/NetScene that joins it, stops the server and prints the checks
# and the errors of both. Logs: build-netserver-<loader>.log, build-netclient-<loader>.log
cd "$(dirname "$0")/.."
LOADER=${LOADER:-neoforge}
case $LOADER in
	neoforge) PROC='fml.modFolders=growling_man' ;;
	fabric) PROC="$(basename "$PWD")/(fabric/)?\.gradle/loom-cache" ;;
	*) echo "LOADER must be neoforge or fabric"; exit 2 ;;
esac
SLOG=build-netserver-$LOADER.log
CLOG=build-netclient-$LOADER.log
pkill -f "$PROC" 2>/dev/null
rm -rf $LOADER/run-net/world $SLOG $CLOG
mkdir -p $LOADER/run-net && echo "eula=true" > $LOADER/run-net/eula.txt
printf 'online-mode=false\nspawn-protection=0\ndifficulty=normal\n' > $LOADER/run-net/server.properties
uuid=$(python3 -c 'import hashlib,uuid;h=bytearray(hashlib.md5(b"OfflinePlayer:Dev").digest());h[6]=h[6]&0x0f|0x30;h[8]=h[8]&0x3f|0x80;print(uuid.UUID(bytes=bytes(h)))')
echo "[{\"uuid\":\"$uuid\",\"name\":\"Dev\",\"level\":4,\"bypassesPlayerLimit\":false}]" > $LOADER/run-net/ops.json
. tools/watchdog.sh
kill_dev() { pkill -f "$PROC" 2>/dev/null; }
./gradlew :$LOADER:runNetServer --console=plain < /dev/null > $SLOG 2>&1 &
wait_started $SLOG || { kill_dev; exit 1; }
run_watched $CLOG 600 ./gradlew :$LOADER:runNetClient --console=plain < /dev/null
pkill -f "$PROC" 2>/dev/null; sleep 3; pkill -9 -f "$PROC" 2>/dev/null
wait 2>/dev/null
grep -E '\[scene\] (CHECK|DONE|t=)' $CLOG | sed -E 's/^.*\[scene\] //'
echo "--- server errors"
grep -E '/(ERROR|FATAL)\]|Caused by|Exception|lost connection|kicked' $SLOG | grep -v 'server.properties' | sed -E 's/^\[[^]]*\] //' | cut -c1-300 | sort | uniq -c | sort -rn | head -20
echo "--- client errors"
grep -E '/(ERROR|FATAL)\]|Caused by|Exception|Disconnected' $CLOG | sed -E 's/^\[[^]]*\] //' | cut -c1-300 | sort | uniq -c | sort -rn | head -20
grep -q '\[scene\] DONE .* passed' $CLOG
