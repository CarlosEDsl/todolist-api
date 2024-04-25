package com.carloseduardo.treino.services;

import com.carloseduardo.treino.models.Task;
import com.carloseduardo.treino.models.User;
import com.carloseduardo.treino.repositories.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserService userService;

    public Task findById(Long id){
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(()->new RuntimeException("Tarefas não encontradas"));
    }

    @Transactional
    public Task create(Task obj){
        User user = this.userService.findById(obj.getUser().getId());
        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;
    }

    public Task update(Task obj){
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }

    public void delete(Task obj){
        findById(obj.getId());
        try {
            this.taskRepository.deleteById(obj.getId());
        }catch (Exception e){
            throw new RuntimeException("Futuro erro (delete do task)");
        }
    }
}
