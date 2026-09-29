package com.sofodev.thedragonlib.handlers;

import net.neoforged.fml.common.EventBusSubscriber;
import com.sofodev.thedragonlib.TheDragonLib;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(modid = TheDragonLib.MODID)
public class ProcessHandler {

    private static List<IProcess> processes = new ArrayList<>();
    private static List<IProcess> newProcesses = new ArrayList<>();

    public static void init() {
    }

    public static void addProcess(IProcess process) {
        newProcesses.add(process);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Pre event) {
        Iterator<IProcess> i = processes.iterator();

        while (i.hasNext()) {
            IProcess process = i.next();
            if (process.isDead()) {
                i.remove();
            } else {
                process.updateProcess();
            }
        }

        if (!newProcesses.isEmpty()) {
            processes.addAll(newProcesses);
            newProcesses.clear();
        }
    }

    @SubscribeEvent
    public static void onWorldClose(LevelEvent.Unload event) {
        processes.clear();
        newProcesses.clear();
    }
}
