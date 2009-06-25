/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package is.hax.google.spring.web;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Vidar Svansson
 * @since: 05.26.2009
 */
public class UserInterceptor extends HandlerInterceptorAdapter {

	private UserService userService;

    private boolean requireLogin;


    @Autowired
    public UserInterceptor(UserService userService) {

        this.userService = userService;

    }

    public void setRequireLogin(boolean requireLogin) {
        this.requireLogin = requireLogin;
    }

    @Override
	public boolean preHandle(HttpServletRequest request,
                          HttpServletResponse response,
                          Object handler) throws Exception {

        boolean authenticated = userService.isUserLoggedIn();
        request.setAttribute("authenticated", authenticated);
        if (!authenticated) {
			String loginUrl = userService.createLoginURL(request.getRequestURI());
            request.setAttribute("loginUrl", loginUrl);
            if(requireLogin){
                response.sendRedirect(loginUrl);
                return false;
            }
		} else {
            User user = userService.getCurrentUser();
            request.setAttribute("admin", userService.isUserAdmin());
            request.setAttribute("user", user);
			request.setAttribute("logoutUrl", userService.createLogoutURL("/"));
		}

        return true;
    }

}
