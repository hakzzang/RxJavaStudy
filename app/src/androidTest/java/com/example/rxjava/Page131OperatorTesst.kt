package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.observables.GroupedObservable
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

    @Test
    fun `Observable의flatmap을통한_출력값확인`() {
        //Transforming
        val source = Observable
            .just(1,2,3)
            .flatMap { Observable.just(it*10) }

        source.subscribe { println("#value:$it") }
        val test = source.test()
        test.assertValues(10,20,30)
    }

    @Test
    fun `Observable의buffer를통한_출력값확인`() {
        //Transforming
        val source = Observable.range(0, 10).buffer(3)
        source.subscribe { integers ->
            println(integers.toString())
        }
        val test = source.test()
        test.assertValues(listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), listOf(9))
    }

    @Test
    fun `Observable의scan을통한_출력값확인`() {
        //Transforming
        val source = Observable
            .range(1, 5)
            .scan { x, y -> x + y }
        source.subscribe(System.out::println)
        val test = source.test()
        test.assertValues(1, 3, 6, 10, 15)
    }

    @Test
    fun `Observable의groupby연산자_출력값확인`() {
        //Transforming
        val source = Observable.just(
            "Magenta Circle",
            "Cyan Circle",
            "Yellow Triangle",
            "Yellow Circle",
            "Magenta Triangle",
            "Cyan Triangle"
        ).groupBy { item ->
            when {
                item.contains("Circle") -> return@groupBy "C"
                item.contains("Triangle") -> return@groupBy "T"
                else -> return@groupBy "None"
            }
        }
        source.subscribe { group ->
            println(group.key + "그룹 발행 시작")
            group.subscribe { shape ->
                println(group.key + ":" + shape)
            }
        }
    }

    @Test
    fun `Observable의debounce연산자_출력값확인`() {
        //filtering
        val source = Observable.create<String> { emitter ->
            emitter.onNext("1")
            Thread.sleep(100L)
            emitter.onNext("2")
            emitter.onNext("3")
            emitter.onNext("4")
            emitter.onNext("5")
            Thread.sleep(100L)
            emitter.onNext("6")
            Thread.sleep(100L)
        }.debounce(10, TimeUnit.MILLISECONDS)
        source.subscribe {
            println(it)
        }
        val test = source.test()
        test.assertValues("1", "5", "6")
        Thread.sleep(1000L)
    }

    @Test
    fun `Observable의distinct연산자_출력값확인`() {
        //filtering
        val source = Observable.just(1,2,2,1,3)
            .distinct()
        source.subscribe { println(it) }
        val test = source.test()
        test.assertValues(1, 2, 3)
    }

    @Test
    fun `Observable의elementAt연산자_출력값확인`() {
        //filtering
        val source = Observable.just(1,2,3,4).elementAt(2)
        source.subscribe {
            println(it)
        }
        val test = source.test()
        test.assertValue(3)
    }

    @Test
    fun `Observable의filter연산자_출력값확인`() {
        //filtering
        val source = Observable.just(2, 30, 22, 5, 60, 1)
            .filter { x -> x > 10 }
        source.subscribe { println(it) }
        val test = source.test()
        test.assertValues(30, 22, 60)
    }

    @Test
    fun `Observable의sample연산자_출력값확인`() {
        //filtering
        val source = Observable.interval(100, TimeUnit.MILLISECONDS)
            .sample(150, TimeUnit.MILLISECONDS)
        source.subscribe { println(it) }
        Thread.sleep(1000L)
    }

    @Test
    fun `Observable의skip연산자_출력값확인`() {
        //filtering
        val source = Observable.just(1, 2, 3, 4).skip(2)
        source.subscribe { println(it) }
        val test = source.test()
        test.assertValues(3, 4)
    }

    @Test
    fun `Observable의take연산자_출력값확인`() {
        //filtering
        val source = Observable.just(1, 2, 3, 4).take(2)
        source.subscribe { println(it) }
        val test = source.test()
        test.assertValues(1, 2)
    }

    @Test
    fun `Observable의all연산자_출력값확인`() {
        //filtering
        val source = Observable.just(2,1, -1, 5)
            .all { integer -> integer > 0 }
        source.subscribe { it -> println(it) }
        val test = source.test()
        test.assertValue(false)
    }

    @Test
    fun `Observable의amb연산자_출력값확인`() {
        //filtering
        val source1 = Observable.just(20, 40, 60).delay(0, TimeUnit.MILLISECONDS)
        val source2 = Observable.just(1,2,3).delay(100, TimeUnit.MILLISECONDS)
        val source3 = Observable.just(20, 40, 60).delay(200, TimeUnit.MILLISECONDS)
        val list = listOf(source1, source2, source3)
        Observable.amb(list).subscribe {
            println(it)
        }
    }
}