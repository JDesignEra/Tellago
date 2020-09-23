package com.tellago.models

import com.google.firebase.Timestamp

data class Goal(

    var gCreationDate: Timestamp? = null,
    var gCurrentAmount: Long = 0,
    var gDeadline: Timestamp? = null,
    var gFullAmount: Long = 0,
    var gIcon: String = "",
    var gLastReminder: String = "",
    var gOwner: String = "",
    var gProgressTracker: String = "",
    var gReminderFreq: String = "",
    var gTitle: String = "",
    var goalid: String = ""


)