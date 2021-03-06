package com.nisovin.shopkeepers.commands.shopkeepers;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.nisovin.shopkeepers.api.ShopkeepersPlugin;
import com.nisovin.shopkeepers.commands.lib.Command;
import com.nisovin.shopkeepers.commands.lib.CommandContextView;
import com.nisovin.shopkeepers.commands.lib.CommandException;
import com.nisovin.shopkeepers.commands.lib.CommandInput;
import com.nisovin.shopkeepers.commands.lib.arguments.FixedValuesArgument;
import com.nisovin.shopkeepers.commands.lib.arguments.OptionalArgument;
import com.nisovin.shopkeepers.config.Settings;
import com.nisovin.shopkeepers.debug.DebugOptions;
import com.nisovin.shopkeepers.lang.Messages;
import com.nisovin.shopkeepers.text.Text;

class CommandDebug extends Command {

	private static final class DebugOptionArgument extends FixedValuesArgument {

		private static Map<String, String> getDebugOptionsMap() {
			return DebugOptions.getAll().stream().collect(Collectors.toMap(key -> key, key -> key));
		}

		private static final Text invalidArgumentMsg = Text.parse("&cUnknown debug option '{argument}'. Available options: "
				+ String.join(", ", DebugOptions.getAll()));

		public DebugOptionArgument(String name) {
			super(name, getDebugOptionsMap());
		}

		@Override
		public Text getInvalidArgumentErrorMsg(String argumentInput) {
			if (argumentInput == null) argumentInput = "";
			invalidArgumentMsg.setPlaceholderArguments(this.getDefaultErrorMsgArgs());
			invalidArgumentMsg.setPlaceholderArguments(Collections.singletonMap("argument", argumentInput));
			return invalidArgumentMsg;
		}
	}

	private static final String ARGUMENT_DEBUG_OPTION = "option";

	CommandDebug() {
		super("debug");

		// Set permission:
		this.setPermission(ShopkeepersPlugin.DEBUG_PERMISSION);

		// Set description:
		this.setDescription(Messages.commandDescriptionDebug);

		// Arguments:
		this.addArgument(new OptionalArgument<>(new DebugOptionArgument(ARGUMENT_DEBUG_OPTION)));
	}

	@Override
	protected void execute(CommandInput input, CommandContextView context) throws CommandException {
		CommandSender sender = input.getSender();
		String debugOption = context.get(ARGUMENT_DEBUG_OPTION);
		if (debugOption == null) {
			// Toggle debug mode:
			Settings.debug = !Settings.debug;
			Settings.onSettingsChanged();
			sender.sendMessage(ChatColor.GREEN + "Debug mode " + (Settings.debug ? "enabled" : "disabled"));
		} else {
			assert DebugOptions.getAll().contains(debugOption); // Validated by the argument
			// Toggle debug option:
			boolean enabled;
			if (Settings.debugOptions.contains(debugOption)) {
				Settings.debugOptions.remove(debugOption);
				enabled = false;
			} else {
				Settings.debugOptions.add(debugOption);
				enabled = true;
			}
			Settings.onSettingsChanged();
			sender.sendMessage(ChatColor.GREEN + "Debug option '" + debugOption + "' " + (enabled ? "enabled" : "disabled"));
		}
	}
}
