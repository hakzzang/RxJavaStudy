package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.Observable
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Page156ErrorHandlingOperatorTest {
    @Test
    fun `catch연산자_테스트`() {
        val src1 = Observable
            .just("1", "2", "a", "4")
            .map { it.toInt() }

        src1.subscribe({
            println("onNext:$it")
        }, {
            println("onError:${it.message}")
        }, {
            println("onCompleted")
        })
        val test = src1.test()
        test.assertError(NumberFormatException::class.java)
    }

    @Test
    fun `onErrorReturn연산자_테스트`() {
        val src1 = Observable
            .just("1", "2", "a", "4")
            .map { it.toInt() }
            .onErrorReturn { return@onErrorReturn -1 }

        src1.subscribe({
            println("onNext:$it")
        }, {
            println("onError:${it.message}")
        }, {
            println("onCompleted")
        })
        val test = src1.test()
        test.assertValues(1, 2, -1)
    }

    @Test
    fun `onErrorResumeNext연산자_테스트`() {
        val src1 = Observable
            .just("1", "2", "a", "4")
            .map { it.toInt() }
            .onErrorResumeNext { return@onErrorResumeNext Observable.just(100, 200, 300) }

        src1.subscribe({
            println("onNext:$it")
        }, {
            println("onError:${it.message}")
        }, {
            println("onCompleted")
        })
        val test = src1.test()
        test.assertValues(1, 2, 100, 200, 300)
    }

    @Test
    fun `retry연산자_테스트`() {
        val src1 = Observable
            .just("1", "2", "a", "4")
            .map { it.toInt() }
            .retry(1)

        src1.subscribe({
            println("onNext:$it")
        }, {
            println("onError:${it}")
        }, {
            println("onCompleted")
        })
        val test = src1.test()
        test.assertValues(1, 2, 1, 2)
        test.assertError(NumberFormatException::class.java)
    }
}