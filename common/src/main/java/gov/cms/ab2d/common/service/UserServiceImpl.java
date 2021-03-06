package gov.cms.ab2d.common.service;

import gov.cms.ab2d.common.config.Mapping;
import gov.cms.ab2d.common.dto.UserDTO;
import gov.cms.ab2d.common.model.Role;
import gov.cms.ab2d.common.model.User;
import gov.cms.ab2d.common.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@Slf4j
/**
 * Just gets the current user from the authentication context.
 */
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Mapping mapping;

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? userRepository
                .findByUsername(
                        auth.getPrincipal() instanceof String ? (String) auth.getPrincipal() :
                                ((org.springframework.security.core.userdetails.User) auth
                                        .getPrincipal())
                                        .getUsername()) : null;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            String userNotPresentMsg = "User is not present in our database";
            log.error(userNotPresentMsg);
            throw new ResourceNotFoundException(userNotPresentMsg);
        } else {
            return user;
        }
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = mapping.getModelMapper().map(userDTO, User.class);
        User createdUser = userRepository.saveAndFlush(user);
        return mapping.getModelMapper().map(createdUser, UserDTO.class);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        // When updating there needs to be verification that the user exists
        User user = getUserByUsername(userDTO.getUsername());
        userDTO.setId(user.getId());
        User mappedUser = mapping.getModelMapper().map(userDTO, User.class);
        User updatedUser = userRepository.saveAndFlush(mappedUser);
        return mapping.getModelMapper().map(updatedUser, UserDTO.class);
    }

    @Override
    public void setupUserImpersonation(String username, HttpServletRequest request) {
        User user = getUserByUsername(username);

        log.info("Admin user is impersonating user {}", username);

        setupUserAndRolesInSecurityContext(user, request);
    }

    @Override
    public void setupUserAndRolesInSecurityContext(User user, HttpServletRequest request) {
        List<GrantedAuthority> authorities = getGrantedAuth(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null, authorities);
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        log.info("Successfully logged in");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * Retrieve the list of granted authorities from the user's roles
     *
     * @param user - the user
     * @return - the granted authorities
     */
    @Override
    public List<GrantedAuthority> getGrantedAuth(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : user.getRoles()) {
            log.info("Adding role {}", role.getName());
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    @Override
    public UserDTO enableUser(String username) {
        User user = getUserByUsername(username);
        user.setEnabled(true);

        User updatedUser = userRepository.saveAndFlush(user);
        return mapping.getModelMapper().map(updatedUser, UserDTO.class);
    }

    @Override
    public UserDTO disableUser(String username) {
        User user = getUserByUsername(username);
        user.setEnabled(false);

        User updatedUser = userRepository.saveAndFlush(user);
        return mapping.getModelMapper().map(updatedUser, UserDTO.class);
    }
}
