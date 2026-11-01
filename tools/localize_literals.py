"""Hardcoded English Component.literal("...") texts -> Component.translatable("text.growling_man.port.<slug>").

Writes the English values to tools/lang/en_us.json (merged into the lang file by convert_resources.py). Idempotent."""
import codecs, json, os, re, sys

ROOT = 'src/main/java'
OUT = 'tools/lang/en_us.json'
lang = json.load(open(OUT)) if os.path.exists(OUT) else {}
by_value = {v: k for k, v in lang.items() if k.startswith('text.growling_man.port.')}


def slug(text):
    plain = re.sub(r'§.', '', text).lower()
    words = re.findall(r'[a-z0-9]+', plain)[:7]
    base = 'text.growling_man.port.' + ('_'.join(words) or 'text')
    key, n = base, 2
    while key in lang and lang[key] != text:
        key = '%s_%d' % (base, n); n += 1
    return key


def repl(m):
    raw = m.group(1)
    if not re.search(r'[A-Za-z]{2,}', raw) or not (' ' in raw or '\\u00A7' in raw): return m.group(0)
    text = codecs.decode(raw, 'unicode_escape')
    key = by_value.get(text) or slug(text)
    lang[key] = text; by_value[text] = key
    return 'Component.translatable("%s")' % key


n = 0
for dp, _, fs in os.walk(ROOT):
    for f in fs:
        if not f.endswith('.java'): continue
        p = os.path.join(dp, f); s = open(p, encoding='utf-8').read()
        t = re.sub(r'Component\.literal\("((?:[^"\\]|\\.)*)"\)', repl, s)
        if t != s:
            with open(p, 'w', encoding='utf-8') as fh: fh.write(t)
            n += 1
with open(OUT, 'w', encoding='utf-8') as fh:
    json.dump(dict(sorted(lang.items())), fh, indent=2, ensure_ascii=False); fh.write('\n')
print('files', n, 'keys', len(lang))
