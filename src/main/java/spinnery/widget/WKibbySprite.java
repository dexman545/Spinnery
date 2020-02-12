package spinnery.widget;

import net.minecraft.util.ResourceLocation;
import spinnery.Spinnery;

@OnlyIn(Dist.CLIENT)
public final class WKibbySprite extends WStaticImage {
	public WKibbySprite() {
		setTexture(new ResourceLocation(Spinnery.MOD_ID, "textures/kirby.png"));
	}
}
