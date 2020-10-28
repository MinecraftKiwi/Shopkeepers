package com.nisovin.shopkeepers.villagers;

import java.util.Map;

import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.nisovin.shopkeepers.SKShopkeepersPlugin;
import com.nisovin.shopkeepers.Settings;
import com.nisovin.shopkeepers.api.ShopkeepersPlugin;
import com.nisovin.shopkeepers.pluginhandlers.CitizensHandler;
import com.nisovin.shopkeepers.ui.defaults.VillagerEditorHandler;
import com.nisovin.shopkeepers.util.ItemUtils;
import com.nisovin.shopkeepers.util.Log;
import com.nisovin.shopkeepers.util.TextUtils;

/**
 * Handles prevention of trading, hiring and editing of regular villagers (including wandering traders).
 */
public class VillagerInteractionListener implements Listener {

	private final ShopkeepersPlugin plugin;

	public VillagerInteractionListener(ShopkeepersPlugin plugin) {
		this.plugin = plugin;
	}

	// HIGH, since we don't want to handle hiring if another plugin has cancelled the event.
	// But not HIGHEST, so that other plugins can still react to us canceling the event.
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	void onEntityInteract(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof AbstractVillager)) return;
		AbstractVillager villager = (AbstractVillager) event.getRightClicked();
		boolean isVillager = (villager instanceof Villager);
		boolean isWanderingTrader = (!isVillager && villager instanceof WanderingTrader);
		if (!isVillager && !isWanderingTrader) return; // Unknown villager sub-type

		if (plugin.getShopkeeperRegistry().isShopkeeper(villager)) {
			// Shopkeeper interaction is handled elsewhere
			return;
		}
		Log.debug("Interaction with non-shopkeeper villager ..");

		if (CitizensHandler.isNPC(villager)) {
			// Ignore any interaction with Citizens NPCs
			Log.debug("  ignoring (probably Citizens) NPC");
			return;
		}

		if ((isVillager && Settings.disableOtherVillagers) || (isWanderingTrader && Settings.disableWanderingTraders)) {
			// Prevent trading with non-shopkeeper villagers:
			event.setCancelled(true);
			Log.debug("  trading prevented");
		}

		// Only react to main hand events:
		if (event.getHand() != EquipmentSlot.HAND) return;

		Player player = event.getPlayer();
		boolean overrideTrading = false;
		if (this.handleEditRegularVillager(player, villager)) {
			// Villager editor for regular villagers.
			overrideTrading = true;
		} else if (this.handleHireOtherVillager(player, villager)) {
			// Hiring of regular villagers.
			overrideTrading = true;
		}

		if (overrideTrading) {
			// The villager interaction resulted in some action that overrides the default trading behavior:
			event.setCancelled(true);
		}
	}

	private boolean handleEditRegularVillager(Player player, AbstractVillager villager) {
		if (!player.isSneaking()) return false;
		if ((Settings.editRegularVillagers && villager instanceof Villager)
				|| (Settings.editRegularWanderingTraders && villager instanceof WanderingTrader)) {
			Log.debug("  possible villager editor request ..");
			// Open the villager editor:
			// Silent request (fails if the player is missing the permission):
			VillagerEditorHandler villagerEditor = new VillagerEditorHandler(villager);
			if (SKShopkeepersPlugin.getInstance().getUIRegistry().requestUI(villagerEditor, player, true)) {
				Log.debug("    ..success (normal trading prevented).");
				return true;
			} else {
				Log.debug("    ..no access (probably missing permission).");
			}
		}
		return false;
	}

	// Returns false, if the player wasn't able to hire this villager.
	private boolean handleHireOtherVillager(Player player, AbstractVillager villager) {
		if (!(Settings.hireOtherVillagers && villager instanceof Villager)
				&& !(Settings.hireWanderingTraders && villager instanceof WanderingTrader)) {
			return false;
		}
		Log.debug("  possible hire ..");

		// Check if the player is allowed to remove (attack) the entity (in case the entity is protected by another
		// plugin).
		Log.debug("    checking villager access.");
		if (!this.checkEntityAccess(player, villager)) {
			Log.debug("    ..no permission to remove villager.");
			return false;
		}

		// Hire him if holding his hiring item.
		PlayerInventory playerInventory = player.getInventory();
		ItemStack itemInMainHand = playerInventory.getItemInMainHand();
		if (!Settings.isHireItem(itemInMainHand)) {
			// TODO Show hire item via hover event?
			TextUtils.sendMessage(player, Settings.msgVillagerForHire,
					"costs", Settings.hireOtherVillagersCosts,
					"hire-item", Settings.hireItem.getType().name()
			); // TODO Also print required hire item name and lore?
			Log.debug("    ..not holding hire item.");
			return false;
		}

		// Check if the player has enough of those hiring items:
		final int costs = Settings.hireOtherVillagersCosts;
		if (costs > 0) {
			ItemStack[] storageContents = playerInventory.getStorageContents();
			if (!ItemUtils.containsAtLeast(storageContents, Settings.hireItem, costs)) {
				TextUtils.sendMessage(player, Settings.msgCantHire);
				Log.debug("    ..not holding enough hire items.");
				return false;
			}

			Log.debug("  Villager hiring: The player has the needed amount of hiring items.");
			int inHandAmount = itemInMainHand.getAmount();
			int remaining = inHandAmount - costs;
			Log.debug(() -> "  Villager hiring: in hand=" + inHandAmount + " costs=" + costs + " remaining=" + remaining);
			if (remaining > 0) {
				itemInMainHand.setAmount(remaining);
			} else { // remaining <= 0
				playerInventory.setItemInMainHand(null); // Remove item in hand
				if (remaining < 0) {
					// Remove remaining costs from inventory:
					ItemUtils.removeItems(storageContents, Settings.hireItem, -remaining);
					// Apply the change to the player's inventory:
					ItemUtils.setStorageContents(playerInventory, storageContents);
				}
			}
		}

		// Give player the shop creation item
		ItemStack shopCreationItem = Settings.createShopCreationItem();
		Map<Integer, ItemStack> remaining = playerInventory.addItem(shopCreationItem);
		if (!remaining.isEmpty()) {
			villager.getWorld().dropItem(villager.getLocation(), shopCreationItem);
		}

		// Remove the entity:
		// Note: The leashed trader llamas for the wandering trader will break and the llamas will remain.
		villager.remove();

		// Update client's inventory:
		player.updateInventory();

		TextUtils.sendMessage(player, Settings.msgHired);
		Log.debug("    ..success (normal trading prevented).");
		return true;
	}

	// Returns true if the player can access (remove / attack) this entity.
	private boolean checkEntityAccess(Player player, Entity entity) {
		TestEntityDamageByEntityEvent fakeDamageEvent = new TestEntityDamageByEntityEvent(player, entity);
		plugin.getServer().getPluginManager().callEvent(fakeDamageEvent);
		return !fakeDamageEvent.isCancelled();
	}
}
