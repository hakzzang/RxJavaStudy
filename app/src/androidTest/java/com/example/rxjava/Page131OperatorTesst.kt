package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.Observable
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
class Page131OperatorTesst {
    @Test
    fun `Observable의defer를5초후에_발행한_값_확인`() {
        val SECOND = 1_000L

        val justSrc = Observable.just(Date())
        val deferSrc = Observable.defer { Observable.just(Date()) }
        println("#1:${Date()}")
        Thread.sleep(5000L)
        println("#2:${Date()}")
        justSrc.subscribe { println("#justSrc:$it") }
        deferSrc.subscribe { println("#justSrc:$it") }

        val justTest = justSrc.test()
        val deferTest = deferSrc.test()
        val justValue = justTest.values()[0].time
        val deferValue = deferTest.values()[0].time
        assertEquals(deferValue.minus(justValue) / SECOND, 5)
    }

    @Test
    fun `Observable의empty값complete확인`() {
        val source = Observable.empty<String>()
        source
            .subscribe({
                println("#onNext")
            }, {

            }, {
                println("#onComplete")
            })
        val test = source.test()
        test.assertComplete()
    }

    @Test
    fun `Observable의never값notcomplete확인`() {
        val source = Observable.never<String>()
        source
            .subscribe({
                println("#onNext")
            }, {

            }, {
                println("#onComplete")
            })
        val test = source.test()
        test.assertNotComplete()
    }
}