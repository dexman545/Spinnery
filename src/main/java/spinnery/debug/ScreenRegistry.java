package spinnery.debug;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ScreenRegistry {
	public ScreenRegistry() {
		// NO-OP
	}

	@SubscribeEvent
	public void registerScreens(RegistryEvent.Register<ContainerType<?>> event) {
		event.getRegistry().register(IForgeContainerType.create((windowId, inventory, data) -> {
			return new TestContainer(windowId, inventory);
		}));
	}
}