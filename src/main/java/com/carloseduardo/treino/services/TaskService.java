package com.carloseduardo.treino.services;

import com.carloseduardo.treino.models.Task;
import com.carloseduardo.treino.models.User;
import com.carloseduardo.treino.repositories.TaskRepository;

import com.carloseduardo.treino.services.exceptions.DataBindingViolationException;
import com.carloseduardo.treino.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserService userService;

    public Task findById(Long id){
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(()->new ObjectNotFoundException("Tarefas não encontradas"));
    }

    public List<Task> findAllById(Long UserId){
        List<Task> tasks = this.taskRepository.findByUser_Id(UserId);
        return tasks;
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

    public void delete(Long id){
        findById(id);
        try {
            this.taskRepository.deleteById(id);
        }catch (Exception e){
            throw new DataBindingViolationException("Futuro erro (delete do task)");
        }
    }
}
