package me.jezza.jc.annotation;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import me.jezza.jc.util.SuffixMap;

/**
 * @author Jezza
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Processor extends AbstractProcessor {
	private static final String TARGET_ANNOTATION_KEY = "me.jezza.jc.annotation.base";

	private final SuffixMap<String, DataEntry> map;

	public Processor() {
		map = new SuffixMap<>();
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		final String property = System.getProperty(TARGET_ANNOTATION_KEY);
		if (property == null) {
			System.out.println("No target annotation provided. (" + TARGET_ANNOTATION_KEY + ")");
			return Collections.emptySet();
		}
		return Collections.singleton(property);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		System.out.println(roundEnv.processingOver());
		if (annotations.isEmpty()) {
			System.out.println("Write stuff out...");
			System.out.println(map);
			try {
				FileObject resource = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", "data.map");
				Writer writer = resource.openWriter();
				writer.write("This is a test!");
				writer.flush();
				writer.close();

			} catch (IOException e) {
				processingEnv.getMessager().printMessage(Kind.ERROR, "Failed to generate output data.");
			}
			return false;
		}
		System.out.println("Starting: " + annotations);
		for (TypeElement annotation : annotations) {
			Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
			if (elements.isEmpty()) {
				return false;
			}
			for (Element element : elements) {
				process(element, roundEnv);
			}
		}
		System.out.println("Done...");
		return true;
	}

	private void process(Element element, RoundEnvironment roundEnv) {
		System.out.println("Processing " + element.getKind() + ": " + element.getSimpleName());
		final String name;
		if (element.getKind() == ElementKind.CLASS) {
			name = element.toString();
		} else if (element.getKind() == ElementKind.METHOD) {
			name = element.getEnclosingElement().toString() + '.' + element.getSimpleName();
		} else {
			processingEnv.getMessager().printMessage(Kind.ERROR, "Unhandled element type: " + element);
			return;
		}

		List<? extends AnnotationMirror> mirrors = element.getAnnotationMirrors();
		if (mirrors.size() != 1) {
			processingEnv.getMessager().printMessage(Kind.ERROR, "Unhandled annotation mirror: " + mirrors);
			return;
		}
		AnnotationMirror mirror = mirrors.get(0);
		Map<? extends ExecutableElement, ? extends AnnotationValue> values = mirror.getElementValues();
		if (values.size() != 1) {
			processingEnv.getMessager().printMessage(Kind.ERROR, "Unhandled annotation values: " + values);
			return;
		}
		Entry<? extends ExecutableElement, ? extends AnnotationValue> next = values.entrySet().iterator().next();
		String value = (String) next.getValue().getValue();
		map.put(name.split("\\."), new DataEntry(element.getKind() == ElementKind.METHOD, value));
	}

	private static final class DataEntry {
		public final boolean method;
		public final String value;

		public DataEntry(boolean method, String value) {
			this.method = method;
			this.value = value;
		}

		@Override
		public String toString() {
			return method
					? value + "()"
					: value;
		}
	}
}
