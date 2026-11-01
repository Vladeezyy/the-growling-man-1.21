#!/bin/sh
# Starts the dev server (LOADER=neoforge|fabric) on a fresh world, waits until it is up or has failed (max 10 min),
# kills it, prints a summary. Log: build-server-<loader>.log
cd "$(dirname "$0")/.."
LOADER=${LOADER:-neoforge}
case $LOADER in
	neoforge) PROC='fml.modFolders=growling_man' ;;
	fabric) PROC="$(basename "$PWD")/(fabric/)?\.gradle/loom-cache" ;;
	*) echo "LOADER must be neoforge or fabric"; exit 2 ;;
esac
LOG=build-server-$LOADER.log
pkill -f "$PROC" 2>/dev/null
mkdir -p $LOADER/run && echo "eula=true" > $LOADER/run/eula.txt
rm -rf $LOADER/run/world $LOG
./gradlew :$LOADER:runServer --console=plain > $LOG 2>&1 &
i=0
until grep -qE 'Done \(|crash report has been saved|Minecraft Crash Report|Failed to start|Registry loading errors|BUILD FAILED|FAILED TO BIND|Could not create the Java Virtual Machine' $LOG 2>/dev/null || [ $i -gt 300 ]; do sleep 2; i=$((i+1)); done
sleep 3
pkill -f "$PROC" 2>/dev/null; sleep 2; pkill -9 -f "$PROC" 2>/dev/null
wait 2>/dev/null
[ $i -gt 300 ] && echo "TIMEOUT (no Done / crash in 10 min)"
grep -E 'Done \(|Minecraft Crash Report|Failed to start|Registry loading errors|FAILED TO BIND' $LOG | head -3
echo "--- errors"
grep -E '/(ERROR|FATAL)\]|\[.*(ERROR|FATAL).*\]|Caused by|Exception:' $LOG | sed -E 's/^\[[^]]*\] //' | cut -c1-300 | sort | uniq -c | sort -rn | head -${1:-40}
