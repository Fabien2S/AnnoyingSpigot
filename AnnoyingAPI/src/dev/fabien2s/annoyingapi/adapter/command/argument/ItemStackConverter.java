package dev.fabien2s.annoyingapi.adapter.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.fabien2s.annoyingapi.command.argument.ICommandArgumentConverter;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.item.ArgumentItemStack;
import net.minecraft.commands.arguments.item.ArgumentPredicateItemStack;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemStackConverter implements ICommandArgumentConverter<CommandListenerWrapper, ItemStack, ArgumentPredicateItemStack> {

    @Override
    public ItemStack convert(CommandContext<CommandListenerWrapper> context, Class<ItemStack> type, ArgumentPredicateItemStack o) throws CommandSyntaxException {
        net.minecraft.world.item.ItemStack nmsItemStack = o.a(1, false);
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }

    @Override
    public ArgumentType<ArgumentPredicateItemStack> getArgumentType() {
        return ArgumentItemStack.a();
    }

    @Override
    public Class<ArgumentPredicateItemStack> getArgumentClass() {
        return ArgumentPredicateItemStack.class;
    }

}
