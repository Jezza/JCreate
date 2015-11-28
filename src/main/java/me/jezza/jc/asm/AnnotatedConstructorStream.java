package me.jezza.jc.asm;

import com.google.common.collect.ArrayListMultimap;
import me.jezza.jc.lib.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Jezza
 */
public class AnnotatedConstructorStream<A extends Annotation> extends ConstructorStream implements AnnotatedMemberStream<Constructor<?>, A> {
	private final Class<A> annotation;

	protected AnnotatedConstructorStream(Stream<Constructor<?>> stream, Class<A> annotation) {
		super(stream.filter(constructor -> constructor.getDeclaredAnnotationsByType(annotation).length != 0));
		this.annotation = annotation;
	}

	@Override
	public ArrayListMultimap<Constructor<?>, A> toMap() {
		return collect(Utils.toArrayListMultimap(Function.identity(), constructor -> constructor.getDeclaredAnnotationsByType(annotation)));
	}

	@Override
	public List<A> annotationList() {
		return map(constructor -> constructor.getDeclaredAnnotationsByType(annotation)).flatMap(Stream::of).collect(Collectors.toList());
	}

	@Override
	public Set<A> annotationSet() {
		return map(constructor -> constructor.getDeclaredAnnotationsByType(annotation)).flatMap(Stream::of).collect(Collectors.toSet());
	}
}