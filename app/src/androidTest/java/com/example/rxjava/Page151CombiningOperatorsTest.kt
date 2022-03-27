package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.Observable
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Page151CombiningOperatorsTest {
    @Test
    fun `combineLatest연산자_테스트`() {
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
        val test = result.test()
        Thread.sleep(5000L)
        test.assertValues("1A", "2A", "2B", "2C", "3C", "3D", "4D", "5D")
    }
}