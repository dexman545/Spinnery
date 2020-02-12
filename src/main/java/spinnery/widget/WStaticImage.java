package spinnery.widget;

import net.minecraft.util.ResourceLocation;
import spinnery.client.BaseRenderer;
import spinnery.widget.api.WFocusedMouseListener;

@OnlyIn(Dist.CLIENT)
@WFocusedMouseListener
public class WStaticImage extends WAbstractWidget {
	protected ResourceLocation texture;

	@Override
	public void draw() {
		if (isHidden()) {
			return;
		}

		int x = getX();
		int y = getY();
		int z = getZ();

		int sX = getWidth();
		int sY = getHeight();

		BaseRenderer.drawImage(x, y, z, sX, sY, getTexture());
	}

	public ResourceLocation getTexture() {
		return texture;
	}

	public <W extends WStaticImage> W setTexture(ResourceLocation texture) {
		this.texture = texture;
		return (W) this;
	}
}
