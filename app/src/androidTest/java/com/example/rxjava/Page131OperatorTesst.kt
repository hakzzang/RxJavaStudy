package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.Observable
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import java.util.concurrent.TimeUnit
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

    @Test
    fun `Observable의range1_3_출력값123확인`() {
        val source = Observable.range(1,3)
        source.subscribe {
            println("#onNext:$it")
        }
        val test = source.test()
        test.assertValues(1,2,3)
    }

    @Test
    fun `Observable의timer출력값확인`() {
        val source = Observable.timer(1, TimeUnit.SECONDS)
        println("#start:${Date()}")
        source.subscribe(
            {},{},{
                println("#onComplete:${Date()}")
            }
        )
        Thread.sleep(3000L)
    }

    @Test
    fun `Observable의map을통한_출력값확인`() {
        //Transforming
        val source = Observable.just(1, 2, 3).map { it * 10 }
        source.subscribe { println("#value:$it") }
        val test = source.test()
        test.assertValues(10,20,30)
    }
}