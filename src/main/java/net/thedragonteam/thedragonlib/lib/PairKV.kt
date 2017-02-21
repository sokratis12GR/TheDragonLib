package net.thedragonteam.thedragonlib.lib

class PairKV<K, V>(var key: K?, var value: V?) {

    override fun hashCode(): Int {
        return this.key!!.hashCode() * 13 + if (this.value == null) 0 else this.value!!.hashCode()
    }

    override fun equals(var1: Any?): Boolean {
        if (this === var1) {
            return true
        } else if (var1 !is PairKV<*, *>) {
            return false
        } else {
            if (this.key != null) {
                if (this.key != var1.key) {
                    return false
                }
            } else if (var1.key != null) {
                return false
            }

            if (this.value != null) {
                if (this.value != var1.value) {
                    return false
                }
            } else if (var1.value != null) {
                return false
            }

            return true
        }
    }
}