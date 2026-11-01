#!/usr/bin/env python3
"""GeckoLib 4 (1.21.1) -> GeckoLib 5 (1.21.11+) for this mod's MCreator entities. Run from the repo root after
port_1_21_11.py: python tools/port_geckolib5.py. The models, renderers and glow layers are all generated from one
MCreator template each, so they are rewritten from the values read out of the old classes; the entities get their
animation controllers converted in place."""
import glob
import os
import re

ROOT = 'common/src/main/java/net/voxla/growlingman/'
PKG = 'net.voxla.growlingman'


def read(p):
    return open(p, encoding='utf-8').read()


def write(p, s):
    open(p, 'w', encoding='utf-8', newline='\n').write(s)


def model(path):
    s = read(path)
    if 'GeoRenderState' in s:
        return None
    cls = re.search(r'public class (\w+) extends GeoModel<(\w+)>', s)
    name, entity = cls.group(1), cls.group(2)
    anim = re.search(r'getAnimationResource\(.*?\{\s*return (.*?);', s, re.S).group(1)
    geo = re.search(r'getModelResource\(.*?\{\s*return (.*?);', s, re.S).group(1)
    tex = re.search(r'getTextureResource\(.*?\{\s*return (.*?);', s, re.S).group(1)
    assert 'entity.getTexture()' in tex, path
    write(path, f'''package {PKG}.entity.model;

import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.Identifier;

import {PKG}.port.GeoTextures;
import {PKG}.entity.{entity};

import javax.annotation.Nullable;

public class {name} extends GeoModel<{entity}> {{
	@Override
	public Identifier getAnimationResource({entity} entity) {{
		return {anim};
	}}

	@Override
	public Identifier getModelResource(GeoRenderState renderState) {{
		return {geo};
	}}

	@Override
	public Identifier getTextureResource(GeoRenderState renderState) {{
		return GeoTextures.get(renderState);
	}}

	@Override
	public void addAdditionalStateData({entity} entity, @Nullable Object relatedObject, GeoRenderState renderState) {{
		GeoTextures.set(renderState, entity.getTexture());
	}}
}}
''')
    return name, 'setCustomAnimations' in s


def layer(path):
    s = read(path)
    if 'TextureLayerGeoLayer' in s:
        return
    cls = re.search(r'public class (\w+) extends GeoRenderLayer<(\w+)>', s)
    name, entity = cls.group(1), cls.group(2)
    tex = re.search(r'private static final Identifier LAYER = (.*?);', s).group(1)
    assert 'RenderType.eyes(LAYER)' in s, path
    write(path, f'''package {PKG}.entity.layer;

import software.bernie.geckolib.renderer.layer.builtin.TextureLayerGeoLayer;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

import {PKG}.entity.{entity};

/** The glowing eyes: the layer texture drawn again with the eyes render type (full bright). */
public class {name}<R extends LivingEntityRenderState & GeoRenderState> extends TextureLayerGeoLayer<{entity}, Void, R> {{
	private static final Identifier LAYER = {tex};

	public {name}(GeoRenderer<{entity}, Void, R> entityRenderer) {{
		super(entityRenderer, LAYER, RenderTypes::eyes);
	}}
}}
''')


