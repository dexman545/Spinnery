package spinnery.debug;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.ITextComponent;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import spinnery.client.BaseRenderer;
import spinnery.common.BaseContainerScreen;
import spinnery.widget.WInterface;
import spinnery.widget.WPanel;
import spinnery.widget.WSlot;
import spinnery.widget.WStaticText;
import spinnery.widget.WTooltip;
import spinnery.widget.WVerticalBar;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

public class TestContainerScreen extends BaseContainerScreen<TestContainer> {
	MutableFloat currentPower = new MutableFloat(0);
	MutableInt maximumPower = new MutableInt(0);

	WInterface mainInterface;

	LiteralText powerText;

	WTooltip powerTooltip;

	WVerticalBar powerBar;

	WStaticText tooltipText;

	public TestContainerScreen(ITextComponent name, TestContainer container, PlayerEntity player) {
		super(name, container, player);

		mainInterface = getInterface();

		WPanel mainPanel = mainInterface.createChild(WPanel.class, Position.of(0, 0, 2), Size.of(170, 164))
				.setParent(mainInterface);

		mainPanel.setLabel(name);

		mainPanel.center();

		powerText = new LiteralText("FooBar");

		powerTooltip = mainPanel.createChild(WTooltip.class, Position.of(mainPanel, 0, 0, 3), Size.of(96, 32));

		powerBar = mainPanel.createChild(WVerticalBar.class, Position.of(mainPanel, 8, 24, 1), Size.of(24, 48))
				.setLimit(maximumPower)
				.setProgress(currentPower);

		tooltipText = powerTooltip.createChild(WStaticText.class, Position.of(powerTooltip, 4, 4, 0), Size.of(0, 0))
				.setText(powerText);

		powerTooltip.setHidden(true);

		powerTooltip.add(tooltipText);

		powerBar.setOnFocusGained((widget) -> {
			powerTooltip.setHidden(false);
		});

		powerTooltip.setOnMouseMoved((widget, mouseX, mouseY) -> {
			powerTooltip.setPosition(Position.of(mouseX + 12, mouseY - 4, 3));
			tooltipText.setPosition(Position.of(powerTooltip));
			powerTooltip.setSize(Size.of(BaseRenderer.getFontRenderer().getStringWidth(powerText.asString()) + 18, BaseRenderer.getFontRenderer().fontHeight));
		});

		powerBar.setOnFocusReleased((widget) -> {
			powerTooltip.setHidden(true);
		});

		powerBar.setProgress(currentPower);

		//WTabHolder randomTab = mainPanel.createChild(WTabHolder.class, Position.of(8, 8, 8), Size.of(128, 128));
		//WTabHolder.WTab tabA = randomTab.addTab(Items.BRAIN_CORAL, "Tab");
		//tabA.createChild(WKibbySprite.class, Position.of(tabA, 0, 0, 0), Size.of(32, 32));

		//powerBar.overrideStyle("background", "arno:textures/widget/" + name.asString().substring(0, name.asString().indexOf(' ')).toLowerCase() + "_battery_bar_background.png");
		//powerBar.overrideStyle("foreground", "arno:textures/widget/" + name.asString().substring(0, name.asString().indexOf(' ')).toLowerCase() + "_battery_bar_foreground.png");

		WSlot.addPlayerInventory(Position.of(mainPanel, 4, 84, 0), Size.of(18, 18), mainPanel);
	}

	@Override
	public void tick() {
		currentPower.setValue(256);
		maximumPower.setValue(256);

		//tooltipText.setText(new LiteralText("").append(new TranslatableText("text.arno.power")).append(currentPower.floatValue() + ArnoResolver.POWER_SYMBOL));
	}
}