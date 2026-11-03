package dev.tminasen.api_todo.todo;

import java.time.LocalDate;

import org.jspecify.annotations.Nullable;

public record TodoResponse(
		long id,
		String title,
		@Nullable String note,
		@Nullable LocalDate dueDate,
		@Nullable TagResponse tag) {
}
