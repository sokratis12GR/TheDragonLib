package net.thedragonteam.thedragonlib.util

import java.util.*

object ArrayUtils {

    fun arrayToLowercase(array: Array<out String>): Array<String?> {
        val lowercaseArray = arrayOfNulls<String>(array.size)
        for (i in array.indices) {
            lowercaseArray[i] = array[i].toLowerCase(Locale.ENGLISH)
        }
        return lowercaseArray
    }

    /**
     * Shift all objects in an array by the number of places specified.
     *
     *
     * Example:
     * Object[] array = {1, 2, 3, 4, 5}
     * array = arrayShift(array, 1);
     * array now equals {5, 1, 2, 3, 4}
     * Shift can be a positive or negative number
     *
     *
     * Shift is not restricted to any max or min value so it could for example be linked to a counter that
     * continuously increments.
     */
    fun arrayShift(input: Array<Any>, shift: Int): Array<Any?> {
        val newArray = arrayOfNulls<Any>(input.size)

        for (i in input.indices) {
            var newPos = (i + shift) % input.size

            if (newPos < 0) {
                newPos += input.size
            }

            newArray[newPos] = input[i]
        }

        return newArray
    }
}