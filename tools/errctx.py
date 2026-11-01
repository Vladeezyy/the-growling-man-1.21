"""errctx.py LOG PATTERN [OFFSET]: for each javac error whose message matches PATTERN, print the source line at
line+OFFSET, then a histogram of method/identifier names found there."""
import re, sys, collections
log, pat = sys.argv[1], sys.argv[2]; off = int(sys.argv[3]) if len(sys.argv) > 3 else 0
cache = {}; hist = collections.Counter()
for l in open(log, encoding='utf-8', errors='replace'):
    m = re.match(r'\s*(/.+?\.java):(\d+): error: (.*)', l)
    if not m or not re.search(pat, m.group(3)): continue
    f, n = m.group(1), int(m.group(2))
    if f not in cache: cache[f] = open(f, encoding='utf-8').read().split('\n')
    src = cache[f][n - 1 + off].strip()
    mm = re.search(r'(\w+)\s*\(', src)
    hist[mm.group(1) if mm else src[:60]] += 1
for k, v in hist.most_common(60): print(v, k)
