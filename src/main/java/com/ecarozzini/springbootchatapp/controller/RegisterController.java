package com.ecarozzini.springbootchatapp.controller;

import com.ecarozzini.springbootchatapp.DatabaseManager;
import com.ecarozzini.springbootchatapp.models.ModelUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.ecarozzini.springbootchatapp.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class RegisterController {
    DatabaseManager dbMan = new DatabaseManager();
    List<String> filteredWords = dbMan.loadFilteredWordList();
    String errorMessage;

    public RegisterController() {
        System.out.println("Debug message: Register Controller constructor called");
    }


    @GetMapping("/chatapppages/registerPage")
    public String registerPage() {
        return "registerPage";
    }

    @PostMapping(value = "/register")
    public ModelAndView register(@ModelAttribute ModelUser mU, @NotNull Model model, ModelMap modelMap) {
        System.out.println("called register");
        System.out.println(mU.toString());
        model.addAttribute(mU.getDisplayName());
        model.addAttribute(mU.getLoginName());
        model.addAttribute(mU.getPassword());
        modelMap.addAttribute("Attribute", "redirectWithRedirectPrefix");
        if (registerNewUser(mU.getDisplayName(), mU.getLoginName(), mU.getPassword())) {
            System.out.println(errorMessage);
            return new ModelAndView("redirect:/chatapppages/loginPage.html", modelMap);
        } else {
            System.out.println(errorMessage);
            return new ModelAndView("redirect:/chatapppages/registerPage.html", modelMap);
        }
    }

    @GetMapping("/ping")
    public String getMethod() {
        System.out.println("Debug Message: called getMethod");
        return "loginPage";
    }

    boolean registerNewUser(String userName, String loginName, String password) {
        String usernameRegex = "";
        for (String w : filteredWords) {
            if (usernameRegex == "") usernameRegex = "[" + w + "]";
            usernameRegex = usernameRegex + "|[" + w + "]";
        }
        Pattern p = Pattern.compile(usernameRegex);
        System.out.println("username regex: "+usernameRegex);
        Matcher m = p.matcher(userName);
        //if(m.find() && !filteredWords.contains("")) {
        //    errorMessage = "Found filtered word in username";
        //    return false;
        //}

        if (!dbMan.checkIfUnique(loginName, "Logins", "loginName")) {
            errorMessage = "loginName not unique";
            return false;
        }

        String passwordRegex =
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])" +
                        "(?=.*[@#$%^&+=.\\-_*!])([a-zA-Z0-9@#$%^&+=*.\\-_])" +
                        "(?=.*[^\\w\\d\\s:])([^\\s]){3,}$";
        //matches a password with a minimum of 6 characters, at least 1 each of a lowercase-, uppercase letter,
        // number and special character
        p = Pattern.compile(passwordRegex);
        m = p.matcher(password);
        if (!m.find()) {
            errorMessage = "Password doesn't meet criteria";
            return false;
        }
        if (!dbMan.createNewUserInDB(userName)) {
            if (!dbMan.createNewLoginInDB(loginName, password, dbMan.getLatestGeneratedUserID())) {

                errorMessage = "Registered new User and Login";
                return true;
            } else {
                errorMessage = "Failed to Register new Login";
                return false;
            }

        } else {
            errorMessage = "Failed to Register User";
            return false;
        }
    }
}
