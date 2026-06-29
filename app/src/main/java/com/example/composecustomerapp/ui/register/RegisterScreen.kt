package com.example.composecustomerapp.ui.register

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composecustomerapp.ui.components.BlingLabel
import com.example.composecustomerapp.ui.components.BlingTextField
import com.example.composecustomerapp.ui.components.BlingYellow
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay

@SuppressLint("MissingPermission")
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory),
    onNavigateBack: () -> Unit = {},
    onLogoClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            // Permission granted, will fetch location when needed
        } else {
            Toast.makeText(
                context,
                "Location permission is required to capture address coordinates",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun requestLocation() {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocation || hasCoarseLocation) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        viewModel.updateLocation(location.latitude, location.longitude)
                        // Optional: show a small toast or update UI to show coordinates captured
                    }
                }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

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
                        .imePadding()
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
                            keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email
                    BlingLabel("EMAIL ADDRESS")
                    BlingTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChanged,
                        placeholder = "example@domain.com",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
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
                            keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
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
                        })

                    Spacer(modifier = Modifier.height(24.dp))

                    // Address
                    BlingLabel("ADDRESS")
                    BlingTextField(
                        value = uiState.address, onValueChange = {
                        viewModel.onAddressChanged(it)
//                        requestLocation()
                    }, placeholder = "Full address", keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ), keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }), trailingIcon = {
                        IconButton(onClick = { requestLocation() }) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Capture Location",
                                tint = if (uiState.latitude != null) BlingYellow else Color.LightGray
                            )
                        }
                    })

                    if (uiState.latitude != null && uiState.longitude != null) {
                        Text(
                            text = "Location captured: ${
                                String.format(
                                    "%.4f", uiState.latitude
                                )
                            }, ${String.format("%.4f", uiState.longitude)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF059669), // Green
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

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
                            containerColor = BlingYellow, contentColor = Color.Black
                        ),
                        enabled = uiState.canRegister && !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp), color = Color.Black
                            )
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
    text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
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
            .clickable(onClick = onClick), contentAlignment = Alignment.Center
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
