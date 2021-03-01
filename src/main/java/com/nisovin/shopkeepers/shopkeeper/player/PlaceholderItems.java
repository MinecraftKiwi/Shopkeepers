package com.nisovin.shopkeepers.shopkeeper.player;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.nisovin.shopkeepers.util.MinecraftEnumUtils;
import com.nisovin.shopkeepers.util.TextUtils;

/**
 * Helper methods related to placeholder items.
 * <p>
 * In some situations, such as for example when setting up the trades of certain types of player shopkeepers, players
 * can use these placeholder items as substitutes for items they do not have.
 * <p>
 * The display name of the placeholder item specifies the substituted item type. Other properties of the substituted
 * item cannot be specified.
 */
public class PlaceholderItems {

	/**
	 * Checks if the given {@link ItemStack} is of the type used by placeholder items (i.e. {@link Material#NAME_TAG}).
	 * 
	 * @param itemStack
	 *            the item stack
	 * @return <code>true</code> if the item stack is of the type used by placeholder items
	 */
	public static boolean isPlaceholderItemType(ItemStack itemStack) {
		if (itemStack == null) return false;
		if (itemStack.getType() != Material.NAME_TAG) return false;
		return true;
	}

	/**
	 * Gets the {@link Material} that is substituted by the given placeholder {@link ItemStack}, if it actually is a
	 * placeholder item.
	 * 
	 * @param placeholderItem
	 *            the (potential) placeholder item
	 * @return the substituted material, or <code>null</code> if either the given item stack is not a placeholder, or
	 *         the material could not be determined
	 */
	public static Material getSubstitutedMaterial(ItemStack placeholderItem) {
		if (!isPlaceholderItemType(placeholderItem)) return null;

		// Get the display name:
		ItemMeta meta = placeholderItem.getItemMeta();
		assert meta != null;
		String displayName = meta.getDisplayName();
		assert displayName != null; // But can be empty

		// Get the corresponding material name:
		String materialName = TextUtils.decolorize(displayName);
		materialName = MinecraftEnumUtils.normalizeEnumName(materialName);

		// Lookup the substituted material:
		Material substitutedMaterial = Material.getMaterial(materialName);
		if (substitutedMaterial == null) return null;

		// Validate the material:
		if (substitutedMaterial.isLegacy() || substitutedMaterial.isAir() || !substitutedMaterial.isItem()) {
			return null;
		}
		return substitutedMaterial;
	}

	/**
	 * Checks if the given {@link ItemStack} is a valid placeholder item, i.e. with valid
	 * {@link #getSubstitutedMaterial(ItemStack) substituted material}.
	 * 
	 * @param itemStack
	 *            the item stack
	 * @return <code>true</code> if the item stack is a valid placeholder
	 */
	public static boolean isPlaceholderItem(ItemStack itemStack) {
		return getSubstitutedMaterial(itemStack) != null;
	}

	/**
	 * Gets the {@link ItemStack} that is substituted by the given placeholder {@link ItemStack}, if it actually is a
	 * {@link #isPlaceholderItem(ItemStack) valid placeholder}.
	 * 
	 * @param placeholderItem
	 *            the (potential) placeholder item
	 * @return the substituted item stack, or <code>null</code> if the given item stack is not a valid placeholder
	 */
	public static ItemStack getSubstitutedItem(ItemStack placeholderItem) {
		Material substitutedMaterial = getSubstitutedMaterial(placeholderItem);
		if (substitutedMaterial == null) return null;
		// We preserve the stack size of the placeholder item stack:
		return new ItemStack(substitutedMaterial, placeholderItem.getAmount());
	}

	/**
	 * If the given item stack is a {@link #isPlaceholderItem(ItemStack) valid placeholder}, this returns the
	 * {@link #getSubstitutedItem(ItemStack) substituted item stack}. Otherwise, this returns the given
	 * {@link ItemStack} itself.
	 * 
	 * @param itemStack
	 *            the (potential) placeholder item
	 * @return either the substituted item stack, if the given item stack is a valid placeholder, or otherwise the given
	 *         item stack itself
	 */
	public static ItemStack replace(ItemStack itemStack) {
		ItemStack substitutedItem = getSubstitutedItem(itemStack);
		return (substitutedItem != null) ? substitutedItem : itemStack;
	}

	private PlaceholderItems() {
	}
}