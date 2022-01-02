package com.integral.etherium.items;

import com.integral.etherium.core.EtheriumUtil;
import com.integral.etherium.core.IEtheriumConfig;

import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;

public class EtheriumIngot extends Item {
	private final IEtheriumConfig config;

	public EtheriumIngot(IEtheriumConfig config) {
		super(EtheriumUtil.defaultProperties(config, EtheriumIngot.class).stacksTo(64).fireResistant());
		this.setRegistryName(new ResourceLocation(config.getOwnerMod(), "etherium_ingot"));
		this.config = config;
	}

	@Override
	public String getDescriptionId() {
		return this.config.isStandalone() ? "item.enigmaticlegacy." + this.getRegistryName().getPath() : super.getDescriptionId();
	}

}
