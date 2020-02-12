package spinnery.debug;

import net.minecraft.entity.player.PlayerInventory;
import spinnery.common.BaseContainer;
import spinnery.widget.WSlot;

public class TestContainer extends BaseContainer {
	public TestContainer(int synchronizationID, PlayerInventory newLinkedPlayerInventory) {
		super(synchronizationID, newLinkedPlayerInventory);

		WSlot.addHeadlessPlayerInventory(getInterface());
	}
}