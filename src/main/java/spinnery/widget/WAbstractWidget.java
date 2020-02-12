package spinnery.widget;

import net.minecraft.text.LiteralText;
import net.minecraft.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tickable;
import spinnery.registry.ThemeRegistry;
import spinnery.registry.WidgetRegistry;
import spinnery.util.EventUtilities;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;
import spinnery.widget.api.Style;
import spinnery.widget.api.WDelegatedEventListener;
import spinnery.widget.api.WEventListener;
import spinnery.widget.api.WLayoutElement;
import spinnery.widget.api.WStyleProvider;
import spinnery.widget.api.WThemable;
import spinnery.widget.api.listener.WAlignListener;
import spinnery.widget.api.listener.WCharTypeListener;
import spinnery.widget.api.listener.WFocusGainListener;
import spinnery.widget.api.listener.WFocusLossListener;
import spinnery.widget.api.listener.WKeyPressListener;
import spinnery.widget.api.listener.WKeyReleaseListener;
import spinnery.widget.api.listener.WMouseClickListener;
import spinnery.widget.api.listener.WMouseDragListener;
import spinnery.widget.api.listener.WMouseMoveListener;
import spinnery.widget.api.listener.WMouseReleaseListener;
import spinnery.widget.api.listener.WMouseScrollListener;
import spinnery.widget.api.listener.WTooltipDrawListener;

