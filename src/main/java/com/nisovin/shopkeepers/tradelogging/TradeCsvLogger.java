package com.nisovin.shopkeepers.tradelogging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.nisovin.shopkeepers.api.events.ShopkeeperTradeEvent;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import com.nisovin.shopkeepers.api.shopkeeper.TradingRecipe;
import com.nisovin.shopkeepers.api.shopkeeper.player.PlayerShopkeeper;
import com.nisovin.shopkeepers.config.Settings;
import com.nisovin.shopkeepers.util.ItemUtils;
import com.nisovin.shopkeepers.util.Log;
import com.nisovin.shopkeepers.util.TextUtils;

/**
 * Logs trades to csv files.
 */
public class TradeCsvLogger implements Listener {

	public static final String TRADE_LOGS_FOLDER = "trade-logs";

	private static final String FILE_NAME_PREFIX = "purchases-";
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

	private final File tradeLogsFolder;

	public TradeCsvLogger(File pluginDataFolder) {
		this.tradeLogsFolder = new File(pluginDataFolder, TRADE_LOGS_FOLDER);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	void onTradeCompleted(ShopkeeperTradeEvent event) {
		if (!Settings.enablePurchaseLogging) {
			return;
		}
		Player player = event.getPlayer();
		Shopkeeper shopkeeper = event.getShopkeeper();
		String ownerString = (shopkeeper instanceof PlayerShopkeeper) ? ((PlayerShopkeeper) shopkeeper).getOwnerString() : "[Admin]";

		TradingRecipe tradingRecipe = event.getTradingRecipe();
		ItemStack resultItem = tradingRecipe.getResultItem();
		ItemStack requiredItem1 = tradingRecipe.getItem1();
		ItemStack requiredItem2 = tradingRecipe.getItem2();

		Inventory inventory = event.getClickEvent().getInventory();
		ItemStack usedItem1 = inventory.getItem(0);
		ItemStack usedItem2 = ItemUtils.getNullIfEmpty(inventory.getItem(1));
		if (ItemUtils.isEmpty(usedItem1)) {
			usedItem1 = usedItem2;
			usedItem2 = null;
		}

		// TODO fully serialize the traded items? (metadata)
		// TODO do the file writing async
		Date now = new Date();
		File file = new File(tradeLogsFolder, FILE_NAME_PREFIX + DATE_FORMAT.format(now) + ".csv");
		boolean isNew = !file.exists();
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
			if (isNew) writer.append("TIME,PLAYER,SHOP ID,SHOP TYPE,SHOP POS,OWNER,ITEM TYPE,DATA,QUANTITY,CURRENCY 1,CURRENCY 1 AMOUNT,CURRENCY 2,CURRENCY 2 AMOUNT\n");
			writer.append("\"" + TIME_FORMAT.format(now) + "\",\"" + TextUtils.getPlayerString(player) + "\",\"" + shopkeeper.getUniqueId()
					+ "\",\"" + shopkeeper.getType().getIdentifier() + "\",\"" + shopkeeper.getPositionString() + "\",\"" + ownerString
					+ "\",\"" + resultItem.getType().name() + "\",\"" + ItemUtils.getDurability(resultItem) + "\",\"" + resultItem.getAmount()
					+ "\",\"" + (usedItem1 != null ? usedItem1.getType().name() + ":" + ItemUtils.getDurability(usedItem1) : "")
					+ "\",\"" + (requiredItem1.getAmount())
					+ "\",\"" + (usedItem2 != null ? usedItem2.getType().name() + ":" + ItemUtils.getDurability(usedItem2) : "")
					+ "\",\"" + (requiredItem2 != null ? requiredItem2.getAmount() : "")
					+ "\"\n");
		} catch (IOException e) {
			Log.severe("IO exception while trying to log purchase", e);
		}
	}
}
