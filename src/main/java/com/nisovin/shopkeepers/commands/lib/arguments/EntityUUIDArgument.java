package com.nisovin.shopkeepers.commands.lib.arguments;

import java.util.Collections;
import java.util.UUID;
import java.util.function.Predicate;

import org.bukkit.entity.Entity;

import com.nisovin.shopkeepers.commands.lib.ArgumentFilter;

/**
 * Argument for entity UUIDs.
 * <p>
 * By default this accepts any UUID regardless of whether it corresponds to an existing entity.
 */
public class EntityUUIDArgument extends ObjectUUIDArgument {

	// Note: Not providing a default argument filter that only accepts uuids of existing entities, because this can be
	// achieved more efficiently by using EntityByUUIDArgument instead.

	public EntityUUIDArgument(String name) {
		this(name, ArgumentFilter.acceptAny());
	}

	public EntityUUIDArgument(String name, ArgumentFilter<UUID> filter) {
		this(name, filter, DEFAULT_MINIMAL_COMPLETION_INPUT);
	}

	public EntityUUIDArgument(String name, ArgumentFilter<UUID> filter, int minimalCompletionInput) {
		super(name, filter, minimalCompletionInput);
	}

	// Using the uuid argument's 'missing argument' message if the uuid is missing.
	// Using the uuid argument's 'invalid argument' message if the uuid is invalid.
	// Using the filter's 'invalid argument' message if the uuid is not accepted.

	/**
	 * Gets the default uuid completion suggestions.
	 * 
	 * @param uuidPrefix
	 *            the uuid prefix, may be empty, not <code>null</code>
	 * @param filter
	 *            only suggestions for entities accepted by this predicate get included
	 * @return the entity uuid completion suggestions
	 */
	public static Iterable<UUID> getDefaultCompletionSuggestions(String uuidPrefix, Predicate<Entity> filter) {
		// TODO Actually provide UUID suggestions? Maybe for nearby or targeted entities?
		return Collections.emptyList();
	}

	@Override
	protected Iterable<UUID> getCompletionSuggestions(String idPrefix) {
		return getDefaultCompletionSuggestions(idPrefix, (entity) -> true);
	}
}
