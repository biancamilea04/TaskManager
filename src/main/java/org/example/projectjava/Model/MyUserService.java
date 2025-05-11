package org.example.projectjava.Model;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class MyUserService implements UserDetailsService {

    @Autowired
    private MyUserRepository myUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        Optional<MyUser> user = myUserRepository.findByUsername(username);
        System.out.println(username);
        if(user.isPresent()){
            var myUser = user.get();
            return User.builder()
                    .username(myUser.getUsername())
                    .password(myUser.getPassword())
                    .build();
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }

    public Optional<MyUser> authenticate(String username, String password) {
          Optional<MyUser> user = myUserRepository.findByUsername(username);
          if(user.isPresent()){
              if(user.get().getPassword().equals(password)){
                  return user;
              }
          }
          return Optional.empty();
    }
}
