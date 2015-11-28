package me.jezza.jc.asm;

import com.google.common.collect.ArrayListMultimap;
import me.jezza.jc.lib.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Jezza
 */
public class AnnotatedFieldStream<A extends Annotation> extends FieldStream implements AnnotatedMemberStream<Field, A> {
	private final Class<A> annotation;

	protected AnnotatedFieldStream(Stream<Field> stream, Class<A> annotation) {
		super(stream.filter(field -> field.getDeclaredAnnotationsByType(annotation).length != 0));
		this.annotation = annotation;
	}

	@Override
	public ArrayListMultimap<Field, A> toMap() {
		return collect(Utils.toArrayListMultimap(Function.identity(), field -> field.getDeclaredAnnotationsByType(annotation)));
	}

	@Override
	public List<A> annotationList() {
		return map(field -> field.getDeclaredAnnotationsByType(annotation)).flatMap(Stream::of).collect(Collectors.toList());
	}

	@Override
	public Set<A> annotationSet() {
		return map(constructor -> constructor.getDeclaredAnnotationsByType(annotation)).flatMap(Stream::of).collect(Collectors.toSet());
	}
}