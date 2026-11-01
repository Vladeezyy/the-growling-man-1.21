"""Performance rewrites with the same results (idempotent; run after port_fixups.py):
- world.getEntitiesOfClass(Player.class, box, e -> true) -> GMEntities.players(world, box): the level's player list tested
  against the box, instead of every entity section in boxes up to 600 blocks wide
- for (Entity x : world.getEntitiesOfClass(Entity.class, box, e -> true)...) { if (x instanceof Player ...) {...} } (the
  loop body is that one if, without else) -> the players only; the same with GrowlingManEntity -> a search of that class
"""
import re
import sys
from pathlib import Path

sys.path.insert(0, __file__.rsplit('/', 1)[0])
from port_java import match_paren

ROOT = Path(sys.argv[1] if len(sys.argv) > 1 else 'common/src/main/java/net/voxla/growlingman')
CALL = re.compile(r'(\b[\w.]+)\.getEntitiesOfClass\(Player\.class, ')
total = 0
for f in (ROOT / 'procedures').rglob('*.java'):
    s = f.read_text()
    out, i, n = [], 0, 0
    for m in CALL.finditer(s):
        if m.start() < i:
            continue
        open_at = m.end() - len('Player.class, ') - 1
        close = match_paren(s, open_at)
        args = s[m.end():close]
        if not args.rstrip().endswith(', e -> true'):
            continue
        box = args.rstrip()[:-len(', e -> true')]
        out.append(s[i:m.start()])
        out.append('GMEntities.players(%s, %s)' % (m.group(1), box))
        i = close + 1
        n += 1
    out.append(s[i:])
    t = ''.join(out)
    if n:
        if 'import net.voxla.growlingman.port.GMEntities;' not in t:
            t = t.replace('package net.voxla.growlingman.procedures;\n', 'package net.voxla.growlingman.procedures;\n\nimport net.voxla.growlingman.port.GMEntities;', 1)
        f.write_text(t)
        total += n
LOOP = re.compile(r'for \(Entity (\w+) : (\w+)\.getEntitiesOfClass\(Entity\.class, ')
loops = 0
for f in (ROOT / 'procedures').rglob('*.java'):
    s = f.read_text()
    changed = False
    for m in reversed(list(LOOP.finditer(s))):
        var, world = m.group(1), m.group(2)
        open_at = m.end() - len('Entity.class, ') - 1
        close = match_paren(s, open_at)
        args = s[m.end():close].rstrip()
        if not args.endswith(', e -> true'):
            continue
        box = args[:-len(', e -> true')]
        brace = s.index('{', close)
        end = match_paren(s, brace)
        body = s[brace + 1:end].strip()
        kind = None
        for cls, rep in (('Player', 'GMEntities.playersAsEntities(%s, %s)'), ('GrowlingManEntity', '%s.getEntitiesOfClass(GrowlingManEntity.class, %s, e -> true)')):
            if body.startswith('if (%s instanceof %s' % (var, cls)):
                cond_end = match_paren(body, 3)
                block = body.index('{', cond_end)
                block_end = match_paren(body, block)
                if body[block_end + 1:].strip() == '':
                    kind = rep % (world, box)
        if kind is None:
            continue
        s = s[:m.start()] + 'for (Entity %s : %s' % (var, kind) + s[close + 1:]
        changed = True
        loops += 1
    if changed:
        if 'import net.voxla.growlingman.port.GMEntities;' not in s:
            s = s.replace('package net.voxla.growlingman.procedures;\n', 'package net.voxla.growlingman.procedures;\n\nimport net.voxla.growlingman.port.GMEntities;', 1)
        f.write_text(s)
print('players searches:', total, '- entity loops:', loops)
