package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.Observable
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class Page151CombiningOperatorsTest {
    @Test
    fun `combineLatest연산자_테스트`() {
        // 두 Observable에서 가장 최근 발행한 아이템을 취합하여 하나로 발행하는 연산자
        val src1 = Observable.create<Int> { emitter ->
            Thread {
                for (number in 1..5) {
                    emitter.onNext(number)
                    try {
                        Thread.sleep(1000L)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.start()
        }

        val src2 = Observable.create<String> { emitter ->
            Thread {
                try {
                    Thread.sleep(500L)
                    emitter.onNext("A")
                    Thread.sleep(700L)
                    emitter.onNext("B")
                    Thread.sleep(100L)
                    emitter.onNext("C")
                    Thread.sleep(700L)
                    emitter.onNext("D")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }

        val result = Observable.combineLatest(src1, src2) { number, word -> number.toString() + word }
        result.subscribe { println(it) }
        val test = result.test()
        Thread.sleep(5000L)
        test.assertValues("1A", "2A", "2B", "2C", "3C", "3D", "4D", "5D")
    }

    @Test
    fun `zip연산자_테스트`() {
        // 여러 Observable을 하나로 결합하여 지정된 함수를 통해서 하나의 아이템으로 발행한다.
        // 순서를 엄격하게 지켜서 출력하는게 combineLatest 와는 다르다.
        val src1 = Observable.create<Int> { emitter ->
            Thread {
                for (number in 1..5) {
                    emitter.onNext(number)
                    try {
                        Thread.sleep(1000L)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.start()
        }

        val src2 = Observable.create<String> { emitter ->
            Thread {
                try {
                    Thread.sleep(500L)
                    emitter.onNext("A")
                    Thread.sleep(700L)
                    emitter.onNext("B")
                    Thread.sleep(100L)
                    emitter.onNext("C")
                    Thread.sleep(700L)
                    emitter.onNext("D")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }

        val result = Observable.zip(src1, src2) { number, word -> number.toString() + word }
        result.subscribe { println(it) }
        val test = result.test()
        Thread.sleep(5000L)
        test.assertValues("1A", "2B", "3C", "4D")
    }

    @Test
    fun `merge연산자_테스트`() {
        // merge 연산자를 이용하면 여러 Observable을 하나의 Observable처럼 결합하여 사용 가능하다.
        val src1: Observable<Int> = Observable.intervalRange(1, 5, 0, 100, TimeUnit.MILLISECONDS)
            .map { (it * 20).toInt() }

        val src2 = Observable.create<Int> { emitter ->
            Thread {
                try {
                    Thread.sleep(350L)
                    emitter.onNext(1)
                    Thread.sleep(200)
                    emitter.onNext(1)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }

        val result = Observable.merge(src1, src2)
        val test = result.test()
        result.subscribe { println(it)}
        Thread.sleep(5000L)
        test.assertValues(20, 40, 60, 80, 1, 100, 1)
    }
}