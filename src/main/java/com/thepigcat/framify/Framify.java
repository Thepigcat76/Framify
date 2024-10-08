package com.thepigcat.framify;

import com.mojang.logging.LogUtils;
import com.thepigcat.framify.registries.FYBlocks;
import com.thepigcat.framify.registries.FYItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(Framify.MODID)
public final class Framify {
    public static final String MODID = "framify";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public Framify(IEventBus modEventBus, ModContainer modContainer) {
        FYBlocks.BLOCKS.register(modEventBus);
        FYItems.ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, FYConfig.SPEC);
    }

    static {
        CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.examplemod")) //The language key for the title of your CreativeModeTab
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon(FYBlocks.FRAMED_BLOCK::toStack)
                .displayItems((parameters, output) -> {
                    for (DeferredHolder<Item, ? extends Item> item : FYItems.ITEMS.getEntries())  {
                        output.accept(item.get());
                    }
                }).build());
    }
}
