#!/bin/sh
# Runs port/DevScene (LOADER=neoforge|fabric, max 15 min): a new world, every entity, the creative tab, the Lurker book.
# Prints the checks and the errors; screenshots in <loader>/run/screenshots/scene_*.png. Log: build-scene-<loader>.log
cd "$(dirname "$0")/.."
LOADER=${LOADER:-neoforge}
LOG=build-scene-$LOADER.log
rm -rf $LOADER/run/saves/ml_scene $LOG
find $LOADER/run/screenshots -name 'scene_*.png' -delete 2>/dev/null
perl -e 'alarm 900; exec @ARGV' ./gradlew :$LOADER:runScene --console=plain "$@" > $LOG 2>&1
code=$?
pkill -f 'build/moddev/sceneRunProgramArgs' 2>/dev/null; pkill -f "growling_man.scene" 2>/dev/null
echo "exit $code"
grep -E '\[scene\] (CHECK|DONE|spawned|player)' $LOG | sed -E 's/^.*\[scene\] //'
echo "--- errors"
grep -E '/(ERROR|FATAL)\]|\[.*(ERROR|FATAL).*\]|Caused by|Exception' $LOG | sed -E 's/^\[[^]]*\] //' | cut -c1-250 | sort | uniq -c | sort -rn | head -25
