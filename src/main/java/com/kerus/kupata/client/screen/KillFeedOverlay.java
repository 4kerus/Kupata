package com.kerus.kupata.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class KillFeedOverlay {

    private static final List<KillFeedEntry> killFeed = new LinkedList<>();
    private static final int MAX_FEED_SIZE = 5; // Максимальное количество записей в килл-фиде
    private static final long DISPLAY_TIME = 5000; // Время отображения записи (в миллисекундах)

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        Font font = minecraft.font;
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int y = 30; // Отступ ниже количества игроков

        GuiGraphics guiGraphics = event.getGuiGraphics();
        RenderSystem.enableBlend();

        // Отображение килл-фида
        for (KillFeedEntry entry : killFeed) {
            String message = entry.message();
            int textWidth = font.width(message);
            int x = screenWidth - textWidth - 10; // Отступ от правого края
            guiGraphics.drawString(font, message, x, y, 0xFFFFFF, true);
            y += 10; // Отступ между строками
        }

        RenderSystem.disableBlend();
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        if (event.getEntity() instanceof net.minecraft.world.entity.player.Player player) {
            String victim = player.getName().getString();
            String killer;

            if (event.getSource().getEntity() != null) {
                killer = event.getSource().getEntity().getName().getString();
                addKillFeedEntry(killer + " убил " + victim);
            } else {
                killer = event.getSource().getMsgId();
                addKillFeedEntry(victim + " умер от " + killer);
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            long currentTime = System.currentTimeMillis();
            Iterator<KillFeedEntry> iterator = killFeed.iterator();

            // Удаляем записи, которые отображаются дольше DISPLAY_TIME
            while (iterator.hasNext()) {
                KillFeedEntry entry = iterator.next();
                if (currentTime - entry.timestamp() > DISPLAY_TIME) {
                    iterator.remove();
                }
            }
        }
    }

    private static void addKillFeedEntry(String message) {
        killFeed.add(0, new KillFeedEntry(message, System.currentTimeMillis()));

        // Удаляем старые записи, если их больше, чем MAX_FEED_SIZE
        if (killFeed.size() > MAX_FEED_SIZE) {
            killFeed.remove(killFeed.size() - 1);
        }
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(KillFeedOverlay.class);
    }

    private record KillFeedEntry(String message, long timestamp) {
    }
}