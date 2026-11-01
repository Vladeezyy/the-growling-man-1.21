#!/usr/bin/env python3
"""NeoForge 26.2 -> 26.3: the mechanical rules for this mod's Java code. Idempotent, run from the repo root:
python tools/port_26_3.py. Input (GLFW -> SDL) is modding-kb/scripts/port_263_input.py."""
import glob
import re

ROOT = 'common/src/main/java/net/voxla/growlingman/'

RENAMES = [
    # MCreator's command source: no text name and entity arguments any more (name component, server)
    (r'(new CommandSourceStack\([^;]*?), "", (Component\.literal\(""\)), ([^;]*?), null\)', r'\1, \2, \3)'),
    # 26.3: the swing is a SwingState ticked by LivingEntity itself; the arm's progress is getSwingAnimation
    (r'\n\t\tthis\.updateSwingTime\(\);', ''),
    (r'\bgetAttackAnim\(', 'getSwingAnimation('),
    (r'\.setInvulnerable\(', '.setPermanentlyInvulnerable('),
]


def book_menu(s):
    """The Lurker book menu kept an empty (0 slot) item handler; NeoForge 26.3 removed IItemHandler."""
    s = s.replace('\tprivate IItemHandler internal;\n', '').replace('\t\tthis.internal = new ItemStackHandler(0);\n', '')
    s = re.sub(r'import net\.neoforged\.neoforge\.items\.[\w.]+;\n', '', s)
    return s


def port(s):
    for a, b in RENAMES:
        s = re.sub(a, b, s)
    return book_menu(s)


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
