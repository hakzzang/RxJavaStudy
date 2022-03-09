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
import java.util.concurrent.Executors

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

    @Test
    fun `Observable의fromArray연산자_Array내용확인_ValueCount확인`() {
        val observable = Observable.fromArray("A", "B", "C")
        observable.subscribe(System.out::println)

        val test = observable.test()
        test.assertValues("A", "B", "C")
        test.assertValueCount(3)
    }

    @Test
    fun `Observable의fromIterable연산자_Iterator_내용확인_ValueCount확인`() {
        val observable = Observable.fromIterable(listOf("A", "B", "C"))
        observable.subscribe(System.out::println)

        val test = observable.test()
        test.assertValues("A", "B", "C")
        test.assertValueCount(3)
    }

    @Test
    fun `Observable의fromFuture연산자_내용확인`() {
        val future = Executors.newSingleThreadExecutor()
            .submit<String> {
                Thread.sleep(5_000L)
                return@submit "Hello World"
            }
        val observable = Observable.fromFuture(future)
        observable.subscribe(System.out::println)

        val test = observable.test()
        test.assertValue("Hello World")
    }
}