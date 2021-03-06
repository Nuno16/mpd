package utils.iterators;

import utils.myiterators.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.*;

public class Queries {
    public static <T> Iterable<T> from(T... vals) {
        return () -> new IteratorArray<>(vals);
    }

    public static <T> Iterable<T> filter(Iterable<T> src, Predicate<T> p) {
        return () -> new FilterIterator<T>(src, p);
    }

    public static <T,R> Iterable<R> flatMap(Iterable<T> src, Function<T,Iterable<R>> mapper) {
        return () -> new FlatMapIterator(src, mapper);
    }


    public static <T> Iterable<T> takeWhile(Iterable<T> src,Predicate<T> p) {
        return () -> new TakeWhileIterator<T>(src, p);
    }

    public static <T,U> Iterable<U> map(Iterable<T> src, Function<T, U> mapper) {
        return () -> new MapIterator<T,U>(src, mapper);
    }

    public static <T> Iterable<T> skipWhile(Iterable<T> src, Predicate<T> p) {
        return () -> new SkipWhileIterator<T>(src, p);
    }



    public static <T> Iterable<T> skip(Iterable<T> src, int n) {
        return () -> {
            int count = n;
            Iterator<T> it = src.iterator();

            while (count > 0 && it.hasNext()) { --count; it.next(); }
            return  it;
        };
    }

    public static <T>  Iterable<T> justEvens(Iterable<T> src) {
        return () -> new JustEvensIterator<T>( src);
    }

    // terminal operations

    public static <T,R> R reduce(Iterable<T> src, R initial, BiFunction<R,T,R> accum) {
        R res = initial;
        for(T item: src) {
            res = accum.apply(res, item);
        }
        return res;
    }

    public static <T> void forEach(Iterable<T> src, Consumer<T> action) {
        Iterator<T> it = src.iterator();
        while(it.hasNext())
            action.accept(it.next());
    }

    public static <T> Optional<T> first(Iterable<T> src) {
        Iterator<T> it = src.iterator();
        if (!it.hasNext()) return Optional.empty();
        return Optional.of(it.next());
    }


    public static <T> Optional<T> reduce(Iterable<T> src, BinaryOperator<T> accumulator) {
        Iterator<T> it = src.iterator();
        if (!it.hasNext()) return Optional.empty();
        T res = it.next();
        while(it.hasNext()) {
            res = accumulator.apply(res, it.next());
        }
        return Optional.of(res);

    }

    public static <T> T[] toArray(Iterable<T> src) {
        List<T> res = new ArrayList<>();
        forEach(src, v-> res.add(v));
        return (T[]) res.toArray();
    }
}
