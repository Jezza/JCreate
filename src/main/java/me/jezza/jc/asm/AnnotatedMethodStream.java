package me.jezza.jc.asm;

import com.google.common.collect.ArrayListMultimap;
import me.jezza.jc.lib.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Jezza
 */
public class AnnotatedMethodStream<A extends Annotation> extends MethodStream implements AnnotatedMemberStream<Method, A> {
	private final Class<A> annotation;

	protected AnnotatedMethodStream(Stream<Method> stream, Class<A> annotation) {
		super(stream.filter(method -> method.getDeclaredAnnotationsByType(annotation).length != 0));
		this.annotation = annotation;
	}

	@Override
	public ArrayListMultimap<Method, A> toMap() {
		return collect(Utils.toArrayListMultimap(Function.identity(), method -> method.getDeclaredAnnotationsByType(annotation)));
	}

	@Override
	public List<A> annotationList() {
		return map(method -> method.getDeclaredAnnotationsByType(annotation)).flatMap(Stream::of).collect(Collectors.toList());
	}

	@Override
	public Set<A> annotationSet() {
		return map(method -> method.getDeclaredAnnotationsByType(annotation)).flatMap(Stream::of).collect(Collectors.toSet());
	}
}
