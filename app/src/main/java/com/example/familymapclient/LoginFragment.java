package com.example.familymapclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

import java.util.concurrent.Executors;

import Requests.LoginRequest;
import Requests.RegisterRequest;
import Results.LoginResult;
import Results.RegisterResult;

public class LoginFragment extends Fragment {
    public interface Listener { void userAuthenticated(); }
    private Listener listener;
    public void registerListener(Listener listener) {this.listener = listener;}

    private Button loginButton;
    private Button registerButton;
    private EditText serverHost;
    private EditText serverPort;
    private EditText username;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private RadioGroup genderChoice;

    public LoginFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        serverHost = (EditText) view.findViewById(R.id.loginServerHostField);
        serverPort = (EditText) view.findViewById(R.id.loginServerPortField);
        username = (EditText) view.findViewById(R.id.loginUsernameField);
        password = (EditText) view.findViewById(R.id.loginPasswordField);
        firstName = (EditText) view.findViewById(R.id.loginFirstNameField);
        lastName = (EditText) view.findViewById(R.id.loginLastNameField);
        email = (EditText) view.findViewById(R.id.loginEmailField);
        genderChoice = (RadioGroup) view.findViewById(R.id.loginGenderButtons);
        loginButton = view.findViewById(R.id.loginButton);
        registerButton = view.findViewById(R.id.registerButton);

        serverHost.addTextChangedListener(new enforceValidLogin());
        serverPort.addTextChangedListener(new enforceValidLogin());
        username.addTextChangedListener(new enforceValidLogin());
        password.addTextChangedListener(new enforceValidLogin());

        serverHost.addTextChangedListener(new enforceValidRegister());
        serverPort.addTextChangedListener(new enforceValidRegister());
        username.addTextChangedListener(new enforceValidRegister());
        firstName.addTextChangedListener(new enforceValidRegister());
        lastName.addTextChangedListener(new enforceValidRegister());
        email.addTextChangedListener(new enforceValidRegister());
        genderChoice.setOnCheckedChangeListener((radioGroup, i) -> enforceValidRegister());

        loginButton.setEnabled(false);
        registerButton.setEnabled(false);

        loginButton.setOnClickListener(View -> Toast.makeText(getActivity(), R.string.loginButton,Toast.LENGTH_SHORT).show());
        registerButton.setOnClickListener(View -> {
            Toast.makeText(getActivity(), R.string.registerButton, Toast.LENGTH_SHORT).show();

            assert listener != null;

            String gender = genderChoice.getCheckedRadioButtonId() == R.id.loginGenderMale ? "m" :"f";

            RegisterRequest req = new RegisterRequest(username.getText().toString(), password.getText().toString(),
                                                        email.getText().toString(), firstName.getText().toString(),
                                                        lastName.getText().toString(), gender);

            Handler uiThreadHandler = new Handler() {
                @Override
                public void handleMessage(Message message) {
                    boolean success = message.getData().getBoolean("Success", false);
                    if (success)
                        listener.userAuthenticated();
                    else
                        Toast.makeText(LoginFragment.this.getActivity(), "Invalid Register", Toast.LENGTH_SHORT).show();
                }
            };

            RegisterTask task = new RegisterTask(uiThreadHandler, req, serverHost.getText().toString(), serverPort.getText().toString());
            Executors.newSingleThreadExecutor().submit(task);
            System.out.println(req);

        });

        return view;
    }

    // The following functions and classes disable the login and register buttons until the proper fields have been filled out

    private void enforceValidLogin() {
        loginButton.setEnabled(!serverHost.getText().toString().isEmpty() &&
                !serverPort.getText().toString().isEmpty() &&
                !username.getText().toString().isEmpty());
    }
    private class enforceValidLogin implements TextWatcher{
        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        public void onTextChanged(CharSequence s, int start, int before, int count){}
        public void afterTextChanged(Editable s) { enforceValidLogin(); }
    }
    private void enforceValidRegister(){
        registerButton.setEnabled(!serverHost.getText().toString().isEmpty() &&
                !serverPort.getText().toString().isEmpty() &&
                !username.getText().toString().isEmpty() &&
                !password.getText().toString().isEmpty() &&
                !firstName.getText().toString().isEmpty() &&
                !lastName.getText().toString().isEmpty() &&
                !email.getText().toString().isEmpty() &&
                genderChoice.getCheckedRadioButtonId() != -1);
    }
    private class enforceValidRegister implements TextWatcher{
        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        public void onTextChanged(CharSequence s, int start, int before, int count){}
        public void afterTextChanged(Editable s) { enforceValidRegister(); }
    }

    // Implements a base for which the following classes run the corresponding function on a separate thread
    private static class Task implements Runnable {
        protected final Handler messageHandler;
        protected final ServerProxy proxy;

        protected Task(Handler messageHandler, String serverHost, String serverPort) {
            this.messageHandler = messageHandler;
            this.proxy = new ServerProxy(serverHost, serverPort);
        }
        public void run() {} //Implemented when inherited
        protected void sendMessage(boolean success) {
            // GET MESSAGE //
            Message message = Message.obtain();
            // SET MESSAGE DATA //
            Bundle messageBundle = new Bundle();
            messageBundle.putBoolean("Success", success);
            message.setData(messageBundle);
            // SEND MESSAGE TO UI THREAD //
            messageHandler.sendMessage(message);
        }
    }

    private static class RegisterTask extends Task{
        private final RegisterRequest req;
        public RegisterTask(Handler messageHandler, RegisterRequest req, String serverHost, String serverPort){
            super(messageHandler, serverHost, serverPort);
            this.req = req;
        }
        @Override
        public void run() {
            RegisterResult result = proxy.register(req);
            sendMessage(result.isSuccess());
        }
    }

    private static class LoginTask extends Task{
        private final LoginRequest req;
        public LoginTask(Handler messageHandler, LoginRequest req, String serverHost, String serverPort){
            super(messageHandler, serverHost, serverPort);
            this.req = req;
        }
        @Override
        public void run() {
            LoginResult result = proxy.login(req);
            sendMessage(result.isSuccess());
        }
    }
}
