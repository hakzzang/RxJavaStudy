package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MaybeTestCodes {
    @Test
    fun `Maybe의create에서`() {
        Maybe.create<Int> { emitter ->
            emitter.onSuccess(100)
            emitter.onSuccess(200)
            emitter.onComplete()
        }.doOnSuccess { println("doOnSuccess1") }
            .doOnComplete { println("doOnComplete1") }
            .subscribe(System.out::println)

        Maybe.create<Int> { emitter -> emitter.onComplete() }
            .doOnSuccess { println("doOnSuccess2") }
            .doOnComplete { println("doOnComplete2") }
            .subscribe(System.out::println)
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
}