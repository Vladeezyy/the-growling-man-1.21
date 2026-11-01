"""Decompiled (Vineflower) MCreator Forge 1.20.1 code -> the shape of MCreator's own source, so that tools/port_java.py
and tools/port_multiloader.py apply (idempotent; run first). Vineflower merges the scopes of MCreator's blocks, which
leaves a lambda capturing a reassigned local (`_setval`); here the player variable writes become plain statements.
- ((PlayerVariables) X.getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables())) -> GrowlingManModVariables.of(X)
- X.getCapability(PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> { ... }); -> { PlayerVariables _vars = of(X); ... }
- casts the decompiler adds on registry objects: (MobEffect)X.get() -> X.get() (1.21 wants the holder)
- Vineflower's 3-space indents -> tabs (MCreator's), which the regex rules of port_java.py expect
- nested-class imports (Mod.EventBusSubscriber, TickEvent.Phase, RenderGuiEvent.Pre, ...) -> the outer class, as in MCreator
"""
import re
import sys
from pathlib import Path

ROOT = Path(sys.argv[1] if len(sys.argv) > 1 else 'common/src/main/java')
V = 'GrowlingManModVariables'
ORELSE = re.compile(r'\(\(' + V + r'\.PlayerVariables\)\s*([\w.()]+?)\s*\.getCapability\(' + V + r'\.PLAYER_VARIABLES_CAPABILITY, null\)\s*\.orElse\(new ' + V + r'\.PlayerVariables\(\)\)\)')
IFPRESENT = re.compile(r'([\w.()]+?)\.getCapability\(' + V + r'\.PLAYER_VARIABLES_CAPABILITY, null\)\.ifPresent\(capability -> \{')


def ifpresent(s):
    out, i = [], 0
    for m in IFPRESENT.finditer(s):
        if m.start() < i:
            continue
        j = m.end()
        depth = 1
        while depth:
            depth += {'{': 1, '}': -1}.get(s[j], 0)
            j += 1
        # s[j-1] is the lambda's closing brace, then ");"
        tail = re.match(r'\s*\);', s[j:])
        body = s[m.end():j - 1].replace('capability.', '_vars.')
        # Vineflower renames the local the lambda captured (`_setvalx`) and keeps the old name inside the lambda
        local = re.findall(r'\b(?:double|boolean|String) (_setval\w*) =', s[max(0, m.start() - 2000):m.start()])
        if local and local[-1] != '_setval':
            body = re.sub(r'\b_setval\b', local[-1], body)
        out.append(s[i:m.start()])
        indent = re.match(r'\t*', s[s.rfind('\n', 0, m.start()) + 1:]).group(0)
        out.append('{\n' + indent + '\t%s.PlayerVariables _vars = %s.of(%s);' % (V, V, m.group(1)) + body + '}')
        i = j + tail.end()
    out.append(s[i:])
    return ''.join(out)


NESTED = re.compile(r'^import (net\.minecraftforge\.[a-z.]+)\.([A-Z]\w*)((?:\.[A-Z]\w*)+);\n', re.M)


def nested_imports(s):
    for m in list(NESTED.finditer(s)):
        pkg, outer, inner = m.group(1), m.group(2), m.group(3)[1:]
        simple = inner.rsplit('.', 1)[-1]
        outer_import = 'import %s.%s;\n' % (pkg, outer)
        s = s.replace(m.group(0), '' if outer_import in s else outer_import, 1)
        # the simple name where it is not already qualified (annotations included)
        s = re.sub(r'(?<![\w.])(@?)%s\b' % simple, lambda u: u.group(1) + outer + '.' + inner, s)
    return s


changed = 0
for f in ROOT.rglob('*.java'):
    s = f.read_text()
    n = re.sub(r'^((?:   )+)', lambda m: '\t' * (len(m.group(1)) // 3), s, flags=re.M)
    n = re.sub(r'getCapability\(\s*' + V + r'\.PLAYER_VARIABLES_CAPABILITY, null\s*\)', 'getCapability(%s.PLAYER_VARIABLES_CAPABILITY, null)' % V, n)
    n = ORELSE.sub(lambda m: '%s.of(%s)' % (V, m.group(1)), n)
    n = re.sub(r'\(MobEffect\)(GrowlingManModMobEffects\.\w+\.get\(\))', r'\1', n)
    n = ifpresent(n)
    n = nested_imports(n)
    if n != s:
        f.write_text(n)
        changed += 1
print('changed', changed)
