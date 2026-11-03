package dev.tminasen.api_todo.openapi;

import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.JsonSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.jspecify.annotations.Nullable;
import org.springdoc.core.customizers.OpenApiCustomizer;

/**
 * record コンポーネントの JSpecify {@link Nullable} を OpenAPI 3.1 のスキーマに反映する。
 * <ul>
 * <li>{@code @Nullable} なし: {@code required} に追加する</li>
 * <li>{@code @Nullable} あり: {@code null} を許容する。{@code *Response} では
 * Jackson がキーを常に出力するため {@code required} にも追加する</li>
 * </ul>
 */
class RecordNullabilityCustomizer implements OpenApiCustomizer {

	private static final String RESPONSE_SUFFIX = "Response";

	private final Map<String, Class<?>> recordsBySimpleName;

	RecordNullabilityCustomizer(Map<String, Class<?>> recordsBySimpleName) {
		this.recordsBySimpleName = Map.copyOf(recordsBySimpleName);
	}

	@Override
	public void customise(OpenAPI openApi) {
		var components = openApi.getComponents();
		if (components == null || components.getSchemas() == null) {
			return;
		}
		components.getSchemas().forEach((name, schema) -> {
			var record = recordsBySimpleName.get(name);
			if (record != null) {
				customiseSchema(schema, record, name.endsWith(RESPONSE_SUFFIX));
			}
		});
	}

	private void customiseSchema(Schema<?> schema, Class<?> record, boolean isResponse) {
		Map<String, Schema> properties = schema.getProperties();
		if (properties == null) {
			return;
		}
		for (RecordComponent component : record.getRecordComponents()) {
			var name = component.getName();
			var property = properties.get(name);
			if (property == null) {
				continue;
			}
			boolean nullable = component.getAnnotatedType().isAnnotationPresent(Nullable.class);
			if (nullable) {
				properties.put(name, allowNull(property));
			}
			if (!nullable || isResponse) {
				addRequired(schema, name);
			}
		}
	}

	private static Schema<?> allowNull(Schema<?> property) {
		Set<String> types = property.getTypes();
		if (types == null && property.getType() != null) {
			types = Set.of(property.getType());
		}
		if (types == null || types.isEmpty()) {
			// $ref など型を持たないプロパティは参照先を書き換えずに oneOf で包む
			return new JsonSchema().oneOf(List.of(property, new JsonSchema().types(Set.of("null"))));
		}
		var withNull = new LinkedHashSet<>(types);
		withNull.add("null");
		property.setTypes(withNull);
		return property;
	}

	private static void addRequired(Schema<?> schema, String name) {
		List<String> required = schema.getRequired();
		if (required == null) {
			schema.setRequired(new ArrayList<>(List.of(name)));
		}
		else if (!required.contains(name)) {
			required.add(name);
		}
	}

}
