package com.wilson.model;

import lombok.Data;

@Data
public class Person implements Comparable {
    private String name;
    private double height;
    private double weight;

    public Person(String name, double height, double weight) {
        this.name = name;
        this.height = height;
        this.weight = weight;
    }

    public Person() {
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) {
            return 0;
        }
        if (this.getClass() != o.getClass()) {
            return 0;
        }
        Person person = (Person) o;
        return Double.compare(person.getHeight(), this.getHeight());
    }
}
