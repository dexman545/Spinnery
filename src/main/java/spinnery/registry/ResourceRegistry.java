package spinnery.registry;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.impl.SyntaxError;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.Level;
import spinnery.Spinnery;
import spinnery.widget.api.Theme;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

@OnlyIn(Dist.CLIENT)
public class ResourceRegistry {
	public static final IFutureReloadListener RESOURCE_LISTENER = (stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) -> {
		clear();
		load(resourceManager);
		return null;
	};

	@SubscribeEvent
	public static void initialize(FMLServerStartingEvent event) {
		event.getServer().getResourceManager().addReloadListener(RESOURCE_LISTENER);
	}

	public static void clear() {
		ThemeRegistry.clear();
	}

	public static void load(IResourceManager resourceManager) {
		Collection<ResourceLocation> themeFiles = resourceManager.getAllResourceLocations("spinnery",
				(string) -> string.endsWith(".theme.json5"));

		for (ResourceLocation id : themeFiles) {
			try {
				ResourceLocation themeId = new ResourceLocation(id.getNamespace(),
						id.getPath().replaceFirst("^spinnery/", "").replaceFirst("\\.theme\\.json5", ""));
				register(themeId, resourceManager.getResource(id).getInputStream());
			} catch (IOException e) {
				Spinnery.LOGGER.warn("[Spinnery] Failed to load theme {}.", id);
			}
		}
	}

	public static void register(ResourceLocation id, InputStream inputStream) {
		try {
			JsonObject themeDef = Jankson.builder().build().load(inputStream);
			Theme theme = Theme.of(id, themeDef);
			ThemeRegistry.register(theme);
		} catch (IOException e) {
			Spinnery.LOGGER.log(Level.ERROR, "Could not read theme file", e);
		} catch (SyntaxError syntaxError) {
			Spinnery.LOGGER.log(Level.ERROR, "Syntax error in theme file", syntaxError);
		}
	}
}
