package spinnery.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundNBT;

public class InventoryUtilities {
	/**
	 * @param inventory Inventory CompoundNBT will be written from
	 * @return CompoundNBT from inventory
	 * @reason Support >64 ItemStack#count
	 */
	public static <T extends Inventory> CompoundNBT write(T inventory, CompoundNBT tag) {
		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			tag.put(String.valueOf(i), StackUtilities.write(inventory.getStackInSlot(i)));
		}

		return tag;
	}

	/**
	 * @param tag CompoundNBT Inventory will be read from
	 * @return Inventory from tag
	 * @reeason Support >64 ItemStack#count
	 */
	public static <T extends Inventory> T read(T inventory, CompoundNBT tag) {
		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			inventory.setInventorySlotContents(i, StackUtilities.read((CompoundNBT) tag.get(String.valueOf(i))));
		}

		return inventory;
	}
}
