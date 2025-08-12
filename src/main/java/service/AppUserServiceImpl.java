package service;

import domain.AppUser;
import domain.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import repository.AppUserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements UserDetailsService {

	 @Autowired
	    private AppUserRepository appUserRepository;

	    @Override
	    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	        AppUser user = appUserRepository.findByUsername(username);
	        if (user == null) {
	            throw new UsernameNotFoundException(username);
	        }
	        return new User(user.getUsername(), user.getPassword(), convertAuthorities(user.getRoles()));
	    }

	    private Collection<? extends GrantedAuthority> convertAuthorities(Set<Role> set) {
	        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + set.toString()));
	    }
}
