package net.presc.indispensablecommands.mixin.network;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.presc.indispensablecommands.IndispensableCommands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TeamS2CPacket.SerializableTeam.class)
public abstract class SerializableTeamMixin {

    @WrapOperation(method = "<init>(Lnet/minecraft/scoreboard/Team;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Team;getNameTagVisibilityRule()Lnet/minecraft/scoreboard/AbstractTeam$VisibilityRule;"))
    public AbstractTeam.VisibilityRule returnNeverIfUsingCustomNames(Team instance, Operation<AbstractTeam.VisibilityRule> original) {
        if (IndispensableCommands.getConfig().displayAbovePlayer()) {
            return AbstractTeam.VisibilityRule.NEVER;
        }
        return original.call(instance);
    }
}