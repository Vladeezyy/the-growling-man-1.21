"""MCreator Forge 1.20.1 variables -> the loader-neutral GrowlingManModVariables (Platform attachments and payloads).
Usage: python3 -I tools/port_variables_common.py <original GrowlingManModVariables.java> <template .java> <out .java>
The template is the common variables class of the Midnight Lurker port; its fields are replaced with the original's
(WorldVariables, MapVariables, PlayerVariables, with defaults) and its clonePlayer with the original's copy rules.
"""
import re
import sys

src, tpl, out = sys.argv[1:4]
s = open(src).read()
t = open(tpl).read()


def body(text, name):
    i = text.index('public static class %s' % name)
    j = text.index('{', i) + 1
    depth = 1
    k = j
    while depth:
        depth += {'{': 1, '}': -1}.get(text[k], 0)
        k += 1
    return i, j, k - 1


def fields(name):
    i, j, k = body(s, name)
    return re.findall(r'public (double|boolean|String) (\w+) = ([^;]+);', s[j:k])


T = {'double': 'Double', 'boolean': 'Boolean', 'String': 'String'}
clone = s[s.index('void clonePlayer'):s.index('void onPlayerLoggedIn(')]
alive_at = clone.index('if (!event.isWasDeath())')
copy = re.findall(r'clone\.(\w+) = original\.\w+;', clone[:alive_at])
alive = re.findall(r'clone\.(\w+) = original\.\w+;', clone[alive_at:])

t = t.replace('net.mcreator.midnightlurker', 'net.voxla.growlingman').replace('MidnightlurkerMod', 'GrowlingManMod')
t = t.replace('midnightlurker_worldvars', 'growling_man_worldvars').replace('midnightlurker_mapvars', 'growling_man_mapvars')
t = t.replace('as in 3.3.7', 'as in 1.0.1')

for name, ind in (('WorldVariables', '\t\t'), ('MapVariables', '\t\t'), ('PlayerVariables', '\t\t')):
    fs = fields(name)
    i, j, k = body(t, name)
    b = t[j:k]
    b = re.sub(r'\n\t\tpublic (double|boolean|String) \w+ = [^;]+;', '', b)
    b = re.sub(r'\n\t\t\t(nbt\.put\w+\("\w+", \w+\);|\w+ = nbt\.get\w+\("\w+"\);)', '', b)
    decl = ''.join('\n%spublic %s %s = %s;' % (ind, ty, n, v) for ty, n, v in fs)
    reads = ''.join('\n\t\t\t%s = nbt.get%s("%s");' % (n, T[ty], n) for ty, n, v in fs)
    writes = ''.join('\n\t\t\tnbt.put%s("%s", %s);' % (T[ty], n, n) for ty, n, v in fs)
    if name == 'PlayerVariables':
        b = decl + b
        b = b.replace('CompoundTag nbt = new CompoundTag();', 'CompoundTag nbt = new CompoundTag();' + writes, 1)
        b = re.sub(r'(public void deserializeNBT\([^)]*\) \{)', lambda m: m.group(1) + reads, b, 1)
    else:
        b = re.sub(r'(public static final String DATA_NAME = "\w+";)', lambda m: m.group(1) + '\n' + decl, b, 1)
        b = re.sub(r'(public void read\(CompoundTag nbt, HolderLookup\.Provider lookupProvider\) \{)', lambda m: m.group(1) + reads, b, 1)
        b = re.sub(r'(public CompoundTag save\(CompoundTag nbt, HolderLookup\.Provider lookupProvider\) \{)', lambda m: m.group(1) + writes, b, 1)
    t = t[:j] + b + t[k:]

i = t.index('PlayerVariables clone = new PlayerVariables();')
j = t.index('Platform.INSTANCE.setPlayerVariables(event.getEntity(), clone);')
lines = ''.join('\n\t\t\tclone.%s = original.%s;' % (n, n) for n in copy)
lines += '\n\t\t\tif (!event.isWasDeath()) {' + ''.join('\n\t\t\t\tclone.%s = original.%s;' % (n, n) for n in alive) + '\n\t\t\t}\n\t\t\t'
t = t[:i] + 'PlayerVariables clone = new PlayerVariables();' + lines + t[j:]
open(out, 'w').write(t)
print('world %d, map %d, player %d; copied %d, alive %d' % (len(fields('WorldVariables')), len(fields('MapVariables')), len(fields('PlayerVariables')), len(copy), len(alive)))
