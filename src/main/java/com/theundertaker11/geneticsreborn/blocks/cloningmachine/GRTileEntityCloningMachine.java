package com.theundertaker11.geneticsreborn.blocks.cloningmachine;

import com.theundertaker11.geneticsreborn.GeneticsReborn;
import com.theundertaker11.geneticsreborn.items.GRItems;
import com.theundertaker11.geneticsreborn.tile.GRTileEntityBasicEnergyReceiver;
import com.theundertaker11.geneticsreborn.util.ModUtils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class GRTileEntityCloningMachine extends GRTileEntityBasicEnergyReceiver implements ITickable {
	public GRTileEntityCloningMachine() {
		super();
	}
	

	public GRTileEntityCloningMachine(String name) {
		super(name);
	}

	@Override
	public void update() {
		int rfpertick = (GeneticsReborn.baseRfPerTickCloningMachine + (this.overclockers * 1300));
		if (canSmelt()) {
			if (this.storage.getEnergyStored() > rfpertick) {
				this.storage.extractEnergy(rfpertick, false);
				ticksCooking++;
				markDirty();
			}
			if (ticksCooking < 0) ticksCooking = 0;

			if (ticksCooking >= getTotalTicks()) {
				smeltItem();
				ticksCooking = 0;
			}
		} else ticksCooking = 0;
	}

	public static ItemStack getSmeltingResultForItem(ItemStack stack) {
		if (stack.getItem() == GRItems.OrganicMatter && stack.getTagCompound() != null) {
			if (!ModUtils.getTagCompound(stack).hasKey("mobTag")) return ItemStack.EMPTY;
			for (String entityClass : GeneticsReborn.CloningBlacklist) {
				if (entityClass.equals(ModUtils.getTagCompound(stack).getString("entityCodeName")))
					return ItemStack.EMPTY;
			}
			ItemStack result = new ItemStack(GRItems.OrganicMatter);
			ModUtils.getTagCompound(result).setString("entityName", ModUtils.getTagCompound(stack).getString("entityName"));
			ModUtils.getTagCompound(result).setString("entityCodeName", ModUtils.getTagCompound(stack).getString("entityCodeName"));
			return result;
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Doesn't just check if the item is good, also checks if there is room in the output.
	 */
	private boolean canSmelt() {
		return smeltItem(false);
	}

	private void smeltItem() {
		smeltItem(true);
	}

	/**
	 * checks that there is an item to be smelted in one of the input slots and that there is room for the result in the output slots
	 * If desired, performs the smelt
	 *
	 * @param performSmelt if true, perform the smelt.  if false, check whether smelting is possible, but don't change the inventory
	 * @return false if no items can be smelted, true otherwise
	 */
	private boolean smeltItem(boolean performSmelt) {
		ItemStack result;
		IItemHandler inventory = this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		IItemHandler inventoryoutput = this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);

		if (inventory != null && !inventory.getStackInSlot(0).isEmpty()) {
			result = getSmeltingResultForItem(inventory.getStackInSlot(0));
			if (!result.isEmpty()) {
				ItemStack inputSlotStack = inventory.getStackInSlot(0);
				ItemStack outputSlotStack = inventoryoutput.getStackInSlot(0);
				if (outputSlotStack.isEmpty()) {
					if (inventoryoutput.insertItem(0, result, !performSmelt).isEmpty()) {
						inventory.extractItem(0, 1, !performSmelt);
						if (performSmelt) spawnEntity(ModUtils.getTagCompound(inputSlotStack));
						markDirty();
						return true;
					}
				} else {
					if (!inventoryoutput.insertItem(0, result, true).isEmpty()) {
						return false;
					} else {
						inventoryoutput.insertItem(0, result, !performSmelt);
						inventory.extractItem(0, 1, !performSmelt);
						markDirty();
						if (performSmelt) spawnEntity(ModUtils.getTagCompound(inputSlotStack));
						return true;
					}
				}
			}
		}
		return false;
	}

	public void spawnEntity(NBTTagCompound tag) {
		if (this.getWorld().isRemote) return;
		NBTBase mobCompound = tag.getTag("mobTag");
		String type = tag.getString("type");
		EntityLivingBase entityLivingBase = createEntity(this.getWorld(), type);
		if (entityLivingBase != null) {
			entityLivingBase.readEntityFromNBT((NBTTagCompound) mobCompound);
			entityLivingBase.setLocationAndAngles(pos.getX() + .5, pos.getY() + 2.0, pos.getZ() + .5, 0, 0);

			this.getWorld().spawnEntity(entityLivingBase);
		}
	}

	public EntityLivingBase createEntity(World world, String type) {
		EntityLivingBase entityLivingBase;
		try {
			entityLivingBase = (EntityLivingBase) Class.forName(type).getConstructor(World.class).newInstance(world);
		} catch (Exception e) {
			entityLivingBase = null;
		}
		return entityLivingBase;
	}

	public double getTotalTicks() {
		return (double) (GeneticsReborn.baseTickCloningMachine - (this.overclockers * GeneticsReborn.OVERCLOCK_BONUS));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		return compound;

	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
	}

}
