package spinnery.common;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import spinnery.util.StackUtilities;
import spinnery.widget.WAbstractWidget;
import spinnery.widget.WInterface;
import spinnery.widget.WSlot;
import spinnery.widget.api.Action;
import spinnery.widget.api.WNetworked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BaseContainer extends Container implements ITickable {
	public static final int PLAYER_INVENTORY = 0;
	protected final WInterface serverInterface;
	public Map<Integer, IInventory> linkedInventories = new HashMap<>();
	public Map<Integer, Map<Integer, ItemStack>> cachedInventories = new HashMap<>();
	protected Set<WSlot> splitSlots = new HashSet<>();
	protected Set<WSlot> singleSlots = new HashSet<>();
	protected Map<Integer, Map<Integer, ItemStack>> previewStacks = new HashMap<>();
	protected ItemStack previewCursorStack = ItemStack.EMPTY;
	protected World linkedWorld;

	public BaseContainer(int synchronizationID, PlayerInventory linkedPlayerInventory) {
		super(null, synchronizationID);
		getInventories().put(PLAYER_INVENTORY, linkedPlayerInventory);
		setLinkedWorld(linkedPlayerInventory.player.world);
		serverInterface = new WInterface(this);
	}

	public Map<Integer, IInventory> getInventories() {
		return linkedInventories;
	}

	public <C extends BaseContainer> C setLinkedWorld(World linkedWorld) {
		this.linkedWorld = linkedWorld;
		return (C) this;
	}

	@OnlyIn(Dist.CLIENT)
	public ItemStack getPreviewCursorStack() {
		return previewCursorStack;
	}

	@OnlyIn(Dist.CLIENT)
	public <C extends BaseContainer> C setPreviewCursorStack(ItemStack previewCursorStack) {
		this.previewCursorStack = previewCursorStack;
		return (C) this;
	}

	@OnlyIn(Dist.CLIENT)
	public void flush() {
		getInterface().getContainer().getDragSlots(GLFW.GLFW_MOUSE_BUTTON_1).clear();
		getInterface().getContainer().getDragSlots(GLFW.GLFW_MOUSE_BUTTON_2).clear();
		getInterface().getContainer().getPreviewStacks().clear();
		getInterface().getContainer().setPreviewCursorStack(ItemStack.EMPTY);
	}

	@OnlyIn(Dist.CLIENT)
	public Set<WSlot> getDragSlots(int mouseButton) {
		switch (mouseButton) {
			case 0:
				return splitSlots;
			case 1:
				return singleSlots;
			default:
				return null;
		}
	}

	public WInterface getInterface() {
		return serverInterface;
	}

	@OnlyIn(Dist.CLIENT)
	public Map<Integer, Map<Integer, ItemStack>> getPreviewStacks() {
		return previewStacks;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isDragging() {
		return getDragSlots(GLFW.GLFW_MOUSE_BUTTON_1).isEmpty() || getDragSlots(GLFW.GLFW_MOUSE_BUTTON_2).isEmpty();
	}

	public void onInterfaceEvent(int widgetSyncId, WNetworked.Event event, CompoundNBT payload) {
		Set<WAbstractWidget> checkWidgets = serverInterface.getAllWidgets();
		for (WAbstractWidget widget : checkWidgets) {
			if (!(widget instanceof WNetworked)) continue;
			if (((WNetworked) widget).getSyncId() == widgetSyncId) {
				((WNetworked) widget).onInterfaceEvent(event, payload);
				return;
			}
		}
	}

	public void onSlotDrag(int[] slotNumber, int[] inventoryNumber, Action action) {
		HashMap<Integer, WSlot> slots = new HashMap<>();

		for (int i = 0; i < slotNumber.length; ++i) {
			for (WAbstractWidget widget : serverInterface.getAllWidgets()) {
				if (widget instanceof WSlot && ((WSlot) widget).getSlotNumber() == slotNumber[i] && ((WSlot) widget).getInventoryNumber() == inventoryNumber[i]) {
					slots.put(i, (WSlot) widget);
				}
			}
		}

		if (slots.isEmpty()) {
			return;
		}

		int split = -1;

		if (action == Action.DRAG_SPLIT || action == Action.DRAG_SPLIT_PREVIEW) {
			split = getPlayerInventory().getItemStack().getCount() / slots.size();
		} else if (action == Action.DRAG_SINGLE || action == Action.DRAG_SINGLE_PREVIEW) {
			split = 1;
		}

		ItemStack stackA = ItemStack.EMPTY;

		if (action == Action.DRAG_SINGLE || action == Action.DRAG_SPLIT) {
			stackA = getPlayerInventory().getItemStack();
		} else if (action == Action.DRAG_SINGLE_PREVIEW || action == Action.DRAG_SPLIT_PREVIEW) {
			stackA = getPlayerInventory().getItemStack().copy();
		}

		for (Integer number : slots.keySet()) {
			WSlot slotA = slots.get(number);

			ItemStack stackB = ItemStack.EMPTY;

			if (action == Action.DRAG_SINGLE || action == Action.DRAG_SPLIT) {
				stackB = slotA.getStack();
			} else if (action == Action.DRAG_SINGLE_PREVIEW || action == Action.DRAG_SPLIT_PREVIEW) {
				stackB = slotA.getStack().copy();
			}


			Pair<ItemStack, ItemStack> stacks = StackUtilities.clamp(stackA, stackB, split, split);

			if (action == Action.DRAG_SINGLE || action == Action.DRAG_SPLIT) {
				stackA = stacks.getFirst();
				slotA.getInterface().getContainer().previewCursorStack = ItemStack.EMPTY;
				slotA.setStack(stacks.getSecond());
			} else if (action == Action.DRAG_SINGLE_PREVIEW || action == Action.DRAG_SPLIT_PREVIEW) {
				slotA.getInterface().getContainer().previewCursorStack = stacks.getFirst().copy();
				slotA.setPreviewStack(stacks.getSecond().copy());
			}
		}
	}

	public PlayerInventory getPlayerInventory() {
		return (PlayerInventory) linkedInventories.get(PLAYER_INVENTORY);
	}

	public void onSlotAction(int slotNumber, int inventoryNumber, int button, Action action, PlayerEntity player) {
		WSlot slotA = null;

		for (WAbstractWidget widget : serverInterface.getAllWidgets()) {
			if (widget instanceof WSlot && ((WSlot) widget).getSlotNumber() == slotNumber && ((WSlot) widget).getInventoryNumber() == inventoryNumber) {
				slotA = (WSlot) widget;
			}
		}

		if (slotA == null) {
			return;
		}

		ItemStack stackA = slotA.getStack();
		ItemStack stackB = player.inventory.getItemStack();

		switch (action) {
			case PICKUP: {
				if (!stackA.isItemEqual(stackB) || stackA.getTag() != stackB.getTag()) {
					if (button == 0) { // Swap with existing // LMB
						if (slotA.isOverrideMaximumCount()) {
							if (stackA.isEmpty()) {
								ItemStack stackC = stackA.copy();
								stackA = stackB.copy();
								stackB = stackC.copy();
							} else if (stackB.isEmpty()) {
								int maxA = slotA.getMaxStackSize();
								int maxB = stackB.getMaxStackSize();

								int countA = stackA.getCount();
								int countB = stackB.getCount();

								int availableA = maxA - countA;
								int availableB = maxB - countB;

								ItemStack stackC = stackA.copy();
								stackC.setCount(Math.min(countA, availableB));
								stackB = stackC.copy();
								stackA.shrink(Math.min(countA, availableB));
							}
						} else {
							ItemStack stackC = stackA.copy();
							stackA = stackB.copy();
							stackB = stackC.copy();
						}
					} else if (button == 1 && !stackB.isEmpty()) { // Add to existing // RMB
						if (stackA.isEmpty()) { // If existing is empty, initialize it // RMB
							stackA = new ItemStack(stackB.getItem(), 1);
							stackA.setTag(stackB.getTag());
							stackB.shrink(1);
						}
					} else if (button == 1) { // Split existing // RMB
						if (slotA.isOverrideMaximumCount()) {
							ItemStack stackC = stackA.split(Math.min(stackA.getCount(), stackA.getMaxStackSize()) / 2);
							stackB = stackC.copy();
						} else {
							ItemStack stackC = stackA.split(stackA.getCount() / 2);
							stackB = stackC.copy();
						}
					}
				} else {
					if (button == 0) {
						StackUtilities.clamp(stackB, stackA, stackB.getMaxStackSize(), slotA.getMaxStackSize()); // Add to existing // LMB
					} else {
						boolean canStackTransfer = stackB.getCount() >= 1 && stackA.getCount() < slotA.getMaxStackSize();
						if (canStackTransfer) { // Add to existing // RMB
							stackA.grow(1);
							stackB.shrink(1);
						}
					}
				}
				break;
			}
			case CLONE: {
				if (player.isCreative()) {
					stackB = new ItemStack(stackA.getItem(), stackA.getMaxStackSize()); // Clone existing // MMB
					stackB.setTag(stackA.getTag());
				}
				break;
			}
			case QUICK_MOVE: {
				for (WAbstractWidget widget : serverInterface.getAllWidgets()) {
					if (widget instanceof WSlot && ((WSlot) widget).getLinkedInventory() != slotA.getLinkedInventory()) {
						WSlot slotB = ((WSlot) widget);
						ItemStack stackC = slotB.getStack();

						if (!stackA.isEmpty() && (stackC.getCount() < slotB.getMaxStackSize() || stackC.getCount() < stackA.getMaxStackSize())) {
							if (stackC.isEmpty() || (stackA.isItemEqual(stackC) && stackA.getTag() == stackB.getTag())) {
								Pair<ItemStack, ItemStack> result = StackUtilities.clamp(stackA, stackC, slotA.getMaxStackSize(), slotB.getMaxStackSize());
								stackA = result.getFirst();
								slotB.setStack(result.getSecond());
								break;

							}
						}
					}
				}
				break;
			}
			case PICKUP_ALL: {
				ItemStack stackC = getInterface().getContainer().getPlayerInventory().getItemStack();

				for (WAbstractWidget widget : getInterface().getAllWidgets()) {
					if (widget instanceof WSlot && ((WSlot) widget).getStack().isItemEqual(stackC)) {
						StackUtilities.clamp(((WSlot) widget).getStack(), stackC, ((WSlot) widget).getMaxStackSize(), stackC.getMaxStackSize());
					}
				}

				return;
			}
		}

		slotA.setStack(stackA);
		((PlayerInventory) linkedInventories.get(PLAYER_INVENTORY)).setItemStack(stackB);
	}

	public World getWorld() {
		return linkedWorld;
	}

	public IInventory getInventory(int inventoryNumber) {
		return linkedInventories.get(inventoryNumber);
	}

	@Deprecated
	@Override
	public Slot addSlot(Slot slot) {
		return super.addSlot(slot);
	}

	@Deprecated
	@Override
	public ItemStack slotClick(int identifier, int button, ClickType action, PlayerEntity player) {
		return ItemStack.EMPTY;
	}

	public void onContentChanged(Inventory inventory) {
		for (WAbstractWidget widget : serverInterface.getAllWidgets()) {
			if (widget instanceof WSlot) {
				WSlot slotA = ((WSlot) widget);


				if (cachedInventories.get(slotA.getInventoryNumber()) != null && cachedInventories.get(slotA.getInventoryNumber()).get(slotA.getSlotNumber()) != null) {
					ItemStack stackA = slotA.getStack();
					ItemStack stackB = cachedInventories.get(slotA.getInventoryNumber()).get(slotA.getSlotNumber());

					if ((!stackA.isEmpty() || !stackB.isEmpty()) || (stackA.getCount() != stackB.getCount()) || !stackA.isItemEqual(stackB)) {
						// TODO
						//Packet.INSTANCE.sendToPlayer(this.getPlayerInventory().player, NetworkHandler.SLOT_UPDATE_PACKET, NetworkHandler.createSlotUpdatePacket(syncId, slotA.getSlotNumber(), slotA.getInventoryNumber(), slotA.getStack()));
					}

					cachedInventories.get(slotA.getInventoryNumber()).put(slotA.getSlotNumber(), slotA.getStack());
				} else {
					cachedInventories.computeIfAbsent(slotA.getInventoryNumber(), value -> new HashMap<>());

					ItemStack stackA = slotA.getStack();
					ItemStack stackB = Optional.ofNullable(cachedInventories.get(slotA.getInventoryNumber()).get(slotA.getSlotNumber())).orElse(ItemStack.EMPTY);

					if ((!stackA.isEmpty() || !stackB.isEmpty()) || (stackA.getCount() != stackB.getCount()) || !stackA.isItemEqual(stackB)) {
						// TODO
						//ServerSidePacketRegistry.INSTANCE.sendToPlayer(this.getPlayerInventory().player, NetworkHandler.SLOT_UPDATE_PACKET, NetworkHandler.createSlotUpdatePacket(syncId, slotA.getSlotNumber(), slotA.getInventoryNumber(), slotA.getStack()));
					}

					cachedInventories.get(slotA.getInventoryNumber()).put(slotA.getSlotNumber(), slotA.getStack());
				}
			}
		}
	}

	@Deprecated
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

	@Override
	public void tick() {
	}
}
