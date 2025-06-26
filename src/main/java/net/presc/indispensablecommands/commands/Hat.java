package net.presc.indispensablecommands.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.presc.indispensablecommands.events.LocalizationManager;

import static net.minecraft.server.command.CommandManager.literal;

public class Hat {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var hatNode = literal("hat")
                .executes(context -> executeHat(context.getSource()));
        dispatcher.register(hatNode);
    }

    private static int executeHat(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return 0;

        ItemStack mainHand = player.getMainHandStack();
        if (mainHand.isEmpty()) {
            String errorMessage = LocalizationManager.getTranslation(
                    player.getUuid(),
                    "command.hat.error.empty_hand"
            );
            source.sendError(Text.literal(errorMessage).formatted(Formatting.RED));
            return 0;
        }

        ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
        player.equipStack(EquipmentSlot.HEAD, mainHand.copy());
        player.setStackInHand(player.getActiveHand(), helmet);

        String successMessage = LocalizationManager.getTranslation(
                player.getUuid(),
                "command.hat.success"
        );
        source.sendFeedback(() ->
                Text.literal(successMessage).formatted(Formatting.GOLD), false);
        return 1;
    }
}