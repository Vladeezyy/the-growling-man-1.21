#!/bin/sh
# port/PerfScene (LOADER=neoforge|fabric, max 20 min): server tick and the natural spawn checks. Log: build-perf-<loader>.log
cd "$(dirname "$0")/.."
LOADER=${LOADER:-neoforge}
LOG=build-perf-$LOADER.log
rm -rf $LOADER/run/saves/ml_perf $LOG $LOADER/run/perf.jfr
perl -e 'alarm 1200; exec @ARGV' ./gradlew :$LOADER:runPerf --console=plain "$@" > $LOG 2>&1
code=$?
pkill -f 'build/moddev/perfRunProgramArgs' 2>/dev/null; pkill -f "growling_man.perf" 2>/dev/null
echo "exit $code"
grep -E '\[perf\] (PERF|DONE|[0-9]+ Lurker)' $LOG | sed -E 's/^.*\[perf\] //'
echo "--- errors"
grep -E '/(ERROR|FATAL)\]|\[.*(ERROR|FATAL).*\]|Caused by|Exception' $LOG | sed -E 's/^\[[^]]*\] //' | cut -c1-250 | sort | uniq -c | sort -rn | head -15
