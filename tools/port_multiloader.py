#!/usr/bin/env python3
"""NeoForge-only MCreator code → loader-neutral common code (idempotent; run from the repository root).

    python3 -I tools/port_multiloader.py          # rewrite common/src/main/java, regenerate the event indexes

- NeoForge event / bus / dist / FMLPaths imports → the same names in net.voxla.growlingman.port.bus
- DeferredRegister / DeferredHolder → port.platform.Registrar / RegistryEntry (mob effects: .holder() where a Holder is used)
- x.getData(GrowlingManModVariables.PLAYER_VARIABLES) → GrowlingManModVariables.of(x)
- x.getPersistentData() → PortUtil.data(x)
- NeoForge's EntityType.Builder methods → vanilla ones
- port/bus/EventIndex.java and ClientEventIndex.java: every @SubscribeEvent method whose event is in port.bus
"""
import re
import sys
from pathlib import Path

ROOT = Path("common/src/main/java/net/voxla/growlingman")
PKG = "net.voxla.growlingman"
BUS = ROOT / "port" / "bus"
BUS_TYPES = {p.stem for p in BUS.glob("*.java")} - {"package-info", "GMBus", "EventIndex", "ClientEventIndex"}

IMPORTS = {
    "net.neoforged.fml.common.Mod": None,
    "net.neoforged.bus.api.IEventBus": None,
    "net.neoforged.neoforge.registries.DeferredRegister": f"{PKG}.port.platform.Registrar",
    "net.neoforged.neoforge.registries.DeferredHolder": f"{PKG}.port.platform.RegistryEntry",
}


def rewrite_imports(src):
    def repl(m):
        name = m.group(1)
        if name in IMPORTS:
            return "" if IMPORTS[name] is None else f"import {IMPORTS[name]};\n"
        simple = name.rsplit(".", 1)[1]
        if name.startswith("net.neoforged.") and simple in BUS_TYPES:
            return f"import {PKG}.port.bus.{simple};\n"
        return m.group(0)
    return re.sub(r"import (net\.neoforged\.[\w.]+);\n", repl, src)


def receiver_start(s, end):
    """Start index of the primary expression that ends at s[end] (exclusive), e.g. a.b(c).d or (cast) x"""
    i = end
    while True:
        if i > 0 and s[i - 1] == ")":
            depth = 0
            j = i - 1
            while j >= 0:
                if s[j] == ")":
                    depth += 1
                elif s[j] == "(":
                    depth -= 1
                    if depth == 0:
                        break
                j -= 1
            i = j
            k = i
            while k > 0 and (s[k - 1].isalnum() or s[k - 1] in "_$"):
                k -= 1
            i = k
        else:
            k = i
            while k > 0 and (s[k - 1].isalnum() or s[k - 1] in "_$"):
                k -= 1
            if k == i:
                return i
            i = k
        if i > 0 and s[i - 1] == ".":
            i -= 1
            continue
        return i


def wrap_calls(src, suffix, make):
    """x<suffix> → make(x) for every occurrence"""
    out = src
    while True:
        pos = out.find(suffix)
        if pos < 0:
            return out
        start = receiver_start(out, pos)
        recv = out[start:pos]
        if not recv:
            sys.exit(f"no receiver before {suffix!r}: {out[max(0, pos - 80):pos + 40]!r}")
        out = out[:start] + make(recv) + out[pos + len(suffix):]


def ensure_import(src, fqcn):
    if f"import {fqcn};" in src or not re.search(r"\b" + fqcn.rsplit(".", 1)[1] + r"\b", src):
        return src
    return re.sub(r"^(package [\w.]+;\n)", r"\1\nimport " + fqcn + ";\n", src, count=1, flags=re.M)


