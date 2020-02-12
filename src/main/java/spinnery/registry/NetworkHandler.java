package spinnery.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import spinnery.Spinnery;
import spinnery.packet.SlotClickPacket;
import spinnery.packet.SlotDragPacket;
import spinnery.packet.SlotUpdatePacket;
import spinnery.packet.WidgetSyncPacket;

public class NetworkHandler {
	public static SimpleChannel INSTANCE;

	public NetworkHandler() {
		INSTANCE =  NetworkRegistry.newSimpleChannel(new ResourceLocation(Spinnery.MOD_ID, Spinnery.MOD_ID), () -> "1.0", s -> true, s -> true);
	}

	public static void initialize() {
		INSTANCE.registerMessage(
			SlotClickPacket.ID,
			SlotClickPacket.class,
			SlotClickPacket::encode,
			SlotClickPacket::new,
			SlotClickPacket::handle
		);

		INSTANCE.registerMessage(
			SlotDragPacket.ID,
			SlotDragPacket.class,
			SlotDragPacket::encode,
			SlotDragPacket::new,
			SlotDragPacket::handle
		);

		INSTANCE.registerMessage(
			SlotUpdatePacket.ID,
			SlotUpdatePacket.class,
			SlotUpdatePacket::encode,
			SlotUpdatePacket::new,
			SlotUpdatePacket::handle
		);

		INSTANCE.registerMessage(
			WidgetSyncPacket.ID,
			WidgetSyncPacket.class,
			WidgetSyncPacket::encode,
			WidgetSyncPacket::new,
			WidgetSyncPacket::handle
		);
	}
}
