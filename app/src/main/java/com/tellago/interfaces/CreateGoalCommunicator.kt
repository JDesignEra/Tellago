package com.tellago.interfaces

import com.google.firebase.Timestamp

interface CreateGoalCommunicator {
    // Might require more functions (repeat same data type?)
    fun passStringDataComOne(editTextStringInput: String)
    fun firstFormSubmit(editTextStringInput : String, stateCareer : Int, stateFamily : Int, stateLeisure : Int)
    fun secondFormSubmit(editTextStringInput : String, stateCareer : Int, stateFamily : Int, stateLeisure : Int, durationIntInput : Int, durationStringInput : String, reminderInput : Int)
    fun passLongDataComOne(LongInput: Long)
    fun passTimestampDataComOne(TimeStampInput: Timestamp)
    fun passStringDataComTwo(editTextStringInput: String)
    fun passLongDataComTwo(LongInput: Long)
    fun passTimestampDataComTwo(TimeStampInput: Timestamp)
}