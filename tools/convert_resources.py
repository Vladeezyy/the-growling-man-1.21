"""Convert The Deep Void 1.20.1 (Forge) resources to the 1.21.1 data / resource pack format.

Usage: python3 -I tools/convert_resources.py <upstream src/main/resources> <out src/main/resources>

Rewrites out/assets and out/data from scratch (META-INF, pack.mcmeta, logo and mixin configs are left alone), then
copies tools/overlay on top (hand-written 1.21 data: enchantments, jukebox songs, enum extensions, extra tags).
"""
import json, os, re, shutil, sys

SRC, OUT = sys.argv[1], sys.argv[2]
HERE = os.path.dirname(os.path.abspath(__file__))
OVERLAY = os.path.join(HERE, 'overlay')

FOLDERS = {'advancements': 'advancement', 'loot_tables': 'loot_table', 'recipes': 'recipe', 'structures': 'structure',
           'predicates': 'predicate', 'item_modifiers': 'item_modifier', 'functions': 'function'}
TAG_FOLDERS = {'blocks': 'block', 'items': 'item', 'entity_types': 'entity_type', 'fluids': 'fluid',
               'game_events': 'game_event', 'functions': 'function'}
C_TAGS = {'c:tools/bows': 'c:tools/bow', 'c:tools/shields': 'c:tools/shield', 'c:tools/shield': 'c:tools/shield'}
stats = {}
# Unused MCreator leftovers that never loaded (element rotations other than ±22.5/45, upper-case file name)
UNUSED_ASSETS = {'assets/growling_man/models/custom/%s.json' % m for m in ('flaregun', 'falsehydrahead', 'bone_helix', 'bone_helix_new')} | {
    'assets/growling_man/models/block/pusSackNew.json'}


def count(k):
    stats[k] = stats.get(k, 0) + 1


def ns_id(v):
    return v if ':' in v else 'minecraft:' + v


# ------------------------------------------------------------------ shared predicates
def item_predicate(p):
    """1.20.1 ItemPredicate -> 1.20.5+ ItemPredicate (items holder set, sub-predicates for nbt / enchantments)."""
    if not isinstance(p, dict): return p
    p = dict(p)
    if 'item' in p: p['items'] = [p.pop('item')]
    if 'tag' in p: p['items'] = '#' + p.pop('tag')
    preds = p.pop('predicates', {})
    if 'nbt' in p: preds['minecraft:custom_data'] = p.pop('nbt')
    for key, comp in (('enchantments', 'minecraft:enchantments'), ('stored_enchantments', 'minecraft:stored_enchantments')):
        if key in p:
            lst = []
            for e in p.pop(key):
                e = dict(e)
                if 'enchantment' in e: e['enchantments'] = ns_id(e.pop('enchantment'))
                lst.append(e)
            preds[comp] = lst
    if 'potion' in p: preds['minecraft:potion_contents'] = ns_id(p.pop('potion'))
    if 'durability' in p: preds['minecraft:damage'] = {'durability': p.pop('durability')}
    if preds: p['predicates'] = preds
    return p


def location_predicate(p):
    if not isinstance(p, dict): return p
    p = dict(p)
    if 'biome' in p: p['biomes'] = p.pop('biome')
    if 'structure' in p: p['structures'] = p.pop('structure')
    if isinstance(p.get('block'), dict):
        b = dict(p['block'])
        if 'tag' in b: b['blocks'] = '#' + b.pop('tag')
        if 'nbt' in b: b['nbt'] = b['nbt']
        p['block'] = b
    if isinstance(p.get('fluid'), dict):
        f = dict(p['fluid'])
        if 'fluid' in f: f['fluids'] = [f.pop('fluid')]
        if 'tag' in f: f['fluids'] = '#' + f.pop('tag')
        p['fluid'] = f
    return p


def entity_predicate(p):
    if not isinstance(p, dict): return p
    p = dict(p)
    if isinstance(p.get('equipment'), dict):
        p['equipment'] = {k: item_predicate(v) for k, v in p['equipment'].items()}
    for k in ('location', 'stepping_on'):
        if k in p: p[k] = location_predicate(p[k])
    for k in ('vehicle', 'passenger', 'targeted_entity'):
        if k in p: p[k] = entity_predicate(p[k])
    if isinstance(p.get('type_specific'), dict) and p['type_specific'].get('type') == 'player':
        pass
    return p


