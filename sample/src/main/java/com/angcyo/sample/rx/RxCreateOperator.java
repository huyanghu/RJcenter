package com.angcyo.sample.rx;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Rx 创建操作符
 * Created by robi on 2016-07-13 18:08.
 */
public class RxCreateOperator {
    /**
     * create方法测试
     */
    public static void createDemo() {
        Observable
                //此方法的执行线程,由subscribeOn指定,且只有最后一次有效
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        try {
                            if (!subscriber.isUnsubscribed()) {
                                RxDemo.log(RxDemo.getMethodName());
                                subscriber.onNext("create call");
//                        throw new IllegalArgumentException("异常测试");
                            }
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .observeOn(Schedulers.newThread())//决定之后的观察在什么线程执行
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        RxDemo.log("map " + RxDemo.getMethodName());
                        return "s";
                    }
                })
                .observeOn(Schedulers.newThread())//决定之后的观察在什么线程执行
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        RxDemo.log("map 2 " + RxDemo.getMethodName());
                        return "s 2";
                    }
                })
                .observeOn(Schedulers.newThread())//决定之后的观察在什么线程执行
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Sub());
    }

    /**
     * just方法测试
     */
    public static void justDemo() {
        Observable.just("a", "b", "c")
//                .observeOn(Schedulers.computation())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        RxDemo.log(RxDemo.getMethodName() + " computation");
                        return "d";
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        RxDemo.log(RxDemo.getMethodName() + " io");
                        return "d";
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Sub());
    }

    public static void fromDemo() {
        Observable.from(new Future<String>() {
            @Override
            public boolean cancel(boolean b) {
                RxDemo.log(RxDemo.getMethodName());
                return false;
            }

            @Override
            public boolean isCancelled() {
                RxDemo.log(RxDemo.getMethodName());
                return false;
            }

            @Override
            public boolean isDone() {
                RxDemo.log(RxDemo.getMethodName());
                return false;
            }

            @Override
            public String get() throws InterruptedException, ExecutionException {
                RxDemo.log(RxDemo.getMethodName());
                return null;
            }

            @Override
            public String get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                RxDemo.log(RxDemo.getMethodName());
                return null;
            }
        }).observeOn(Schedulers.newThread()).subscribe(new Sub());

        Observable.from(new Integer[]{1, 2, 3}).subscribe(new Sub());
    }

    public static void repeatDemo() {
        //重复多少次
//        Observable.just("a", "b", "c", "d").repeat(3, Schedulers.computation()).map(new Func1<String, String>() {
//            @Override
//            public String call(String s) {
//                RxDemo.log(RxDemo.getMethodName() + "--" + s);
//                return "" + System.currentTimeMillis();
//            }
//        }).subscribe(new Sub());

        Observable.just("a", "b", "c", "d").repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<? extends Void> observable) {
                RxDemo.log(RxDemo.getMethodName());
//                return observable;
                return Observable.just("!");
            }
        }).subscribe(new Sub());
    }

    /**
     * 测试专用
     */
    public static class Sub extends Subscriber {

        @Override
        public void onCompleted() {
            RxDemo.log(RxDemo.getMethodName());
        }

        @Override
        public void onError(Throwable e) {
            RxDemo.log(RxDemo.getMethodName());
        }

        @Override
        public void onNext(Object o) {
            RxDemo.log(RxDemo.getMethodName() + " " + o.toString());
        }
    }
}
