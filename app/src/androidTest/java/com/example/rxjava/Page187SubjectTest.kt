package com.example.rxjava

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.AsyncSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.ReplaySubject
import io.reactivex.rxjava3.subjects.UnicastSubject
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class Page187SubjectTest {
    @Test
    fun `PublishSubject_테스트코드1`() {
        // 테스트 187 페이지
        // 가장 기본적인 Subject
        val src = PublishSubject.create<String>()
        src.subscribe({item-> println("A:$item") }, {t->t.printStackTrace()}, {println("onComplete")})
        src.subscribe({item-> println("B:$item") }, {t->t.printStackTrace()}, {println("onComplete")})
        src.onNext("Hello")
        src.onNext("World")
        src.onNext("!!!")
        src.onComplete()
        Thread.sleep(1000L)
    }

    @Test
    fun `PublishSubject_테스트코드2`() {
        // 테스트 188 페이지
        // 가장 기본적인 Subject
        // 아이템을 발행한 뒤 구독하면 아무런 아이템을 소비할 수 없다
        val src = PublishSubject.create<String>()
        src.onNext("Hello")
        src.onNext("World")
        src.onNext("!!!")
        src.onComplete()
        src.map { it.length }.subscribe { println(it) }
        Thread.sleep(1000L)
    }

    @Test
    fun `PublishSubject_테스트코드3`() {
        // 테스트189 페이지
        // Subject는 Observer이기도 하므로, 다른 Observable의 구독자로 이벤트를 처리할 수 있다.
        val src1 = Observable.interval(1, TimeUnit.SECONDS)
        val src2 = Observable.interval(500, TimeUnit.MILLISECONDS)
        val subject = PublishSubject.create<String>()
        src1.map { "A: $it"}.subscribe(subject)
        src2.map { "B: $it"}.subscribe(subject)
        subject.subscribe { println(it) }
        Thread.sleep(30000L)
    }

    @Test
    fun `SerializeSubject_테스트코드`() {
        // 190페이지 테스트코드
        // 서로 다른 스레드에서 Subject에 접근하여 아이템을 발행하는 상황에서 Subject는 스레드 안전을 보장하지 못 한다.
        // RxJava에서는 내부적으로 SerializedSubject를 가진다.
        // 밑의 예제는 스레드에 안전하지 않은 사례이다
        // 테스트코드가 190페이지와 191페이지가 동일하다. 어떻게 확인해야할 지 모르겠다.

        val counter = AtomicInteger()
        val subject = PublishSubject.create<Any>().toSerialized()
        subject.doOnNext({counter.incrementAndGet()})
            .doOnNext({counter.decrementAndGet()})
            .filter{counter.get()!=0}
            .subscribe({item-> println("A:$item") }, {t->t.printStackTrace()}, {println("onComplete")})

        val runnable = Runnable {
            for(i in 0 until 100_000) {
                try {
                    Thread.sleep(1)
                } catch (exception:Exception) {
                    exception.printStackTrace()
                }
                subject.onNext(i)
            }
        }

        Thread(runnable).start()
        Thread(runnable).start()
        Thread.sleep(1000)
        println("종료")
    }

    @Test
    fun `BehaviorSubject테스트코드`() {
        // 192페이지 테스트코드
        // 새로운 Observer를 통해 구독시 가장 마지막 아이템만을 발행
        // 이후에 발행되는 아이템들은 PublishSubject와 모두 동일하게 수신
        val subject = BehaviorSubject.create<Int>()
        subject.subscribe { println("A: $it") }
        subject.onNext(1)
        subject.onNext(2)
        subject.subscribe { println("B: $it") }
        subject.onNext(3)
        subject.subscribe { println("C: $it") }
        Thread.sleep(1000L)
    }

    @Test
    fun `ReplaySubject테스트코드`() {
        // 193페이지 테스트코드
        // 새로운 Observer를 통해 구독시 가장 마지막 아이템만을 발행
        // 이후에 발행되는 아이템들은 PublishSubject와 모두 동일하게 수신
        val subject = ReplaySubject.create<Int>()
        subject.subscribe { println("A: $it") }
        subject.onNext(1)
        subject.onNext(2)
        subject.subscribe { println("B: $it") }
        subject.onNext(3)
        subject.subscribe { println("C: $it") }
        Thread.sleep(1000L)
    }

    @Test
    fun `AsyncSubject테스트코드`() {
        // 194페이지 테스트코드
        // onComplete() 호출 직전에 발행된 아이템만을 구독자에게 전달한다.
        val subject = AsyncSubject.create<Int>()
        subject.subscribe { println("A: $it") }
        subject.onNext(1)
        subject.onNext(2)
        subject.subscribe { println("B: $it") }
        subject.onNext(3)
        subject.onComplete()
        subject.subscribe { println("C: $it") }
        Thread.sleep(1000L)
    }

    @Test
    fun `UnicastSubject테스트코드`() {
        // 195페이지 테스트코드
        val subject = UnicastSubject.create<Long>()
        Observable.interval(1, TimeUnit.SECONDS)
            .subscribe(subject)
        Thread.sleep(3000)
        subject.subscribe { println("A: $it") }
        Thread.sleep(3000)
        subject.subscribe { println("B: $it") }
        Thread.sleep(3000)
    }
}