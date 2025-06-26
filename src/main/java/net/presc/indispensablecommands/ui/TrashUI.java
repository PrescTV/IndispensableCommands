package net.presc.indispensablecommands.ui;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.item.ItemStack;

public class TrashUI extends GenericContainerScreenHandler {
    private final DefaultedList<ItemStack> contenu = DefaultedList.ofSize(18, ItemStack.EMPTY);

    public TrashUI(int syncId, PlayerInventory playerInventory) {
        super(ScreenHandlerType.GENERIC_9X2, syncId, playerInventory, new TrashInventory(18), 2);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
    }

    public static class Factory implements NamedScreenHandlerFactory {
        @Override
        public TrashUI createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
            return new TrashUI(syncId, inv);
        }

        @Override
        public Text getDisplayName() {
            return Text.literal("Trash");
        }
    }
}
