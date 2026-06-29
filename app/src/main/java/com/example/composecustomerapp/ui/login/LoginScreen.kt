package com.example.composecustomerapp.ui.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.google.firebase.messaging.FirebaseMessaging

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory),
    onNavigateToRegister: () -> Unit = {},
    onLoginSuccess: (String) -> Unit = {},
    onLogoClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showForgotPasswordDialog = false
                viewModel.resetForgotPasswordState()
            },
            title = {
                Text(
                    text = when {
                        uiState.isOtpVerified -> "Reset Password"
                        uiState.isOtpSent -> "Verify OTP"
                        else -> "Forgot Password"
                    },
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            text = {
                Column {
                    when {
                        !uiState.isOtpSent -> {
                            Text("Enter your email address to receive an OTP.", color = Color.Black)
                            Spacer(modifier = Modifier.height(16.dp))
                            BlingTextField(
                                value = uiState.forgotPasswordEmail,
                                onValueChange = viewModel::onForgotPasswordEmailChanged,
                                placeholder = "Email address",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                            )
                            if (uiState.forgotPasswordError != null) {
                                Text(
                                    text = uiState.forgotPasswordError!!,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        !uiState.isOtpVerified -> {
                            Text("Enter the OTP sent to ${uiState.forgotPasswordEmail}", color = Color.Black)
                            Spacer(modifier = Modifier.height(16.dp))
                            BlingTextField(
                                value = uiState.otp,
                                onValueChange = viewModel::onOtpChanged,
                                placeholder = "Enter 6-digit OTP",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                            )
                            if (uiState.verifyOtpError != null) {
                                Text(
                                    text = uiState.verifyOtpError!!,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        else -> {
                            Text("Enter your new password below.", color = Color.Black)
                            Spacer(modifier = Modifier.height(16.dp))
                            BlingTextField(
                                value = uiState.newPassword,
                                onValueChange = viewModel::onNewPasswordChanged,
                                placeholder = "New password (min 5 chars)",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                visualTransformation = PasswordVisualTransformation()
                            )
                            if (uiState.resetPasswordError != null) {
                                Text(
                                    text = uiState.resetPasswordError!!,
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when {
                            !uiState.isOtpSent -> {
                                viewModel.forgotPassword { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                }
                            }
                            !uiState.isOtpVerified -> {
                                viewModel.verifyOtp { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                }
                            }
                            else -> {
                                viewModel.resetPassword { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    showForgotPasswordDialog = false
                                    viewModel.resetForgotPasswordState()
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BlingYellow, contentColor = Color.Black),
                    enabled = when {
                        uiState.isOtpVerified -> !uiState.isResetPasswordLoading
                        uiState.isOtpSent -> !uiState.isVerifyOtpLoading
                        else -> !uiState.isForgotPasswordLoading
                    }
                ) {
                    if (uiState.isForgotPasswordLoading || uiState.isVerifyOtpLoading || uiState.isResetPasswordLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = when {
                                uiState.isOtpVerified -> "Reset Password"
                                uiState.isOtpSent -> "Verify OTP"
                                else -> "Send OTP"
                            }
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showForgotPasswordDialog = false
                    viewModel.resetForgotPasswordState()
                }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF63656A)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 80.dp)
                .fillMaxSize()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp)
                )
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 32.dp, vertical = 40.dp)
        ) {
            // Logo and App Name
            Row(
                modifier = Modifier.clickable(onClick = onLogoClick),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(BlingYellow, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "B",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Text(
                    text = "Bling",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 36.sp,
                    lineHeight = 44.sp
                ),
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sign in to continue shopping",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(48.dp))

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

            Spacer(modifier = Modifier.height(32.dp))

            BlingLabel("PASSWORD")
            BlingTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChanged,
                placeholder = "Your password",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (uiState.canSignIn) {
                            viewModel.signIn { userType, userId ->
                                try {
                                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            println("AuthDebug: FCM Token = ${task.result}, userId = $userId")
                                            viewModel.updateFcmToken(userId, task.result)
                                        }
                                    }
                                } catch (e: Exception) {
                                    println("AuthDebug: Firebase error: ${e.message}")
                                }
                                onLoginSuccess(userType)
                            }
                        }
                    }
                ),
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = viewModel::togglePasswordVisibility) {
                        Icon(
                            imageVector = if (uiState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (uiState.isPasswordVisible) "Toggle password visibility" else null,
                            tint = Color.LightGray
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            // Sign In Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.signIn { userType, userId ->
                        try {
                            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    println("AuthDebug: FCM Token = ${task.result}, userId = $userId")
                                    viewModel.updateFcmToken(userId, task.result)
                                }
                            }
                        } catch (e: Exception) {
                            println("AuthDebug: Firebase error: ${e.message}")
                        }
                        onLoginSuccess(userType)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlingYellow,
                    contentColor = Color.Black
                ),
                enabled = uiState.canSignIn && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                } else {
                    Text(
                        text = "Sign in",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            TextButton(
                onClick = { showForgotPasswordDialog = true },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Forgot password?",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.3f))
                Text(
                    text = "New here?",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.3f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Create an Account \u2192",
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = FontFamily.Serif,
                        color = BlingYellow,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}