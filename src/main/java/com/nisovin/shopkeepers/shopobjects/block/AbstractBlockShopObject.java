package com.nisovin.shopkeepers.shopobjects.block;

import org.bukkit.block.Block;

import com.nisovin.shopkeepers.api.shopkeeper.ShopCreationData;
import com.nisovin.shopkeepers.api.shopobjects.block.BlockShopObject;
import com.nisovin.shopkeepers.shopkeeper.AbstractShopkeeper;
import com.nisovin.shopkeepers.shopobjects.AbstractShopObject;

public abstract class AbstractBlockShopObject extends AbstractShopObject implements BlockShopObject {

	protected AbstractBlockShopObject(AbstractShopkeeper shopkeeper, ShopCreationData creationData) {
		super(shopkeeper, creationData);
	}

	@Override
	public abstract AbstractBlockShopObjectType<?> getType();

	@Override
	public boolean isSpawned() {
		return (this.getBlock() != null);
	}

	@Override
	public boolean isActive() {
		// Same as isSpawned by default:
		return this.isSpawned();
	}

	@Override
	public Object getId() {
		Block block = this.getBlock();
		if (block == null) return null; // Not active
		return this.getType().getObjectId(block);
	}
}
