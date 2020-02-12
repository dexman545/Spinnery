package spinnery.packet;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import spinnery.common.BaseContainer;
import spinnery.widget.api.Action;

import java.util.function.Supplier;

public class SlotDragPacket {
	public static final int ID = 1;

	int syncId;
	int[] slotNumbers;
	int[] inventoryNumbers;
	Action action;

	public SlotDragPacket(int syncId, int[] slotNumbers, int[] inventoryNumbers, Action action) {
		this.syncId = syncId;
		this.slotNumbers = slotNumbers;
		this.inventoryNumbers = inventoryNumbers;
		this.action = action;
	}

	public SlotDragPacket(PacketBuffer buffer) {
		this.syncId = buffer.readInt();
		this.slotNumbers = buffer.readVarIntArray();
		this.inventoryNumbers = buffer.readVarIntArray();
		this.action = buffer.readEnumValue(Action.class);
	}

	public void encode(PacketBuffer buffer) {
		buffer.writeInt(syncId);
		buffer.writeVarIntArray(slotNumbers);
		buffer.writeVarIntArray(inventoryNumbers);
		buffer.writeEnumValue(action);
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			PlayerEntity player = context.get().getSender();

			if (player.openContainer instanceof BaseContainer && player.container.windowId == syncId) {
				((BaseContainer) player.openContainer).onSlotDrag(slotNumbers, inventoryNumbers, action);
			}
		});
	}
}