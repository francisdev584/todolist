package br.com.francinildo.todolist.task;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final ITaskRepository taskRepository;

    public TaskController(ITaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest servletRequest) {
        var idUser = servletRequest.getAttribute("idUser");
        taskModel.setId((UUID)idUser);
        TaskModel taskCreated = this.taskRepository.save(taskModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(taskCreated);
    }
}
