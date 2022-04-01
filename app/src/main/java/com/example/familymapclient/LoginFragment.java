package com.example.familymapclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.concurrent.Executors;

import Requests.LoginRequest;
import Requests.RegisterRequest;

public class LoginFragment extends Fragment {
    public interface Listener { void userAuthenticated(); }
    private Listener listener;
    public void registerListener(Listener listener) {this.listener = listener;}

    private EditText serverHost;
    private EditText serverPort;
    private EditText username;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private RadioGroup genderChoice;
    private Button loginButton;
    private Button registerButton;

    public LoginFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // INITIALIZE VARIABLES //
        serverHost = view.findViewById(R.id.loginServerHostField);
        serverPort = view.findViewById(R.id.loginServerPortField);
        username = view.findViewById(R.id.loginUsernameField);
        password = view.findViewById(R.id.loginPasswordField);
        firstName = view.findViewById(R.id.loginFirstNameField);
        lastName = view.findViewById(R.id.loginLastNameField);
        email = view.findViewById(R.id.loginEmailField);
        genderChoice = view.findViewById(R.id.loginGenderButtons);
        loginButton = view.findViewById(R.id.loginButton);
        registerButton = view.findViewById(R.id.registerButton);

        // DISABLE BUTTONS UNTIL PROPER FIELDS ARE FILLED //
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);

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

        assert listener != null;
        // PREPARE HANDLER FOR UPCOMING THREADS //
        Handler formSubmissionHandler = new formSubmissionHandler();

        loginButton.setOnClickListener(View -> {
            LoginRequest req = new LoginRequest(username.getText().toString(), password.getText().toString());
            Task task = new Task(formSubmissionHandler, serverHost.getText().toString(), serverPort.getText().toString(), req);
            Executors.newSingleThreadExecutor().submit(task);
        });
        registerButton.setOnClickListener(View -> {
            String gender = genderChoice.getCheckedRadioButtonId() == R.id.loginGenderMale ? "m" :"f";
            RegisterRequest req = new RegisterRequest(username.getText().toString(), password.getText().toString(), email.getText().toString(), firstName.getText().toString(), lastName.getText().toString(), gender);
            Task task = new Task(formSubmissionHandler, serverHost.getText().toString(), serverPort.getText().toString(), req);
            Executors.newSingleThreadExecutor().submit(task);
        });

        return view;
    }

    private class formSubmissionHandler extends Handler {
        public formSubmissionHandler() { super(Looper.getMainLooper()); }
        @Override
        public void handleMessage(Message message) {
            boolean success = message.getData().getBoolean("success", false);
            if (success)
                listener.userAuthenticated();
            else
                Toast.makeText(LoginFragment.this.getActivity(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
        }
    }

    // The following functions and classes disable the login and register buttons until the proper fields have been filled out
    private void enforceValidLogin() {
        loginButton.setEnabled( !serverHost.getText().toString().isEmpty() &&
                                !serverPort.getText().toString().isEmpty() &&
                                !username.getText().toString().isEmpty() &&
                                !password.getText().toString().isEmpty() );
        }
        private class enforceValidLogin implements TextWatcher{
        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        public void onTextChanged(CharSequence s, int start, int before, int count){}
        public void afterTextChanged(Editable s)
            { enforceValidLogin(); }
    }
    private void enforceValidRegister(){
        registerButton.setEnabled( loginButton.isEnabled() && // Accounts for the same fields as the login button
                                    !firstName.getText().toString().isEmpty() &&
                                    !lastName.getText().toString().isEmpty() &&
                                    !email.getText().toString().isEmpty() &&
                                    genderChoice.getCheckedRadioButtonId() != -1);
    }
    private class enforceValidRegister implements TextWatcher{
        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        public void onTextChanged(CharSequence s, int start, int before, int count){}
        public void afterTextChanged(Editable s)
            { enforceValidRegister(); }
    }


    public void reAuthenticate(LoginInfo logInf) {
        Task task = new Task(new formSubmissionHandler(), logInf.getServerHost(), logInf.getServerPort(), new LoginRequest(logInf.getUsername(), logInf.getPassword()));
        Executors.newSingleThreadExecutor().submit(task);
    }

}