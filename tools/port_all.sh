#!/bin/sh
# Re-runs the Java port of the decompiled 1.0.1 sources from the first commit (the files written by hand are kept):
# port_decompiled.py -> port_java.py -> port_multiloader.py
set -e
C=common/src/main/java/net/voxla/growlingman
KEEP=" GrowlingManMod.java network/ClearMindKeyMessage.java network/GrowlingManModVariables.java configuration/GrowlingManConfigConfiguration.java "
FIRST=$(git rev-list --max-parents=0 HEAD)
for f in $(git ls-tree -r --name-only $FIRST -- $C | sed "s|$C/||"); do
  case "$KEEP" in *" $f "*) ;; *) git show $FIRST:$C/$f > $C/$f ;; esac
done
rm -f $C/init/GrowlingManModConfigs.java
python3 -I tools/port_decompiled.py
python3 -I tools/port_java.py common/src/main/java
python3 -I tools/port_multiloader.py
python3 -I tools/port_fixups.py
python3 -I tools/port_perf.py
