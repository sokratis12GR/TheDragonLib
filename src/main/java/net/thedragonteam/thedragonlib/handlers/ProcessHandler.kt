package net.thedragonteam.thedragonlib.handlers

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*

class ProcessHandler {

    @SubscribeEvent
    fun onServerTick(event: TickEvent.ServerTickEvent) {
        when (event.phase) {
            TickEvent.Phase.START -> {

                val i = processes.iterator()

                while (i.hasNext()) {
                    val process = i.next()
                    if (process.isDead)
                        i.remove()
                    else
                        process.updateProcess()
                }

                if (!newProcesses.isEmpty()) {
                    processes.addAll(newProcesses)
                    newProcesses.clear()
                }
            }
            else -> {
            }
        }
    }

    @SubscribeEvent
    fun onWorldClose(event: WorldEvent.Unload) {
        processes.clear()
        newProcesses.clear()
    }

    companion object {

        private val processes = ArrayList<IProcess>()
        private val newProcesses = ArrayList<IProcess>()

        fun init() {
            MinecraftForge.EVENT_BUS.register(ProcessHandler())
        }

        fun addProcess(process: IProcess) {
            newProcesses.add(process)
        }
    }

}