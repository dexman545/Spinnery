package spinnery.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class StackUtilities {
	/**
	 * @param stack ItemStack CompoundNBT will be written from
	 * @return ItemStack from tag
	 * @reason Support >64 ItemStack#count.
	 */
	public static CompoundNBT write(ItemStack stack) {
		ResourceLocation identifier = Registry.ITEM.getKey(stack.getItem());

		CompoundNBT tag = new CompoundNBT();

		tag.putString("id", identifier == null ? "minecraft:air" : identifier.toString());
		tag.putInt("Count", stack.getCount());
		if (stack.getTag() != null) {
			tag.put("tag", stack.getTag().copy());
		}

		return tag;
	}

	/**
	 * @param tag CompoundNBT ItemStack will be read from
	 * @return ItemStack from tag
	 * @reason Support >64 ItemStack#count.
	 */
	public static ItemStack read(CompoundNBT tag) {
		Item item = Registry.ITEM.getValue(new ResourceLocation(tag.getString("id"))).get();
		int count = tag.getInt("Count");

		ItemStack stack = new ItemStack(item, count);

		if (tag.contains("tag", 10)) {
			stack.setTag(tag.getCompound("tag"));
			stack.getItem().updateItemStackNBT(tag);
		}

		if (item.isDamageable()) {
			stack.setDamage(stack.getDamage());
		}

		return stack;
	}

	/**
	 * @param stackA Source ItemStack
	 * @param stackB Destination ItemStack
	 * @param maxA   Max. count of stackA
	 * @param maxB   Max. count of stackB
	 * @return Results
	 * @reason Support merge of stacks with customized maximum count.
	 */
	public static Pair<ItemStack, ItemStack> clamp(ItemStack stackA, ItemStack stackB, int maxA, int maxB) {
		Item itemA = stackA.getItem();
		Item itemB = stackB.getItem();

		if (stackA.isItemEqual(stackB)) {
			int countA = stackA.getCount();
			int countB = stackB.getCount();

			int availableA = Math.max(0, maxA - countA);
			int availableB = Math.max(0, maxB - countB);

			stackB.grow(Math.min(countA, availableB));
			stackA.setCount(Math.max(countA - availableB, 0));
		} else {
			if (stackA.isEmpty() && !stackB.isEmpty()) {
				int countA = stackA.getCount();
				int availableA = maxA - countA;

				int countB = stackB.getCount();

				stackA = new ItemStack(itemB, Math.min(countB, availableA));
				stackA.setTag(stackB.getTag());
				stackB.shrink(Math.min(countB, availableA));
			} else if (stackB.isEmpty() && !stackA.isEmpty()) {
				int countB = stackB.getCount();
				int availableB = maxB - countB;

				int countA = stackA.getCount();

				stackB = new ItemStack(itemA, Math.min(countA, availableB));
				stackB.setTag(stackA.getTag());
				stackA.shrink(Math.min(countA, availableB));
			}
		}

		return new Pair<>(stackA, stackB);
	}
}
