package spinnery.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import spinnery.widget.api.Style;
import spinnery.widget.api.Theme;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ThemeRegistry {
	public static final ResourceLocation DEFAULT_THEME = new ResourceLocation("spinnery", "default");
	private static final Map<ResourceLocation, Theme> themes = new HashMap<>();
	private static Theme defaultTheme;

	public static void clear() {
		themes.clear();
	}

	public static void register(Theme theme) {
		if (theme == null) return;
		if (theme.getId().equals(DEFAULT_THEME)) {
			defaultTheme = theme;
		} else {
			themes.put(theme.getId(), theme);
		}
	}

	public static Style getStyle(ResourceLocation themeId, ResourceLocation widgetId) {
		Theme theme = themes.get(themeId);
		if (theme == null) theme = defaultTheme;
		return theme.getStyle(widgetId);
	}
}
