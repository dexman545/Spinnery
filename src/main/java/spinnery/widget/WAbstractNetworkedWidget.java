package spinnery.widget;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import spinnery.packet.WidgetSyncPacket;
import spinnery.registry.NetworkHandler;
import spinnery.widget.api.WNetworked;

import java.util.function.BiConsumer;

@SuppressWarnings("unchecked")
public abstract class WAbstractNetworkedWidget extends WAbstractWidget implements WNetworked {
	protected BiConsumer<Event, CompoundNBT> consumerOnInterfaceEvent;
	protected int syncId;

	@Override
	public int getSyncId() {
		return syncId;
	}

	public <W extends WAbstractNetworkedWidget> W setSyncId(int syncId) {
		this.syncId = syncId;
		return (W) this;
	}

	@Override
	public void onInterfaceEvent(Event event, CompoundNBT payload) {
		if (this.consumerOnInterfaceEvent != null) {
			this.consumerOnInterfaceEvent.accept(event, payload);
		}
	}

	public BiConsumer<Event, CompoundNBT> getOnInterfaceEvent() {
		return consumerOnInterfaceEvent;
	}

	public <W extends WAbstractNetworkedWidget> W setOnInterfaceEvent(BiConsumer<Event, CompoundNBT> consumerOnInterfaceEvent) {
		this.consumerOnInterfaceEvent = consumerOnInterfaceEvent;
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public void sendCustomEvent(CompoundNBT payload) {
		NetworkHandler.INSTANCE.sendToServer(WidgetSyncPacket.createCustomInterfaceEventPacket(this, payload));
	}
}
