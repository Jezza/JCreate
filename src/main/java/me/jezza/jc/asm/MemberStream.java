package me.jezza.jc.asm;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Jezza
 */
public interface MemberStream<T> {

	Stream<T> backingStream();

	MemberStream<T> from(Class<?> declaringClass);

	MemberStream<T> in(String packageName);

	MemberStream<T> in(String packageName, boolean direct);

	<A extends Annotation> AnnotatedMemberStream<T, A> with(Class<A> annotation);

	MemberStream<T> filter(Predicate<? super T> predicate);

	MemberStream<T> sorted();

	MemberStream<T> sorted(Comparator<? super T> comparator);

	MemberStream<T> peek(Consumer<? super T> action);

	MemberStream<T> limit(long maxSize);

	MemberStream<T> skip(long n);

	IntStream mapToInt(ToIntFunction<? super T> mapper);

	<R> Stream<R> map(Function<? super T, ? extends R> mapper);

	<R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper);

	T reduce(T identity, BinaryOperator<T> accumulator);

	Optional<T> reduce(BinaryOperator<T> accumulator);

	<U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner);

	Object[] toArray();

	<A> A[] toArray(IntFunction<A[]> generator);

	List<T> toList();

	Set<T> toSet();

	<R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner);

	<R, A> R collect(Collector<? super T, A, R> collector);

	void forEach(Consumer<? super T> action);

	void forEachOrdered(Consumer<? super T> action);

	long count();

	Optional<T> min(Comparator<? super T> comparator);

	Optional<T> max(Comparator<? super T> comparator);

	boolean anyMatch(Predicate<? super T> predicate);

	boolean allMatch(Predicate<? super T> predicate);

	boolean noneMatch(Predicate<? super T> predicate);

	Optional<T> findFirst();

	Optional<T> findAny();

	Iterator<T> iterator();

	Spliterator<T> spliterator();

	boolean isParallel();

	MemberStream<T> sequential();

	MemberStream<T> parallel();

	MemberStream<T> unordered();

	MemberStream<T> onClose(Runnable closeHandler);

	void close();
}
