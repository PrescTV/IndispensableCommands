package net.presc.indispensablecommands.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.server.command.CommandManager.literal;

@Mixin(MessageCommand.class)
public abstract class MixinMsgCommand {
    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void disableVanillaMsg(CommandDispatcher<ServerCommandSource> dispatcher, CallbackInfo ci) {
        // Annule l'enregistrement vanilla
        ci.cancel();

        // Enregistre nos versions bloquées
        registerBlockedVersion(dispatcher, "msg");
        registerBlockedVersion(dispatcher, "w");
        registerBlockedVersion(dispatcher, "tell");
        registerBlockedVersion(dispatcher, "m"); // Pour /m
    }

    private static void registerBlockedVersion(CommandDispatcher<ServerCommandSource> dispatcher, String command) {
        dispatcher.register(literal(command)
                .executes(ctx -> {
                    ctx.getSource().sendError(
                            Text.literal("Utilisez /mail pour envoyer des messages privés")
                                    .formatted(Formatting.RED)
                    );
                    return 0;
                })
        );
    }
}