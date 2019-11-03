package com.company;

//Pair class that we use to map both usernames and passwords to Person in Login
import java.io.Serializable;

public class PairCopy<K,V> implements Serializable {

    private K key;

    public K getKey() { return key; }


    private V value;

    public V getValue() { return value; }


    public PairCopy(K key, V value) {
        this.key = key;
        this.value = value;
    }


    @Override
    public String toString() {
        return key + "=" + value;
    }


    @Override
    public int hashCode() {
        // name's hashCode is multiplied by an arbitrary prime number (13)
        // in order to make sure there is a difference in the hashCode between
        // these two parameters:
        //  name: a  value: aa
        //  name: aa value: a
        return key.hashCode() * 13 + (value == null ? 0 : value.hashCode());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof PairCopy) {
            PairCopy pair = (PairCopy) o;
            if (key != null ? !key.equals(pair.key) : pair.key != null) return false;
            if (value != null ? !value.equals(pair.value) : pair.value != null) return false;
            return true;
        }
        return false;
    }
}