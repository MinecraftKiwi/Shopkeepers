package com.nisovin.shopkeepers.shopobjects.living;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import com.nisovin.shopkeepers.AbstractShopkeeper;
import com.nisovin.shopkeepers.api.ShopCreationData;

public class ZombieShop extends LivingEntityShop {

	private boolean baby = false;

	protected ZombieShop(LivingEntityObjectType<ZombieShop> livingObjectType, AbstractShopkeeper shopkeeper, ShopCreationData creationData) {
		super(livingObjectType, shopkeeper, creationData);
	}

	@Override
	protected void load(ConfigurationSection config) {
		super.load(config);
		baby = config.getBoolean("baby");
	}

	@Override
	protected void save(ConfigurationSection config) {
		super.save(config);
		config.set("baby", baby);
	}

	@Override
	protected void applySubType() {
		super.applySubType();
		if (!this.isActive()) return;
		assert entity.getType() == EntityType.ZOMBIE;
		((Zombie) entity).setBaby(baby);
	}

	@Override
	public ItemStack getSubTypeItem() {
		return new ItemStack(Material.MONSTER_EGG, 1, (short) 54);
	}

	@Override
	public void cycleSubType() {
		baby = !baby;
		this.applySubType();
	}
}
