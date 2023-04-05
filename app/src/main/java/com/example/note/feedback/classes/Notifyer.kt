package com.example.note.feedback.classes

import com.example.note.feedback.interfaces.Observable
import com.example.note.feedback.interfaces.Observer
import java.util.*

class Notifyer : Observable {

    enum class NotifyTypes{
        POSITIVE, NEGATIVE, OTHER
    }

    private var observers: LinkedList<Observer>? = null

    init{
        observers = LinkedList()
    }

    override fun addObserver(o: Observer) {
        observers!!.add(o)
    }

    override fun removeObserver(o: Observer) {
        observers!!.remove(o)
    }

    override fun notify(type: NotifyTypes) {
        observers!!.forEach {
            it.update(type)
        }
    }
}