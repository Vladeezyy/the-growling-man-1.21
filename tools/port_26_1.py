#!/usr/bin/env python3
"""NeoForge 1.21.11 -> 26.1.2: the mechanical rules for this mod's Java code. Idempotent, run from the repo root:
python tools/port_26_1.py. The scenes are ported by modding-kb/scripts/port_26_scenes.py."""
import glob
import re

ROOT = 'common/src/main/java/net/voxla/growlingman/'

RENAMES = [
    # GeckoLib 5.5 moved from software.bernie.geckolib to com.geckolib
    (r'\bsoftware\.bernie\.geckolib\b', 'com.geckolib'),
    # 26.1: GUI drawing is "extraction" into a render state
    (r'\bnet\.minecraft\.client\.gui\.GuiGraphics\b', 'net.minecraft.client.gui.GuiGraphicsExtractor'),
    (r'\bGuiGraphics\b', 'GuiGraphicsExtractor'),
    (r'\.drawString\(', '.text('),
    # the fluid overlay check gets a light getter
    (r'shouldDisplayFluidOverlay\(BlockState state, BlockAndTintGetter world', 'shouldDisplayFluidOverlay(BlockState state, BlockAndLightGetter world'),
    (r'import net\.minecraft\.world\.level\.BlockAndTintGetter;', 'import net.minecraft.world.level.BlockAndLightGetter;'),
    (r'public int getLightBlock\(BlockState state\)', 'public int getLightDampening(BlockState state)'),
    (r'public int getLightColor\(float partialTick\)', 'public int getLightCoords(float partialTick)'),
]


def block_values(s):
    """StateHolder#getValues() is a stream of Property.Value since 26.1 (MCreator's "copy the block states" loop)."""
    s = s.replace('for (Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {\n',
                  'for (Property.Value<?> entry : _bso.getValues().toList()) {\n')
    s = s.replace('getProperty(entry.getKey().getName())', 'getProperty(entry.property().getName())')
    s = s.replace('_bs.setValue(_property, (Comparable) entry.getValue())', '_bs.setValue(_property, (Comparable) entry.value())')
    return s


def gui(s):
    """Container screens: the size goes to the constructor, the background is extractBackground, labels
    extractLabels, a widget's contents extractContents."""
    s = re.sub(r'super\(container, inventory, text\);\n((?:\t\t[^\n]*\n)*?)\t\tthis\.imageWidth = (\d+);\n\t\tthis\.imageHeight = (\d+);\n',
               r'super(container, inventory, text, \2, \3);\n\1', s)
    s = re.sub(r'\n\t@Override\n\tpublic void render\(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks\) \{\n'
               r'\t\tsuper\.render\(guiGraphics, mouseX, mouseY, partialTicks\);\n\t\tthis\.renderTooltip\(guiGraphics, mouseX, mouseY\);\n\t\}\n', '', s)
    s = s.replace('protected void renderBg(GuiGraphicsExtractor guiGraphics, float partialTicks, int gx, int gy) {\n',
                  'public void extractBackground(GuiGraphicsExtractor guiGraphics, int gx, int gy, float partialTicks) {\n\t\tsuper.extractBackground(guiGraphics, gx, gy, partialTicks);\n')
    s = s.replace('protected void renderLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY)', 'protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY)')
    s = s.replace('public void renderContents(GuiGraphicsExtractor guiGraphics', 'public void extractContents(GuiGraphicsExtractor guiGraphics')
    return s


def port(s):
    for a, b in RENAMES:
        s = re.sub(a, b, s)
    s = block_values(s)
    s = gui(s)
    return s


def main():
    changed = 0
    for path in glob.glob(ROOT + '**/*.java', recursive=True):
        s = open(path, encoding='utf-8').read()
        o = port(s)
        if o != s:
            open(path, 'w', encoding='utf-8', newline='\n').write(o)
            changed += 1
    print('changed', changed)


if __name__ == '__main__':
    main()
