"""Static checks of @EventBusSubscriber classes: no @SubscribeEvent methods, non-static handlers, events on the wrong bus.
With --fix, drops the annotation from classes that have no handlers."""
import os, re, sys
MOD_BUS = {'FMLCommonSetupEvent', 'FMLClientSetupEvent', 'FMLConstructModEvent', 'RegisterEvent', 'EntityAttributeCreationEvent',
           'EntityAttributeModificationEvent', 'RegisterSpawnPlacementsEvent', 'RegisterCapabilitiesEvent', 'RegisterMenuScreensEvent',
           'RegisterClientExtensionsEvent', 'ModConfigEvent', 'RegisterKeyMappingsEvent', 'EntityRenderersEvent', 'RegisterParticleProvidersEvent',
           'BuildCreativeModeTabContentsEvent', 'RegisterPayloadHandlersEvent', 'RegisterGuiLayersEvent', 'RegisterDimensionSpecialEffectsEvent',
           'ModifyDefaultComponentsEvent', 'RegisterColorHandlersEvent', 'RegisterShadersEvent', 'ModelEvent', 'TextureAtlasStitchedEvent',
           'RegisterNamedRenderTypesEvent', 'AddPackFindersEvent', 'GatherDataEvent', 'FMLLoadCompleteEvent', 'InterModEnqueueEvent',
           'RegisterItemDecorationsEvent', 'RegisterClientReloadListenersEvent', 'DataPackRegistryEvent', 'NewRegistryEvent'}
fix = '--fix' in sys.argv
bad = 0
for dp, _, fs in os.walk('src/main/java'):
    for f in fs:
        p = os.path.join(dp, f); s = open(p).read()
        for m in re.finditer(r'@EventBusSubscriber(\([^)]*\))?\s*\n\s*public (?:static )?(?:abstract )?(?:class|record) (\w+)', s):
            args = m.group(1) or ''
            modbus = 'Bus.MOD' in args
            # class body
            start = s.index('{', m.end()); depth = 0; i = start
            while True:
                if s[i] == '{': depth += 1
                elif s[i] == '}':
                    depth -= 1
                    if depth == 0: break
                i += 1
            body = s[start:i]
            # strip nested subscriber classes from the body
            body = re.sub(r'@EventBusSubscriber[\s\S]*?$', '', body) if body.count('@EventBusSubscriber') else body
            client_cls = 'Dist.CLIENT' in args
            handlers = re.findall(r'@SubscribeEvent(?:\([^)]*\))?\s*(?:@\w+(?:\([^)]*\))?\s*)*public (static )?void \w+\(([\w.]+)', body)
            server_handlers = [h for h in re.findall(r'((?:@\w+(?:\([^)]*\))?\s*)*)public static void \w+\(', body) if '@SubscribeEvent' in h and (client_cls or '@OnlyIn(Dist.CLIENT)' not in h)]
            if handlers and not server_handlers and not client_cls:
                bad += 1; print('ONLY CLIENT HANDLERS', p, m.group(2))
            if not handlers:
                bad += 1; print('NO HANDLERS', p, m.group(2))
                if fix:
                    s = s[:m.start()] + s[m.start():].replace(m.group(0), m.group(0)[m.group(0).index('public'):].join([''] * 1) if False else re.sub(r'@EventBusSubscriber(\([^)]*\))?\s*\n', '', m.group(0), count=1), 1)
                    open(p, 'w').write(s)
                continue
            for static, ev in handlers:
                if not static: bad += 1; print('NOT STATIC', p, ev)
                base = ev.split('.')[0]
                if base in MOD_BUS and not modbus: bad += 1; print('MOD EVENT ON GAME BUS', p, ev)
                if base not in MOD_BUS and modbus: bad += 1; print('GAME EVENT ON MOD BUS', p, ev)
print(bad, 'problems')
