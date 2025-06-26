package net.presc.indispensablecommands.ui;

import net.minecraft.inventory.SimpleInventory;

public class TrashInventory extends SimpleInventory {
    public TrashInventory(int size) {
        super(size);
    }

    @Override
    public void markDirty() {
    }

    @Override
    public void onClose(net.minecraft.entity.player.PlayerEntity player) {
        clear();
    }

    @Override
    public boolean canPlayerUse(net.minecraft.entity.player.PlayerEntity player) {
        return true;
    }
}
