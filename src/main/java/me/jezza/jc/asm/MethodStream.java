package me.jezza.jc.asm;

import me.jezza.jc.lib.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Jezza
 */
public class MethodStream implements MemberStream<Method> {
	protected Stream<Method> stream;

	protected MethodStream(Stream<Method> stream) {
		this.stream = stream;
	}

	@Override
	public Stream<Method> backingStream() {
		return stream;
	}

	@Override
	public MethodStream from(Class<?> declaringClass) {
		stream = stream.filter(method -> method.getDeclaringClass() == declaringClass);
		return this;
	}

	@Override
	public MethodStream in(String packageName) {
		return in(packageName, true);
	}

	@Override
	public MethodStream in(String packageName, boolean direct) {
		stream.filter(method -> {
			String s = Utils.packageName(method.getDeclaringClass());
			return direct ? packageName.equals(s) : packageName.startsWith(s);
		});
		return this;
	}

	@Override
	public <A extends Annotation> AnnotatedMethodStream<A> with(Class<A> annotation) {
		return new AnnotatedMethodStream<>(stream, annotation);
	}

	@Override
	public MethodStream filter(Predicate<? super Method> predicate) {
		stream = stream.filter(predicate);
		return this;
	}

	@Override
	public MethodStream sorted() {
		stream = stream.sorted();
		return this;
	}

	@Override
	public MethodStream sorted(Comparator<? super Method> comparator) {
		stream = stream.sorted(comparator);
		return this;
	}

	@Override
	public MethodStream peek(Consumer<? super Method> action) {
		stream = stream.peek(action);
		return this;
	}

	@Override
	public MethodStream limit(long maxSize) {
		stream = stream.limit(maxSize);
		return this;
	}

	@Override
	public MethodStream skip(long n) {
		stream = stream.skip(n);
		return this;
	}

	@Override
	public IntStream mapToInt(ToIntFunction<? super Method> mapper) {
		return stream.mapToInt(mapper);
	}

	@Override
	public <R> Stream<R> map(Function<? super Method, ? extends R> mapper) {
		return stream.map(mapper);
	}

	@Override
	public <R> Stream<R> flatMap(Function<? super Method, ? extends Stream<? extends R>> mapper) {
		return stream.flatMap(mapper);
	}

	@Override
	public Method reduce(Method identity, BinaryOperator<Method> accumulator) {
		return stream.reduce(identity, accumulator);
	}

	@Override
	public Optional<Method> reduce(BinaryOperator<Method> accumulator) {
		return stream.reduce(accumulator);
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super Method, U> accumulator, BinaryOperator<U> combiner) {
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
	public List<Method> toList() {
		return collect(Collectors.toList());
	}

	@Override
	public Set<Method> toSet() {
		return collect(Collectors.toSet());
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super Method> accumulator, BiConsumer<R, R> combiner) {
		return stream.collect(supplier, accumulator, combiner);
	}

	@Override
	public <R, A> R collect(Collector<? super Method, A, R> collector) {
		return stream.collect(collector);
	}

	@Override
	public void forEach(Consumer<? super Method> action) {
		stream.forEach(action);
	}

	@Override
	public void forEachOrdered(Consumer<? super Method> action) {
		stream.forEachOrdered(action);
	}

	@Override
	public long count() {
		return stream.count();
	}

	@Override
	public Optional<Method> min(Comparator<? super Method> comparator) {
		return stream.min(comparator);
	}

	@Override
	public Optional<Method> max(Comparator<? super Method> comparator) {
		return stream.max(comparator);
	}

	@Override
	public boolean anyMatch(Predicate<? super Method> predicate) {
		return stream.anyMatch(predicate);
	}

	@Override
	public boolean allMatch(Predicate<? super Method> predicate) {
		return stream.allMatch(predicate);
	}

	@Override
	public boolean noneMatch(Predicate<? super Method> predicate) {
		return stream.noneMatch(predicate);
	}

	@Override
	public Optional<Method> findFirst() {
		return stream.findFirst();
	}

	@Override
	public Optional<Method> findAny() {
		return stream.findAny();
	}

	@Override
	public Iterator<Method> iterator() {
		return stream.iterator();
	}

	@Override
	public Spliterator<Method> spliterator() {
		return stream.spliterator();
	}

	@Override
	public boolean isParallel() {
		return stream.isParallel();
	}

	@Override
	public MethodStream sequential() {
		stream = stream.sequential();
		return this;
	}

	@Override
	public MethodStream parallel() {
		stream = stream.parallel();
		return this;
	}

	@Override
	public MethodStream unordered() {
		stream = stream.unordered();
		return this;
	}

	@Override
	public MethodStream onClose(Runnable closeHandler) {
		stream = stream.onClose(closeHandler);
		return this;
	}

	@Override
	public void close() {
		stream.close();
	}
}
