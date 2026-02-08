package com.islami.Aha.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.islami.Aha.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val uiState by viewModel.registerState.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            onNavigateToHome()
            viewModel.resetRegisterState()
        }
    }

    RegisterScreenContent(
        uiState = uiState,
        onNameChange = viewModel::onRegisterNameChange,
        onEmailChange = viewModel::onRegisterEmailChange,
        onPasswordChange = viewModel::onRegisterPasswordChange,
        onConfirmPasswordChange = viewModel::onRegisterConfirmPasswordChange,
        onTogglePasswordVisibility = viewModel::toggleRegisterPasswordVisibility,
        onToggleConfirmPasswordVisibility = viewModel::toggleRegisterConfirmPasswordVisibility,
        onRegisterClick = {
            focusManager.clearFocus()
            viewModel.register()
        },
        onLoginClick = onNavigateToLogin
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenContent(
    uiState: RegisterUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onLoginClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Gray900
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ================================================================
            // HEADER
            // ================================================================
            Text(
                text = "Buat Akun Baru",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Gray900
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Daftar untuk menyimpan dan sinkronkan data ibadah Anda",
                fontSize = 14.sp,
                color = Gray500,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ================================================================
            // FORM CARD
            // ================================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Name
                    AuthTextField(
                        value = uiState.name,
                        onValueChange = onNameChange,
                        label = "Nama Lengkap",
                        placeholder = "Masukkan nama lengkap Anda",
                        leadingIcon = Icons.Default.Person,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        isError = uiState.nameError != null,
                        errorMessage = uiState.nameError
                    )

                    // Email
                    AuthTextField(
                        value = uiState.email,
                        onValueChange = onEmailChange,
                        label = "Email",
                        placeholder = "Masukkan email Anda",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        isError = uiState.emailError != null,
                        errorMessage = uiState.emailError
                    )

                    // Password
                    AuthTextField(
                        value = uiState.password,
                        onValueChange = onPasswordChange,
                        label = "Password",
                        placeholder = "Minimal 6 karakter",
                        leadingIcon = Icons.Default.Lock,
                        trailingIcon = if (uiState.isPasswordVisible)
                            Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        onTrailingIconClick = onTogglePasswordVisibility,
                        visualTransformation = if (uiState.isPasswordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        isError = uiState.passwordError != null,
                        errorMessage = uiState.passwordError
                    )

                    // Confirm Password
                    AuthTextField(
                        value = uiState.confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        label = "Konfirmasi Password",
                        placeholder = "Ulangi password Anda",
                        leadingIcon = Icons.Default.Lock,
                        trailingIcon = if (uiState.isConfirmPasswordVisible)
                            Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        onTrailingIconClick = onToggleConfirmPasswordVisibility,
                        visualTransformation = if (uiState.isConfirmPasswordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                onRegisterClick()
                            }
                        ),
                        isError = uiState.confirmPasswordError != null,
                        errorMessage = uiState.confirmPasswordError
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ================================================================
            // REGISTER BUTTON with gradient
            // ================================================================
            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                enabled = !uiState.isLoading
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(EmeraldDark, Emerald)
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = SurfaceWhite,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Daftar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ================================================================
            // TERMS
            // ================================================================
            Text(
                text = "Dengan mendaftar, Anda menyetujui",
                fontSize = 12.sp,
                color = Gray500,
                textAlign = TextAlign.Center
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { },
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "Syarat & Ketentuan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Emerald
                    )
                }
                Text(text = " dan ", fontSize = 12.sp, color = Gray500)
                TextButton(
                    onClick = { },
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "Kebijakan Privasi",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Emerald
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ================================================================
            // FOOTER
            // ================================================================
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Sudah punya akun? ", fontSize = 14.sp, color = Gray500)
                TextButton(
                    onClick = onLoginClick,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Masuk",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ============================================================================
// PREVIEW SECTION
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    HabitIslamiTheme {
        RegisterScreenContent(
            uiState = RegisterUiState(),
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisibility = {},
            onToggleConfirmPasswordVisibility = {},
            onRegisterClick = {},
            onLoginClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Register with Errors")
@Composable
fun RegisterScreenErrorPreview() {
    HabitIslamiTheme {
        RegisterScreenContent(
            uiState = RegisterUiState(
                name = "Ab",
                email = "invalid",
                password = "123",
                confirmPassword = "456",
                nameError = "Nama minimal 3 karakter",
                emailError = "Format email tidak valid",
                passwordError = "Password minimal 6 karakter",
                confirmPasswordError = "Password tidak cocok"
            ),
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisibility = {},
            onToggleConfirmPasswordVisibility = {},
            onRegisterClick = {},
            onLoginClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Register Loading")
@Composable
fun RegisterScreenLoadingPreview() {
    HabitIslamiTheme {
        RegisterScreenContent(
            uiState = RegisterUiState(
                name = "John Doe",
                email = "john@example.com",
                password = "password123",
                confirmPassword = "password123",
                isLoading = true
            ),
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisibility = {},
            onToggleConfirmPasswordVisibility = {},
            onRegisterClick = {},
            onLoginClick = {}
        )
    }
}
