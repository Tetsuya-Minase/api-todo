package dev.tminasen.api_todo.todo;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

public record CreateTodoRequest(
		@NotBlank @Size(max = 100) String title,
		@Nullable String note,
		@Nullable LocalDate dueDate) {
}
