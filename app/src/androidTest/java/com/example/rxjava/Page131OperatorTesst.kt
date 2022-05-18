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
        // just를 통해서 Date를 확인하게 되면, 쓰레드가 sleep 되기 이전 시간을 가리키는데
        // defer는 쓰레드의 sleep 지연된 후의 시간을 가리킨다.
        // Defer — 옵저버가 구독하기 전까지는 Observable 생성을 지연하고 구독이 시작되면 옵저버 별로 새로운 Observable을 생성한다
        val SECOND = 1_000L

        val justSrc = Observable.just(Date())
        val deferSrc = Observable.defer { Observable.just(Date()) }
        println("#1:${Date()}")
        Thread.sleep(5000L)
        println("#2:${Date()}")
        justSrc.subscribe { println("#1 now:$it") }
        deferSrc.subscribe { println("#2 now:$it") }

        val justTest = justSrc.test()
        val deferTest = deferSrc.test()
        val justValue = justTest.values()[0].time
        val deferValue = deferTest.values()[0].time
        assertEquals(deferValue.minus(justValue) / SECOND, 5)
    }

    @Test
    fun `Observable의empty값complete확인`() {
        // empty 연산자는 아이템을 발행하지는 않지만, 정상적으로 Stream을 종료
        val source = Observable.empty<String>()
        source.subscribe({
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
        // never 연산자는 empty와 마찬가지로 아이템을 발행하지 않지만, 스트림도 종료하지 않는다.
        val source = Observable.never<String>()
        source.subscribe({
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
        // 특정범위의 정수를 순서대로 발행하는 Observable 을 생성한다.
        val source = Observable.range(1,3)
        source.subscribe {
            println("#onNext:$it")
        }
        val test = source.test()
        test.assertValues(1,2,3)
    }

    @Test
    fun `Observable의timer출력값확인`() {
        // 특정 시간 동안 지연시킨 뒤 아이템을 발행한다.
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
        // Transforming, 변형하는 연산자
        // map은 발행되는 값에 대해 원하는 수식을 적용하거나 다른 타입으로 변환시킬 수 있다.
        val source = Observable.just(1, 2, 3).map { it * 10 }
        source.subscribe { println("#value:$it") }
        val test = source.test()
        test.assertValues(10,20,30)
    }

    @Test
    fun `Observable의flatmap을통한_출력값확인`() {
        // Observable을 또 다른 Observable로 변환한다.
        val source = Observable
            .just("a","b","c")
            .flatMap { str -> Observable.just(str+1, str+2) }

        source.subscribe { println("#value:$it") }
        val test = source.test()
        test.assertValues("a1", "a2", "b1", "b2", "c1", "c2")
    }

    @Test
    fun `Observable의flatMap을통한_구구단출력`() {
        // flatMap은 다중 for문처럼 동작할 수 있어서, 구구단 프로그램도 작성이 가능하다.
        Observable.range(2, 8)
            .flatMap { x -> Observable.range(1, 9)
                .map { y -> "$x * $y = ${x * y}" }
            }.subscribe {
                println(it)
            }
    }

    @Test
    fun `Observable의buffer를통한_출력값확인`() {
        // buffer 연산자는 Observable이 발행하는 아이템을 묶어서 List로 발행한다.
        val source = Observable.range(0, 10).buffer(3)
        source.subscribe { integers: MutableList<Int> ->
            println("버퍼 데이터 발행")
            println(integers.toString())
        }
        val test = source.test()
        test.assertValues(listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), listOf(9))
    }

    @Test
    fun `Observable의scan을통한_출력값확인`() {
        // scan 연산자는 순차적으로 발행되는 아이템들의 연산을 다음 아이템 발행의 첫 번쨰 인자로 전달한다.
        val source = Observable.range(1, 5)
            .scan { x, y -> x + y }
        source.subscribe(System.out::println)
        val test = source.test()
        test.assertValues(1, 3, 6, 10, 15)
    }

    @Test
    fun `Observable의groupby연산자_출력값확인`() {
        // groupBy 연산자는 GroupbedObservable로 재정의 할 수 있다.
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
        source.subscribe { group: GroupedObservable<String, String> ->
            println(group.key + "그룹 발행 시작")
            group.subscribe { shape ->
                println(group.key + ":" + shape)
            }
        }
    }

    @Test
    fun `Observable의debounce연산자_출력값확인`() {
        // 필터링 연산자
        // Observable의 시간 흐름이 지속되는 상태에서 다른 항목들은 배출하지 않고 특정 시간 마다 그 시점에 존재하는 항목
        // 하나를 Observable로부터 배출한다
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
        // 중복 발행을 하지 않도록 필터링한다.
        val source = Observable.just(1,2,2,1,3)
            .distinct()
        source.subscribe { println(it) }
        val test = source.test()
        test.assertValues(1, 2, 3)
    }

    @Test
    fun `Observable의elementAt연산자_출력값확인`() {
        // 특정 인덱스에 해당하는 아이템을 필터링한다.
        val source = Observable.just(1,2,3,4).elementAt(2)
        source.subscribe {
            println(it)
        }
        val test = source.test()
        test.assertValue(3)
    }

    @Test
    fun `Observable의filter연산자_출력값확인`() {
        // 조건식이 true일 때만 발행하도록 한다.
        val source = Observable.just(2, 30, 22, 5, 60, 1)
            .filter { x -> x > 10 }
        source.subscribe { println(it) }
        val test = source.test()
        test.assertValues(30, 22, 60)
    }

    @Test
    fun `Observable의sample연산자_출력값확인`() {
        // 특정 시간 간격으로 최근에 Observable이 배출한 항목들을 배출한다
        val source = Observable.interval(100, TimeUnit.MILLISECONDS)
            .sample(300, TimeUnit.MILLISECONDS)
        source.subscribe { println(it) }
        Thread.sleep(1000L)
    }

    @Test
    fun `Observable의skip연산자_출력값확인`() {
        // 발행하는 n개의 아이템을 무시하고 이후에 나오는 아이템을 발행한다.
        val source = Observable.just(1, 2, 3, 4).skip(2)
        source.subscribe { println(it) }
        val test = source.test()
        test.assertValues(3, 4)
    }

    @Test
    fun `Observable의take연산자_출력값확인`() {
        // n개의 아이템만을 방출하도록 하는 연산자
        val source = Observable.just(1, 2, 3, 4).take(2)
        source.subscribe { println(it) }
        val test = source.test()
        test.assertValues(1, 2)
    }

    @Test
    fun `Observable의all연산자_출력값확인`() {
        // 모든 발행되는 아이템이 조건에 부합하는 지, true false로 결과값을 알려줌
        val source = Observable.just(2,1, -1, 5)
            .all { integer -> integer > 0 }
        source.subscribe { it -> println(it) }
        val test = source.test()
        test.assertValue(false)
    }

    @Test
    fun `Observable의amb연산자_출력값확인`() {
        // 여러 개의 Observable을 동시에 구독하고 그중 가장 먼저 아이템을 발행하는 Observable을 선택
        val source1 = Observable.just(20, 40, 60).delay(100, TimeUnit.MILLISECONDS)
        val source2 = Observable.just(1,2,3).delay(10, TimeUnit.MILLISECONDS)
        val source3 = Observable.just(20, 40, 60).delay(200, TimeUnit.MILLISECONDS)
        val list = listOf(source1, source2, source3)
        val ambSource = Observable.amb(list)
        ambSource.subscribe({
            println("onNext:$it")
        }, {
            println("onError:${it.message}")
        }, {
            println("onComplete")
        })
        val test = ambSource.test()
        Thread.sleep(1000L)
        test.assertValues(1,2,3)
    }
}