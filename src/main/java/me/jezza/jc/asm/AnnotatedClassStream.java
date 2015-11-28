package me.jezza.jc.asm;

import com.google.common.collect.ArrayListMultimap;
import me.jezza.jc.lib.Utils;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Jezza
 */
public class AnnotatedClassStream<A extends Annotation> extends ClassStream implements AnnotatedMemberStream<Class<?>, A> {
	private final Class<A> annotation;

	protected AnnotatedClassStream(Stream<Class<?>> stream, Class<A> annotation) {
		super(stream.filter(type -> type.getDeclaredAnnotationsByType(annotation).length != 0));
		this.annotation = annotation;
	}

	@Override
	public ArrayListMultimap<Class<?>, A> toMap() {
		return collect(Utils.toArrayListMultimap(Function.identity(), type -> type.getDeclaredAnnotationsByType(annotation)));
	}

	@Override
	public List<A> annotationList() {
		return map(type -> type.getDeclaredAnnotationsByType(annotation)).flatMap(Stream::of).collect(Collectors.toList());
	}

	@Override
	public Set<A> annotationSet() {
		return map(type -> type.getDeclaredAnnotationsByType(annotation)).flatMap(Stream::of).collect(Collectors.toSet());
	}
}