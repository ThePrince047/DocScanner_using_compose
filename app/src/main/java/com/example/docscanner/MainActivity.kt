package com.example.docscanner

import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.appcompat.app.AppCompatActivity // Needed for getClient
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.modifier.modifierLocalMapOf
import coil.compose.AsyncImage
import com.example.docscanner.ui.theme.DocScannerTheme

class MainActivity : AppCompatActivity() {

    val options = GmsDocumentScannerOptions.Builder()
        .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
        .setGalleryImportAllowed(true)
        .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,GmsDocumentScannerOptions.RESULT_FORMAT_PDF)
        .build()

    private val documentScanner = GmsDocumentScanning.getClient(options)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DocScannerTheme {
                var imagelist by remember {
                    mutableStateOf<List<Uri>>(emptyList())

                }
                val scanResult = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) {
                    if(it.resultCode== RESULT_OK){
                        val result = GmsDocumentScanningResult.fromActivityResultIntent(it.data)
                        imagelist = result?.pages?.map { it.imageUri } ?: emptyList()
                    }
                }
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    Column (
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        imagelist.forEach {
                            AsyncImage(model = it, contentDescription = null)
                        }
                        Button(onClick = {
                            documentScanner.getStartScanIntent(this@MainActivity).addOnSuccessListener {
                                scanResult.launch(IntentSenderRequest.Builder(it).build())
                            }.addOnFailureListener{
                                Toast.makeText(applicationContext,it.message,Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Text(text = "Scan Document")
                        }
                    }
                }
            }
        }
    }
}
