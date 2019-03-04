package com.nisovin.shopkeepers.api.shopkeeper;

import java.util.List;

import com.nisovin.shopkeepers.api.types.SelectableType;

public interface ShopType<T extends Shopkeeper> extends SelectableType {

	// override to enforce that each subtype actually specifies a non-default display name
	@Override
	public abstract String getDisplayName();

	/**
	 * Gets a user-friendly one line description of this shop type.
	 *
	 * @return the description
	 */
	public String getDescription();

	/**
	 * Gets a user-friendly short (but possibly multi-line) description of how to setup this shop type after creation.
	 *
	 * @return the setup description
	 */
	public String getSetupDescription();

	/**
	 * Gets a user-friendly short (possibly multi-line) description of how to setup the trades for this shop type.
	 * 
	 * @return the trade setup description
	 */
	public List<String> getTradeSetupDescription();
}
