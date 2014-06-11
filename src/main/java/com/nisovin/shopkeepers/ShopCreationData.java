package com.nisovin.shopkeepers;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Holds the different possible arguments which might be needed (or not needed) for the creation of a shopkeeper of a certain type.
 * By moving those here, into a separate class, we can later easily add new arguments here without breaking old code.
 */
public class ShopCreationData {
	/**
	 * The owner and/or creator of the new shop.
	 */
	public Player creator;
	/**
	 * The chest which is backing the (player-)shop.
	 */
	public Block chest;
	/**
	 * The spawn location.
	 */
	public Location location;
	/**
	 * The type of shop to create.
	 */
	public ShopType shopType;
	/**
	 * The object type for this new shop.
	 */
	public ShopObjectType objectType;
	/**
	 * The entity this shop shall be assigned to.
	 */
	public LivingEntity shopEntity;

	public ShopCreationData() {
	}

	// constructors for common attribute groups:

	public ShopCreationData(Player creator, ShopType shopType, Location location, ShopObjectType objectType) {
		this.creator = creator;
		this.shopType = shopType;
		this.location = location;
		this.objectType = objectType;
	}

	public ShopCreationData(Player creator, ShopType shopType, Block chest, Location location, ShopObjectType objectType) {
		this(creator, shopType, location, objectType);
		this.chest = chest;
	}

	public ShopCreationData(Player creator, ShopType shopType, Location location, ShopObjectType objectType, LivingEntity shopEntity) {
		this(creator, shopType, location, objectType);
		this.shopEntity = shopEntity;
	}
}