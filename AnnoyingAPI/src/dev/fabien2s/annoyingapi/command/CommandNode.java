package dev.fabien2s.annoyingapi.command;

import lombok.Getter;

public class CommandNode {

    @Getter private final String name;
    @Getter private final CommandNode[] children;

    protected CommandNode(String name, CommandNode... children) {
        this.name = name;
        this.children = children;
    }

}
