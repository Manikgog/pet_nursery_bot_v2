package ru.pet.nursery.commands;

import ru.pet.nursery.action.Action;
import ru.pet.nursery.action.InfoAction;
import ru.pet.nursery.action.StartAction;

import java.util.HashMap;
import java.util.Map;

public class Commands {
    public final static Map<String, Action> commandsActions;

    static {
        commandsActions = new HashMap<>();
        commandsActions.put("/start", new StartAction());
        commandsActions.put("/info", new InfoAction());

    }
}
