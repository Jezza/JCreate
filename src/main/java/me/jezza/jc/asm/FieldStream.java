package me.jezza.jc.asm;

import me.jezza.jc.lib.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Jezza
 */
public class FieldStream implements MemberStream<Field> {
	protected Stream<Field> stream;

	protected FieldStream(Stream<Field> stream) {
		this.stream = stream;
	}

	@Override
	public Stream<Field> backingStream() {
		return stream;
	}

	@Override
	public FieldStream from(Class<?> declaringClass) {
		stream = stream.filter(field -> field.getDeclaringClass() == declaringClass);
		return this;
	}

	@Override
	public FieldStream in(String packageName) {
		return in(packageName, true);
	}

	@Override
	public FieldStream in(String packageName, boolean direct) {
		stream.filter(field -> {
			String s = Utils.packageName(field.getDeclaringClass());
			return direct ? packageName.equals(s) : packageName.startsWith(s);
		});
		return this;
	}

	@Override
	public <A extends Annotation> AnnotatedFieldStream<A> with(Class<A> annotation) {
		return new AnnotatedFieldStream<>(stream, annotation);
	}

	@Override
	public FieldStream filter(Predicate<? super Field> predicate) {
		stream = stream.filter(predicate);
		return this;
	}

	@Override
	public FieldStream sorted() {
		stream = stream.sorted();
		return this;
	}

	@Override
	public FieldStream sorted(Comparator<? super Field> comparator) {
		stream = stream.sorted(comparator);
		return this;
	}

	@Override
	public FieldStream peek(Consumer<? super Field> action) {
		stream = stream.peek(action);
		return this;
	}

	@Override
	public FieldStream limit(long maxSize) {
		stream = stream.limit(maxSize);
		return this;
	}

	@Override
	public FieldStream skip(long n) {
		stream = stream.skip(n);
		return this;
	}

	@Override
	public IntStream mapToInt(ToIntFunction<? super Field> mapper) {
		return stream.mapToInt(mapper);
	}

	@Override
	public <R> Stream<R> map(Function<? super Field, ? extends R> mapper) {
		return stream.map(mapper);
	}

	@Override
	public <R> Stream<R> flatMap(Function<? super Field, ? extends Stream<? extends R>> mapper) {
		return stream.flatMap(mapper);
	}

	@Override
	public Field reduce(Field identity, BinaryOperator<Field> accumulator) {
		return stream.reduce(identity, accumulator);
	}

	@Override
	public Optional<Field> reduce(BinaryOperator<Field> accumulator) {
		return stream.reduce(accumulator);
	}

	@Override
	public <U> U reduce(U identity, BiFunction<U, ? super Field, U> accumulator, BinaryOperator<U> combiner) {
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
	public List<Field> toList() {
		return collect(Collectors.toList());
	}

	@Override
	public Set<Field> toSet() {
		return collect(Collectors.toSet());
	}

	@Override
	public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super Field> accumulator, BiConsumer<R, R> combiner) {
		return stream.collect(supplier, accumulator, combiner);
	}

	@Override
	public <R, A> R collect(Collector<? super Field, A, R> collector) {
		return stream.collect(collector);
	}

	@Override
	public void forEach(Consumer<? super Field> action) {
		stream.forEach(action);
	}

	@Override
	public void forEachOrdered(Consumer<? super Field> action) {
		stream.forEachOrdered(action);
	}

	@Override
	public long count() {
		return stream.count();
	}

	@Override
	public Optional<Field> min(Comparator<? super Field> comparator) {
		return stream.min(comparator);
	}

	@Override
	public Optional<Field> max(Comparator<? super Field> comparator) {
		return stream.max(comparator);
	}

	@Override
	public boolean anyMatch(Predicate<? super Field> predicate) {
		return stream.anyMatch(predicate);
	}

	@Override
	public boolean allMatch(Predicate<? super Field> predicate) {
		return stream.allMatch(predicate);
	}

	@Override
	public boolean noneMatch(Predicate<? super Field> predicate) {
		return stream.noneMatch(predicate);
	}

	@Override
	public Optional<Field> findFirst() {
		return stream.findFirst();
	}

	@Override
	public Optional<Field> findAny() {
		return stream.findAny();
	}

	@Override
	public Iterator<Field> iterator() {
		return stream.iterator();
	}

	@Override
	public Spliterator<Field> spliterator() {
		return stream.spliterator();
	}

	@Override
	public boolean isParallel() {
		return stream.isParallel();
	}

	@Override
	public FieldStream sequential() {
		stream = stream.sequential();
		return this;
	}

	@Override
	public FieldStream parallel() {
		stream = stream.parallel();
		return this;
	}

	@Override
	public FieldStream unordered() {
		stream = stream.unordered();
		return this;
	}

	@Override
	public FieldStream onClose(Runnable closeHandler) {
		stream = stream.onClose(closeHandler);
		return this;
	}

	@Override
	public void close() {
		stream.close();
	}
}