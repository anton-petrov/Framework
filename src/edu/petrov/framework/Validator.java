package edu.petrov.framework;

/**
 * Created by anton on 6/8/16.
 */
public interface Validator<R> {
    // Валидирует переданое значение
    boolean isValid(R result);
}
