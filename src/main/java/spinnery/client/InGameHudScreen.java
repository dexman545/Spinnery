package spinnery.client;

import net.minecraft.client.gui.IngameGui;
import spinnery.widget.WInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InGameHudScreen {
	protected static WInterface hudInterface = null;
	protected static IngameGui inGameHudCache = null;
	protected static List<Runnable> onInitialize = new ArrayList<>();

	public static void onInitialize(IngameGui inGameHud) {
		inGameHudCache = inGameHud;
		hudInterface = ((Accessor) inGameHud).getInterface();
		for (Runnable r : onInitialize) {
			r.run();
		}
	}

	public static void addOnInitialize(Runnable... r) {
		onInitialize.addAll(Arrays.asList(r));
	}

	public static void removeOnInitialize(Runnable... r) {
		onInitialize.removeAll(Arrays.asList(r));
	}

	public static WInterface getHolder() {
		return hudInterface;
	}

	public static IngameGui getInGameHud() {
		return inGameHudCache;
	}

	public interface Accessor {
		WInterface getInterface();

		IngameGui getInGameHud();
	}
}
