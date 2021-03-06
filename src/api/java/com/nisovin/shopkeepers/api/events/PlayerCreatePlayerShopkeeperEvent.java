package com.nisovin.shopkeepers.api.events;

import com.nisovin.shopkeepers.api.shopkeeper.ShopCreationData;
import com.nisovin.shopkeepers.api.shopkeeper.player.PlayerShopkeeper;

/**
 * This event is called whenever a player attempts to create a {@link PlayerShopkeeper}.
 * <p>
 * This is called before the max shops limit is checked for the owner of the new shopkeeper. If this event is cancelled,
 * the shopkeeper will not be created.
 * <p>
 * If you want to react to all shopkeeper creations, take a look at {@link ShopkeeperAddedEvent}.
 */
public class PlayerCreatePlayerShopkeeperEvent extends PlayerCreateShopkeeperEvent {

	private int maxShopsLimit;

	public PlayerCreatePlayerShopkeeperEvent(ShopCreationData creationData, int maxShopsLimit) {
		super(creationData);
		this.maxShopsLimit = maxShopsLimit;
	}

	/**
	 * Gets the maximum number of shops the owner of the new shopkeeper can have.
	 * <p>
	 * {@link Integer#MAX_VALUE} indicates no limit.
	 * 
	 * @return the owner's max shops limit
	 */
	public int getMaxShopsLimit() {
		return maxShopsLimit;
	}

	/**
	 * Sets the maximum number of shops the owner of the new shopkeeper can have.
	 * <p>
	 * The new max shops limit only affects this specific shopkeeper creation. If the player already has more shops than
	 * this, the shop will not be created.
	 * <p>
	 * {@link Integer#MAX_VALUE} indicates no limit.
	 * 
	 * @param maxShopsLimit
	 *            the owner's max shops limit to use for this shopkeeper creation
	 */
	public void setMaxShopsLimit(int maxShopsLimit) {
		if (maxShopsLimit < 0) {
			throw new IllegalArgumentException("maxShopsLimit cannot be negative!");
		}
		this.maxShopsLimit = maxShopsLimit;
	}
}
