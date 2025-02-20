package br.com.francinildo.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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

        // Validação de data
        var currentDate = LocalDateTime.now();

        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt()) ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início/termino deve ser maior que a data atual");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de termino deve ser maior que a data de início");
        }

        TaskModel taskCreated = this.taskRepository.save(taskModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(taskCreated);
    }

    @GetMapping("/")
    public ResponseEntity list(HttpServletRequest servletRequest) {
        var idUser = servletRequest.getAttribute("idUser");
        var tasks = taskRepository.findByIdUser((UUID)idUser);
        return ResponseEntity.ok().body(tasks);
    }
}
