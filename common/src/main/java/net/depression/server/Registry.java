package net.depression.server;

import net.depression.mental.MentalStatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Registry {
    public static final HashMap<UUID, MentalStatus> mentalStatus = new HashMap<>();
    public static final HashSet<UUID> quitPlayers = new HashSet<>();
}
