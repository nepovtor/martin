package com.example.buildmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;

public class BuildMod implements ModInitializer {
    public static final String MOD_ID = "buildmod";

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("buildcube")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    World world = source.getWorld();
                    BlockPos pos = new BlockPos(source.getPosition());
                    for (int x = 0; x < 5; x++) {
                        for (int y = 0; y < 5; y++) {
                            for (int z = 0; z < 5; z++) {
                                BlockPos newPos = pos.add(x, y, z);
                                world.setBlockState(newPos, Blocks.BRICKS.getDefaultState());
                            }
                        }
                    }
                    source.sendFeedback(() -> Text.literal("Cube built!"), false);
                    return 1;
                }));
        });
    }
}
