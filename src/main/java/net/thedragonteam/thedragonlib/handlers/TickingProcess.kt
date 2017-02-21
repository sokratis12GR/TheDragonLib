package net.thedragonteam.thedragonlib.handlers

class TickingProcess : IProcess {
    var tick = 0

    override fun updateProcess() {
        tick++
    }

    override val isDead: Boolean
        get() = false
}