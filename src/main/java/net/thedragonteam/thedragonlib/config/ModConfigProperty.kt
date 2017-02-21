package net.thedragonteam.thedragonlib.config

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ModConfigProperty(val name: String, val category: String, val comment: String = "")