package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.Observable
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class Page161doOnXXXOperatorTest {

    @Test
    fun `doOnEach연산자`() {
        Observable.just(1, 2, 3)
            .doOnEach { notification ->
                println("value:${notification.value}")
                println("isOnNext:${notification.isOnNext}")
                println("isOnComplete:${notification.isOnComplete}")
                println("isOnError:${notification.isOnError}")
                println("error:${notification.error}")

                if (notification.error != null) {
                    notification.error.printStackTrace()
                }
            }.subscribe { println("subscribed = $it") }
    }

    @Test
    fun `doOnNext연산자`() {
        Observable
            .just(1, 2, 3)
            .doOnNext { item ->
                //return 시키는 값이 없음
                return@doOnNext
            }
            .subscribe { println("subscribed = $it") }
    }

    @Test
    fun `doOnSubscribe연산자`() {
        val src1 = Observable
            .just(1, 2, 3)
            .doOnSubscribe { item ->
                println("구독 시작")
                //return 시키는 값이 없음
                return@doOnSubscribe
            }
        src1.subscribe {
            println(it)
        }
        val test = src1.test()
        test.assertValues(1, 2, 3)

    }

    @Test
    fun `doOnCompleted연산자`() {
        val src1 = Observable
            .just(1, 2, 3)
            .doOnComplete {
                println("구독 완료")
                //return 시키는 값이 없음
                return@doOnComplete
            }
        src1.subscribe {
            println(it)
        }
        val test = src1.test()
        test.assertValues(1, 2, 3)

    }

    @Test
    fun `doOnError연산자`() {
        val src1 = Observable
            .just(1, 2, 3, 0)
            .map { 10 / it }
            .doOnError {
                println("오류:$it")
                //return 시키는 값이 없음
                return@doOnError
            }
        val test = src1.test()
        test.assertError(ArithmeticException::class.java)

    }

    @Test
    fun `doOnCompleted연산자-error가_발생할때_결과확인`() {
        val src1 = Observable
            .just(1, 2, 3, 0)
            .map { 10 / it }
            .doOnComplete {
                println("doOnComplete")
                //return 시키는 값이 없음
                return@doOnComplete
            }

        val test = src1.test()
        test.assertError(ArithmeticException::class.java)
    }

    @Test
    fun `doOnTerminate연산자-error가_발생할때_결과확인`() {
        val src1 = Observable
            .just(1, 2, 3, 0)
            .map { 10 / it }
            .doOnTerminate {
                println("doOnTerminate")
                //return 시키는 값이 없음
                return@doOnTerminate
            }
        val test = src1.test()
        test.assertError(ArithmeticException::class.java)
    }

    @Test
    fun `doOnDispose연산자`() {
        val src1 = Observable
            .interval(500, TimeUnit.MILLISECONDS)
            .doOnDispose {
                println("doOnDispose")
                //return 시키는 값이 없음
                return@doOnDispose
            }
        val result = src1.subscribe({
            println(it)
        }, {

        }, {
            println("doOnCompleted")
        })
        Thread.sleep(1000L)
        result.dispose()
        Thread.sleep(5000L)
        println("isDisposed:${result.isDisposed}")
    }

}