def renderer(path, head_turn):
    s = read(path)
    if 'GeoEntityRenderer<' not in s or 'RenderPassInfo' in s or 'getRenderType(R' in s:
        return
    cls = re.search(r'public class (\w+) extends GeoEntityRenderer<(\w+)>', s)
    name, entity = cls.group(1), cls.group(2)
    mdl = re.search(r'super\(renderManager, new (\w+)\(\)\);', s).group(1)
    shadow = re.search(r'this\.shadowRadius = ([\d.]+f);', s).group(1)
    lay = re.search(r'this\.addRenderLayer\(new (\w+)\(this\)\);', s)
    scale = re.search(r'float scale = ([\d.]+f);', s)
    scale = scale.group(1) if scale else '1f'
    imports = [
        'software.bernie.geckolib.renderer.base.GeoRenderState',
        'software.bernie.geckolib.renderer.GeoEntityRenderer',
        'net.minecraft.resources.Identifier',
        'net.minecraft.client.renderer.rendertype.RenderTypes',
        'net.minecraft.client.renderer.rendertype.RenderType',
        'net.minecraft.client.renderer.entity.state.LivingEntityRenderState',
        'net.minecraft.client.renderer.entity.EntityRendererProvider',
    ]
    if head_turn:
        imports[:0] = ['software.bernie.geckolib.renderer.base.RenderPassInfo',
                       'software.bernie.geckolib.renderer.base.BoneSnapshots',
                       'software.bernie.geckolib.constant.DefaultAnimations']
    own = [f'{PKG}.entity.model.{mdl}'] + ([f'{PKG}.entity.layer.{lay.group(1)}'] if lay else []) + [f'{PKG}.entity.{entity}']
    body = f'''	public {name}(EntityRendererProvider.Context renderManager) {{
		super(renderManager, new {mdl}());
		this.shadowRadius = {shadow};
'''
    if scale != '1f':
        body += f'		withScale({scale});\n'
    if lay:
        body += f'		withRenderLayer(new {lay.group(1)}<>(this));\n'
    body += '''	}

	@Override
	public RenderType getRenderType(R renderState, Identifier texture) {
		return RenderTypes.entityTranslucent(texture);
	}
'''
    if head_turn:
        body += '''
	@Override
	public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
		DefaultAnimations.hardcodedHeadRotation(renderPassInfo, snapshots, "head");
	}
'''
    write(path, f'package {PKG}.client.renderer;\n\n' + ''.join(f'import {i};\n' for i in imports) + '\n'
          + ''.join(f'import {i};\n' for i in own)
          + f'\npublic class {name}<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<{entity}, R> {{\n'
          + body + '}\n')


ENTITY_IMPORTS = {
    'import software.bernie.geckolib.animation.PlayState;': 'import software.bernie.geckolib.animation.object.PlayState;',
    'import software.bernie.geckolib.animation.AnimationState;': 'import software.bernie.geckolib.animation.state.AnimationTest;',
    'import software.bernie.geckolib.animation.AnimatableManager;': 'import software.bernie.geckolib.animatable.manager.AnimatableManager;',
}


def entity(path):
    s = read(path)
    if 'implements GeoEntity' not in s or 'AnimationTest<' in s:
        return
    cls = re.search(r'public class (\w+)', s).group(1)
    for a, b in ENTITY_IMPORTS.items():
        s = s.replace(a, b)
    s = s.replace('(AnimationState event)', f'(AnimationTest<{cls}> event)')
    s = s.replace('event.getLimbSwingAmount()', 'this.walkAnimation.speed()')
    s = re.sub(r'new AnimationController<>\(this, ', 'new AnimationController<>(', s)
    # the procedure controller: the shared GeckoLib 5 implementation
    s = re.sub(r'(private PlayState procedurePredicate\(AnimationTest<\w+> event\) \{).*?\n\t\}\n',
               r'\1\n\t\treturn GeoAnims.procedure(event, this.animationprocedure, animation -> this.animationprocedure = animation);\n\t}\n',
               s, flags=re.S)
    s = re.sub(r'\tprivate boolean lastloop;\n', '', s)
    s = s.replace('import software.bernie.geckolib.animatable.GeoEntity;',
                  'import software.bernie.geckolib.animatable.GeoEntity;\n\nimport ' + PKG + '.port.GeoAnims;')
    write(path, s)


def main():
    heads = {}
    for p in glob.glob(ROOT + 'entity/model/*.java'):
        r = model(p)
        if r:
            heads[r[0]] = r[1]
    for p in glob.glob(ROOT + 'entity/layer/*.java'):
        layer(p)
    for p in glob.glob(ROOT + 'client/renderer/*.java'):
        s = read(p)
        m = re.search(r'new (\w+Model)\(\)', s)
        renderer(p, heads.get(m.group(1), False) if m else False)
    for p in glob.glob(ROOT + 'entity/*.java'):
        entity(p)


if __name__ == '__main__':
    main()
