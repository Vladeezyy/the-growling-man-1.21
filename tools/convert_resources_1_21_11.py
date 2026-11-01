#!/usr/bin/env python3
"""Resources 1.21.1 -> 1.21.11 for this mod. Idempotent, run from the repo root:
    python tools/convert_resources_1_21_11.py <1.21.1 client resources jar>
The jar (any NeoForge 21.1 client-extra-aka-minecraft-resources.jar) is read for the old spawn egg templates only.
- 1.21.4: every item needs an item definition (assets/<ns>/items/<item>.json) pointing at its model.
- 1.21.5: spawn eggs have one texture each; ours are tinted from the 1.21.1 template with the colors the eggs were
  registered with (tools/spawn_egg_colors.json), so they look as before.
- 1.21.2: recipe ingredients are plain item ids / #tags."""
import io
import json
import os
import sys
import zipfile

from PIL import Image

ASSETS = 'common/src/main/resources/assets/growling_man/'
DATA = 'common/src/main/resources/data/growling_man/'


def dump(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w', encoding='utf-8', newline='\n') as f:
        json.dump(obj, f, indent=2)
        f.write('\n')


def tint(image, argb):
    r, g, b = (argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF
    out = image.copy()
    px = out.load()
    for y in range(out.height):
        for x in range(out.width):
            pr, pg, pb, pa = px[x, y]
            px[x, y] = (pr * r // 255, pg * g // 255, pb * b // 255, pa)
    return out


def spawn_eggs(jar):
    with zipfile.ZipFile(jar) as z:
        base = Image.open(io.BytesIO(z.read('assets/minecraft/textures/item/spawn_egg.png'))).convert('RGBA')
        overlay = Image.open(io.BytesIO(z.read('assets/minecraft/textures/item/spawn_egg_overlay.png'))).convert('RGBA')
    colors = json.load(open('tools/spawn_egg_colors.json'))
    for name, (primary, secondary) in colors.items():
        egg = tint(base, primary & 0xFFFFFFFF)
        egg.alpha_composite(tint(overlay, secondary & 0xFFFFFFFF))
        egg.save(ASSETS + 'textures/item/' + name + '.png')
        dump(ASSETS + 'models/item/' + name + '.json', {'parent': 'minecraft:item/generated', 'textures': {'layer0': 'growling_man:item/' + name}})


def item_definitions():
    for f in sorted(os.listdir(ASSETS + 'models/item')):
        name = f[:-5]
        dump(ASSETS + 'items/' + name + '.json', {'model': {'type': 'minecraft:model', 'model': 'growling_man:item/' + name}})


def ingredient(value):
    if isinstance(value, list):
        return [ingredient(v) for v in value]
    if isinstance(value, dict):
        if 'item' in value:
            return value['item']
        if 'tag' in value:
            return '#' + value['tag']
    return value


def recipes():
    for f in os.listdir(DATA + 'recipe'):
        path = DATA + 'recipe/' + f
        r = json.load(open(path, encoding='utf-8'))
        if 'key' in r:
            r['key'] = {k: ingredient(v) for k, v in r['key'].items()}
        if 'ingredients' in r:
            r['ingredients'] = [ingredient(v) for v in r['ingredients']]
        if 'ingredient' in r:
            r['ingredient'] = ingredient(r['ingredient'])
        dump(path, r)


if __name__ == '__main__':
    spawn_eggs(sys.argv[1])
    item_definitions()
    recipes()
