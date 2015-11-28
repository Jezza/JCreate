package me.jezza.jc.asm;

import me.jezza.jc.lib.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Jezza
 */
public class ConstructorStream implements MemberStream<Constructor<?>> {
	protected Stream<Constructor<?>> stream;

	protected ConstructorStream(Stream<Constructor<?>> stream) {
		this.stream = stream;
	}

	@Override
	public Stream<Constructor<?>> backingStream() {
		return stream;
	}

	@Override
	public ConstructorStream from(Class<?> declaringClass) {
		stream = stream.filter(constructor -> constructor.getDeclaringClass() == declaringClass);
		return this;
	}

	@Override
	public ConstructorStream in(String packageName) {
		return in(packageName, true);
	}

	@Override
	public ConstructorStream in(String packageName, boolean direct) {
		stream.filter(constructor -> {
			String s = Utils.packageName(constructor.getDeclaringClass());
			return direct ? packageName.equals(s) : packageName.startsWith(s);
		});
		return this;
	}

	@Override
	public <A extends Annotation> AnnotatedConstructorStream<A> with(Class<A> annotation) {
		return new AnnotatedConstructorStream<>(stream, annotation);
	}

	@Override
	public ConstructorStream filter(Predicate<? super Constructor<?>> predicate) {
		stream = stream.filter(predicate);
		return this;
	}

	@Override
	public ConstructorStream sorted() {
		stream = stream.sorted();
		return this;
	}

	@Override
	public ConstructorStream sorted(Comparator<? super Constructor<?>> comparator) {
		stream = stream.sorted(comparator);
		return this;
	}

	@Override
	public ConstructorStream peek(Consumer<? super Constructor<?>> action) {
		stream = stream.peek(action);
		return this;
	}

	@Override
	public ConstructorStream limit(long maxSize) {
		stream = stream.limit(maxSize);
		return this;
	}

	@Override
	public ConstructorStream skip(long n) {
		stream = stream.skip(n);
		return this;
	}

	@Override
	public IntStream mapToInt(ToIntFunction<? super Constructor<?>> mapper) {
		return stream.mapToInt(mapper);
	}

	@Override
	public <R> Stream<R> map(Function<? super Constructor<?>, ? extends R> mapper) {
		return stream.map(mapper);
	}

	@Override
	public <R> Stream<R> flatMap(Function<? super Constructor<?>, ? extends Stream<? extends R>> mapper) {
		return stream.flatMap(mapper);
	}

	@Override
	public Constructor<?> reduce(Constructor<?> identity, BinaryOperator<Constructor<?>> accumulator) {
		return stream.reduce(identity, accumulator);
	}

	@Override
	public Optional<Constructor<?>> reduce(BinaryOperator<Constructor<?>> accumulator) {
		return stream.reduce(accumulator);
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super Constructor<?>, U> accumulator, BinaryOperator<U> combiner) {
		return stream.reduce(identity, accumulator, combiner);
	}

	@Override

	public Object[] toArray() {
		return stream.toArray();
	}

	@Override
	public <A> A[] toArray(IntFunction<A[]> generator) {
		return stream.toArray(generator);
	}

	@Override
	public List<Constructor<?>> toList() {
		return collect(Collectors.toList());
	}

	@Override
	public Set<Constructor<?>> toSet() {
		return collect(Collectors.toSet());
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super Constructor<?>> accumulator, BiConsumer<R, R> combiner) {
		return stream.collect(supplier, accumulator, combiner);
	}

	@Override
	public <R, A> R collect(Collector<? super Constructor<?>, A, R> collector) {
		return stream.collect(collector);
	}

	@Override
	public void forEach(Consumer<? super Constructor<?>> action) {
		stream.forEach(action);
	}

	@Override
	public void forEachOrdered(Consumer<? super Constructor<?>> action) {
		stream.forEachOrdered(action);
	}

	@Override
	public long count() {
		return stream.count();
	}

	@Override
	public Optional<Constructor<?>> min(Comparator<? super Constructor<?>> comparator) {
		return stream.min(comparator);
	}

	@Override
	public Optional<Constructor<?>> max(Comparator<? super Constructor<?>> comparator) {
		return stream.max(comparator);
	}

	@Override
	public boolean anyMatch(Predicate<? super Constructor<?>> predicate) {
		return stream.anyMatch(predicate);
	}

	@Override
	public boolean allMatch(Predicate<? super Constructor<?>> predicate) {
		return stream.allMatch(predicate);
	}

	@Override
	public boolean noneMatch(Predicate<? super Constructor<?>> predicate) {
		return stream.noneMatch(predicate);
	}

	@Override
	public Optional<Constructor<?>> findFirst() {
		return stream.findFirst();
	}

	@Override
	public Optional<Constructor<?>> findAny() {
		return stream.findAny();
	}

	@Override
	public Iterator<Constructor<?>> iterator() {
		return stream.iterator();
	}

	@Override
	public Spliterator<Constructor<?>> spliterator() {
		return stream.spliterator();
	}

	@Override
	public boolean isParallel() {
		return stream.isParallel();
	}

	@Override
	public ConstructorStream sequential() {
		stream = stream.sequential();
		return this;
	}

	@Override
	public ConstructorStream parallel() {
		stream = stream.parallel();
		return this;
	}

	@Override
	public ConstructorStream unordered() {
		stream = stream.unordered();
		return this;
	}

	@Override
	public ConstructorStream onClose(Runnable closeHandler) {
		stream = stream.onClose(closeHandler);
		return this;
	}

	@Override
	public void close() {
		stream.close();
	}
}