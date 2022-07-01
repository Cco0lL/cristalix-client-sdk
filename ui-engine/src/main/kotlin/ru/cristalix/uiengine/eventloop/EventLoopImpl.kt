package ru.cristalix.uiengine.eventloop

class EventLoopImpl : EventLoop {

    private val runningTasks: MutableList<Task> = ArrayList()
    private var eventLoopBuffer: MutableList<Task>? = null
    private var inEventLoop = false
    override val runningAnimations: MutableList<Animation> = ArrayList()
    override var animationContext: AnimationContext? = null

    override fun schedule(delaySeconds: Double, action: () -> Unit): Task {
        val task = Task(System.currentTimeMillis() + (delaySeconds * 1000).toLong(), action)
        if (inEventLoop) {
            if (eventLoopBuffer == null) eventLoopBuffer = ArrayList(1)
            eventLoopBuffer!!.add(task)
        } else {
            runningTasks.add(task)
        }
        return task
    }

    override fun update() {
        val time = System.currentTimeMillis()

        val runningTasks = runningTasks
        if (runningTasks.isNotEmpty()) {
            inEventLoop = true
            with(runningTasks.iterator()) {
                while (hasNext()) {
                    val task = next()
                    if (task.cancelled) {
                        remove()
                        continue
                    }
                    if (time >= task.scheduledTo) {
                        remove()
                        try {
                            task.action()
                        } catch (ex: Exception) {
                            RuntimeException("Error while executing task", ex).printStackTrace()
                        }
                    }
                }
            }
            inEventLoop = false
            eventLoopBuffer?.apply { runningTasks.addAll(this) }
            eventLoopBuffer = null
        }

        val runningAnimations = runningAnimations
        if (runningAnimations.isNotEmpty()) {
            with(runningAnimations.iterator()) {
                while (hasNext()) {
                    if (!next().update(time)) remove()
                }
            }
        }
    }
}
