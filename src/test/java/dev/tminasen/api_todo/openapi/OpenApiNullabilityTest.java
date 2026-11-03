package dev.tminasen.api_todo.openapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("nodb")
class OpenApiNullabilityTest {

	@Autowired
	MockMvcTester mvc;

	DocumentContext doc;

	@BeforeEach
	void fetchApiDocs() throws Exception {
		var result = mvc.get().uri("/v3/api-docs").exchange();
		assertThat(result).hasStatusOk();
		doc = JsonPath.parse(result.getResponse().getContentAsString());
	}

	// OpenAPI 3.1 の type は単一なら文字列、複数なら配列で出力される
	private List<String> types(String path) {
		Object type = doc.read(path);
		if (type instanceof List<?> list) {
			return list.stream().map(String::valueOf).toList();
		}
		return List.of(String.valueOf(type));
	}

	@Test
	void should_beOpenApi31() {
		assertThat(doc.read("$.openapi", String.class)).startsWith("3.1");
	}

	@Nested
	class TodoResponseSchema {

		@Test
		void should_requireAllProperties_when_propertiesIncludeNullable() {
			List<String> required = doc.read("$.components.schemas.TodoResponse.required");

			assertThat(required).containsExactlyInAnyOrder("id", "title", "note", "dueDate", "tag");
		}

		@Test
		void should_allowNullType_when_propertyIsNullable() {
			List<String> noteTypes = types("$.components.schemas.TodoResponse.properties.note.type");
			List<String> dueDateTypes = types("$.components.schemas.TodoResponse.properties.dueDate.type");

			assertThat(noteTypes).containsExactlyInAnyOrder("string", "null");
			assertThat(dueDateTypes).containsExactlyInAnyOrder("string", "null");
		}

		@Test
		void should_notAllowNullType_when_propertyIsNonNull() {
			List<String> titleTypes = types("$.components.schemas.TodoResponse.properties.title.type");

			assertThat(titleTypes).doesNotContain("null");
		}

		@Test
		void should_wrapRefWithOneOf_when_refPropertyIsNullable() {
			Map<String, Object> tag = doc.read("$.components.schemas.TodoResponse.properties.tag");
			List<Map<String, Object>> oneOf = doc.read("$.components.schemas.TodoResponse.properties.tag.oneOf");

			assertThat(tag).doesNotContainKey("$ref");
			assertThat(oneOf).hasSize(2);
			assertThat(oneOf).anySatisfy(s -> assertThat(s).containsEntry("$ref", "#/components/schemas/TagResponse"));
			assertThat(oneOf).anySatisfy(s -> assertThat(s).containsEntry("type", "null"));
		}

	}

	@Nested
	class TagResponseSchema {

		@Test
		void should_notContainNull_when_referencedFromNullableProperty() {
			String tagSchemaJson = JsonPath.parse((Object) doc.read("$.components.schemas.TagResponse")).jsonString();

			assertThat(tagSchemaJson).doesNotContain("null");
		}

	}

	@Nested
	class CreateTodoRequestSchema {

		@Test
		void should_requireOnlyNonNullProperties() {
			List<String> required = doc.read("$.components.schemas.CreateTodoRequest.required");

			assertThat(required).containsExactly("title");
		}

		@Test
		void should_allowNullType_when_propertyIsNullable() {
			List<String> noteTypes = types("$.components.schemas.CreateTodoRequest.properties.note.type");

			assertThat(noteTypes).containsExactlyInAnyOrder("string", "null");
		}

	}

}
