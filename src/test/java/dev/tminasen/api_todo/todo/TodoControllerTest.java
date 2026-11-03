package dev.tminasen.api_todo.todo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("nodb")
class TodoControllerTest {

	@Autowired
	MockMvcTester mvc;

	@Nested
	class CreateTodo {

		@Test
		void should_return201_when_requestIsValid() {
			// Given
			var body = """
					{"title": "牛乳を買う", "note": null}
					""";

			// When
			var result = post(body);

			// Then
			assertThat(result).hasStatus(HttpStatus.CREATED);
			assertThat(result).bodyJson().extractingPath("$.title").isEqualTo("牛乳を買う");
		}

		@Test
		void should_return400_when_titleIsNull() {
			var result = post("""
					{"title": null}
					""");

			assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
			assertThat(result).hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
		}

		@Test
		void should_return400_when_titleIsMissing() {
			var result = post("{}");

			assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
		}

		@ParameterizedTest
		@ValueSource(strings = { "", "   " })
		void should_return400_when_titleIsBlank(String title) {
			var result = post("""
					{"title": "%s"}
					""".formatted(title));

			assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
			assertThat(result).hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
		}

		@Test
		void should_return201_when_titleIs100Characters() {
			var result = post("""
					{"title": "%s"}
					""".formatted("a".repeat(100)));

			assertThat(result).hasStatus(HttpStatus.CREATED);
		}

		@Test
		void should_return400_when_titleIs101Characters() {
			var result = post("""
					{"title": "%s"}
					""".formatted("a".repeat(101)));

			assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
		}

		private MockMvcTester.MockMvcRequestBuilder post(String body) {
			return mvc.post().uri("/todos").contentType(MediaType.APPLICATION_JSON).content(body);
		}

	}

	@Nested
	class GetTodo {

		@Test
		void should_returnTodoWithNullKeys_when_idIsGiven() {
			var result = mvc.get().uri("/todos/{id}", 1L);

			assertThat(result).hasStatusOk();
			assertThat(result).bodyJson().extractingPath("$.id").isEqualTo(1);
			assertThat(result).bodyJson().hasPath("$.note");
			assertThat(result).bodyJson().hasPath("$.tag");
		}

	}

}
