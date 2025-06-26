package net.presc.indispensablecommands.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.presc.indispensablecommands.IndispensableCommands;
import net.presc.indispensablecommands.data.NickManager;
import net.presc.indispensablecommands.commands.OP.Freeze;
import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapOperation(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getName()Lnet/minecraft/text/Text;"))
    public Text setCustomName(PlayerEntity player, Operation<Text> original) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            return NickManager.getPlayerNameManager(Objects.requireNonNull(serverPlayer.getServer()),
                    IndispensableCommands.getConfig()).getFullPlayerName(serverPlayer);
        }
        return original.call(player);
    }

    // Méthode pour le freeze
    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void onTickMovement(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;

        if (Freeze.isFrozen(player.getUuid())) {
            player.setVelocity(0, player.getVelocity().y, 0);
            player.velocityDirty = true;
            ci.cancel();

            if (player.age % 10 == 0 && player.getWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                serverWorld.spawnParticles(
                        net.minecraft.particle.ParticleTypes.SNOWFLAKE,
                        player.getX(), player.getY() + 1, player.getZ(),
                        5, 0.3, 0.5, 0.3, 0.01
                );
            }
        }
    }
}