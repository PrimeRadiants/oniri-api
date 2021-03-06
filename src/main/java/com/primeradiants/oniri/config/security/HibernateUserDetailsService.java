package com.primeradiants.oniri.config.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.primeradiants.oniri.user.UserEntity;
import com.primeradiants.oniri.user.UserManager;

/**
 * Custom implementation of the UserDetailsService
 * Used to retreive user from database using hibernate entities
 * @author Shanira
 * @since 0.1.0
 */
@Service("hibernateUserDetailsService")
public class HibernateUserDetailsService implements UserDetailsService {

	@Autowired private UserManager userManager;
	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = userManager.getUser(username);
		
		if(user == null)
			throw new UsernameNotFoundException(username);
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("user"));
		
		if(user.getAdmin())
			authorities.add(new SimpleGrantedAuthority("admin"));
		
		UserDetails result = new User(user.getUsername(), user.getPassword(), user.getEnabled(), true, true, true, authorities);
		return result;
	}

	
}
