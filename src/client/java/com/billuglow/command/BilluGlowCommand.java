package com.billuglow.command;

import com.billuglow.ColorUtil;
import com.billuglow.GlowManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Usage:
 *   /billuglow <player> <color>   - makes that player glow that color, for you only
 *   /billuglow off                - clears every glow you've set
 *   /billuglow off <player>       - clears the glow for just that one player
 */
public final class BilluGlowCommand {

	private BilluGlowCommand() {}

	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher,
			CommandRegistryAccess registryAccess) {
		dispatcher.register(ClientCommandManager.literal("billuglow")
			.then(ClientCommandManager.literal("off")
				.executes(BilluGlowCommand::executeOffAll)
				.then(ClientCommandManager.argument("player", StringArgumentType.word())
					.suggests(BilluGlowCommand::suggestOnlinePlayers)
					.executes(BilluGlowCommand::executeOffPlayer)))
			.then(ClientCommandManager.argument("player", StringArgumentType.word())
				.suggests(BilluGlowCommand::suggestOnlinePlayers)
				.then(ClientCommandManager.argument("color", StringArgumentType.word())
					.suggests((ctx, builder) -> CommandSource.suggestMatching(ColorUtil.names(), builder))
					.executes(BilluGlowCommand::executeSetGlow))));
	}

	private static CompletableFuture<Suggestions> suggestOnlinePlayers(
			CommandContext<FabricClientCommandSource> ctx, SuggestionsBuilder builder) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.getNetworkHandler() != null) {
			return CommandSource.suggestMatching(
				client.getNetworkHandler().getPlayerList().stream()
					.map(entry -> entry.getProfile().getName()),
				builder);
		}
		return builder.buildFuture();
	}

	/** Looks up an online player by name (case-insensitive). Sends an error and returns null if not found. */
	private static PlayerListEntry resolveOnlinePlayer(FabricClientCommandSource source, String name) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.getNetworkHandler() == null) {
			source.sendError(Text.literal("§cYou must be connected to a server to use this command."));
			return null;
		}
		PlayerListEntry entry = client.getNetworkHandler().getPlayerList().stream()
			.filter(e -> e.getProfile().getName().equalsIgnoreCase(name))
			.findFirst()
			.orElse(null);
		if (entry == null) {
			source.sendError(Text.literal("§cCould not find an online player named '" + name + "'."));
		}
		return entry;
	}

	private static int executeOffAll(CommandContext<FabricClientCommandSource> ctx) {
		int count = GlowManager.size();
		GlowManager.clearAll();
		ctx.getSource().sendFeedback(Text.literal(
			"§bBilluGlow§f: cleared " + count + " glowing player(s)."));
		return 1;
	}

	private static int executeOffPlayer(CommandContext<FabricClientCommandSource> ctx) {
		String name = StringArgumentType.getString(ctx, "player");
		PlayerListEntry entry = resolveOnlinePlayer(ctx.getSource(), name);
		if (entry == null) {
			return 0;
		}

		UUID uuid = entry.getProfile().getId();
		boolean wasGlowing = GlowManager.remove(uuid);
		if (wasGlowing) {
			ctx.getSource().sendFeedback(Text.literal(
				"§bBilluGlow§f: stopped glowing " + entry.getProfile().getName() + "."));
		} else {
			ctx.getSource().sendFeedback(Text.literal(
				"§bBilluGlow§f: " + entry.getProfile().getName() + " wasn't glowing."));
		}
		return 1;
	}

	private static int executeSetGlow(CommandContext<FabricClientCommandSource> ctx) {
		String name = StringArgumentType.getString(ctx, "player");
		String colorInput = StringArgumentType.getString(ctx, "color");

		Integer color = ColorUtil.parse(colorInput);
		if (color == null) {
			ctx.getSource().sendError(Text.literal(
				"§cUnknown color '" + colorInput + "'. Try a name like teal, cyan, red, lime — or a hex code like #1abc9c."));
			return 0;
		}

		PlayerListEntry entry = resolveOnlinePlayer(ctx.getSource(), name);
		if (entry == null) {
			return 0;
		}

		UUID uuid = entry.getProfile().getId();
		GlowManager.setGlow(uuid, color);
		ctx.getSource().sendFeedback(Text.literal(
			"§bBilluGlow§f: " + entry.getProfile().getName() + " is now glowing."));
		return 1;
	}
}
