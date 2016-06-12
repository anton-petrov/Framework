package edu.petrov.framework;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by anton on 6/12/16.
 */
public class TransferQueue<T> {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition full = lock.newCondition();
    private final Condition empty = lock.newCondition();

    final Object[] items;
    long _putIndex = 0;
    long _getIndex = 0;

    private int putIndex() {
        return (int) (_putIndex % (items.length));
    }

    private int getIndex() {
        return (int) (_getIndex % (items.length));
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return (int) (_putIndex - _getIndex);
    }


    public int maxSize() {
        return items.length;
    }

    public TransferQueue(int capacity) {
        items = new Object[capacity];
    }

    public void put(T value) throws InterruptedException {
        while (!tryPut(value, Long.MAX_VALUE, TimeUnit.DAYS)) ;
    }

    public boolean tryPut(T value) throws InterruptedException {
        return tryPut(value, 0, TimeUnit.MILLISECONDS);
    }

    public boolean tryPut(T value, long time, TimeUnit unit) throws InterruptedException {
        boolean result;
        lock.lock();
        try {
            result = _put(value);
            if (!result) {
                full.await(time, unit);
                result = _put(value);
            }
            if (result) {
                empty.signal();
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    private boolean _put(T value) {
        if (_putIndex - _getIndex >= maxSize()) {
            return false;
        }
        items[putIndex()] = value;
        _putIndex++;
        return true;

    }

    public T get() throws InterruptedException {
        T result;
        while ((result = tryGet(Long.MAX_VALUE, TimeUnit.DAYS)) == null) ;
        return result;
    }

    public T tryGet() throws InterruptedException {
        return tryGet(0, TimeUnit.MILLISECONDS);
    }

    public T tryGet(long time, TimeUnit unit) throws InterruptedException {
        lock.lock();
        T result = null;
        try {
            result = _get();
            if (result == null) {
                empty.await(time, unit);
                result = _get();
            }
            if (result != null) {
                full.signal();
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    private T _get() {
        if (isEmpty()) {
            return null;
        }
        T result = (T) items[getIndex()];
        _getIndex++;
        return result;
    }

    public static void main(String[] args) throws InterruptedException {
        TransferQueue<? super Integer> queue = new TransferQueue<>(10);
        for (Integer i = 0; i < queue.maxSize(); i++) {
            queue.put(i);
        }
        queue.tryPut(10);
        while (!queue.isEmpty()) {
            System.out.println(queue.get());
        }
    }
}