def effect_visibility(src):
    """MobEffect.initializeClient(IClientMobEffectExtensions) → the methods of port.EffectVisibility on the effect"""
    m = re.search(r"\n\t@Override\n\tpublic void initializeClient\([^)]*\) \{\n\t\tconsumer\.accept\(new IClientMobEffectExtensions\(\) \{\n(.*?)\n\t\t\}\);\n\t\}\n", src, re.S)
    if not m:
        return src
    methods = re.split(r"\n(?=\t\t\t@Override)", m.group(1))
    kept = ["\n".join(line[2:] for line in part.split("\n")) for part in methods if "isVisibleIn" in part]
    src = src[:m.start()] + "\n" + "\n\n".join(kept) + "\n" + src[m.end():]
    src = re.sub(r"(public class \w+ extends MobEffect)( implements [\w., ]+)?( \{)",
                 lambda m: m.group(1) + (m.group(2) + ", " if m.group(2) else " implements ") + "net.voxla.growlingman.port.EffectVisibility" + m.group(3), src)
    return re.sub(r"import net\.neoforged\.neoforge\.client\.extensions\.common\.IClientMobEffectExtensions;\n", "", src)


def rewrite_code(path, src):
    src = rewrite_imports(src)
    # unused MCreator imports (not on Fabric's classpath)
    src = re.sub(r"import org\.checkerframework\.[\w.]+;\n", "", src)
    src = re.sub(r"\bDeferredRegister<", "Registrar<", src)
    src = re.sub(r"\bDeferredRegister\.create\(", "Registrar.create(", src)
    src = re.sub(r"\bDeferredHolder<", "RegistryEntry<", src)
    for tags, reg in (("BlockTags", "BLOCK"), ("ItemTags", "ITEM"), ("EntityTypeTags", "ENTITY_TYPE"), ("BiomeTags", "BIOME")):
        src = src.replace(f"{tags}.create(", f"net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.{reg}, ")
    src = src.replace(".setShouldReceiveVelocityUpdates(true)", "")
    src = src.replace("new SimpleParticleType(true))", "new SimpleParticleType(true) {})").replace("new SimpleParticleType(false))", "new SimpleParticleType(false) {})")
    src = re.sub(r"new DeferredSpawnEggItem\((GrowlingManModEntities\.\w+),", r"new SpawnEggItem(\1.get(),", src)
    src = src.replace("import net.neoforged.neoforge.common.DeferredSpawnEggItem;", "import net.minecraft.world.item.SpawnEggItem;")
    src = re.sub(r"new SavedData\.Factory<>\((\w+)::new, (\w+)::load\)", r"new SavedData.Factory<>(\1::new, \2::load, null)", src)
    # NeoForge-only overrides that NeoForge still calls without @Override
    src = re.sub(r"@Override\n(\s*)public boolean (shouldDisplayFluidOverlay|canHarvestBlock)\(", r"\1public boolean \2(", src)
    src = src.replace("public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader world, BlockPos pos, Player player) {",
                      "public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state) {")
    # NeoForge's onDestroyedByPlayer (before the block is removed) → vanilla destroy (after a player removed it), both loaders call it
    src = re.sub(r"public boolean onDestroyedByPlayer\(BlockState blockstate, Level world, BlockPos pos, Player entity, boolean willHarvest, FluidState fluid\) \{\n"
                 r"(\s*)boolean retval = super\.onDestroyedByPlayer\(blockstate, world, pos, entity, willHarvest, fluid\);\n((?:.*\n)*?)\s*return retval;\n",
                 r"public void destroy(LevelAccessor world, BlockPos pos, BlockState blockstate) {\n\1super.destroy(world, pos, blockstate);\n\2", src)
    # block entities: the item handler is a NeoForge capability (registered by the NeoForge module, Fabric sees the Container)
    src = re.sub(r"\s*private final SidedInvWrapper handler = new SidedInvWrapper\(this, null\);\n", "\n", src)
    src = re.sub(r"\n\s*public SidedInvWrapper getItemHandler\(\) \{\n\s*return handler;\n\s*\}\n", "\n", src)
    src = src.replace("import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;\n", "")
    src = effect_visibility(src)
    for fqcn in ("net.minecraft.world.level.LevelAccessor", "net.minecraft.world.item.SpawnEggItem"):
        src = ensure_import(src, fqcn)
    src = src.replace(".setTrackingRange(", ".clientTrackingRange(").replace(".setUpdateInterval(", ".updateInterval(")
    src = wrap_calls(src, ".getData(GrowlingManModVariables.PLAYER_VARIABLES)", lambda r: f"GrowlingManModVariables.of({r})")
    src = wrap_calls(src, ".getPersistentData()", lambda r: f"{PKG}.port.PortUtil.data({r})")
    src = src.replace("FakePlayerFactory.getMinecraft(", f"{PKG}.port.platform.Platform.INSTANCE.fakePlayer(")
    src = re.sub(r"import net\.neoforged\.neoforge\.common\.util\.FakePlayerFactory;\n", "", src)
    if path.name != "GrowlingManModMobEffects.java":
        src = re.sub(r"\bGrowlingManModMobEffects\.(?!REGISTRY\b)([A-Z0-9_]+)\b(?!\.(holder|get|value|getId|getKey)\b)",
                     r"GrowlingManModMobEffects.\1.holder()", src)
    return src


