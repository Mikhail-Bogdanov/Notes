package com.example.note.feedback.interfaces

import com.example.note.feedback.classes.Notifyer

interface Observable {
    fun addObserver(o: Observer)
    fun removeObserver(o: Observer)
    fun notify(type: Notifyer.NotifyTypes)
}