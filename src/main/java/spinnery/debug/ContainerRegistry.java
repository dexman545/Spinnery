package spinnery.debug;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.util.ResourceLocation;

public class ContainerRegistry {
	public static final ResourceLocation TEST_CONTAINER = register(new ResourceLocation("test"));

	public ContainerRegistry() {
		// NO-OP
	}

	public static void initialize() {
		// NO-OP
	}

	public static <I extends ResourceLocation> I register(I ID) {
		ContainerProviderRegistry.INSTANCE.registerFactory(ID, (syncId, id, player, buffer) -> new TestContainer(syncId, player.inventory));
		return ID;
	}
}