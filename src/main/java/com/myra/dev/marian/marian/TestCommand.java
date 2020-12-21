package com.myra.dev.marian.marian;

import com.myra.dev.marian.management.commands.Command;
import com.myra.dev.marian.management.commands.CommandContext;
import com.myra.dev.marian.management.commands.CommandSubscribe;
import com.myra.dev.marian.utilities.Permissions;

@CommandSubscribe(
        name = "test",
        requires = Permissions.MARIAN
)
public class TestCommand implements Command {
    @Override
    public void execute(CommandContext ctx) throws Exception {
        // Code
    }
}
