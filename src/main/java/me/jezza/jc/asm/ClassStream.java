package me.jezza.jc.asm;

import me.jezza.jc.lib.Utils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Jezza
 */
public class ClassStream implements MemberStream<Class<?>> {
	protected Stream<Class<?>> stream;

	protected ClassStream(Stream<Class<?>> stream) {
		this.stream = stream;
	}

	@Override
	public Stream<Class<?>> backingStream() {
		return stream;
	}

	@Override
	public ClassStream from(Class<?> declaringClass) {
		stream = stream.filter(declaringClass::isAssignableFrom);
		return this;
	}

	@Override
	public ClassStream in(String packageName) {
		return in(packageName, true);
	}

	@Override
	public ClassStream in(String packageName, boolean direct) {
		stream = stream.filter(type -> {
			String s = Utils.packageName(type);
			return direct ? packageName.equals(s) : packageName.startsWith(s);
		});
		return this;
	}

	@Override
	public <A extends Annotation> AnnotatedClassStream<A> with(Class<A> annotation) {
		return new AnnotatedClassStream<>(stream, annotation);
	}

	@Override
	public ClassStream filter(Predicate<? super Class<?>> predicate) {
		stream = stream.filter(predicate);
		return this;
	}

	@Override
	public ClassStream sorted() {
		stream = stream.sorted();
		return this;
	}

	@Override
	public ClassStream sorted(Comparator<? super Class<?>> comparator) {
		stream = stream.sorted(comparator);
		return this;
	}

	@Override
	public ClassStream peek(Consumer<? super Class<?>> action) {
		stream = stream.peek(action);
		return this;
	}

	@Override
	public ClassStream limit(long maxSize) {
		stream = stream.limit(maxSize);
		return this;
	}

	@Override
	public ClassStream skip(long n) {
		stream = stream.skip(n);
		return this;
	}

	@Override
	public IntStream mapToInt(ToIntFunction<? super Class<?>> mapper) {
		return stream.mapToInt(mapper);
	}

	@Override
	public <R> Stream<R> map(Function<? super Class<?>, ? extends R> mapper) {
		return stream.map(mapper);
	}

	@Override
	public <R> Stream<R> flatMap(Function<? super Class<?>, ? extends Stream<? extends R>> mapper) {
		return stream.flatMap(mapper);
	}

	@Override
	public Class<?> reduce(Class<?> identity, BinaryOperator<Class<?>> accumulator) {
		return stream.reduce(identity, accumulator);
	}

	@Override
	public Optional<Class<?>> reduce(BinaryOperator<Class<?>> accumulator) {
		return stream.reduce(accumulator);
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super Class<?>, U> accumulator, BinaryOperator<U> combiner) {
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
	public List<Class<?>> toList() {
		return collect(Collectors.toList());
	}

	@Override
	public Set<Class<?>> toSet() {
		return collect(Collectors.toSet());
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super Class<?>> accumulator, BiConsumer<R, R> combiner) {
		return stream.collect(supplier, accumulator, combiner);
	}

	@Override
	public <R, A> R collect(Collector<? super Class<?>, A, R> collector) {
		return stream.collect(collector);
	}

	@Override
	public void forEach(Consumer<? super Class<?>> action) {
		stream.forEach(action);
	}

	@Override
	public void forEachOrdered(Consumer<? super Class<?>> action) {
		stream.forEachOrdered(action);
	}

	@Override
	public long count() {
		return stream.count();
	}

	@Override
	public Optional<Class<?>> min(Comparator<? super Class<?>> comparator) {
		return stream.min(comparator);
	}

	@Override
	public Optional<Class<?>> max(Comparator<? super Class<?>> comparator) {
		return stream.max(comparator);
	}

	@Override
	public boolean anyMatch(Predicate<? super Class<?>> predicate) {
		return stream.anyMatch(predicate);
	}

	@Override
	public boolean allMatch(Predicate<? super Class<?>> predicate) {
		return stream.allMatch(predicate);
	}

	@Override
	public boolean noneMatch(Predicate<? super Class<?>> predicate) {
		return stream.noneMatch(predicate);
	}

	@Override
	public Optional<Class<?>> findFirst() {
		return stream.findFirst();
	}

	@Override
	public Optional<Class<?>> findAny() {
		return stream.findAny();
	}

	@Override
	public Iterator<Class<?>> iterator() {
		return stream.iterator();
	}

	@Override
	public Spliterator<Class<?>> spliterator() {
		return stream.spliterator();
	}

	@Override
	public boolean isParallel() {
		return stream.isParallel();
	}

	@Override
	public ClassStream sequential() {
		stream = stream.sequential();
		return this;
	}

	@Override
	public ClassStream parallel() {
		stream = stream.parallel();
		return this;
	}

	@Override
	public ClassStream unordered() {
		stream = stream.unordered();
		return this;
	}

	@Override
	public ClassStream onClose(Runnable closeHandler) {
		stream = stream.onClose(closeHandler);
		return this;
	}

	@Override
	public void close() {
		stream.close();
	}
}
