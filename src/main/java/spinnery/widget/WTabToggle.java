package spinnery.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import spinnery.client.BaseRenderer;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class WTabToggle extends WToggle {
	public static final int SHADOW_ON = 0;
	public static final int BACKGROUND_ON = 1;
	public static final int HIGHLIGHT_ON = 2;
	public static final int OUTLINE_ON = 3;
	public static final int SHADOW_OFF = 4;
	public static final int BACKGROUND_OFF = 5;
	public static final int HIGHLIGHT_OFF = 6;
	public static final int OUTLINE_OFF = 7;
	public static final int LABEL = 8;
	Item symbol;
	Text name;

	public WTabToggle(WPosition position, WSize size, WInterface linkedInterface, Item symbol, Text name) {
		super(position, size, linkedInterface);

		setInterface(linkedInterface);

		setPosition(position);

		setSize(size);

		setSymbol(symbol);

		setName(name);

		setTheme("light");
	}

	public static WWidget.Theme of(Map<String, String> rawTheme) {
		WWidget.Theme theme = new WWidget.Theme();
		theme.put(SHADOW_ON, WColor.of(rawTheme.get("shadow_on")));
		theme.put(BACKGROUND_ON, WColor.of(rawTheme.get("background_on")));
		theme.put(HIGHLIGHT_ON, WColor.of(rawTheme.get("highlight_on")));
		theme.put(OUTLINE_ON, WColor.of(rawTheme.get("outline_on")));
		theme.put(SHADOW_OFF, WColor.of(rawTheme.get("shadow_off")));
		theme.put(BACKGROUND_OFF, WColor.of(rawTheme.get("background_off")));
		theme.put(HIGHLIGHT_OFF, WColor.of(rawTheme.get("highlight_off")));
		theme.put(OUTLINE_OFF, WColor.of(rawTheme.get("outline_off")));
		theme.put(LABEL, WColor.of(rawTheme.get("label")));
		return theme;
	}

	public Text getName() {
		return name;
	}

	public void setName(Text name) {
		this.name = name;
	}

	@Override
	public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (!getToggleState()) {
			super.onMouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void draw() {
		if (isHidden()) {
			return;
		}

		int x = getX();
		int y = getY();
		int z = getZ();

		int sX = getWidth();
		int sY = getHeight() + 4;

		if (!getToggleState()) {
			BaseRenderer.drawPanel(x, y, z - 1, sX, sY, getResourceAsColor(SHADOW_OFF), getResourceAsColor(BACKGROUND_OFF), getResourceAsColor(HIGHLIGHT_OFF), getResourceAsColor(OUTLINE_OFF));
		} else {
			BaseRenderer.drawPanel(x, y, z - 1, sX, sY, getResourceAsColor(SHADOW_ON), getResourceAsColor(BACKGROUND_ON), getResourceAsColor(HIGHLIGHT_ON), getResourceAsColor(OUTLINE_ON));
		}

		RenderSystem.enableLighting();
		BaseRenderer.getItemRenderer().renderGuiItemIcon(new ItemStack(getSymbol(), 1), x + 4, y + 4);
		RenderSystem.disableLighting();
		BaseRenderer.drawText(isLabelShadowed(), name.asFormattedString(), x + 24, (int) (y + sY / 2 - 4.5), getResourceAsColor(LABEL).RGB);
	}

	public Item getSymbol() {
		return symbol;
	}

	public void setSymbol(Item symbol) {
		this.symbol = symbol;
	}
}
