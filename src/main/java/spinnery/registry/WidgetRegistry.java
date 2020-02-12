package spinnery.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.ResourceLocation;
import spinnery.widget.WAbstractWidget;
import spinnery.widget.WButton;
import spinnery.widget.WDropdown;
import spinnery.widget.WDynamicImage;
import spinnery.widget.WHorizontalBar;
import spinnery.widget.WHorizontalScrollbar;
import spinnery.widget.WHorizontalSlider;
import spinnery.widget.WPanel;
import spinnery.widget.WSlot;
import spinnery.widget.WStaticImage;
import spinnery.widget.WStaticText;
import spinnery.widget.WTabHolder;
import spinnery.widget.WTabToggle;
import spinnery.widget.WTextArea;
import spinnery.widget.WTextField;
import spinnery.widget.WTexturedButton;
import spinnery.widget.WToggle;
import spinnery.widget.WTooltip;
import spinnery.widget.WVerticalBar;
import spinnery.widget.WVerticalScrollbar;
import spinnery.widget.WVerticalSlider;

public class WidgetRegistry {
	private static BiMap<ResourceLocation, Class<? extends WAbstractWidget>> widgetMap = HashBiMap.create();

	public static Class<? extends WAbstractWidget> get(String className) {
		for (Class<? extends WAbstractWidget> widgetClass : widgetMap.values()) {
			if (widgetClass.getName().equals(className)) {
				return widgetClass;
			}
		}
		return null;
	}

	public static Class<? extends WAbstractWidget> get(ResourceLocation id) {
		return widgetMap.get(id);
	}

	public static ResourceLocation getId(Class<? extends WAbstractWidget> wClass) {
		return widgetMap.inverse().get(wClass);
	}

	public static void initialize() {
		register(new ResourceLocation("spinnery", "widget"), WAbstractWidget.class);
		register(new ResourceLocation("spinnery", "button"), WButton.class);
		register(new ResourceLocation("spinnery", "dropdown"), WDropdown.class);
		register(new ResourceLocation("spinnery", "dynamic_image"), WDynamicImage.class);
		register(new ResourceLocation("spinnery", "horizontal_slider"), WHorizontalSlider.class);
		register(new ResourceLocation("spinnery", "panel"), WPanel.class);
		register(new ResourceLocation("spinnery", "slot"), WSlot.class);
		register(new ResourceLocation("spinnery", "static_image"), WStaticImage.class);
		register(new ResourceLocation("spinnery", "static_text"), WStaticText.class);
		register(new ResourceLocation("spinnery", "tab_holder"), WTabHolder.class);
		register(new ResourceLocation("spinnery", "tab_toggle"), WTabToggle.class);
		register(new ResourceLocation("spinnery", "toggle"), WToggle.class);
		register(new ResourceLocation("spinnery", "tooltip"), WTooltip.class);
		register(new ResourceLocation("spinnery", "vertical_slider"), WVerticalSlider.class);
		register(new ResourceLocation("spinnery", "vertical_bar"), WVerticalBar.class);
		register(new ResourceLocation("spinnery", "horizontal_bar"), WHorizontalBar.class);
		register(new ResourceLocation("spinnery", "vertical_scrollbar"), WVerticalScrollbar.class);
		register(new ResourceLocation("spinnery", "horizontal_scrollbar"), WHorizontalScrollbar.class);
		register(new ResourceLocation("spinnery", "textured_button"), WTexturedButton.class);
		register(new ResourceLocation("spinnery", "text_area"), WTextArea.class);
		register(new ResourceLocation("spinnery", "text_field"), WTextField.class);
	}

	public static void register(ResourceLocation id, Class<? extends WAbstractWidget> wClass) {
		widgetMap.put(id, wClass);
	}
}
