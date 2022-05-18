package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.Observable
import org.junit.Test
import org.junit.runner.RunWith
import org.reactivestreams.Publisher
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun `PAGE115_Observable의create로_아이템발행_테스트`() {
        // 아이템 발행의 완료를 알릴 수 있다.
        // 발행이 완료된 후에는 아이템을 출력할 수 없다.
        val observable = Observable.create<String> { emitter ->
            emitter.onNext("Hello")
            emitter.onNext("World")
            emitter.onComplete()
            emitter.onNext("World2")
        }
        observable.subscribe({
            println("Success: $it")
        },{
            println("ERROR: $it")
        }, {
            println("COMPLETE")
        })
        val test = observable.test()
        test.assertValues("Hello", "World")
    }

    @Test
    fun `PAGE116_Observable의create로_IllegalAccessException_에러_테스트`() {
        // error 발생시 에러를 알릴 수 있다.
        // error 발생시에 해당 스트림이 종료되어, onComplete가 호출되지 않아 complete를 알리지 못한다.
        val observable = Observable.create<String> { emitter ->
            emitter.onNext("Hello")
            emitter.onError(IllegalAccessException("Error"))
            emitter.onNext("World")
            emitter.onComplete()
        }
        observable.subscribe({
            println("Success: $it")
        },{
            println("ERROR: $it")
        }, {
            println("COMPLETE")
        })
        val test = observable.test()
        test.assertError(IllegalAccessException::class.java)
    }

    @Test
    fun `PAGE117_Observable의just로_아이템발행_테스트`(){
        // just를 통해서 여러 개의 아이템을 발행할 수 있다.
        val observable = Observable.just("Hello", "World")
        observable.subscribe({
            println("Success: $it")
        },{
            println("ERROR: $it")
        }, {
            println("COMPLETE")
        })

        val test = observable.test()
        test.assertValues("Hello", "World")
    }

    @Test
    fun `PAGE119_Observable의fromArray로_아이템발행_테스트`() {
        // fromArray를 통해서 동일한 자료형의 아이템을 출력할 수 있다.
        val array = arrayOf("A", "B", "C")
        val observable = Observable.fromArray(*array)
        observable.subscribe(System.out::println)

        val test = observable.test()
        test.assertValues("A", "B", "C")
        test.assertValueCount(3)
    }

    @Test
    fun `PAGE119_Observable의fromIterable로_아이템발행_테스트`() {
        // fromIterable을 통해서 동일한 자료형의 아이템을 출력할 수 있다.
        val observable = Observable.fromIterable(listOf("A", "B", "C"))
        observable.subscribe(System.out::println)

        val test = observable.test()
        test.assertValues("A", "B", "C")
        test.assertValueCount(3)
    }

    @Test
    fun `PAGE120_future_and_callable_테스트`() {
        val executorService = Executors.newSingleThreadExecutor()

        val hello = Callable {
            Thread.sleep(10000L)
            "Hello"
        }

        val helloFuture: Future<String> = executorService.submit(hello)
        println("Started!")

        println(helloFuture.get())  // blocking call

        println("End!!")
        executorService.shutdown()
    }
    @Test
    fun `PAGE120_Observable의fromFuture_아이템발행_테스트`() {
        // fromFuture를 통해서 5s 후에 아이템 발행 확인
        val callable = Callable {
            Thread.sleep(5_000L)
            return@Callable "Hello World"
        }
        val future = Executors.newSingleThreadExecutor().submit(callable)
        val observable = Observable.fromFuture(future)
        observable.subscribe(System.out::println)

        val test = observable.test()
        test.assertValue("Hello World")
    }

    @Test
    fun `PAGE120_Observable의fromPublisher_아이템발행_테스트`() {
        // Publisher를 통해서 아이템을 발행하는 것을 확인
        val observable = Observable.fromPublisher<String>(Publisher { subscriber ->
            subscriber.onNext("A")
            subscriber.onNext("B")
            subscriber.onNext("C")
            subscriber.onComplete()
        })

        observable.subscribe({
            println("onNext:$it")
        }, {
            println("onError:$it")
        }, {
            println("onComplete")
        })

        val test = observable.test()
        test.assertValues("A", "B", "C")
        test.assertValueCount(3)
    }

    @Test
    fun `PAGE121_Observable의fromCallable_아이템발행_테스트`() {
        val callable = Callable {
            Thread.sleep(5_000L)
            return@Callable "Hello"
        }

        val source = Observable.fromCallable(callable)
        source.subscribe({
            println("onNext:$it")
        }, {
            println("onError:$it")
        }, {
            println("onComplete")
        })
        val test = source.test()
        test.assertValues("Hello")
    }


}