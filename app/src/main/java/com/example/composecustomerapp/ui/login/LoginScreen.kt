package com.example.composecustomerapp.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory),
    onNavigateToRegister: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onLogoClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF63656A) // Dark gray outer background
    ) {
        Column(
            modifier = Modifier
                .padding(top = 80.dp)
                .fillMaxSize()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp)
                )
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

            // Welcome Back
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

            Spacer(modifier = Modifier.height(32.dp))

            // Password
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
                            viewModel.signIn(onLoginSuccess)
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

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Sign In Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.signIn(onLoginSuccess)
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

            Spacer(modifier = Modifier.weight(1f))

            // Divider and "New here?"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.LightGray.copy(alpha = 0.3f)
                )
                Text(
                    text = "New here?",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color.LightGray.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Create an Account
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
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
