package net.thedragonteam.thedragonlib.lib;

public class PairKV<K, V> {
    private K key;
    private V value;

    public PairKV(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return this.key.hashCode() * 13 + (this.value == null ? 0 : this.value.hashCode());
    }

    public boolean equals(Object var1) {
        if (this == var1) {
            return true;
        } else if (!(var1 instanceof PairKV)) {
            return false;
        }
        PairKV var2 = (PairKV) var1;
        if (this.key != null) {
            if (!this.key.equals(var2.key)) {
                return false;
            }
        } else if (var2.key != null) {
            return false;
        }

        if (this.value != null) {
            if (!this.value.equals(var2.value)) {
                return false;
            }
        } else if (var2.value != null) {
            return false;
        }

        return true;
    }
}