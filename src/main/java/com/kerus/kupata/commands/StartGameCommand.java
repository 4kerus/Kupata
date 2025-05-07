package com.kerus.kupata.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;
import java.util.Random;

public class StartGameCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("startgame")
                .requires(source -> source.hasPermission(2)) // Требуется уровень прав 2
                .executes(StartGameCommand::execute));
    }

    private static final int MAP_MIN_X = -220;
    private static final int MAP_MAX_X = 220;
    private static final int MAP_MIN_Z = -220;
    private static final int MAP_MAX_Z = 220;

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        Random random = new Random();

        List<ServerPlayer> players = level.getServer().getPlayerList().getPlayers();
        // Телепортируем всех игроков
        for (ServerPlayer player : players) {
            // Генерация случайных координат
            int x = MAP_MIN_X + random.nextInt(MAP_MAX_X - MAP_MIN_X + 1);
            int z = MAP_MIN_Z + random.nextInt(MAP_MAX_Z - MAP_MIN_Z + 1);
            int y = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, new BlockPos(x, 0, z)).getY();

            // Телепортация игрока
            player.teleportTo(x + 0.5, y, z + 0.5);
            player.sendSystemMessage(Component.literal("Ты телепортирован в " + x + ", " + y + ", " + z));
        }

        // Заполнение сундуков
        for (int x = MAP_MIN_X; x <= MAP_MAX_X; x += 10) {
            for (int z = MAP_MIN_Z; z <= MAP_MAX_Z; z += 10) {
                BlockPos pos = new BlockPos(x, level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(x, 0, z)).getY(), z);
                BlockState state = level.getBlockState(pos);
                if (state.is(Blocks.CHEST)) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof ChestBlockEntity chest) {
                        ItemStack stack = new ItemStack(Items.DIAMOND_SWORD);
                        chest.setItem(0, stack);
                    }
                }
            }
        }

        source.sendSuccess(() -> Component.literal("Игра началась! Все игроки телепортированы, заполнено"), true);
        return 1;
    }
}