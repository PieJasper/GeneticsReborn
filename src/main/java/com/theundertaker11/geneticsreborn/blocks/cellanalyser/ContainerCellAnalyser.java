package com.theundertaker11.geneticsreborn.blocks.cellanalyser;

import com.theundertaker11.geneticsreborn.blocks.BaseContainer;
import com.theundertaker11.geneticsreborn.items.GRItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCellAnalyser extends BaseContainer {

	private GRTileEntityCellAnalyser tileInventory;

	private int cachedEnergyUsed;
	private int cachedEnergyStored;
	private int cachedOverclockers;

	private final int INPUT_SLOT_NUMBER = 0;
	private final int OUTPUT_SLOT_NUMBER = 0;

	public ContainerCellAnalyser(InventoryPlayer invPlayer, GRTileEntityCellAnalyser tileInventory) {
		this.tileInventory = tileInventory;
		attachPlayerInventory(invPlayer);

		IItemHandler itemhandlerinput = tileInventory.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		IItemHandler itemhandleroutput = tileInventory.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
		int INPUT_SLOTS_XPOS = 63;
		int INPUT_SLOTS_YPOS = 36;
		addSlotToContainer(new SlotSmeltableInput(itemhandlerinput, INPUT_SLOT_NUMBER, INPUT_SLOTS_XPOS, INPUT_SLOTS_YPOS));


		int OUTPUT_SLOTS_XPOS = 110;
		int OUTPUT_SLOTS_YPOS = 36;
		addSlotToContainer(new SlotOutput(itemhandleroutput, OUTPUT_SLOT_NUMBER, OUTPUT_SLOTS_XPOS, OUTPUT_SLOTS_YPOS));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileInventory.isUseableByPlayer(player);
	}

	@Override
	protected boolean canAcceptItem(Slot slot) {
		return slot.getStack().getItem()== GRItems.OrganicMatter;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		boolean fieldHasChanged = false;
		boolean overclockersChanged = false;
		if (cachedEnergyUsed != tileInventory.getField(0) || cachedEnergyStored != tileInventory.getField(1)) {
			this.cachedEnergyUsed = tileInventory.getField(0);
			this.cachedEnergyStored = tileInventory.getField(1);
			fieldHasChanged = true;
		}
		if (cachedOverclockers != tileInventory.getField(2)) {
			this.cachedOverclockers = tileInventory.getField(2);
			overclockersChanged = true;
		}

		for (IContainerListener listener : this.listeners) {
			if (fieldHasChanged) {
				// Note that although sendProgressBarUpdate takes 2 ints on a server these are truncated to shorts
				listener.sendWindowProperty(this, 0, this.cachedEnergyUsed);
				listener.sendWindowProperty(this, 1, this.cachedEnergyStored);
			}
			if (overclockersChanged) {
				listener.sendWindowProperty(this, 2, this.cachedOverclockers);
			}

		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		tileInventory.setField(id, data);
	}

	public class SlotSmeltableInput extends SlotItemHandler {
		public SlotSmeltableInput(IItemHandler inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		// if this function returns false, the player won't be able to insert the given item into this slot
		@Override
		public boolean isItemValid(ItemStack stack) {
			return (stack.getItem() == GRItems.OrganicMatter);
		}
	}

	public class SlotOutput extends SlotItemHandler {
		public SlotOutput(IItemHandler inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}
	}
}
