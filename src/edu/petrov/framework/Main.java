package edu.petrov.framework;

import java.util.List;

class ExecutorImpl<T> implements Executor<T> {

    @Override
    public void addTask(Task<? extends T> task) {

    }

    @Override
    public void addTask(Task<? extends T> task, Validator<T> validator) {

    }

    @Override
    public void execute() {

    }

    @Override
    public List<T> getValidResults() {
        return null;
    }

    @Override
    public List<T> getInvalidResults() {
        return null;
    }

}

class NumberValidator<T extends Number> implements Validator<T> {

    @Override
    public boolean isValid(T result) {
        return result.longValue() != 0;
    }
}

class NumberTask<T extends Number> implements Task<T> {

    @Override
    public void execute() {
        System.out.println("NumberTask!");
    }

    @Override
    public T getResult() {
        return null;
    }
}

public class Main {

    public static void test(List<Task<Integer>> intTasks) {
        Executor<Number> numberExecutor = new ExecutorImpl<>();

        for (Task<Integer> intTask : intTasks) {
            numberExecutor.addTask(intTask);
        }
        numberExecutor.addTask(new NumberTask<Long>(), new NumberValidator<>());

        numberExecutor.execute();

        System.out.println("Valid results:");
        for (Number number : numberExecutor.getValidResults()) {
            System.out.println(number);
        }
        System.out.println("Invalid results:");
        for (Number number : numberExecutor.getInvalidResults()) {
            System.out.println(number);
        }
    }


    public static void main(String[] args) {

    }
}
