package com.wilson.testMain;

import com.wilson.common.MyPredicate;
import com.wilson.model.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Processor {

    static List<Person> people = new ArrayList() {{
        add(new Person("wilson", 170, 60));
        add(new Person("peter", 175, 55));
        add(new Person("nana", 155, 45));
        add(new Person("iris", 165, 50));
    }};

    public static void main(String[] args) {
//        System.out.println(people);
////        List targetPeople = simpleFilter(people, (e) -> e.getHeight() < 169);
//        List targetPeople = people.stream().filter((p) -> p.getHeight() < 169).collect(Collectors.toList());
//        System.out.println(targetPeople);
//        List targetPerple2 = simpleFilter(people, (e) -> e.getWeight() > 50);
//        System.out.println(targetPerple2);

        Comparator<Person> heightComparator = ((o1, o2) -> (int) (o1.getHeight() - o2.getHeight()));
        Comparator<Person> weightComparator = ((o1, o2) -> (int) (o1.getWeight() - o2.getWeight()));
        System.out.println(people.stream().max(heightComparator).get());
        System.out.println(people.stream().max(weightComparator).get());
        System.out.println(people.stream().sorted(heightComparator).collect(Collectors.toList()));
        System.out.println(people);
        Collections.sort(people);
        System.out.println(people);

        List list = IntStream.range(0, 5).mapToObj(e -> e*e).collect(Collectors.toList());
        System.out.println(list);
    }

    private static List simpleFilter(List<Person> list, MyPredicate<Person> predicate) {
        return list.stream().filter(obj -> predicate.positiveCondition(obj)).collect(Collectors.toList());
    }

}
