"""errlist.py [LOG] [N]: unique javac errors as 'file:line message symbol'."""
import re, sys
log = sys.argv[1] if len(sys.argv) > 1 else 'build-compile.log'; n = int(sys.argv[2]) if len(sys.argv) > 2 else 120
seen = set(); out = []
lines = open(log, errors='replace').read().split('\n')
for i, l in enumerate(lines):
    m = re.match(r'\s*/.+/growling_man/(.+?\.java):(\d+): error: (.*)', l)
    if not m: continue
    key = (m.group(1), m.group(2), m.group(3))
    if key in seen: continue
    seen.add(key); sym = ''
    for j in range(i + 1, min(i + 5, len(lines))):
        if 'symbol:' in lines[j]: sym = lines[j].strip(); break
    out.append('%s:%s %s %s' % (m.group(1), m.group(2), m.group(3)[:120], sym))
print(len(out), 'unique errors')
print('\n'.join(out[:n]))