def loot_condition(c):
    if isinstance(c, list): return [loot_condition(x) for x in c]
    if not isinstance(c, dict): return c
    c = dict(c)
    t = ns_id(c.get('condition', ''))
    if t == 'minecraft:match_tool' and 'predicate' in c: c['predicate'] = item_predicate(c['predicate'])
    if t == 'minecraft:entity_properties' and 'predicate' in c: c['predicate'] = entity_predicate(c['predicate'])
    if t == 'minecraft:location_check' and 'predicate' in c: c['predicate'] = location_predicate(c['predicate'])
    if t in ('minecraft:inverted',) and 'term' in c: c['term'] = loot_condition(c['term'])
    if t in ('minecraft:any_of', 'minecraft:all_of', 'minecraft:alternative') and 'terms' in c:
        c['terms'] = loot_condition(c['terms'])
    if t == 'minecraft:alternative': c['condition'] = 'minecraft:any_of'
    if t == 'minecraft:random_chance_with_looting':
        chance, mult = c.pop('chance'), c.pop('looting_multiplier')
        c = {'condition': 'minecraft:random_chance_with_enchanted_bonus', 'enchantment': 'minecraft:looting',
             'unenchanted_chance': chance,
             'enchanted_chance': {'type': 'minecraft:linear', 'base': chance + mult, 'per_level_above_first': mult}}
    return c


# ------------------------------------------------------------------ loot tables
def loot_function(f):
    f = dict(f)
    t = ns_id(f.get('function', ''))
    if t == 'minecraft:set_nbt':
        f['function'] = 'minecraft:set_custom_data'; count('loot set_nbt')
    if t == 'minecraft:copy_nbt':
        f['function'] = 'minecraft:copy_custom_data'; count('loot copy_nbt')
    if t == 'minecraft:enchant_with_levels':
        if f.pop('treasure', False): f['options'] = '#minecraft:on_random_loot'
        count('loot enchant_with_levels')
    if t == 'minecraft:enchant_randomly' and 'enchantments' in f:
        f['options'] = [ns_id(e) for e in f.pop('enchantments')]
    if t == 'minecraft:looting_enchant':
        f['function'] = 'minecraft:enchanted_count_increase'; f['enchantment'] = 'minecraft:looting'
    if 'conditions' in f: f['conditions'] = loot_condition(f['conditions'])
    return f


def loot_entry(e):
    e = dict(e)
    t = ns_id(e.get('type', ''))
    if t == 'minecraft:loot_table' and 'name' in e: e['value'] = e.pop('name')
    if 'functions' in e: e['functions'] = [loot_function(x) for x in e['functions']]
    if 'conditions' in e: e['conditions'] = loot_condition(e['conditions'])
    if 'children' in e: e['children'] = [loot_entry(x) for x in e['children']]
    return e


def loot_table(j):
    for pool in j.get('pools', []):
        pool['entries'] = [loot_entry(e) for e in pool.get('entries', [])]
        if 'conditions' in pool: pool['conditions'] = loot_condition(pool['conditions'])
        if 'functions' in pool: pool['functions'] = [loot_function(x) for x in pool['functions']]
    if 'functions' in j: j['functions'] = [loot_function(x) for x in j['functions']]
    return j


# ------------------------------------------------------------------ recipes
def stack(v):
    if isinstance(v, str): return {'id': v}
    v = dict(v)
    if 'item' in v: v['id'] = v.pop('item')
    if 'nbt' in v:
        nbt = v.pop('nbt'); count('recipe result nbt (dropped: ' + str(nbt)[:40] + ')')
    return v


def recipe(j):
    t = ns_id(j.get('type', ''))
    if t in ('minecraft:smelting', 'minecraft:blasting', 'minecraft:smoking', 'minecraft:campfire_cooking'):
        j['result'] = stack(j['result'])
    elif t == 'minecraft:stonecutting':
        r = stack(j['result'])
        if 'count' in j: r['count'] = j.pop('count')
        j['result'] = r
    elif 'result' in j and isinstance(j['result'], (dict, str)):
        j['result'] = stack(j['result'])
    if 'output' in j: j['output'] = stack(j['output'])
    return j


