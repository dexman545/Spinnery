package spinnery.widget;

import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.mutable.Mutable;

public abstract class WAbstractBar extends WAbstractWidget {
	protected Mutable<Number> limit;
	protected Mutable<Number> progress;

	public Mutable<Number> getLimit() {
		return limit;
	}

	public <W extends WAbstractBar> W setLimit(Mutable<Number> limit) {
		this.limit = limit;
		return (W) this;
	}

	public Mutable<Number> getProgress() {
		return progress;
	}

	public <W extends WAbstractBar> W setProgress(Mutable<Number> progress) {
		this.progress = progress;
		return (W) this;
	}

	public ResourceLocation getBackgroundTexture() {
		return getStyle().asResourceLocation("background");
	}

	public <W extends WAbstractBar> W setBackgroundTexture(ResourceLocation backgroundTexture) {
		overrideStyle("background", backgroundTexture);
		return (W) this;
	}

	public ResourceLocation getForegroundTexture() {
		return getStyle().asResourceLocation("foreground");
	}

	public <W extends WAbstractBar> W setForegroundTexture(ResourceLocation foregroundTexture) {
		overrideStyle("foreground", foregroundTexture);
		return (W) this;
	}
}
