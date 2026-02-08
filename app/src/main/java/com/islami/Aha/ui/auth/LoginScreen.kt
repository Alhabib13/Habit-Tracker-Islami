package com.islami.Aha.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToRegister: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onSkipLogin: () -> Unit = {}
) {
    val uiState by viewModel.loginState.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onNavigateToHome()
            viewModel.resetLoginState()
        }
    }

    LoginScreenContent(
        uiState = uiState,
        onEmailChange = viewModel::onLoginEmailChange,
        onPasswordChange = viewModel::onLoginPasswordChange,
        onTogglePasswordVisibility = viewModel::toggleLoginPasswordVisibility,
        onLoginClick = {
            focusManager.clearFocus()
            viewModel.login()
        },
        onSkipClick = onSkipLogin,
        onRegisterClick = onNavigateToRegister
    )
}

@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLoginClick: () -> Unit,
    onSkipClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // ================================================================
        // HEADER - Logo smaller at top
        // ================================================================
        Surface(
            modifier = Modifier.size(72.dp),
            shape = RoundedCornerShape(18.dp),
            color = Emerald,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Aha Logo",
                    tint = SurfaceWhite,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Selamat Datang!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Masuk untuk melanjutkan perjalanan ibadah Anda",
            fontSize = 14.sp,
            color = Gray500,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ================================================================
        // FORM CARD with subtle shadow
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
                // Email Field
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

                // Password Field
                AuthTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    label = "Password",
                    placeholder = "Masukkan password Anda",
                    leadingIcon = Icons.Default.Lock,
                    trailingIcon = if (uiState.isPasswordVisible)
                        Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    onTrailingIconClick = onTogglePasswordVisibility,
                    visualTransformation = if (uiState.isPasswordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            onLoginClick()
                        }
                    ),
                    isError = uiState.passwordError != null,
                    errorMessage = uiState.passwordError
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ================================================================
        // BUTTONS
        // ================================================================

        // Login button with gradient
        Button(
            onClick = onLoginClick,
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
                        text = "Masuk",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Gray200)
            Text(text = "  atau  ", fontSize = 14.sp, color = Gray400)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Gray200)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Skip button - outlined style
        OutlinedButton(
            onClick = onSkipClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Emerald),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(Emerald)
            )
        ) {
            Text(
                text = "Lewati & Gunakan Lokal",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // ================================================================
        // FOOTER
        // ================================================================
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Belum punya akun? ", fontSize = 14.sp, color = Gray500)
            TextButton(
                onClick = onRegisterClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Daftar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Gray700,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder, color = Gray400) },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (isError) ErrorRed else Emerald
                )
            },
            trailingIcon = if (trailingIcon != null) {
                {
                    IconButton(onClick = { onTrailingIconClick?.invoke() }) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = "Toggle visibility",
                            tint = Gray400
                        )
                    }
                }
            } else null,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            isError = isError,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Emerald,
                unfocusedBorderColor = Gray200,
                errorBorderColor = ErrorRed,
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = Gray50,
                errorContainerColor = ErrorRed.copy(alpha = 0.05f)
            )
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                color = ErrorRed,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

// ============================================================================
// PREVIEW SECTION
// ============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    HabitIslamiTheme {
        LoginScreenContent(
            uiState = LoginUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onLoginClick = {},
            onSkipClick = {},
            onRegisterClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Login with Error")
@Composable
fun LoginScreenErrorPreview() {
    HabitIslamiTheme {
        LoginScreenContent(
            uiState = LoginUiState(
                email = "invalid-email",
                password = "123",
                emailError = "Format email tidak valid",
                passwordError = "Password minimal 6 karakter"
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onLoginClick = {},
            onSkipClick = {},
            onRegisterClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Login Loading")
@Composable
fun LoginScreenLoadingPreview() {
    HabitIslamiTheme {
        LoginScreenContent(
            uiState = LoginUiState(
                email = "user@example.com",
                password = "password123",
                isLoading = true
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onLoginClick = {},
            onSkipClick = {},
            onRegisterClick = {}
        )
    }
}