SUB = re.compile(r"@SubscribeEvent(?:\(priority\s*=\s*EventPriority\.(\w+)\))?\s*\n(?:\s*@\w+(?:\([^)]*\))?\s*\n)*\s*public static void (\w+)\(\s*([\w.]+) \w+\)")


def index_entries(path, src):
    cls = re.search(r"^public (?:final |abstract )*class (\w+)", src, re.M)
    if not cls:
        return [], []
    pkg = re.search(r"^package ([\w.]+);", src, re.M).group(1)
    annotation = re.search(r"@EventBusSubscriber(\([^)]*\))?\s*\npublic", src)
    client = bool(annotation and annotation.group(1) and "CLIENT" in annotation.group(1))
    common, clientside, skipped = [], [], []
    # nested static classes with their own @EventBusSubscriber (MCreator's EventBusVariableHandlers)
    nested = {m.start(): m.group(1) for m in re.finditer(r"@EventBusSubscriber[^\n]*\n\s*public static class (\w+)", src)}
    for m in SUB.finditer(src):
        event = m.group(3)
        if event.split(".")[0] not in BUS_TYPES:
            skipped.append(f"{path.name}: {event}")
            continue
        owner = cls.group(1)
        before = [n for start, n in nested.items() if start < m.start()]
        if before:
            owner = f"{owner}.{before[-1]}"
        entry = (event, m.group(1) or "NORMAL", f"{pkg}.{owner}::{m.group(2)}")
        (clientside if client else common).append(entry)
    return common + [("__client__",) + e for e in clientside], skipped


def write_index(name, entries, doc):
    lines = [f"package {PKG}.port.bus;", "", f"/** {doc} (generated by tools/port_multiloader.py) */",
             f"public final class {name} {{", f"\tprivate {name}() {{", "\t}", "",
             "\tpublic static void register() {"]
    for event, prio, handler in sorted(entries, key=lambda e: (e[0], e[2])):
        lines.append(f"\t\tGMBus.add({event}.class, EventPriority.{prio}, {handler});")
    lines += ["\t}", "}", ""]
    (BUS / f"{name}.java").write_text("\n".join(lines))


def main():
    common, client, skipped, changed = [], [], [], 0
    for path in sorted(ROOT.rglob("*.java")):
        if BUS in path.parents:
            continue
        src = path.read_text()
        new = rewrite_code(path, src)
        if new != src:
            path.write_text(new)
            changed += 1
        entries, skip = index_entries(path, new)
        skipped += skip
        for e in entries:
            if e[0] == "__client__":
                client.append(e[1:])
            else:
                common.append(e)
    write_index("EventIndex", common, "The common @SubscribeEvent methods")
    write_index("ClientEventIndex", client, "The client-only @SubscribeEvent methods (classes with Dist.CLIENT)")
    print(f"{changed} files rewritten; index: {len(common)} common, {len(client)} client listeners")
    for s in skipped:
        print("not indexed (loader event):", s)


if __name__ == "__main__":
    main()