# ------------------------------------------------------------------ advancements
def advancement(j):
    d = j.get('display')
    if isinstance(d, dict) and isinstance(d.get('icon'), dict): d['icon'] = stack(d['icon'])
    for crit in j.get('criteria', {}).values():
        cond = crit.get('conditions')
        if not isinstance(cond, dict): continue
        trig = ns_id(crit.get('trigger', ''))
        if 'items' in cond and isinstance(cond['items'], list): cond['items'] = [item_predicate(x) for x in cond['items']]
        if 'item' in cond and isinstance(cond['item'], dict): cond['item'] = item_predicate(cond['item'])
        if 'player' in cond:
            cond['player'] = loot_condition(cond['player']) if isinstance(cond['player'], list) else entity_predicate(cond['player'])
        if 'entity' in cond:
            cond['entity'] = loot_condition(cond['entity']) if isinstance(cond['entity'], list) else entity_predicate(cond['entity'])
        if 'location' in cond:
            cond['location'] = loot_condition(cond['location']) if isinstance(cond['location'], list) else location_predicate(cond['location'])
    return j


# ------------------------------------------------------------------ misc data
def biome_modifier(j):
    s = json.dumps(j)
    s = s.replace('"forge:', '"neoforge:')
    return json.loads(s)


def trim_material(j):
    o = j.get('override_armor_materials')
    if isinstance(o, dict): j['override_armor_materials'] = {ns_id(k): v for k, v in o.items()}
    return j


def fix_tag_values(j):
    vals = []
    for v in j.get('values', []):
        if isinstance(v, str):
            v = v.replace('#forge:', '#c:')
            v = C_TAGS.get(v.lstrip('#'), v.lstrip('#')) if v.lstrip('#') in C_TAGS else v
            if v.startswith('#') and v[1:] in C_TAGS: v = '#' + C_TAGS[v[1:]]
        vals.append(v)
    j['values'] = vals
    return j


PROVIDER_KEYS = {'min_inclusive', 'max_inclusive', 'max_exclusive', 'source', 'mean', 'deviation', 'plateau', 'inner', 'distribution', 'min', 'max'}


def flatten_providers(j):
    """1.20.5 dropped the "value" wrapper of int / float providers: {"type": t, "value": {...}} -> {"type": t, ...}."""
    if isinstance(j, list): return [flatten_providers(x) for x in j]
    if not isinstance(j, dict): return j
    j = {k: flatten_providers(v) for k, v in j.items()}
    if set(j) == {'type', 'value'} and isinstance(j['value'], dict) and j['value'] and set(j['value']) <= PROVIDER_KEYS:
        count('flattened provider')
        return dict({'type': j['type']}, **j['value'])
    return j


ENTITY_IDS = None


def entity_ids():
    global ENTITY_IDS
    if ENTITY_IDS is None:
        reg = open(os.path.join(SRC, '..', 'java', 'net', 'mcreator', 'growling_man', 'init', 'GrowlingManModEntities.java')).read()
        ENTITY_IDS = {'growling_man:' + i for i in re.findall(r'register\("(\w+)"', reg)}
    return ENTITY_IDS


def prune_stale_recipes():
    """Recipes that use or make items the 1.99 code no longer registers (1.20.1 also only logged and skipped them)."""
    java = os.path.join(SRC, '..', 'java', 'net', 'mcreator', 'growling_man', 'init')
    items = set(re.findall(r'register\("(\w+)"', open(os.path.join(java, 'GrowlingManModItems.java')).read()))
    blocks = dict(re.findall(r'(\w+) = REGISTRY\.register\("(\w+)"', open(os.path.join(java, 'GrowlingManModBlocks.java')).read()))
    items |= {blocks[b] for b in re.findall(r'(?:block|doubleBlock)\(GrowlingManModBlocks\.(\w+)', open(os.path.join(java, 'GrowlingManModItems.java')).read()) if b in blocks}
    d = os.path.join(OUT, 'data', 'growling_man', 'recipe'); n = 0
    for f in os.listdir(d):
        txt = open(os.path.join(d, f)).read()
        refs = re.findall(r'"(?:item|id)"\s*:\s*"growling_man:(\w+)"', txt)
        gone = sorted({r for r in refs if r not in items})
        if gone:
            os.remove(os.path.join(d, f)); n += 1
            print('SKIPPED recipe', f, '(removed items:', ', '.join(gone) + ')')
    count('stale recipes skipped') if n else None


