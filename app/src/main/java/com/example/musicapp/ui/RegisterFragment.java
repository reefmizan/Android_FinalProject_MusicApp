package com.example.musicapp.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.musicapp.R;
import com.example.musicapp.databinding.FragmentRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.musicapp.models.User;

import java.util.Calendar;


public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupGenderSpinner();
        setupDatePicker();

        binding.btnRegister.setOnClickListener(v -> registerUser(view));

        binding.tvLoginPrompt.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.loginFragment)
        );
    }

    /**
     * Initializes the dropdown menu for gender selection.
     */
    private void setupGenderSpinner() {
        String[] genders = {"Select Gender", "Male", "Female", "Other", "Prefer not to say"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGender.setAdapter(adapter);
    }

    /**
     * Sets up the DatePickerDialog to show when the birthdate field is clicked.
     */
    private void setupDatePicker() {
        binding.etBirthdate.setOnClickListener(v -> {
            // Get current date to open the calendar on today's date
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        binding.etBirthdate.setText(formattedDate);
                    },
                    year, month, day);

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }

    /**
     * Validates input fields and handles user registration via Firebase.
     */
    private void registerUser(View view) {
        String fullName = binding.etFullName.getText().toString().trim();
        String gender = binding.spinnerGender.getSelectedItem().toString();
        String birthdate = binding.etBirthdate.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        // --- Input Validation ---

        if (fullName.length() < 2) {
            binding.etFullName.setError("Name must be at least 2 characters");
            binding.etFullName.requestFocus();
            return;
        }

        if (!fullName.matches("^[a-zA-Z\\s]+$")) {
            binding.etFullName.setError("Name must contain only letters and spaces");
            binding.etFullName.requestFocus();
            return;
        }

        if (gender.equals("Select Gender")) {
            Toast.makeText(requireContext(), "Please select a valid gender", Toast.LENGTH_SHORT).show();
            return;
        }

        if (birthdate.isEmpty()) {
            binding.etBirthdate.setError("Birthdate is required");
            Toast.makeText(requireContext(), "Please select your birthdate", Toast.LENGTH_SHORT).show();
            return;
        }

        // Using Android's built-in email pattern matching
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Please enter a valid email address");
            binding.etEmail.requestFocus();
            return;
        }

        if (password.length() < 6) {
            binding.etPassword.setError("Password must be at least 6 characters");
            binding.etPassword.requestFocus();
            return;
        }

        if (!password.matches(".*[A-Z].*")) {
            binding.etPassword.setError("Password must contain at least one uppercase letter");
            binding.etPassword.requestFocus();
            return;
        }

        if (!password.matches(".*[a-z].*")) {
            binding.etPassword.setError("Password must contain at least one lowercase letter");
            binding.etPassword.requestFocus();
            return;
        }

        if (!password.matches(".*\\d.*")) {
            binding.etPassword.setError("Password must contain at least one number");
            binding.etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("Passwords do not match");
            binding.etConfirmPassword.requestFocus();
            return;
        }

        // --- Execute Firebase Registration ---
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    User newUserProfile = new User(fullName, gender, birthdate, email);

                    db.collection("users").document(uid).set(newUserProfile)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(view).navigate(R.id.searchFragment);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Database Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Registration Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}