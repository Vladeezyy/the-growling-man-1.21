#!/bin/sh
# Runs port/DevScene (LOADER=neoforge|fabric, max 15 min): a new world, the Growling Man, the HUD overlays, the mark in the
# inventory, the clear mind key through the server, the config file, translations.
# Prints the checks and the errors; screenshots in <loader>/run/screenshots/scene_*.png. Log: build-scene-<loader>.log
cd "$(dirname "$0")/.."
LOADER=${LOADER:-neoforge}
case $LOADER in
	neoforge) PROC='fml.modFolders=growling_man' ;;
	fabric) PROC="$(basename "$PWD")/(fabric/)?\.gradle/loom-cache" ;;
	*) echo "LOADER must be neoforge or fabric"; exit 2 ;;
esac
LOG=build-scene-$LOADER.log
rm -rf $LOADER/run/saves/gm_scene $LOADER/run/config/growling_man_config.toml $LOG
find $LOADER/run/screenshots -name 'scene_*.png' -delete 2>/dev/null
# the hang watchdog of the knowledge base (testing/hang-watchdog.md): stops the run on a crash, a hang or 15 min
. tools/watchdog.sh
kill_dev() { pkill -f "$PROC" 2>/dev/null; }
run_watched $LOG 900 ./gradlew :$LOADER:runScene --console=plain "$@" < /dev/null
kill_dev
grep -E '\[scene\] (CHECK|DONE|spawned)' $LOG | sed -E 's/^.*\[scene\] //'
echo "--- errors"
grep -E '/(ERROR|FATAL)\]|\[.*(ERROR|FATAL).*\]|Caused by|Exception' $LOG | sed -E 's/^\[[^]]*\] //' | cut -c1-250 | sort | uniq -c | sort -rn | head -25
grep -q '\[scene\] DONE .* passed' $LOG
