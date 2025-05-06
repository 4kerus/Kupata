package com.kerus.kupata.core.registry;

import com.kerus.kupata.KupataMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class KCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KupataMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> KUPATA_TAB = CREATIVE_MODE_TABS.register("kupata_tab", () -> CreativeModeTab
            .builder()
            .icon(() -> KItems.KUPATA.get().getDefaultInstance())
            .title(Component.translatable("creative_tab.kupatamod.kupata_tab"))
            .displayItems((parameters, output) -> {
                output.accept(KItems.KUPATA.get());
            }).build());

    public static void register(IEventBus bus) {
        CREATIVE_MODE_TABS.register(bus);
    }
}
