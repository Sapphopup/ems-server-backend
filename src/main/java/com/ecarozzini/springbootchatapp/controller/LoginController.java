package com.ecarozzini.springbootchatapp.controller;

import com.ecarozzini.springbootchatapp.DatabaseManager;
import com.ecarozzini.springbootchatapp.models.ModelLogin;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.sql.ResultSet;

@RestController
public class LoginController {
    String loginId;
    DatabaseManager dbMan = new DatabaseManager();

    @GetMapping("/chatapppages/loginPage")
    public String loginPage() {
        return "loginPage";
    }

    @PostMapping(value = "/login")
    public ModelAndView login(
            @ModelAttribute ModelLogin mL, @NotNull Model model, ModelMap mm, HttpServletResponse response
    ) {
        boolean success = true;
        model.addAttribute(mL.getLoginName());
        model.addAttribute(mL.getPassword());
        mm.addAttribute("Attribute", "redirectWithRedirectPrefix");
        if (!loginUser(mL.getLoginName(), mL.getPassword(), response)) {
            System.out.println("Login failed");
            return new ModelAndView("redirect:/chatapppages/loginPage.html");
        } else {
            return new ModelAndView("redirect:/redirect", mm);
        }
    }

    @SneakyThrows
    private boolean loginUser(String loginName, String passwd, HttpServletResponse response) {
        String loginQuery = "SELECT userID FROM Logins WHERE loginName = '" + loginName
                          + "' AND password = '" + passwd + "';";
        ResultSet rs = dbMan.connectToDbAndQueryForData(loginQuery);
        if (rs.next()) {
            loginId = rs.getString("userID");
            System.out.println("Redirecting to app with uid: " + loginId);
            jakarta.servlet.http.Cookie cId =
                    new Cookie("loggedInUserid", rs.getString("userID"));
            jakarta.servlet.http.Cookie cU =
                    new Cookie("loggedInUsername", dbMan.loadUsername(rs.getString("userID")));
            cId.setMaxAge(-1);
            cU.setMaxAge(-1);
            response.addCookie(cId);
            response.addCookie(cU);
            return true;
        } else {
            System.out.println("no valid login");
            return false;
        }
    }


}
