package com.kerus.kupata.core.registry;

import com.kerus.kupata.KupataMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class KItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, KupataMod.MOD_ID);
    public static final RegistryObject<Item> KUPATA = registerSimpleItem("kupata");

    public static RegistryObject<Item> registerSimpleItem(String id){
        return ITEMS.register(id, () -> new Item(new Item.Properties()));
    }

    public static void register(IEventBus bus){
        ITEMS.register(bus);
    }
}
