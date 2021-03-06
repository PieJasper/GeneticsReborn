package com.theundertaker11.geneticsreborn.blocks;

import com.theundertaker11.geneticsreborn.GeneticsReborn;
import com.theundertaker11.geneticsreborn.blocks.airdispersal.AirDispersal;
import com.theundertaker11.geneticsreborn.blocks.bloodpurifier.BloodPurifier;
import com.theundertaker11.geneticsreborn.blocks.cellanalyser.CellAnalyser;
import com.theundertaker11.geneticsreborn.blocks.cloningmachine.CloningMachine;
import com.theundertaker11.geneticsreborn.blocks.coalgenerator.CoalGenerator;
import com.theundertaker11.geneticsreborn.blocks.dnadecrypter.DNADecrypter;
import com.theundertaker11.geneticsreborn.blocks.dnaextractor.DNAExtractor;
import com.theundertaker11.geneticsreborn.blocks.incubator.Incubator;
import com.theundertaker11.geneticsreborn.blocks.plasmidinfuser.PlasmidInfuser;
import com.theundertaker11.geneticsreborn.blocks.plasmidinjector.PlasmidInjector;
import com.theundertaker11.geneticsreborn.render.IItemModelProvider;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class GRBlocks {

	public static Block CellAnalyser;
	public static Block DNAExtractor;
	public static Block DNADecrypter;
	public static Block PlasmidInfuser;
	public static Block BloodPurifier;
	public static Block PlasmidInjector;
	public static Block CloningMachine;
	public static Block AntiFieldBlock;
	public static Block CoalGen;
	public static Block Incubator;
	public static Block AdvIncubator;
	public static Block AirDispersal;
	
	public static Block lightBlock;

	public static void init() {
		CellAnalyser = register(new CellAnalyser("cellanalyser"));
		DNAExtractor = register(new DNAExtractor("dnaextractor"));
		DNADecrypter = register(new DNADecrypter("dnadecrypter"));
		PlasmidInfuser = register(new PlasmidInfuser("plasmidinfuser"));
		BloodPurifier = register(new BloodPurifier("bloodpurifier"));
		PlasmidInjector = register(new PlasmidInjector("plasmidinjector"));
		CloningMachine = register(new CloningMachine("cloningmachine"));
		AntiFieldBlock = register(new BlockBase("antifieldblock"));
		CoalGen = register(new CoalGenerator("coalgenerator"));
		Incubator = register(new Incubator("incubator", false));
		AdvIncubator = register(new Incubator("advincubator", true));
		AirDispersal = register(new AirDispersal("airdispersal"));
		
		lightBlock = register(new LightBlock("light_block", GeneticsReborn.BioluminLightLevel / 15.0F), null);
	}

	private static <T extends Block> T register(T block, ItemBlock itemBlock) {
		ForgeRegistries.BLOCKS.register(block);
		if (itemBlock != null) {
			//GameRegistry.register(itemBlock);
			ForgeRegistries.ITEMS.register(itemBlock);
		}

		if (block instanceof IItemModelProvider) {
			((IItemModelProvider) block).registerItemModel(itemBlock);
		}

		return block;
	}

	private static <T extends Block> T register(T block) {
		ItemBlock itemBlock = new ItemBlock(block);
		itemBlock.setRegistryName(block.getRegistryName());
		return register(block, itemBlock);
	}
}
