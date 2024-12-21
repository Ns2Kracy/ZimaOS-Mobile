package com.zimaspace.zimaos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.platform.LocalContext
import com.zimaspace.zimaos.data.AndroidPreferencesRepository
import com.zimaspace.zimaos.domain.model.ConnectionConfig

enum class InputType {
    ZEROTIER_ID, IP_ADDRESS
}

@Composable
fun App() {
    var zerotierInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var selectedInputType by remember { mutableStateOf(InputType.ZEROTIER_ID) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferencesRepository = remember { AndroidPreferencesRepository(context) }
    
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Connect to Device",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RadioButton(
                    selected = selectedInputType == InputType.ZEROTIER_ID,
                    onClick = { selectedInputType = InputType.ZEROTIER_ID }
                )
                Text(
                    text = "Zerotier ID",
                    modifier = Modifier.clickable { selectedInputType = InputType.ZEROTIER_ID }
                )
                RadioButton(
                    selected = selectedInputType == InputType.IP_ADDRESS,
                    onClick = { selectedInputType = InputType.IP_ADDRESS }
                )
                Text(
                    text = "IP Address",
                    modifier = Modifier.clickable { selectedInputType = InputType.IP_ADDRESS }
                )
            }
            
            OutlinedTextField(
                value = zerotierInput,
                onValueChange = { zerotierInput = it },
                label = { Text(if (selectedInputType == InputType.ZEROTIER_ID) "Zerotier ID" else "IP Address") },
                placeholder = { 
                    Text(
                        if (selectedInputType == InputType.ZEROTIER_ID) 
                            "Enter 16-digit Zerotier ID" 
                        else 
                            "Enter IP Address"
                    ) 
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        val isValid = when (selectedInputType) {
                            InputType.ZEROTIER_ID -> ConnectionConfig.isValidZerotierID(zerotierInput)
                            InputType.IP_ADDRESS -> ConnectionConfig.isValidIPAddress(zerotierInput)
                        }
                        
                        if (isValid) {
                            if (preferencesRepository.pingAddress(zerotierInput)) {
                                val baseUrl = "http://$zerotierInput"
                                preferencesRepository.saveBaseUrl(baseUrl)
                                // TODO: Navigate to login screen
                                showToast(context, "Connection successful!")
                            } else {
                                showToast(context, "Could not connect to the address")
                            }
                        } else {
                            val errorMessage = if (selectedInputType == InputType.ZEROTIER_ID) 
                                "Invalid Zerotier ID. Please enter a 16-digit hexadecimal number" 
                            else 
                                "Invalid IP address format"
                            showToast(context, errorMessage)
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colors.onPrimary
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Connect")
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }
}

private fun showToast(context: android.content.Context, message: String) {
    android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
}
