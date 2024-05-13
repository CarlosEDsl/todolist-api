package com.carloseduardo.treino.services;

import com.carloseduardo.treino.models.Task;
import com.carloseduardo.treino.models.User;
import com.carloseduardo.treino.models.enums.ProfileEnum;
import com.carloseduardo.treino.models.projection.TaskProjection;
import com.carloseduardo.treino.repositories.TaskRepository;

import com.carloseduardo.treino.security.UserSpringSecurity;
import com.carloseduardo.treino.services.exceptions.AuthorizationException;
import com.carloseduardo.treino.services.exceptions.DataBindingViolationException;
import com.carloseduardo.treino.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserService userService;

    public Task findById(Long id){
        Task task = this.taskRepository.findById(id).orElseThrow(()->new ObjectNotFoundException("Tarefa não encontrada"));

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if(Objects.isNull(userSpringSecurity) || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !userHasTask(userSpringSecurity, task))
            throw new AuthorizationException("Acesso negado!");

        return task;

    }

    public List<TaskProjection> findAllByUser(){

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso negado!");

        List<TaskProjection> tasks = this.taskRepository.findByUser_Id(userSpringSecurity.getId());
        return tasks;
    }

    public List<Task> findAll(){
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity) || !userSpringSecurity.hasRole(ProfileEnum.ADMIN))
            throw new AuthorizationException("Acesso negado!");


        List<Task> tasks = this.taskRepository.findAll();
        return tasks;
    }

    @Transactional
    public Task create(Task obj){
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso negado!");

        User user = this.userService.findById(userSpringSecurity.getId());
        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;
    }

    @Transactional
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

    private Boolean userHasTask(UserSpringSecurity userSpringSecurity, Task task) {
        return task.getUser().getId().equals(userSpringSecurity.getId());
    }

}
