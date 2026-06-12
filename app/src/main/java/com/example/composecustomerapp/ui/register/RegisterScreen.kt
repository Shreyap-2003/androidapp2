package com.example.composecustomerapp.ui.register

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composecustomerapp.ui.components.BlingLabel
import com.example.composecustomerapp.ui.components.BlingTextField
import com.example.composecustomerapp.ui.components.BlingYellow
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory),
    onNavigateBack: () -> Unit = {},
    onLogoClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(uiState.isRegistered) {
        if (uiState.isRegistered) {
            Toast.makeText(context, "Registered successfully!", Toast.LENGTH_LONG).show()
            delay(2000)
            viewModel.resetRegistrationState()
            onNavigateBack()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF2F2F2) // Light gray background for the whole screen
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    // Logo and App Name
                    Row(
                        modifier = Modifier.clickable(onClick = onLogoClick),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(BlingYellow, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "B",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                        Text(
                            text = "Bling",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Create Account Title
                    Text(
                        text = "Create account",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Join and start shopping in minutes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // First Name and Last Name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            BlingLabel("FIRST NAME")
                            BlingTextField(
                                value = uiState.firstName,
                                onValueChange = viewModel::onFirstNameChanged,
                                placeholder = "First",
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            BlingLabel("LAST NAME")
                            BlingTextField(
                                value = uiState.lastName,
                                onValueChange = viewModel::onLastNameChanged,
                                placeholder = "Last",
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Phone Number
                    BlingLabel("PHONE NUMBER")
                    BlingTextField(
                        value = uiState.phoneNumber,
                        onValueChange = viewModel::onPhoneNumberChanged,
                        placeholder = "10-digit number",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Password
                    BlingLabel("PASSWORD")
                    BlingTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChanged,
                        placeholder = "Min 5 characters",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = viewModel::togglePasswordVisibility) {
                                Icon(
                                    imageVector = if (uiState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color.LightGray
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Address
                    BlingLabel("ADDRESS")
                    BlingTextField(
                        value = uiState.address,
                        onValueChange = viewModel::onAddressChanged,
                        placeholder = "Full address",
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // I AM A
                    BlingLabel("I AM A")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        UserTypeButton(
                            text = "Customer",
                            isSelected = uiState.userType == UserType.CUSTOMER,
                            onClick = { viewModel.onUserTypeChanged(UserType.CUSTOMER) },
                            modifier = Modifier.weight(1f)
                        )
                        UserTypeButton(
                            text = "Partner",
                            isSelected = uiState.userType == UserType.PARTNER,
                            onClick = { viewModel.onUserTypeChanged(UserType.PARTNER) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (uiState.error != null) {
                        Text(
                            text = uiState.error!!,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Create Account Button
                    Button(
                        onClick = { 
                            focusManager.clearFocus()
                            viewModel.createAccount {} 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlingYellow,
                            contentColor = Color.Black
                        ),
                        enabled = uiState.canRegister && !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                        } else {
                            Text(
                                text = "Create account",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Divider and "Have an account?"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color.LightGray.copy(alpha = 0.3f)
                        )
                        Text(
                            text = "Have an account?",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color.LightGray.copy(alpha = 0.3f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sign In Back Button
                    TextButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sign in",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // Copyright Text
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "© 2024 Bling Quick Commerce. All rights reserved.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
fun UserTypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .background(
                color = if (isSelected) BlingYellow.copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) BlingYellow else Color.LightGray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.Black else Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}
