package spinnery.widget;

import net.minecraft.util.ResourceLocation;
import spinnery.client.BaseRenderer;
import spinnery.widget.api.WFocusedMouseListener;

@OnlyIn(Dist.CLIENT)
@WFocusedMouseListener
public class WDynamicImage extends WAbstractWidget {
	protected ResourceLocation[] textures;

	protected int currentImage = 0;

	public WDynamicImage textures(ResourceLocation... textures) {
		setTextures(textures);
		return this;
	}

	public int.endVertex() {
		if (getCurrentImage() < getTextures().length - 1) {
			setCurrentImage(getCurrentImage() + 1);
		} else {
			setCurrentImage(0);
		}
		return getCurrentImage();
	}

	public int getCurrentImage() {
		return currentImage;
	}

	public <W extends WDynamicImage> W setCurrentImage(int currentImage) {
		this.currentImage = currentImage;
		return (W) this;
	}

	public ResourceLocation[] getTextures() {
		return textures;
	}

	public <W extends WDynamicImage> W setTextures(ResourceLocation... textures) {
		this.textures = textures;
		return (W) this;
	}

	public int previous() {
		if (getCurrentImage() > 0) {
			setCurrentImage(getCurrentImage() - 1);
		} else {
			setCurrentImage(getTextures().length - 1);
		}
		return getCurrentImage();
	}

	@Override
	public void draw() {
		if (isHidden()) {
			return;
		}

		BaseRenderer.drawImage(getX(), getY(), getZ(), getWidth(), getHeight(), getTexture());
	}

	public ResourceLocation getTexture() {
		return textures[currentImage];
	}
}
