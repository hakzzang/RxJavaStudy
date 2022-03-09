package com.example.rxjava

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subscribers.TestSubscriber

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.internal.builders.SuiteMethodBuilder
import java.lang.NullPointerException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun `Observable의create로Hello_World_생성`() {
        val observable = Observable.create<String> { emitter ->
            emitter.onNext("Hello")
            emitter.onNext("World")
            emitter.onComplete()
        }
        val test = observable.test()
        test.assertValues("Hello", "World")
    }

    @Test
    fun `Observable의create로Hello_World_생성시에_고의적으로_IllegalAccessException_에러발생`() {
        val observable = Observable.create<String> { emitter ->
            emitter.onNext("Hello")
            emitter.onError(IllegalAccessException("Error"))
            emitter.onNext("World")
            emitter.onComplete()
        }
        observable.subscribe({
            println("Success:$it")
        },{
            println("ERROR:$it")
        })
        val test = observable.test()
        test.assertError(IllegalAccessException::class.java)
    }

    @Test
    fun `Observable의create로Hello_World_생성시에_에러발생시에_내용확인`() {
        val observable = Observable.create<String> { emitter ->
            emitter.onNext("Hello")
            emitter.onError(IllegalAccessException("Error"))
            emitter.onNext("World")
            emitter.onComplete()
        }
        observable.subscribe({
            println("Success:$it")
        },{
            println("ERROR:$it")
        })
        val test = observable.test()
        test.assertValues("Hello")
    }

    @Test
    fun `Observable의just로Hello_Wolrd_생성`(){
        val observable = Observable.just("Hello", "World")
        observable.subscribe(System.out::println)

        val test = observable.test()
        test.assertValues("Hello", "World")
    }

    @Test
    fun `Observable의just로null생성시에_내부적으로_NullPointerException_발생`() {
        val observable = Observable.just<String>(null)
        observable.subscribe({
            println("Success:$it")
        },{
            println("Error:$it")
        })
        val test = observable.test()
        test.assertError(NullPointerException::class.java)
    }

}