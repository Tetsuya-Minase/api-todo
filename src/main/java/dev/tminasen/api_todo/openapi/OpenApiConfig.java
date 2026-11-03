package dev.tminasen.api_todo.openapi;

import java.util.Map;
import java.util.stream.Collectors;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

@Configuration(proxyBeanMethods = false)
class OpenApiConfig {

	private static final String BASE_PACKAGE = "dev.tminasen.api_todo";

	@Bean
	OpenApiCustomizer recordNullabilityCustomizer() {
		return new RecordNullabilityCustomizer(scanRecords());
	}

	// 単純名が重複する record があると toMap が IllegalStateException で起動を失敗させる
	private static Map<String, Class<?>> scanRecords() {
		var scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter((reader, factory) -> Record.class.getName()
			.equals(reader.getClassMetadata().getSuperClassName()));
		return scanner.findCandidateComponents(BASE_PACKAGE)
			.stream()
			.map(definition -> ClassUtils.resolveClassName(String.valueOf(definition.getBeanClassName()),
					OpenApiConfig.class.getClassLoader()))
			.collect(Collectors.toMap(Class::getSimpleName, type -> type));
	}

}
