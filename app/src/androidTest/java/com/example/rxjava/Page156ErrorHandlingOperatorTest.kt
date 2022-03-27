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
}