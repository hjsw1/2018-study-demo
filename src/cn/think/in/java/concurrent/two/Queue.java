package cn.think.in.java.two;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Queue {

  final int num;
  final List<String> list;
  boolean isFull = false;
  boolean isEmpty = true;


  public Queue(int num) {
    this.num = num;
    this.list = new ArrayList<>();
  }


  public synchronized void put(String value) {
    try {
      if (isFull) {
        System.out.println("putThread 暂停了，让出了锁");
        this.wait();
        System.out.println("putThread 被唤醒了，拿到了锁");
      }

      list.add(value);
      System.out.println("putThread 放入了" + value);
      if (list.size() >= num) {
        isFull = true;
      }
      if (isEmpty) {
        isEmpty = false;
        System.out.println("putThread 通知 getThread");
        this.notify();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public synchronized String get(int index) {
    try {
      if (isEmpty) {
        System.err.println("getThread 暂停了，并让出了锁");
        this.wait();
        System.err.println("getThread 被唤醒了，拿到了锁");
      }

      String value = list.get(index);
      System.err.println("getThread 获取到了" + value);
      list.remove(index);

      Random random = new Random();
      int randomInt = random.nextInt(5);
      if (randomInt == 1) {
        System.err.println("随机数等于1， 清空集合");
        list.clear();
      }

      if (getSize() < num) {
        if (getSize() == 0) {
          isEmpty = true;
        }
        if (isFull) {
          isFull = false;
          System.err.println("getThread 通知 putThread 可以添加了");
          Thread.sleep(10);
          this.notify();
        }
      }
      return value;


    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }


  public int getSize() {
    return list.size();
  }


  public static void main(String[] args) {
    Queue queue = new Queue(10);
    new Thread(new PutThread(queue), "putThread").start();
    new Thread(new GetThread(queue), "getThread").start();
  }
}

class PutThread implements Runnable {

  Queue queue;

  public PutThread(Queue queue) {
    this.queue = queue;
  }

  @Override
  public void run() {
    int i = 0;
    for (; ; ) {
      i++;
      queue.put(i + "号");

    }
  }
}

class GetThread implements Runnable {

  Queue queue;

  public GetThread(Queue queue) {
    this.queue = queue;
  }

  @Override
  public void run() {
    for (; ; ) {
      for (int i = 0; i < queue.getSize(); i++) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        String value = queue.get(i);

      }
    }
  }
}
