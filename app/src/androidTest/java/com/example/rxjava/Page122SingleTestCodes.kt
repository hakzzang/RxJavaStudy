package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.AssertionError
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class Page122SingleTestCodes {
    @Test
    fun `Single의just에서Hello아이템출력`() {
        val single = Single.just("Hello")
        single.subscribe({
            println("onSuccess:$it")
        }, {
            println("onError:$it")
        })
        val test = single.test()
        test.assertValue("Hello")
    }

    @Test
    fun `Single의create에서Hello출력`() {
        val single = Single.create<String> { emitter ->
            emitter.onSuccess("Hello")
        }
        single.subscribe({
            println("onSuccess:$it")
        }, {
            println("onError:$it")
        })
        val test = single.test()
        test.assertValues("Hello")
    }

    @Test
    fun `Single의create에서Hello_World출력시에AssertionError발생확인`() {
        val single = Single.create<String> { emitter ->
            emitter.onSuccess("Hello")
            emitter.onSuccess("World")
        }

        single.subscribe({
            println("onSuccess:$it")
        }, {
            println("onError:$it")
        })
        val test = single.test()
        test.assertError(AssertionError::class.java)
    }

    @Test
    fun `Single의toObservable에서Hello_World출력`() {
        val observable = Single.just("Hello World").toObservable()

        observable.subscribe({
            println("onNext:$it")
        }, {
            println("onError:$it")
        }, {
            println("onComplete")
        })
        val test = observable.test()
        test.assertValue("Hello World")
    }

    @Test
    fun `Maybe의create에서_1개아이템_출력`() {
        val maybe = Maybe.create<Int> { emitter ->
            emitter.onSuccess(100)
            emitter.onSuccess(200)
            emitter.onComplete()
        }
        maybe.doOnSuccess { println("doOnSuccess1") }
            .doOnComplete { println("doOnComplete1") }
            .subscribe(System.out::println)

        val test = maybe.test()
        test.assertValues(100)
    }

    @Test
    fun `Maybe의create에서_complete_출력`() {
        val maybe = Maybe.create<Int> { emitter -> emitter.onComplete() }
        maybe.subscribe({
            println("onSuccess2")
        }, {
            println("onError")
        }, {
            println("onComplete2")
        })

        val test = maybe.test()
        test.assertValues()
        test.assertComplete()
    }

    @Test
    fun `Maybe_just_출력`() {
        //Maybe 또한 Single과 같이 1개의 아이템을 출력
        val maybe = Maybe.just("Hello")

        maybe.subscribe({
            println(it)
        }, {
            println(it.message)
        })
        val test = maybe.test()
        test.assertValue("Hello")
    }

    @Test
    fun `Maybe의Observable변환`() {
        val src1 = Observable.just(1, 2, 3)
        val srcMaybe1 = src1.firstElement()
        srcMaybe1.subscribe(System.out::println)

        val src2 = Observable.empty<Int>()
        val srcMaybe2 = src2.firstElement()
        srcMaybe2.subscribe(System.out::println, {

        }, {
            println("onComplete!")
        })
    }

    @Test
    fun `Completable의create에서Hello출력`() {
        val completable = Completable.create { emitter ->
            emitter.onComplete()
        }
        completable.subscribe {
            //onCompleted 호출
            println("completed1")
        }
        val test = completable.test()
        test.assertValues()
        test.assertComplete()
    }

    @Test
    fun `Completable의fromRunnable실행`() {
        val completable = Completable.fromRunnable {

        }
        completable.subscribe {
            //onCompleted 호출
            println("completed2")
        }
        val test = completable.test()
        test.assertValues()
        test.assertComplete()
    }

    @Test
    fun `Observable_interval에서sleep시_아이템출력확인`() {
        val src = Observable.interval(1, TimeUnit.SECONDS)
        src.subscribe {
            println("#1:$it")
        }
        Thread.sleep(3000L)
        src.subscribe {
            println("#2:$it")
        }
        Thread.sleep(3000L)
    }

    @Test
    fun `Observable의publish연산자_아이템출력확인`() {
        val src = Observable.interval(1, TimeUnit.SECONDS).publish()
        src.connect()
        src.subscribe{
            println("#1:$it")
        }
        Thread.sleep(3000L)
        src.subscribe {
            println("#2:$it")
        }
        Thread.sleep(3000L)
    }
}