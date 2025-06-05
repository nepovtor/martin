package com.example.buildmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;

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

            dispatcher.register(CommandManager.literal("buildhouse")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    World world = source.getWorld();
                    PlayerEntity player = source.getPlayer();
                    if (player == null) {
                        return 0;
                    }

                    int required = 100;
                    if (countItems(player, Items.OAK_PLANKS) < required) {
                        source.sendFeedback(() -> Text.literal("Not enough planks to build a house!"), false);
                        return 1;
                    }

                    removeItems(player, Items.OAK_PLANKS, required);

                    BlockPos base = new BlockPos(source.getPosition());

                    for (int x = 0; x < 5; x++) {
                        for (int z = 0; z < 5; z++) {
                            world.setBlockState(base.add(x, 0, z), Blocks.OAK_PLANKS.getDefaultState());
                        }
                    }

                    for (int y = 1; y < 4; y++) {
                        for (int x = 0; x < 5; x++) {
                            world.setBlockState(base.add(x, y, 0), Blocks.OAK_PLANKS.getDefaultState());
                            world.setBlockState(base.add(x, y, 4), Blocks.OAK_PLANKS.getDefaultState());
                        }
                        for (int z = 0; z < 5; z++) {
                            world.setBlockState(base.add(0, y, z), Blocks.OAK_PLANKS.getDefaultState());
                            world.setBlockState(base.add(4, y, z), Blocks.OAK_PLANKS.getDefaultState());
                        }
                    }

                    for (int x = 0; x < 5; x++) {
                        for (int z = 0; z < 5; z++) {
                            world.setBlockState(base.add(x, 4, z), Blocks.OAK_PLANKS.getDefaultState());
                        }
                    }

                    source.sendFeedback(() -> Text.literal("House built!"), false);
                    return 1;
                }));
        });
    }

    private int countItems(PlayerEntity player, Item item) {
        int count = 0;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private void removeItems(PlayerEntity player, Item item, int amount) {
        for (int i = 0; i < player.getInventory().size() && amount > 0; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(item)) {
                int remove = Math.min(amount, stack.getCount());
                stack.decrement(remove);
                amount -= remove;
                if (stack.isEmpty()) {
                    player.getInventory().setStack(i, ItemStack.EMPTY);
                }
            }
        }
    }
}
