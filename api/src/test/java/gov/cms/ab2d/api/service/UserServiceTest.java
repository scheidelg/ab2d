package gov.cms.ab2d.api.service;

import gov.cms.ab2d.api.SpringBootApp;
import gov.cms.ab2d.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootApp.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testUser() {
        User user = userService.getCurrentUser();

        assertEquals(null, user); // no authentication for now, so will be null
    }
}