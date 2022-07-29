package com.pdfdeal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class MulThreadUtil {
    /**
     * * @Param indexOfStart 待处理列表开始值 含
     * * @Param indexOfEnd 待处理列表结束值 不含
     * * @Param  mulThreadInterface lambdaa表达式
     */
    public static void listMulThreadDealOld(int indexOfStart, int indexOfEnd, MulThreadInterface mulThreadInterface) {
        List<Integer> list = new ArrayList<>();
        for (int i = indexOfStart; i < indexOfEnd; i++) {
            list.add(i);
        }
        //执行
        listMulThreadDealOld(list, mulThreadInterface);
    }

    /**
     * @Param list 待处理列表
     * @Param mulThreadInterface lambdaa表达式
     * <p>
     * 用给定的lambda 方法处理 列表中的每一个元素（非处理列表）
     * 封装了多线程实现代码
     */
    public static <T> void listMulThreadDealOld(List<Integer> list, MulThreadInterface mulThreadInterface) {

        long start = System.currentTimeMillis();
        if (list == null || list.size() == 0) {
            return;
        }
        ExecutorService executorService = ThreadPoolFactory.createFixedThreadPool();//线程池，队列满后加线程至最大，溢出任务在mian线程执行
        Collection<Future<?>> futereList = new LinkedList<>();
        for (Integer taskIdx : list) {
            Runnable runnable = () -> {
                try {
                    eleMulThreadDeal(taskIdx, mulThreadInterface);
                } catch (Exception e) {
//                    System.out.println("捕获1");
                    e.printStackTrace();
                }
            };//多个线程并行，

            Future future = executorService.submit(runnable);//非阻塞执行 （最大线程+队列长度）个任务，其他任务阻塞于此。此时线程每消费一个任务， 新非阻塞执行数量加一。
            futereList.add(future);
          //  System.out.println("收集任务:" + taskIdx);
        }//循环完成后，所有任务必在收集清单，因为submit方法 会在队列溢出时 阻塞。
//        System.out.println("future0");

        // wait for futereList completion
        for (Future currTask : futereList) {//tasks非阻塞，意味着还有runnable在执行
//            System.out.println("消费" + s++);
            try {
                currTask.get();//currTask阻塞、有序执行。意味着当前任务的runnable必然已经执行。
//                currTask.cancel(false);
//                System.out.println("currTask.get();");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }//tasks任务全部执行完毕
        long end = System.currentTimeMillis();
        long curTaskTime = end - start;
        System.out.println("总时间 = " + curTaskTime);
        executorService.shutdown();
    }

    /**
     * 接受 参数 和 lambad方法，调接口来执行异步方法
     */
    private static <T> void eleMulThreadDeal(int taskIdx, MulThreadInterface mulThreadInterface) {
        boolean b = mulThreadInterface.taskDeal(taskIdx);
      //  System.out.println("任务" + taskIdx + ":" + b);
    }

    /**
     * 定义一个能容纳 处理单个任务 方法的空接口
     */
    @FunctionalInterface
    public interface MulThreadInterface {
        boolean taskDeal(int ele);
    }

    public static class ThreadPoolFactory {

        /**
         * 生成固定大小的线程池
         * @return 线程池
         */
        public static ExecutorService createFixedThreadPool() {

            return new ThreadPoolExecutor(
                    // 核心线程数
                   20,
                    // 最大线程数
                    20,
                    // 空闲线程存活时间
                    1L,
                    // 空闲线程存活时间单位
                    TimeUnit.SECONDS,
                    // 工作队列
                    new LinkedBlockingQueue(20000),
                    // 线程工厂
//                r -> new Thread(r, "PST-" + threadNumber.getAndIncrement() + ""),
                    r -> new Thread(r, "xxl-job, admin JobLosedMonitorHelper-callbackThreadPool-" + r.hashCode()),
                    // 拒绝策略
                    new ThreadPoolExecutor.CallerRunsPolicy());
        }

        /**
         * 理想的线程数，使用 2倍cpu核心数
         */
        public static int availableProcessors() {
            int i = Runtime.getRuntime().availableProcessors() ;
            System.out.println(i);
            return i;
        }
    }
}
