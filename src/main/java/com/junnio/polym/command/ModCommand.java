package com.junnio.polym.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ModCommand {
    private static final String GUILD_TAG = "Guild";

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) -> {
            dispatcher.register(CommandManager.literal("guild").then(CommandManager.literal("pass").then(CommandManager.argument("target", EntityArgumentType.player()).executes(ctx -> {
                ServerCommandSource source = ctx.getSource();
                ServerPlayerEntity from = source.getPlayerOrThrow();
                ServerPlayerEntity to = EntityArgumentType.getPlayer(ctx, "target"); // target player [web:36]

                // must currently have the tag
                if (!from.getCommandTags().contains(GUILD_TAG)) { // entity command tags [web:43]
                    source.sendError(Text.literal("You don't have the Guild tag."));
                    return 0;
                }

                // optional: prevent passing to self
                if (from == to) {
                    source.sendError(Text.literal("Pick another player."));
                    return 0;
                }

                // transfer
                from.removeCommandTag(GUILD_TAG); // remove tag API exists on entity/player [web:40][web:43]
                to.addCommandTag(GUILD_TAG);      // command tag set is exposed by Entity API [web:43]

                source.sendFeedback(() -> Text.literal("Passed Guild to " + to.getName().getString()), false);
                to.sendMessage(Text.literal("You received Guild from " + from.getName().getString()));
                return 1;
            }))));
        });
    }
}

