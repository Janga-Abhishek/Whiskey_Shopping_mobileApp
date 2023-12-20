package com.example.abhishek_janga_project2;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.example.abhishek_janga_project2.viewmodels.ShopViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;



public class CheckoutFragment extends Fragment {
    private NavController navController;


    private FirebaseFirestore firestoreDB;
    private CollectionReference cartCollection;

    public CheckoutFragment() {
        // Required empty public constructor
    }
    private Cart cart;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);
        firestoreDB = FirebaseFirestore.getInstance();
        String currentUserKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartCollection = firestoreDB.collection("user_cart").document(currentUserKey).collection("cart");


        EditText firstNameEditText = view.findViewById(R.id.First_Name);
        EditText lastNameEditText = view.findViewById(R.id.Last_Name);
        EditText phoneNumberEditText = view.findViewById(R.id.PhoneNumber);
        EditText emailEditText = view.findViewById(R.id.Email);
        EditText addressLine1EditText = view.findViewById(R.id.Addressline1);
        EditText addressLine2EditText = view.findViewById(R.id.Addressline2);
        EditText postalCodeEditText = view.findViewById(R.id.postal_code);
        EditText cardNumberEditText = view.findViewById(R.id.cardnumber);
        EditText cardHolderNameEditText = view.findViewById(R.id.cardholdername);
        EditText expiryDateEditText = view.findViewById(R.id.expiryDate);
        EditText cvvEditText = view.findViewById(R.id.CVV);

        Button checkoutButton = view.findViewById(R.id.checkoutButton);
        cardNumberEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(16)});

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValidName(firstNameEditText.getText().toString())) {
                    firstNameEditText.setError("Enter a valid first name (only alphabets)");
                    showToast("Enter a valid first name (only alphabets)");
                    return;
                }

                if (!isValidName(lastNameEditText.getText().toString())) {
                    lastNameEditText.setError("Enter a valid last name (only alphabets)");
                    showToast("Enter a valid last name (only alphabets)");
                    return;
                }

                if (!isValidPhoneNumber(phoneNumberEditText.getText().toString())) {
                    phoneNumberEditText.setError("Enter a valid 10-digit phone number");
                    showToast("Enter a valid 10-digit phone number");
                    return;
                }

                if (!isValidEmail(emailEditText.getText().toString())) {
                    emailEditText.setError("Enter a valid email address");
                    showToast("Enter a valid email address");
                    return;
                }

                String addressLine1 = addressLine1EditText.getText().toString();
                String addressLine2 = addressLine2EditText.getText().toString();

                if (addressLine1.isEmpty()) {
                    addressLine1EditText.setError("Address cannot be empty");
                    showToast("Address cannot be empty");
                    return;
                } else if (!isValidAddress(addressLine1)) {
                    addressLine1EditText.setError("Address should not contain special symbols except comma");
                    showToast("Address should not contain special symbols except comma");
                    return;
                }

                if (addressLine2.isEmpty()) {
                    addressLine2EditText.setError("Address cannot be empty");
                    showToast("Address cannot be empty");
                    return;
                } else if (!isValidAddress(addressLine2)) {
                    addressLine2EditText.setError("Address should not contain special symbols except comma");
                    showToast("Address should not contain special symbols except comma");
                    return;
                }

                if (!isValidPostalCode(postalCodeEditText.getText().toString())) {
                    postalCodeEditText.setError("Enter a valid postal code");
                    showToast("Enter a valid postal code");
                    return;
                }

                if (!isValidCardNumber(cardNumberEditText.getText().toString())) {
                    cardNumberEditText.setError("Enter a valid 16-digit card number");
                    showToast("Enter a valid 16-digit card number");
                    return;
                }

                if (!isValidCardHolderName(cardHolderNameEditText.getText().toString())) {
                    cardHolderNameEditText.setError("Card holder name should not contain digits or symbols");
                    showToast("Card holder name should not contain digits or symbols");
                    return;
                }

                if (!isValidExpiryDate(expiryDateEditText.getText().toString())) {
                    expiryDateEditText.setError("Enter expiry date in MM/YY format");
                    showToast("Enter expiry date in MM/YY format");
                    return;
                }

                if (!isValidCVV(cvvEditText.getText().toString())) {
                    cvvEditText.setError("Enter a valid 3-digit CVV");
                    showToast("Enter a valid 3-digit CVV");
                    return;
                }

                clearCartCollection();
                refreshCart();

                navController = Navigation.findNavController(view);

                Toast.makeText(getActivity(), "Checkout successful!", Toast.LENGTH_SHORT).show();
                navController.navigate(R.id.action_checkout_to_orderFragment);

            }
        });

        return view;
    }
    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
    private boolean isValidName(String name) {
        return name.matches("[a-zA-Z]+"); // Only alphabets
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("[0-9]{10}"); // 10-digit number
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(); // Email format
    }

    private boolean isValidAddress(String address) {
        return !address.matches(".*[^a-zA-Z0-9, ].*"); // No special symbols except comma
    }
    private boolean isValidPostalCode(String postalCode) {
        return postalCode.matches("[a-zA-Z0-9]{6}"); // 6-character postal code with letters and numbers
    }


    private boolean isValidCardNumber(String cardNumber) {
        return cardNumber.matches("[0-9]{16}"); // 16-digit card number
    }

    private boolean isValidCardHolderName(String cardHolderName) {
        return cardHolderName.matches("[a-zA-Z ]+"); // No digits or symbols
    }

    private boolean isValidExpiryDate(String expiryDate) {
        return expiryDate.matches("^(0[1-9]|1[0-2])/[0-9]{2}$"); // MM/YY format
    }

    private boolean isValidCVV(String cvv) {
        return cvv.matches("[0-9]{3}"); // 3-digit CVV
    }
    private void clearCartCollection() {
        cartCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot snapshot : task.getResult()) {
                    snapshot.getReference().delete();
                }
            } else {
                // Handle potential errors
            }
        });
    }
    private void refreshCart() {
        // Assuming you have access to the ViewModel, trigger a refresh after checkout
        ShopViewModel viewModel = new ViewModelProvider(requireActivity()).get(ShopViewModel.class);
        viewModel.resetCartAfterCheckout();
    }


}



