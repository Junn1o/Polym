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
                ServerPlayerEntity to = EntityArgumentType.getPlayer(ctx, "target");

                // must currently have the tag
                if (!from.getCommandTags().contains(GUILD_TAG)) {
                    source.sendError(Text.literal("Bạn là Guild Master hả?"));
                    return 0;
                }

                // optional: prevent passing to self
                if (from == to) {
                    source.sendError(Text.literal("Chơi trò gì đây?"));
                    return 0;
                }

                // transfer
                from.removeCommandTag(GUILD_TAG);
                to.addCommandTag(GUILD_TAG);

                source.sendFeedback(() -> Text.literal("Đã chuyển Guild Master " + to.getName().getString()), true);
                to.sendMessage(Text.literal("Bạn đã được bầu là Guild Master " + from.getName().getString()));

                return 1;
            }))));
        });
    }
}