def prune_stale_worldgen():
    """Drop configured features whose feature type the 1.99 code no longer registers, and everything that uses them."""
    java = os.path.join(SRC, '..', 'java', 'net', 'mcreator', 'growling_man')
    features = os.path.join(java, 'init', 'GrowlingManModFeatures.java')
    if not os.path.exists(features):
        return
    registered = set(re.findall(r'register\("(\w+)"', open(features).read()))
    registered.add('structure_feature')
    wg = os.path.join(OUT, 'data', 'growling_man', 'worldgen')
    dead = set()
    for f in os.listdir(os.path.join(wg, 'configured_feature')):
        t = json.load(open(os.path.join(wg, 'configured_feature', f))).get('type', '')
        if t.startswith('growling_man:') and t.split(':')[1] not in registered:
            dead.add('growling_man:' + f[:-5]); os.remove(os.path.join(wg, 'configured_feature', f))
            print('SKIPPED configured_feature', f, '(feature type', t, 'is no longer registered)')
    dead_placed = set()
    for f in os.listdir(os.path.join(wg, 'placed_feature')):
        q = os.path.join(wg, 'placed_feature', f)
        if json.load(open(q)).get('feature') in dead:
            dead_placed.add('growling_man:' + f[:-5]); os.remove(q); print('SKIPPED placed_feature', f)
    # 1.20.5+ lets features touch only the neighbouring chunks; the freeze check of lakes of water-tagged fluids reads
    # biomes up to 19 blocks past the origin. Centre such lakes on the origin (as before 1.18): last modifier, so the
    # placement checks still run at the original spot.
    water = set(json.load(open(os.path.join(OUT, 'data', 'minecraft', 'tags', 'fluid', 'water.json')))['values'])
    for f in os.listdir(os.path.join(wg, 'placed_feature')):
        q = os.path.join(wg, 'placed_feature', f); j = json.load(open(q))
        cf = os.path.join(wg, 'configured_feature', j['feature'].split(':')[1] + '.json') if j['feature'].startswith('growling_man:') else None
        if not cf or not os.path.exists(cf): continue
        c = json.load(open(cf))
        if c.get('type') == 'minecraft:lake' and c['config']['fluid'].get('state', {}).get('Name') in water:
            j['placement'].append({'type': 'minecraft:random_offset', 'xz_spread': -8, 'y_spread': 0})
            json.dump(j, open(q, 'w'), indent=2); count('lake centred')
    bm = os.path.join(OUT, 'data', 'growling_man', 'neoforge', 'biome_modifier')
    for f in os.listdir(bm):
        q = os.path.join(bm, f); txt = open(q).read()
        if any('"%s"' % d in txt for d in dead_placed):
            os.remove(q); print('SKIPPED biome_modifier', f)


# ------------------------------------------------------------------ driver
def target_path(rel):
    parts = rel.split('/')
    if parts[0] != 'data' or len(parts) < 3: return rel
    ns = parts[1]
    if ns == 'forge': ns = 'c'
    rest = parts[2:]
    if rest[0] == 'forge' and len(rest) > 1 and rest[1] == 'biome_modifier': rest = ['neoforge'] + rest[1:]
    if rest[0] in FOLDERS: rest = [FOLDERS[rest[0]]] + rest[1:]
    if rest[0] == 'tags' and len(rest) > 1 and rest[1] in TAG_FOLDERS: rest = ['tags', TAG_FOLDERS[rest[1]]] + rest[2:]
    if rest[0] == 'tags':
        joined = ns + ':' + '/'.join(rest[2:])[:-5]
        if joined in C_TAGS:
            new = C_TAGS[joined].split(':', 1)[1]
            rest = rest[:2] + (new + '.json').split('/')
    return '/'.join(['data', ns] + rest)


def convert_json(rel_out, j):
    j = flatten_providers(j)
    p = rel_out.split('/')
    kind = p[2] if len(p) > 2 else ''
    if kind == 'loot_table': return loot_table(j)
    if kind == 'recipe': return recipe(j)
    if kind == 'advancement': return advancement(j)
    if kind == 'neoforge': return biome_modifier(j)
    if kind == 'trim_material': return trim_material(j)
    if kind == 'tags': return fix_tag_values(j)
    return j


