#!/bin/sh
# Summarise javac errors from build-compile.log: count, top messages, top symbols.
L=${1:-build-compile.log}
echo "errors: $(grep -c ': error:' $L)  files: $(grep ': error:' $L | cut -d: -f1 | sort -u | wc -l)"
grep ': error:' $L | sed -E 's/.*error: //' | sed -E 's/(cannot find symbol|incompatible types|method [a-zA-Z_]+ in class).*/\1/' | sort | uniq -c | sort -rn | head -25
echo "--- symbols"
grep -A2 'cannot find symbol' $L | grep 'symbol:' | sort | uniq -c | sort -rn | head -60
