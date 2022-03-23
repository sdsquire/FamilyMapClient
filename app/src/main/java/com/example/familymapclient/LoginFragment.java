package com.example.familymapclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;

import java.util.concurrent.Executors;
import Requests.RegisterRequest;
import Results.RegisterResult;

public class LoginFragment extends Fragment {
    public interface Listener { void userAuthenticated(); }
    private Listener listener;
    public void registerListener(Listener listener) {this.listener = listener;}

    private Button loginButton;
    private Button registerButton;

    public LoginFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = view.findViewById(R.id.loginButton);
        registerButton = view.findViewById(R.id.registerButton);

        loginButton.setOnClickListener(View -> Toast.makeText(getActivity(), R.string.loginButton,Toast.LENGTH_SHORT).show());
        registerButton.setOnClickListener(View -> {
            Toast.makeText(getActivity(), R.string.registerButton, Toast.LENGTH_SHORT).show();

            //Check if listener is not null

            String serverHost = ((EditText) view.findViewById(R.id.loginServerHostField)).getText().toString();
            String serverPort = ((EditText) view.findViewById(R.id.loginServerPortField)).getText().toString();
            String username = ((EditText) view.findViewById(R.id.loginUserNameField)).getText().toString();
            String password = ((EditText) view.findViewById(R.id.loginPasswordField)).getText().toString();
            String email = ((EditText) view.findViewById(R.id.loginEmailField)).getText().toString();
            String firstName = ((EditText) view.findViewById(R.id.loginFirstNameField)).getText().toString();
            String lastName = ((EditText) view.findViewById(R.id.loginLastNameField)).getText().toString();
            int checkedGender = ((RadioGroup) view.findViewById(R.id.loginGenderButtons)).getCheckedRadioButtonId();
            String gender = checkedGender == R.id.loginGenderMale ? "m" :
                    checkedGender == R.id.loginGenderFemale ? "f": "n/a";

            RegisterRequest req = new RegisterRequest(username, password, email, firstName, lastName, gender);

            Handler uiThreadHandler = new Handler() {
                @Override
                public void handleMessage(Message message) {
                    boolean success = message.getData().getBoolean("Success", false);
                    if (success)
                        listener.userAuthenticated();
                    else
                        Toast.makeText(LoginFragment.this.getActivity(), "Invalid Register", Toast.LENGTH_SHORT);
                }
            };

            RegisterTask task = new RegisterTask(uiThreadHandler, req, serverHost, serverPort);
            Executors.newSingleThreadExecutor().submit(task);
            System.out.println(req);

        });

        return view;


    }

    private static class RegisterTask implements Runnable {
        private final Handler messageHandler;
        private final RegisterRequest req;
        private final ServerProxy proxy;

        public RegisterTask(Handler messageHandler, RegisterRequest req, String serverHost, String serverPort){
            this.messageHandler = messageHandler;
            this.req = req;
            this.proxy = new ServerProxy(serverHost, serverPort);
        }

        @Override
        public void run() {
            RegisterResult result = proxy.register(req);

            Message message = Message.obtain();
            Bundle messageBundle = new Bundle();
            messageBundle.putBoolean("Success", result.isSuccess());
            message.setData(messageBundle);
            messageHandler.handleMessage(message);

        }
    }
}