def main():
    for d in ('assets', 'data'):
        shutil.rmtree(os.path.join(OUT, d), ignore_errors=True)
    n = 0
    for dp, _, fs in os.walk(SRC):
        for f in fs:
            src = os.path.join(dp, f)
            rel = os.path.relpath(src, SRC).replace(os.sep, '/')
            if not (rel.startswith('assets/') or rel.startswith('data/')): continue
            if rel in UNUSED_ASSETS:
                print('SKIPPED', rel, '(unused model the game cannot load: invalid rotation or file name)'); continue
            out_rel = target_path(rel)
            dst = os.path.join(OUT, out_rel)
            os.makedirs(os.path.dirname(dst), exist_ok=True)
            if f.endswith('.json') and rel.startswith('data/'):
                if '/biome_modifier/' in out_rel:
                    txt = open(src, encoding='utf-8').read()
                    missing = [t for t in re.findall(r'"type"\s*:\s*"(growling_man:\w+)"', txt) if t not in entity_ids()]
                    if missing:
                        print('SKIPPED', out_rel, '(entity no longer exists:', ', '.join(missing) + ')'); continue
                try:
                    j = json.load(open(src, encoding='utf-8'))
                except Exception as e:
                    print('BAD JSON', rel, e); shutil.copyfile(src, dst); continue
                j = convert_json(out_rel, j)
                with open(dst, 'w', encoding='utf-8') as fh:
                    json.dump(j, fh, indent=2, ensure_ascii=False); fh.write('\n')
            else:
                shutil.copyfile(src, dst)
            n += 1
    prune_stale_worldgen()
    prune_stale_recipes()
    # paintings: 1.21 painting variants are data (sizes from the 1.20.1 Java registry, in pixels)
    up_java = os.path.join(SRC, '..', 'java', 'net', 'mcreator', 'growling_man', 'init', 'GrowlingManModPaintings.java')
    if os.path.exists(up_java):
        for name, w, h in re.findall(r'register\("(\w+)", \(\) -> new PaintingVariant\((\d+), (\d+)\)\)', open(up_java).read()):
            dst = os.path.join(OUT, 'data', 'growling_man', 'painting_variant', name + '.json')
            os.makedirs(os.path.dirname(dst), exist_ok=True)
            with open(dst, 'w') as fh:
                json.dump({'asset_id': 'growling_man:' + name, 'width': int(w) // 16, 'height': int(h) // 16}, fh, indent=2); fh.write('\n')
            count('painting variant')
    # mob types -> entity type tags (1.20.5 removed MobType)
    mt = json.load(open(os.path.join(HERE, 'mobtypes.json'))) if os.path.exists(os.path.join(HERE, 'mobtypes.json')) else {}
    for tag, ents in mt.items():
        dst = os.path.join(OUT, 'data', 'minecraft', 'tags', 'entity_type', tag + '.json')
        cur = json.load(open(dst)) if os.path.exists(dst) else {'replace': False, 'values': []}
        cur['values'] = sorted(set(cur['values']) | set(ents))
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        with open(dst, 'w') as fh:
            json.dump(cur, fh, indent=2); fh.write('\n')
    # overlay (merges tag files, replaces anything else)
    for dp, _, fs in os.walk(OVERLAY) if os.path.isdir(OVERLAY) else []:
        for f in fs:
            src = os.path.join(dp, f); rel = os.path.relpath(src, OVERLAY)
            dst = os.path.join(OUT, rel); os.makedirs(os.path.dirname(dst), exist_ok=True)
            if '/tags/' in rel.replace(os.sep, '/') and os.path.exists(dst):
                cur = json.load(open(dst)); add = json.load(open(src))
                cur['values'] = cur.get('values', []) + [v for v in add['values'] if v not in cur.get('values', [])]
                with open(dst, 'w') as fh:
                    json.dump(cur, fh, indent=2); fh.write('\n')
            else:
                shutil.copyfile(src, dst)
            count('overlay file')
    # translations: tools/lang/<lang>.json is merged over the upstream lang file (new languages are created)
    ldir = os.path.join(HERE, 'lang'); adir = os.path.join(OUT, 'assets', 'growling_man', 'lang')
    for f in sorted(os.listdir(ldir)) if os.path.isdir(ldir) else []:
        if not f.endswith('.json'): continue
        dst = os.path.join(adir, f)
        cur = json.load(open(dst, encoding='utf-8')) if os.path.exists(dst) else {}
        cur.update(json.load(open(os.path.join(ldir, f), encoding='utf-8')))
        with open(dst, 'w', encoding='utf-8') as fh:
            json.dump(cur, fh, indent=2, ensure_ascii=False); fh.write('\n')
        count('lang ' + f[:-5])
    print('files', n)
    for k, v in sorted(stats.items()): print(' ', v, k)


main()
