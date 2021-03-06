package guru.haun.hackery.blocks;

import guru.haun.hackery.blocks.slots.OutputSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class TraceAnalyzerContainer extends Container {

	protected TraceAnalyzerTE te;
	private int lastprogress = 0;

	public TraceAnalyzerContainer(InventoryPlayer playerInv, TraceAnalyzerTE te){
		this.te = te;
		
			addSlotToContainer(new Slot(te,0,49,35));
			addSlotToContainer(new OutputSlot(te,1,116,35));
			bindPlayerInventory(playerInv);
	}
	
	protected void bindPlayerInventory(InventoryPlayer playerInv) {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(playerInv, j+i*9+9, 8+j*18, 84 + i* 18)); //Main Inventory
			}
		}
		for(int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(playerInv, i, 8+i*18,142));
		}
	}
	
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);
		
		if(slotObject != null && slotObject.getHasStack()){
			ItemStack slotStack = slotObject.getStack();
			stack = slotStack.copy();
			
			if(slot < 2){ //machine slots
				if(!this.mergeItemStack(slotStack, 0, 35, true))
					return null;
			}else if(!this.mergeItemStack(slotStack, 0, 1, false))
				return null;
			
			if(slotStack.stackSize == 0)
				slotObject.putStack(null);
			else
				slotObject.onSlotChanged();
			
			if(slotStack.stackSize == stack.stackSize) return null;
			
			slotObject.onPickupFromSlot(player, slotStack);
		}
		return stack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return te.isUseableByPlayer(var1);
	}

	public void updateProgressBar(int par1, int par2){
		this.te.operationProgress = par2;
	}
	
	public void addCraftingToCrafters(ICrafting ic){
		super.addCraftingToCrafters(ic);
		ic.sendProgressBarUpdate(this, 0, this.te.operationProgress);
	}
	
	public void detectAndSendChanges(){
		super.detectAndSendChanges();
		ICrafting ic = (ICrafting) this.crafters.get(0);
		if(this.lastprogress != this.te.operationProgress)
			ic.sendProgressBarUpdate(this, 0, this.te.operationProgress);
		this.lastprogress = this.te.operationProgress;
	}
}
