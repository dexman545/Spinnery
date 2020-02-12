package spinnery.packet;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import spinnery.common.BaseContainer;
import spinnery.widget.api.Action;

import java.util.function.Supplier;

public class SlotClickPacket {
	public static final int ID = 0;

	int syncId;
	int slotNumber;
	int inventoryNumber;
	int button;
	Action action;

	public SlotClickPacket(int syncId, int slotNumber, int inventoryNumber, int button, Action action) {
		this.syncId = syncId;
		this.slotNumber = slotNumber;
		this.inventoryNumber = inventoryNumber;
		this.button = button;
		this.action = action;
	}

	public SlotClickPacket(PacketBuffer buffer) {
		this.syncId = buffer.readInt();
		this.slotNumber = buffer.readInt();
		this.inventoryNumber = buffer.readInt();
		this.button = buffer.readInt();
		this.action = buffer.readEnumValue(Action.class);
	}

	public void encode(PacketBuffer buffer) {
		buffer.writeInt(syncId);
		buffer.writeInt(slotNumber);
		buffer.writeInt(inventoryNumber);
		buffer.writeInt(button);
		buffer.writeEnumValue(action);
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			PlayerEntity player = context.get().getSender();

			if (player.openContainer instanceof BaseContainer && player.container.windowId == syncId) {
				((BaseContainer) player.openContainer).onSlotAction(slotNumber, inventoryNumber, button, action, player);
			}
		});
	}
}