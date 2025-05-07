package com.kerus.kupata.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class PlayerCountOverlay {

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

//        int playerCount = minecraft.level.players().size();

        long playerCount = minecraft.level.players().stream()
                .filter(player -> {
                    if (player != null) {
                        Player p = (Player) player;
                        return !p.getAbilities().instabuild && !p.isSpectator();
                    }
                    return false;
                })
                .count();

        String text = "Players: " + playerCount;

        Font font = minecraft.font;
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int textWidth = font.width(text);

        int x = screenWidth - textWidth - 10; // Отступ от правого края
        int y = 10; // Отступ от верхнего края

        GuiGraphics guiGraphics = event.getGuiGraphics(); // Используем GuiGraphics
        RenderSystem.enableBlend();
        guiGraphics.drawString(font, text, x, y, 0xFFFFFF, true);
        RenderSystem.disableBlend();
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(PlayerCountOverlay.class);
    }
}