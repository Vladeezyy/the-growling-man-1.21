#!/bin/sh
# port/PerfScene (LOADER=neoforge|fabric, max 20 min): MSPT, FPS, sync calls and the mod's listener time, windows with
# the mod's listeners on and off in the same world. -Pjfr records <loader>/run/perf.jfr. Log: build-perf-<loader>.log
cd "$(dirname "$0")/.."
LOADER=${LOADER:-neoforge}
case $LOADER in
	neoforge) PROC='fml.modFolders=growling_man' ;;
	fabric) PROC="$(basename "$PWD")/(fabric/)?\.gradle/loom-cache" ;;
	*) echo "LOADER must be neoforge or fabric"; exit 2 ;;
esac
LOG=build-perf-$LOADER.log
rm -rf $LOADER/run/saves/gm_perf $LOG $LOADER/run/perf.jfr
. tools/watchdog.sh
kill_dev() { pkill -f "$PROC" 2>/dev/null; }
run_watched $LOG 1200 ./gradlew :$LOADER:runPerf --console=plain "$@" < /dev/null
kill_dev
grep -E '\[perf\] ' $LOG | sed -E 's/^.*\[perf\] //'
echo "--- errors"
grep -E '/(ERROR|FATAL)\]|\[.*(ERROR|FATAL).*\]|Caused by|Exception' $LOG | sed -E 's/^\[[^]]*\] //' | cut -c1-250 | sort | uniq -c | sort -rn | head -15
