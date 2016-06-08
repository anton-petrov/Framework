package edu.petrov.framework;

/**
 * Created by anton on 6/8/16.
 */
public interface Task<E> {
    // Метода запускает таск на выполнение
    void execute();

    // Возвращает результат выполнения
    E getResult();
}
