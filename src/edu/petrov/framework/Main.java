package edu.petrov.framework;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class ExecutorImpl<T> implements Executor<T> {

    private static class TaskAndValidator<T> {

        Task<? extends T> task;

        public TaskAndValidator(Task<? extends T> task, Validator<T> validator) {
            this.task = task;
            this.validator = validator;
        }

        Validator<T> validator;

        public Task<? extends T> getTask() {
            return task;
        }

        public void setTask(Task<? extends T> task) {
            this.task = task;
        }

        public Validator<T> getValidator() {
            return validator;
        }

        public void setValidator(Validator<T> validator) {
            this.validator = validator;
        }
    }

    private List<TaskAndValidator<T>> taskAndValidatorList = new LinkedList<TaskAndValidator<T>>();

    @Override
    public void addTask(Task<? extends T> task) {
        taskAndValidatorList.add(new TaskAndValidator<>(task, null));
    }

    @Override
    public void addTask(Task<? extends T> task, Validator<T> validator) {
        taskAndValidatorList.add(new TaskAndValidator<>(task, validator));
    }

    @Override
    public void execute() {
        for (TaskAndValidator<T> taskAndValidator : taskAndValidatorList) {
            taskAndValidator.getTask().execute();
        }
    }

    @Override
    public List<T> getValidResults() {
        List<T> result = new LinkedList<T>();
        for (TaskAndValidator<T> taskAndValidator : taskAndValidatorList) {
            if (taskAndValidator.getValidator() != null &&
                    taskAndValidator.getValidator().isValid(taskAndValidator.getTask().getResult())) {

                result.add(taskAndValidator.getTask().getResult());
            }
        }
        return result;
    }

    @Override
    public List<T> getInvalidResults() {
        List<T> result = new LinkedList<T>();
        for (TaskAndValidator<T> taskAndValidator : taskAndValidatorList) {
            if (taskAndValidator.getValidator() != null &&
                    !taskAndValidator.getValidator().isValid(taskAndValidator.getTask().getResult())) {

                result.add(taskAndValidator.getTask().getResult());
            }
        }
        return result;
    }

}

class NumberValidator<T extends Number> implements Validator<T> {

    @Override
    public boolean isValid(T result) {
        return result.longValue() != 0;
    }
}

class NumberTask<T extends Number> implements Task<T> {

    private T value;

    public NumberTask(T value) {
        this.value = value;
    }

    @Override
    public void execute() {
        System.out.println("NumberTask! Value => " + value);
    }

    @Override
    public T getResult() {
        return value;
    }
}

public class Main {

    public static void test(List<Task<Integer>> intTasks) {
        Executor<Number> numberExecutor = new ExecutorImpl<>();

        for (Task<Integer> intTask : intTasks) {
            numberExecutor.addTask(intTask);
        }
        numberExecutor.addTask(new NumberTask<>(10L), new NumberValidator<>());

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
        List<Task<Integer>> tasks = new LinkedList<>();
        tasks.add(new NumberTask<>(1));
        tasks.add(new NumberTask<>(2));
        tasks.add(new NumberTask<>(3));
        test(tasks);
    }
}
