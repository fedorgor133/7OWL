package edu.ub.pis2425.a7owls.data.service;

import java.util.regex.Pattern;

import edu.ub.pis2425.a7owls.domain.User;
import edu.ub.pis2425.a7owls.data.service.mockdata.MockClientsHashMap;

public class AuthService {
    private static MockClientsHashMap users;
    static { users = new MockClientsHashMap(); }

    public interface OnLogInListener {
        void onLogInSuccess(User user);
        void onLogInError(Throwable throwable);
    }

    public interface OnSignUpListener {
        void onSignUpSuccess();
        void onSignUpError(Throwable throwable);
    }

    public AuthService() {}

    public void logIn(
            String username,
            String enteredPassword,
            OnLogInListener listener
    ) {
        if (username.isEmpty())
            listener.onLogInError(new Throwable("Username cannot be empty"));
        else if (enteredPassword.isEmpty())
            listener.onLogInError(new Throwable("Password cannot be empty"));
        else if (!users.containsKey(username))
            listener.onLogInError(new Throwable("Username does not exist"));
        else {
            User user = users.get(username);
            if (!user.getPassword().equals(enteredPassword))
                listener.onLogInError(new Throwable("Incorrect password"));
            else {
                listener.onLogInSuccess(user);
            }
        }
    }

    public void signUp(
            String username,
            String password,
            String passwordConfirmation,
            OnSignUpListener listener
    ) {
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$";

        if (username.isEmpty())
            listener.onSignUpError(new Throwable("Username cannot be empty"));
        else if (users.containsKey(username))
            listener.onSignUpError(new Throwable("Username already exists"));
        else if (password.isEmpty())
            listener.onSignUpError(new Throwable("Password cannot be empty"));
        else if (!Pattern.matches(passwordPattern, password))
            listener.onSignUpError(new Throwable("Password must be 8-20 characters long and contain at least 1 letter and 1 number"));
        else if (passwordConfirmation.isEmpty())
            listener.onSignUpError(new Throwable("Password confirmation cannot be empty"));
        else if (!password.equals(passwordConfirmation))
            listener.onSignUpError(new Throwable("Passwords do not match"));
        else {
            users.put(username, new User(username, username, password));
            listener.onSignUpSuccess();
        }
    }
}
