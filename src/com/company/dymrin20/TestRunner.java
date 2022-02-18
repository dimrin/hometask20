package com.company.dymrin20;


import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TestRunner {

    public static <T> void start(Class<T> testClass) {

        T testClassObject = newInstance(testClass);

        List<Method> beforeSuiteMethods = filterByAnnotation(Arrays.asList(testClass.getDeclaredMethods()), BeforeSuite.class);
        if (beforeSuiteMethods.size() > 1) {
            throw new RuntimeException(String.format("Object of type %s must contain only one BeforeSuite method.%n", testClass));
        }

        List<Method> testMethods = filterByAnnotation(Arrays.asList(testClass.getDeclaredMethods()), Test.class);
        if (testMethods.size() == 0) {
            System.err.printf("Object of type %s does not have any tests.%n", testClass);
            System.exit(-1);
        }

        testMethods = testMethods.stream().sorted(Comparator.comparingInt(o -> o.getAnnotation(Test.class).order())).toList();

        List<Method> afterSuiteMethods = filterByAnnotation(Arrays.asList(testClass.getDeclaredMethods()), AfterSuite.class);
        if (afterSuiteMethods.size() > 1) {
            throw new RuntimeException(String.format("Object of type %s must contain only one AfterSuite method.%n", testClass));
        }

        doMethodInvokes(beforeSuiteMethods, testClassObject);
        doMethodInvokes(testMethods, testClassObject);
        doMethodInvokes(afterSuiteMethods, testClassObject);
    }

    private static <T extends Annotation> List<Method> filterByAnnotation(List<Method> methods, Class<T> annotationType) {
        return methods.stream().filter(method -> method.isAnnotationPresent(annotationType)).toList();
    }

    private static <T> void doMethodInvokes(List<Method> methods, T targetObject) {
        methods.forEach(method -> doMethodInvoke(method, targetObject));
    }

    private static <T> void doMethodInvoke(Method method, T targetObject) {
        try {
            method.invoke(targetObject);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("SWW while a invocation of the method of the object.", e);
        }
    }

    private static <T> T newInstance(Class<T> testClass) {
        try {
            return testClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("SWW while a creation of the new instance.", e);
        }
    }
}
