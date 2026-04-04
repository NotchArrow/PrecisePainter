package com.notcharrow.precisepainter.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PaintingCommand {
	public static Map<UUID, RegistryKey<PaintingVariant>> playerData = new HashMap<>();

	public static void register() {
		CommandRegistrationCallback.EVENT.register((
				dispatcher, registry, environment) -> {
			dispatcher.register(
					literal("painting")
						.then(argument("Painting", StringArgumentType.greedyString())
							.suggests((context, builder) ->
								CommandSource.suggestIdentifiers(
									context.getSource().getServer().getRegistryManager().getOrThrow(RegistryKeys.PAINTING_VARIANT).getIds(),
									builder))
							.executes(PaintingCommand::executeCommand))
						.then(literal("reset")
							.executes(PaintingCommand::executeReset)));
		});
	}

	private static int executeCommand(CommandContext<ServerCommandSource> context) {
		if (context.getSource().getPlayer() instanceof ServerPlayerEntity player) {
			Identifier identifier = Identifier.of(StringArgumentType.getString(context, "Painting"));
			playerData.put(player.getUuid(), RegistryKey.of(RegistryKeys.PAINTING_VARIANT, identifier));
			player.sendMessage(Text.literal(
					"The next painting you place will be: " + identifier).fillStyle(Style.EMPTY.withColor(Formatting.YELLOW)),
					true);
		}

		return Command.SINGLE_SUCCESS;
	}

	private static int executeReset(CommandContext<ServerCommandSource> context) {
		if (context.getSource().getPlayer() instanceof ServerPlayerEntity player) {
			playerData.remove(player.getUuid());
			player.sendMessage(Text.literal(
					"The next painting you place will be vanilla randomized").fillStyle(Style.EMPTY.withColor(Formatting.GREEN)),
					true);
		}

		return Command.SINGLE_SUCCESS;
	}
}
