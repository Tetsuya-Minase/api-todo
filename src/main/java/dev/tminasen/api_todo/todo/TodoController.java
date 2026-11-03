package dev.tminasen.api_todo.todo;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// DB 連携は未実装。固定値を返すスタブ
@RestController
@RequestMapping("/todos")
class TodoController {

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	TodoResponse create(@Valid @RequestBody CreateTodoRequest request) {
		return new TodoResponse(1L, request.title(), request.note(), request.dueDate(), null);
	}

	@GetMapping("/{id}")
	TodoResponse get(@PathVariable long id) {
		return new TodoResponse(id, "スタブの ToDo", null, null, null);
	}

}
