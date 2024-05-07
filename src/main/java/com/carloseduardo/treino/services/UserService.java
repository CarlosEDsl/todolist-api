package com.carloseduardo.treino.services;

import com.carloseduardo.treino.models.User;
import com.carloseduardo.treino.models.enums.ProfileEnum;
import com.carloseduardo.treino.repositories.UserRepository;
import com.carloseduardo.treino.services.exceptions.DataBindingViolationException;
import com.carloseduardo.treino.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    public User findById(Long id){
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(()-> new ObjectNotFoundException(
                "Usuário não encontrado no ID: " + id + "Tipo: " + User.class.getName()
        ));
    }

    @Transactional
    public User create(User obj) {
        obj.setId(null); // Garantir que não será enviado um usuário com ID
        obj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet()));
        obj = this.userRepository.save(obj);
        return obj;
    }

    @Transactional
    public User update(User obj) {
        User newObj = findById(obj.getId());
        newObj.setPassword(obj.getPassword());
        newObj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        return this.userRepository.save(newObj);
    }

    public void delete(Long id) {
        findById(id);
        try{
            this.userRepository.deleteById(id);

        }catch (Exception e) {
            throw new DataBindingViolationException("O usuário ainda possuí relacionamentos, logo não pode ser deletado");
        }
    }

}
