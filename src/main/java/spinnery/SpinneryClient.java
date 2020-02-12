package spinnery;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spinnery.registry.NetworkHandler;
import spinnery.registry.ResourceRegistry;
import spinnery.registry.WidgetRegistry;

public class SpinneryClient implements ClientModInitializer {
	public static final String LOG_ID = "Spinnery";
	public static final ResourceLocation MOD_ID = new ResourceLocation(LOG_ID.toLowerCase());
	public static Logger LOGGER = LogManager.getLogger("Spinnery");

	@Override
	public void onInitializeClient() {
		NetworkHandler.initializeClient();
		WidgetRegistry.initialize();
		ResourceRegistry.initialize();
	}
}
