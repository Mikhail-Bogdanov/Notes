package com.example.note.feedback.interfaces

import com.example.note.feedback.classes.Notifyer

interface Observer {
    fun update(type: Notifyer.NotifyTypes)
}