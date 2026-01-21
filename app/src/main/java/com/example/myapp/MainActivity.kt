package com.example.myapp

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapp.ui.theme.MyAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.text.SimpleDateFormat
import java.util.*

data class SiniestroVial(
    val categoria: String,
    val fecha: String,
    val placa: String,
    val nombreChofer: String,
    val cedulaChofer: String,
    val notas: String,
    val coordenadas: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppTheme {
                PantallaPrincipalAgente()
            }
        }
    }
}

@Composable
fun PantallaPrincipalAgente() {
    var seccionSeleccionada by remember { mutableStateOf("registro") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = seccionSeleccionada == "registro",
                    onClick = { seccionSeleccionada = "registro" },
                    label = { Text("Registrar") },
                    icon = {}
                )
                NavigationBarItem(
                    selected = seccionSeleccionada == "lista",
                    onClick = { seccionSeleccionada = "lista" },
                    label = { Text("Historial") },
                    icon = {}
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (seccionSeleccionada == "registro") VistaFormulario()
            else VistaHistorial()
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VistaFormulario() {
    val ctx = LocalContext.current
    val scroll = rememberScrollState()

    var tipo by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    val fecha = remember {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
    }

    var menu by remember { mutableStateOf(false) }
    val categorias = listOf("Choque", "Colisión", "Atropello")

    var foto by remember { mutableStateOf<Bitmap?>(null) }
    var gpsTexto by remember { mutableStateOf("GPS: No capturado") }
    var cargandoGps by remember { mutableStateOf(false) }

    val permisos = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val camara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bmp -> foto = bmp }

    val gps = remember { LocationServices.getFusedLocationProviderClient(ctx) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Reporte de Tránsito",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                ExposedDropdownMenuBox(expanded = menu, onExpandedChange = { menu = !menu }) {
                    OutlinedTextField(
                        value = tipo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Siniestro") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(menu) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        categorias.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = { tipo = it; menu = false }
                            )
                        }
                    }
                }

                OutlinedTextField(value = fecha, onValueChange = {}, readOnly = true, label = { Text("Fecha") })
                OutlinedTextField(value = placa, onValueChange = { placa = it }, label = { Text("Placa") })
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Conductor") })
                OutlinedTextField(value = cedula, onValueChange = { cedula = it }, label = { Text("Cédula") })
                OutlinedTextField(value = notas, onValueChange = { notas = it }, label = { Text("Observaciones") }, minLines = 2)
            }
        }

        foto?.let {
            Card(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                Image(it.asImageBitmap(), null, Modifier.fillMaxSize())
            }
        }

        Card {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(gpsTexto, Modifier.weight(1f))
                if (cargandoGps) CircularProgressIndicator(modifier = Modifier.size(16.dp))
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    if (permisos.allPermissionsGranted) camara.launch(null)
                    else permisos.launchMultiplePermissionRequest()
                }
            ) { Text("Foto") }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    if (permisos.allPermissionsGranted) {
                        cargandoGps = true
                        gps.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                            .addOnSuccessListener {
                                cargandoGps = false
                                gpsTexto = it?.let { l -> "Lat ${l.latitude}, Lon ${l.longitude}" } ?: "GPS no disponible"
                            }
                    } else permisos.launchMultiplePermissionRequest()
                }
            ) { Text("Ubicación") }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (tipo.isBlank() || placa.isBlank() || cedula.isBlank()) {
                    Toast.makeText(ctx, "Datos incompletos", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val prefs = ctx.getSharedPreferences("RegistroAgente", Context.MODE_PRIVATE)
                prefs.edit().putString(
                    "siniestro_${System.currentTimeMillis()}",
                    "$tipo|$fecha|$placa|$nombre|$cedula|$notas|$gpsTexto"
                ).apply()

                ejecutarVibracionLarga(ctx)
                Toast.makeText(ctx, "Registro guardado", Toast.LENGTH_LONG).show()

                tipo = ""; placa = ""; nombre = ""; cedula = ""; notas = ""; foto = null; gpsTexto = "GPS: No capturado"
            }
        ) {
            Text("FINALIZAR REGISTRO", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun VistaHistorial() {
    val ctx = LocalContext.current
    val prefs = ctx.getSharedPreferences("RegistroAgente", Context.MODE_PRIVATE)

    val lista = remember {
        prefs.all.filter { it.key.startsWith("siniestro_") }.values.map {
            val p = it.toString().split("|")
            SiniestroVial(
                p.getOrElse(0) { "" },
                p.getOrElse(1) { "" },
                p.getOrElse(2) { "" },
                p.getOrElse(3) { "" },
                p.getOrElse(4) { "" },
                p.getOrElse(5) { "" },
                p.getOrElse(6) { "" }
            )
        }.reversed()
    }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Historial de Siniestros", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        items(lista) {
            Card {
                Column(Modifier.padding(14.dp)) {
                    Text("${it.categoria} · ${it.fecha}", fontWeight = FontWeight.Bold)
                    Text("Placa: ${it.placa}")
                    Text("Conductor: ${it.nombreChofer}")
                    Text(it.coordenadas, style = MaterialTheme.typography.labelSmall)
                    if (it.notas.isNotBlank()) Text("Obs: ${it.notas}")
                }
            }
        }
    }
}

fun ejecutarVibracionLarga(context: Context) {
    val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        vib.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE))
    else
        @Suppress("DEPRECATION")
        vib.vibrate(5000)
}
