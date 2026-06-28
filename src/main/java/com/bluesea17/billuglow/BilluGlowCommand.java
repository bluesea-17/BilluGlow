package com.bluesea17.billuglow;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class BilluGlowCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                ClientCommandManager.literal("BilluGlow")
                    .then(ClientCommandManager.argument("player", StringArgumentType.word())
                        .executes(ctx -> {
                            String targetName = StringArgumentType.getString(ctx, "player");
                            MinecraftClient client = MinecraftClient.getInstance();
                            if (client.world == null) return 0;

                            for (PlayerEntity player : client.world.getPlayers()) {
                                if (player.getName().getString().equalsIgnoreCase(targetName)) {
                                    player.setGlowing(true);
                                    client.player.sendMessage(Text.literal("Glowing: " + targetName), false);
                                    return 1;
                                }
                            }
                            client.player.sendMessage(Text.literal("Player not found: " + targetName), false);
                            return 0;
                        }))
            );
        });
    }
}

