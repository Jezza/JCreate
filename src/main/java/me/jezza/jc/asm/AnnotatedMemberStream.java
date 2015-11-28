package me.jezza.jc.asm;

import com.google.common.collect.ArrayListMultimap;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * @author Jezza
 */
public interface AnnotatedMemberStream<T, A extends Annotation> extends MemberStream<T> {
	ArrayListMultimap<T, A> toMap();

	List<A> annotationList();

	Set<A> annotationSet();
}
