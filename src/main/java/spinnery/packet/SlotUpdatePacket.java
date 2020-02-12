package spinnery.packet;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import spinnery.common.BaseContainer;
import spinnery.util.StackUtilities;
import spinnery.widget.WAbstractWidget;
import spinnery.widget.WSlot;

import java.util.function.Supplier;

public class SlotUpdatePacket {
	public static final int ID =3;

	int syncId;
	int slotNumber;
	int inventoryNumber;
	ItemStack stack;

	public SlotUpdatePacket(int syncId, int slotNumber, int inventoryNumber, ItemStack stack) {
		this.syncId = syncId;
		this.slotNumber = slotNumber;
		this.inventoryNumber = inventoryNumber;
		this.stack = stack;
	}

	public SlotUpdatePacket(PacketBuffer buffer) {
		this.syncId = buffer.readInt();
		this.slotNumber = buffer.readInt();
		this.inventoryNumber = buffer.readInt();
		this.stack = StackUtilities.read(buffer.readCompoundTag());
	}

	public void encode(PacketBuffer buffer) {
		buffer.writeInt(syncId);
		buffer.writeInt(slotNumber);
		buffer.writeInt(inventoryNumber);
		buffer.writeCompoundTag(StackUtilities.write(stack));
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			PlayerEntity player = context.get().getSender();

			if (player.openContainer instanceof BaseContainer && player.openContainer.windowId == syncId) {
				BaseContainer container = (BaseContainer) player.openContainer;

				container.getInventory(inventoryNumber).setInventorySlotContents(slotNumber, stack);

				for (WAbstractWidget widget : container.getInterface().getAllWidgets()) {
					if (widget instanceof WSlot && ((WSlot) widget).getInventoryNumber() == inventoryNumber && ((WSlot) widget).getSlotNumber() == slotNumber) {
						((WSlot) widget).setStack(container.getInventory(inventoryNumber).getStackInSlot(slotNumber));
					}
				}
			}
		});
	}
}