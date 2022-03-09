package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.Single
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.AssertionError

@RunWith(AndroidJUnit4::class)
class SingleTestCodes {
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
}