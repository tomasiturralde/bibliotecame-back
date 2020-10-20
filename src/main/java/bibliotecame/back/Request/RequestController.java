package bibliotecame.back.Request;

import bibliotecame.back.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/request")
public class RequestController {
    private final UserService userService;

    @Autowired
    public RequestController(UserService userService) {
        this.userService = userService;
    }
}
