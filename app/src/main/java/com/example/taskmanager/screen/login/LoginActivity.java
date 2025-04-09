package com.example.taskmanager.screen.login;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.taskmanager.MainActivity;
import com.example.taskmanager.PublicConstants;
import com.example.taskmanager.databinding.ActivityLoginBinding;
import com.example.taskmanager.model.User;
import com.example.taskmanager.screen.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private DatabaseHelper db ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = new DatabaseHelper(getApplicationContext());

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.edEmailRegister.getText().toString().trim();
                String fullName = binding.edFullNameRegister.getText().toString().trim();
                String pass = binding.edPasswordRegister.getText().toString().trim();
                String reEnterPass = binding.edReEnterPasswordRegister.getText().toString().trim();

                if (email.isBlank() || fullName.isBlank() || pass.isBlank() || reEnterPass.isBlank()) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!pass.equals(reEnterPass)) {
                    Toast.makeText(getApplicationContext(), "Mật khẩu và nhập lại mật khẩu không khớp, vui lòng thử lại", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!email.contains("@gmail.com")) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập email dạng xxxx@gmail.com", Toast.LENGTH_LONG).show();
                    return;
                }

                User newUser = new User(email,pass,fullName);
                db.addUser(newUser);
                Toast.makeText(getApplicationContext(), "Đăng kí thành công", Toast.LENGTH_LONG).show();
            }
        });

        binding.btnShowSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvTitle.setText("Sign In");
                binding.lnSignUp.setVisibility(GONE);
                binding.lnSignIn.setVisibility(VISIBLE);
            }
        });

        binding.btnShowSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvTitle.setText("Sign Up");
                binding.lnSignUp.setVisibility(VISIBLE);
                binding.lnSignIn.setVisibility(GONE);
            }
        });

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.edEmailSignIn.getText().toString().trim();
                String pass = binding.edPasswordSignIn.getText().toString().trim();

                if (email.isBlank() || pass.isBlank()) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!email.contains("@gmail.com")) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập email dạng xxxx@gmail.com", Toast.LENGTH_LONG).show();
                    return;
                }
                if(db.getUserByEmail(email) == null) {
                    Toast.makeText(getApplicationContext(), "Không tìm thấy người dùng, vui lòng xem lại thông tin", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    PublicConstants.user = db.getUserByEmail(email);
                    Toast.makeText(getApplicationContext(), "Đăng nhập thành công", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        });
    }
}