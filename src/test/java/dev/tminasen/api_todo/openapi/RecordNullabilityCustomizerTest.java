package dev.tminasen.api_todo.openapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.JsonSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RecordNullabilityCustomizerTest {

	record SampleResponse(String id, @Nullable String note, @Nullable ChildResponse child) {
	}

	record ChildResponse(String name) {
	}

	record SampleRequest(String title, @Nullable String note) {
	}

	private static final String CHILD_REF = "#/components/schemas/ChildResponse";

	private static Schema<?> stringSchema() {
		return new JsonSchema().types(Set.of("string"));
	}

	private static Schema<?> objectSchema(Map<String, Schema> properties) {
		return new JsonSchema().types(Set.of("object")).properties(new LinkedHashMap<>(properties));
	}

	private static OpenAPI openApi(Map<String, Schema> schemas) {
		return new OpenAPI().components(new Components().schemas(new LinkedHashMap<>(schemas)));
	}

	private static RecordNullabilityCustomizer customizer(Class<?>... records) {
		Map<String, Class<?>> map = new LinkedHashMap<>();
		for (Class<?> record : records) {
			map.put(record.getSimpleName(), record);
		}
		return new RecordNullabilityCustomizer(map);
	}

	@Nested
	class Response {

		private OpenAPI customize() {
			var openApi = openApi(Map.of(
					"SampleResponse", objectSchema(Map.of(
							"id", stringSchema(),
							"note", stringSchema(),
							"child", new JsonSchema().$ref(CHILD_REF))),
					"ChildResponse", objectSchema(Map.of("name", stringSchema()))));
			customizer(SampleResponse.class, ChildResponse.class).customise(openApi);
			return openApi;
		}

		private Schema<?> schema(OpenAPI openApi, String name) {
			return openApi.getComponents().getSchemas().get(name);
		}

		@Test
		void should_requireAllComponents_when_someAreNullable() {
			var required = schema(customize(), "SampleResponse").getRequired();

			assertThat(required).containsExactlyInAnyOrder("id", "note", "child");
		}

		@Test
		void should_addNullType_when_componentIsNullable() {
			var note = (Schema<?>) schema(customize(), "SampleResponse").getProperties().get("note");

			assertThat(note.getTypes()).containsExactlyInAnyOrder("string", "null");
		}

		@Test
		void should_keepTypes_when_componentIsNonNull() {
			var id = (Schema<?>) schema(customize(), "SampleResponse").getProperties().get("id");

			assertThat(id.getTypes()).containsExactly("string");
		}

		@Test
		void should_wrapRefWithOneOf_when_refComponentIsNullable() {
			var child = (Schema<?>) schema(customize(), "SampleResponse").getProperties().get("child");

			assertThat(child.get$ref()).isNull();
			assertThat(child.getOneOf()).hasSize(2);
			assertThat(child.getOneOf()).anySatisfy(s -> assertThat(s.get$ref()).isEqualTo(CHILD_REF));
			assertThat(child.getOneOf()).anySatisfy(s -> assertThat(s.getTypes()).containsExactly("null"));
		}

		@Test
		void should_notModifyReferencedSchema_when_refComponentIsNullable() {
			var childSchema = schema(customize(), "ChildResponse");
			var name = (Schema<?>) childSchema.getProperties().get("name");

			assertThat(childSchema.getTypes()).containsExactly("object");
			assertThat(name.getTypes()).containsExactly("string");
		}

	}

	@Nested
	class Request {

		private Schema<?> customize() {
			var schema = objectSchema(Map.of("title", stringSchema(), "note", stringSchema()));
			customizer(SampleRequest.class).customise(openApi(Map.of("SampleRequest", schema)));
			return schema;
		}

		@Test
		void should_requireOnlyNonNullComponents() {
			assertThat(customize().getRequired()).containsExactly("title");
		}

		@Test
		void should_addNullType_when_componentIsNullable() {
			var note = (Schema<?>) customize().getProperties().get("note");

			assertThat(note.getTypes()).containsExactlyInAnyOrder("string", "null");
		}

	}

	@Nested
	class EdgeCases {

		@Test
		void should_notDuplicateRequired_when_alreadyRequired() {
			var schema = objectSchema(Map.of("title", stringSchema(), "note", stringSchema()));
			schema.setRequired(new ArrayList<>(List.of("title")));

			customizer(SampleRequest.class).customise(openApi(Map.of("SampleRequest", schema)));

			assertThat(schema.getRequired()).containsExactly("title");
		}

		@Test
		void should_notModifySchema_when_noRecordMatches() {
			var note = stringSchema();
			var schema = objectSchema(Map.of("note", note));

			customizer(SampleRequest.class).customise(openApi(Map.of("Unknown", schema)));

			assertThat(schema.getRequired()).isNull();
			assertThat(note.getTypes()).containsExactly("string");
		}

		@Test
		void should_notRequire_when_componentIsMissingFromProperties() {
			var schema = objectSchema(Map.of("note", stringSchema()));

			customizer(SampleRequest.class).customise(openApi(Map.of("SampleRequest", schema)));

			assertThat(schema.getRequired()).isNull();
		}

		@Test
		void should_doNothing_when_componentsAreAbsent() {
			var openApi = new OpenAPI();

			customizer(SampleRequest.class).customise(openApi);

			assertThat(openApi.getComponents()).isNull();
		}

	}

}
