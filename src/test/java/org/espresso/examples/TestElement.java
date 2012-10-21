package org.espresso.examples;

import org.espresso.SqlEngine;
import org.espresso.index.Getter;
import org.espresso.index.HashIndex;
import org.espresso.index.Index;
import org.espresso.index.Indices;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TestElement {
    private final String name;
    private final int age;
    final static int STORE_SIZE = 10000000;

    public TestElement(final String name, final int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public static void main(final String... args) throws SQLException {
        final List<TestElement> store = new ArrayList<TestElement>(STORE_SIZE);
        final Indices<TestElement> indices = new Indices<TestElement>(HashIndex.newIndex(String.class, "name",
                new Getter<TestElement, String>() {
            @Override
            public String get(final TestElement object) {
                return object.getName();
            }
        }));
        for (int i = 0; i < STORE_SIZE; i++) {
            final TestElement element = new TestElement("Name_" + (i % 1000), i % 100);
            store.add(element);
            indices.addToIndices(element);
        }



        long start = System.currentTimeMillis();
        SqlEngine<TestElement> engine = new SqlEngine<TestElement>(TestElement.class,
                "select * from TestElement where name = 'Name_0';");
        List<TestElement> result = engine.execute(store.iterator(), indices);
        long end = System.currentTimeMillis();
        System.out.println("Returned " + result.size() + " in " + (end - start) + "ms.");


    }
}