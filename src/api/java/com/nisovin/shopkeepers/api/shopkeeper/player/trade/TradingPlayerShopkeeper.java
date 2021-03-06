package com.nisovin.shopkeepers.api.shopkeeper.player.trade;

import java.util.List;

import com.nisovin.shopkeepers.api.shopkeeper.offers.TradingOffer;
import com.nisovin.shopkeepers.api.shopkeeper.player.PlayerShopkeeper;

/**
 * Trades arbitrary items (is not limited to currency items).
 * <p>
 * There can be multiple different offers for the same item.
 */
public interface TradingPlayerShopkeeper extends PlayerShopkeeper {

	// OFFERS

	/**
	 * Gets the offers of this shopkeeper.
	 * 
	 * @return an unmodifiable view on the shopkeeper's offers
	 */
	public List<TradingOffer> getOffers();

	/**
	 * Clears the shopkeeper's offers.
	 */
	public void clearOffers();

	/**
	 * Sets the shopkeeper's offers.
	 * <p>
	 * This replaces the shopkeeper's previous offers.
	 * 
	 * @param offers
	 *            the new offers
	 */
	public void setOffers(List<TradingOffer> offers);

	/**
	 * Adds the given offer to the shopkeeper.
	 * <p>
	 * The offer gets added to the end of the current offers. If you want to insert, replace or reorder offers, use
	 * {@link #setOffers(List)} instead.
	 * 
	 * @param offer
	 *            the offer to add
	 */
	public void addOffer(TradingOffer offer);

	/**
	 * Adds the given offers to the shopkeeper.
	 * <p>
	 * The offers get added to the end of the current offers. If you want to insert, replace or reorder offers, use
	 * {@link #setOffers(List)} instead.
	 * 
	 * @param offers
	 *            the offers to add
	 */
	public void addOffers(List<TradingOffer> offers);
}
