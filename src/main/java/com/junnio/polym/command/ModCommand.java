package com.junnio.polym.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ModCommand {
    private static final String GUILD_TAG = "Guild";

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) -> {
            dispatcher.register(Commands.literal("guild").then(Commands.literal("pass").then(Commands.argument("target", EntityArgument.player()).executes(ctx -> {
                CommandSourceStack source = ctx.getSource();
                ServerPlayer from = source.getPlayerOrException();
                ServerPlayer to = EntityArgument.getPlayer(ctx, "target");

                // must currently have the tag
                if (!from.getTags().contains(GUILD_TAG)) {
                    source.sendFailure(Component.literal("Bạn là Guild Master hả?"));
                    return 0;
                }

                // optional: prevent passing to self
                if (from == to) {
                    source.sendFailure(Component.literal("Chơi trò gì đây?"));
                    return 0;
                }

                // transfer
                from.removeTag(GUILD_TAG);
                to.addTag(GUILD_TAG);

                source.sendSuccess(() -> Component.literal("Đã chuyển Guild Master " + to.getName().getString()), true);
                to.sendSystemMessage(Component.literal("Bạn đã được bầu là Guild Master " + from.getName().getString()));

                return 1;
            }))));
        });
    }
}

