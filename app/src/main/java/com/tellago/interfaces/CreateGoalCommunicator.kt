package com.tellago.interfaces

import com.google.firebase.Timestamp

interface CreateGoalCommunicator {
    // Might require more functions (repeat same data type?)
    fun passStringDataComOne(editTextStringInput: String)
    fun passLongDataComOne(LongInput: Long)
    fun passTimestampDataComOne(TimeStampInput: Timestamp)
    fun passStringDataComTwo(editTextStringInput: String)
    fun passLongDataComTwo(LongInput: Long)
    fun passTimestampDataComTwo(TimeStampInput: Timestamp)
}