import static spinnery.registry.ThemeRegistry.DEFAULT_THEME;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class WAbstractWidget implements Tickable,
		WLayoutElement, WThemable, WStyleProvider, WEventListener {
	protected WInterface linkedInterface;
	protected WLayoutElement parent;

	protected Position position = Position.origin();
	protected Size size = Size.of(0, 0);

	protected ITextComponent label = new LiteralText("");
	protected boolean isHidden = false;
	protected boolean hasFocus = false;

	protected WCharTypeListener runnableOnCharTyped;
	protected WMouseClickListener runnableOnMouseClicked;
	protected WKeyPressListener runnableOnKeyPressed;
	protected WKeyReleaseListener runnableOnKeyReleased;
	protected WFocusGainListener runnableOnFocusGained;
	protected WFocusLossListener runnableOnFocusReleased;
	protected WTooltipDrawListener runnableOnDrawTooltip;
	protected WMouseReleaseListener runnableOnMouseReleased;
	protected WMouseMoveListener runnableOnMouseMoved;
	protected WMouseDragListener runnableOnMouseDragged;
	protected WMouseScrollListener runnableOnMouseScrolled;
	protected WAlignListener runnableOnAlign;

	protected ResourceLocation theme;
	protected Style styleOverrides = new Style();

	public WAbstractWidget() {
	}

	////// SHARED //////

	public WInterface getInterface() {
		return linkedInterface;
	}

	public <W extends WAbstractWidget> W setInterface(WInterface linkedInterface) {
		this.linkedInterface = linkedInterface;
		return (W) this;
	}

	@Override
	public void tick() {
	}

	////// CLIENTSIDE //////

	// Common functionality

	@OnlyIn(Dist.CLIENT)
	public boolean hasLabel() {
		return !label.asFormattedString().isEmpty();
	}

	@OnlyIn(Dist.CLIENT)
	public ITextComponent getLabel() {
		return label;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setLabel(ITextComponent label) {
		this.label = label;
		onLayoutChange();
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setLabel(String label) {
		this.label = new LiteralText(label);
		onLayoutChange();
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isLabelShadowed() {
		return getStyle().asBoolean("label.shadow");
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Style getStyle() {
		ResourceLocation widgetId = WidgetRegistry.getId(getClass());
		if (widgetId == null) {
			Class superClass = getClass().getSuperclass();
			while (superClass != Object.class) {
				widgetId = WidgetRegistry.getId(superClass);
				if (widgetId != null) break;
				superClass = superClass.getSuperclass();
			}
		}
		return Style.of(ThemeRegistry.getStyle(getTheme(), widgetId)).mergeFrom(styleOverrides);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ResourceLocation getTheme() {
		if (theme != null) return theme;
		if (parent != null && parent instanceof WThemable) return ((WThemable) parent).getTheme();
		if (linkedInterface != null && linkedInterface.getTheme() != null)
			return linkedInterface.getTheme();
		return DEFAULT_THEME;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setTheme(ResourceLocation theme) {
		this.theme = theme;
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setTheme(String theme) {
		return setTheme(new ResourceLocation(theme));
	}

	// Alignment helpers

	@OnlyIn(Dist.CLIENT)
	public void align() {
	}

	@OnlyIn(Dist.CLIENT)
	public void center() {
		setPosition(Position.of(getPosition())
				.setX(getParent().getX() + getParent().getWidth() / 2 - getWidth() / 2)
				.setY(getParent().getY() + getParent().getHeight() / 2 - getHeight() / 2));
	}

	@OnlyIn(Dist.CLIENT)
	public Position getPosition() {
		return position;
	}

	@OnlyIn(Dist.CLIENT)
	public WLayoutElement getParent() {
		return parent;
	}

	// Focus helpers

	public <W extends WAbstractWidget> W setParent(WLayoutElement parent) {
		this.parent = parent;
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public int getWidth() {
		return size.getWidth();
	}

	@OnlyIn(Dist.CLIENT)
	public int getHeight() {
		return size.getHeight();
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setHeight(int height) {
		return setSize(Size.of(size).setHeight(height));
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setWidth(int width) {
		return setSize(Size.of(size).setWidth(width));
	}

	// WStyleProvider

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setPosition(Position position) {
		this.position = position;
		onLayoutChange();
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public void centerX() {
		setPosition(Position.of(getPosition())
				.setX(getParent().getX() + getParent().getWidth() / 2 - getWidth() / 2));
	}

	// WThemable

	@OnlyIn(Dist.CLIENT)
	public void centerY() {
		setPosition(Position.of(getPosition())
				.setY(getParent().getY() + getParent().getHeight() / 2 - getHeight() / 2));
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W overrideStyle(String property, Object value) {
		getStyle().override(property, value);
		return (W) this;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void draw() {
	}

	// WLayoutElement

	@OnlyIn(Dist.CLIENT)
	public Size getSize() {
		return size;
	}

	// WPositioned

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setSize(Size size) {
		this.size = size;
		onLayoutChange();
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onKeyPressed(int keyPressed, int character, int keyModifier) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveKeyboard(widget))
					widget.onKeyPressed(keyPressed, character, keyModifier);
			}
		}
		if (runnableOnKeyPressed != null) {
			runnableOnKeyPressed.event(this, keyPressed, character, keyModifier);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onKeyReleased(int keyReleased, int character, int keyModifier) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveKeyboard(widget))
					widget.onKeyReleased(keyReleased, character, keyModifier);
			}
		}
		if (runnableOnKeyReleased != null) {
			runnableOnKeyReleased.event(this, keyReleased, character, keyModifier);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onCharTyped(char character, int keyCode) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveKeyboard(widget))
					widget.onCharTyped(character, keyCode);
			}
		}
		if (runnableOnCharTyped != null) {
			runnableOnCharTyped.event(this, character, keyCode);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onFocusGained() {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				widget.onFocusGained();
			}
		}
		if (runnableOnFocusGained != null && isFocused()) {
			runnableOnFocusGained.event(this);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onFocusReleased() {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				widget.onFocusReleased();
			}
		}
		if (runnableOnFocusReleased != null && !isFocused()) {
			runnableOnFocusReleased.event(this);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				widget.onMouseReleased(mouseX, mouseY, mouseButton);
			}
		}
		if (runnableOnMouseReleased != null) {
			runnableOnMouseReleased.event(this, mouseX, mouseY, mouseButton);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveMouse(widget))
					widget.onMouseClicked(mouseX, mouseY, mouseButton);
			}
		}
		if (runnableOnMouseClicked != null) {
			runnableOnMouseClicked.event(this, mouseX, mouseY, mouseButton);
		}
	}

	// WSized

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onMouseDragged(int mouseX, int mouseY, int mouseButton, double deltaX, double deltaY) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveMouse(widget))
					widget.onMouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
			}
		}
		if (runnableOnMouseDragged != null) {
			runnableOnMouseDragged.event(this, mouseX, mouseY, mouseButton, deltaX, deltaY);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onMouseMoved(int mouseX, int mouseY) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (widget instanceof WAbstractWidget)
					((WAbstractWidget) widget).updateFocus(mouseX, mouseY);
				if (EventUtilities.canReceiveMouse(widget)) widget.onMouseMoved(mouseX, mouseY);
			}
		}
		if (runnableOnMouseMoved != null) {
			runnableOnMouseMoved.event(this, mouseX, mouseY);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public boolean updateFocus(int mouseX, int mouseY) {
		if (isHidden()) {
			return false;
		}

		setFocus(isWithinBounds(mouseX, mouseY));
		return isFocused();
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isHidden() {
		return isHidden;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setHidden(boolean isHidden) {
		this.isHidden = isHidden;
		setFocus(false);
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public void setFocus(boolean hasFocus) {
		if (!isFocused() && hasFocus) {
			this.hasFocus = hasFocus;
			onFocusGained();
		}
		if (isFocused() && !hasFocus) {
			this.hasFocus = hasFocus;
			onFocusReleased();
		}

	}

	// Event runners

	@OnlyIn(Dist.CLIENT)
	public boolean isFocused() {
		return hasFocus;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isWithinBounds(int positionX, int positionY) {
		return isWithinBounds(positionX, positionY, 0);
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isWithinBounds(int positionX, int positionY, int tolerance) {
		return positionX + tolerance > getX()
				&& positionX - tolerance < getX() + getWidth()
				&& positionY + tolerance > getY()
				&& positionY - tolerance < getY() + getHeight();
	}

	@OnlyIn(Dist.CLIENT)
	public int getX() {
		return position.getX();
	}

	@OnlyIn(Dist.CLIENT)
	public int getY() {
		return position.getY();
	}

	@OnlyIn(Dist.CLIENT)
	public int getZ() {
		return position.getZ();
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setZ(int z) {
		return setPosition(Position.of(position).setZ(z));
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setY(int y) {
		return setPosition(Position.of(position).setY(y));
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setX(int x) {
		return setPosition(Position.of(position).setX(x));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onMouseScrolled(int mouseX, int mouseY, double deltaY) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveMouse(widget))
					widget.onMouseScrolled(mouseX, mouseY, deltaY);
			}
		}
		if (runnableOnMouseScrolled != null) {
			runnableOnMouseScrolled.event(this, mouseX, mouseY, deltaY);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onDrawTooltip(int mouseX, int mouseY) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				widget.onDrawTooltip(mouseX, mouseY);
			}
		}
		if (runnableOnDrawTooltip != null) {
			runnableOnDrawTooltip.event(this, mouseX, mouseY);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onAlign() {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				widget.onAlign();
			}
		}
		if (runnableOnAlign != null) {
			runnableOnAlign.event(this);
		}
		onLayoutChange();
	}

	// Event runner setters

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WFocusGainListener<W> getOnFocusGained() {
		return runnableOnFocusGained;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnFocusGained(WFocusGainListener<W> linkedRunnable) {
		this.runnableOnFocusGained = linkedRunnable;
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WFocusLossListener<W> getOnFocusReleased() {
		return runnableOnFocusReleased;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnFocusReleased(WFocusLossListener<W> linkedRunnable) {
		this.runnableOnFocusReleased = linkedRunnable;
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WKeyPressListener<W> getOnKeyPressed() {
		return runnableOnKeyPressed;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnKeyPressed(WKeyPressListener<W> linkedRunnable) {
		this.runnableOnKeyPressed = linkedRunnable;
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WCharTypeListener<W> getOnCharTyped() {
		return runnableOnCharTyped;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnCharTyped(WCharTypeListener<W> linkedRunnable) {
		this.runnableOnCharTyped = linkedRunnable;
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WKeyReleaseListener<W> getOnKeyReleased() {
		return runnableOnKeyReleased;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnKeyReleased(WKeyReleaseListener<W> linkedRunnable) {
		this.runnableOnKeyReleased = linkedRunnable;
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WMouseClickListener<W> getOnMouseClicked() {
		return runnableOnMouseClicked;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnMouseClicked(WMouseClickListener<W> linkedRunnable) {
		this.runnableOnMouseClicked = linkedRunnable;
		return (W) this;
	}

	// Event runner getters

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WMouseDragListener<W> getOnMouseDragged() {
		return runnableOnMouseDragged;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnMouseDragged(WMouseDragListener<W> linkedRunnable) {
		this.runnableOnMouseDragged = linkedRunnable;
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WMouseMoveListener<W> getOnMouseMoved() {
		return runnableOnMouseMoved;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnMouseMoved(WMouseMoveListener<W> linkedRunnable) {
		this.runnableOnMouseMoved = linkedRunnable;
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WMouseScrollListener<W> getOnMouseScrolled() {
		return runnableOnMouseScrolled;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnMouseScrolled(WMouseScrollListener<W> linkedRunnable) {
		this.runnableOnMouseScrolled = linkedRunnable;
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WMouseReleaseListener<W> getOnMouseReleased() {
		return runnableOnMouseReleased;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnMouseReleased(WMouseReleaseListener<W> linkedRunnable) {
		this.runnableOnMouseReleased = linkedRunnable;
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WTooltipDrawListener<W> getOnDrawTooltip() {
		return runnableOnDrawTooltip;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnDrawTooltip(WTooltipDrawListener<W> linkedRunnable) {
		this.runnableOnDrawTooltip = linkedRunnable;
		return (W) this;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> WAlignListener<W> getOnAlign() {
		return runnableOnAlign;
	}

	@OnlyIn(Dist.CLIENT)
	public <W extends WAbstractWidget> W setOnAlign(WAlignListener<W> linkedRunnable) {
		this.runnableOnAlign = linkedRunnable;
		return (W) this;
	}
}
