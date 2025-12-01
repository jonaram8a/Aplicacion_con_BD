package com.example.proyecto

// Imports para la app, navegación y UI
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Importación corregida
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto.ui.theme.ProyectoTheme
import com.example.proyecto.data.AppDatabase
import com.example.proyecto.data.VideojuegoEntity
import com.example.proyecto.viewmodel.VideojuegoViewModel
import com.example.proyecto.viewmodel.VideojuegoViewModelFactory
import com.example.proyecto.viewmodel.UsuarioViewModel
import com.example.proyecto.viewmodel.UsuarioViewModelFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob


// --- Enum para las opciones de tema ---
enum class ThemeSetting {
    Light, Dark, System
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Inicializar la base de datos
        val database = AppDatabase.getDatabase(this)
        val videojuegoDao = database.videojuegoDao()
        
        // Poblar la base de datos con juegos iniciales si está vacía
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            val count = videojuegoDao.getCount()
            
            if (count == 0) {
                // Insertar juegos iniciales
                listaDeJuegos.forEach { juego ->
                    val entity = VideojuegoEntity(
                        id = 0, // Auto-generado
                        titulo = juego.titulo,
                        descripcion = juego.descripcion,
                        posterResId = juego.posterResId,
                        bannerResId = juego.bannerResId
                    )
                    videojuegoDao.insertVideojuego(entity)
                }
            }
        }
        
        setContent {
            val context = LocalContext.current
            // --- Estado para guardar la elección del tema ---
            var themeSetting by remember { mutableStateOf(ThemeSetting.System) }
            val useDarkTheme = when (themeSetting) {
                ThemeSetting.Light -> false
                ThemeSetting.Dark -> true
                ThemeSetting.System -> isSystemInDarkTheme()
            }

            // --- NUEVO: Estado para el usuario ---
            // Guardamos el *nombre* del usuario. null = deslogueado.
            var loggedInUser by rememberSaveable { mutableStateOf<String?>(null) }
            
            // Crear ViewModels
            val db = remember { AppDatabase.getDatabase(context) }
            val videojuegoViewModel: VideojuegoViewModel = viewModel(
                factory = VideojuegoViewModelFactory(db.videojuegoDao())
            )
            val usuarioViewModel: UsuarioViewModel = viewModel(
                factory = UsuarioViewModelFactory(db.usuarioDao())
            )

            ProyectoTheme(darkTheme = useDarkTheme) {
                AppNavegacionConMenuLateral(
                    themeSetting = themeSetting,
                    onThemeChange = { newSetting ->
                        themeSetting = newSetting
                    },

                    // --- Pasamos el estado y las funciones de login ---
                    loggedInUser = loggedInUser,
                    onLogin = { username ->
                        loggedInUser = username
                    },
                    onLogout = {
                        loggedInUser = null
                    },
                    videojuegoViewModel = videojuegoViewModel,
                    usuarioViewModel = usuarioViewModel
                )
            }
        }
    }
}
data class Juego(
    val id: Int,
    val titulo: String,
    val posterResId: Int,
    val bannerResId: Int,
    val descripcion: String
)

// Lista de recursos de imágenes disponibles
val recursosPoster = listOf(
    R.drawable.poster_juego1,
    R.drawable.poster_juego2,
    R.drawable.poster_juego3,
    R.drawable.poster_juego4,
    R.drawable.poster_juego5,
    R.drawable.poster_juego6,
    R.drawable.poster_juego7,
    R.drawable.poster_juego8,
    R.drawable.avatar
)

val recursosBanner = listOf(
    R.drawable.banner_juego1,
    R.drawable.banner_juego2,
    R.drawable.banner_juego3,
    R.drawable.banner_juego4,
    R.drawable.banner_juego5,
    R.drawable.banner_juego6,
    R.drawable.banner_juego7,
    R.drawable.banner_juego8,
    R.drawable.avatar
)
val listaDeJuegos = listOf(
    Juego(
        id = 1,
        titulo = "GTA V",
        posterResId = R.drawable.poster_juego1,
        bannerResId = R.drawable.banner_juego1,
        descripcion = "GTA V se desarrolla en la ciudad ficticia de Los Santos, que está inspirada" +
                "en Los Ángeles y sus alrededores. El juego permite a los jugadores explorar un" +
                "vasto mundo abierto, participar en misiones y realizar diversas actividades," +
                "desde robos hasta deportes y carreras."
    ),
    Juego(
        id = 2,
        titulo = "FIFA 18",
        posterResId = R.drawable.poster_juego2,
        bannerResId = R.drawable.banner_juego2,
        descripcion = "FIFA 18 es un videojuego de fútbol, desarrollado por Electronic Arts y" +
                "publicado por EA Sports Canadá y EA Sports Rumania. Es el 25.º de la serie de" +
                "videojuegos de la FIFA. Salió a la venta el 29 de septiembre de 2017, siendo la" +
                "portada del mismo Cristiano Ronaldo"
    ),
    Juego(
        id = 3,
        titulo = "Fortnite",
        posterResId = R.drawable.poster_juego3,
        bannerResId = R.drawable.banner_juego3,
        descripcion = "Fortnite es un videojuego de batalla real desarrollado por Epic Games," +
                "lanzado en 2017, que combina elementos de construcción y supervivencia en un" +
                "entorno multijugador."
    ),
    Juego(
        id = 4,
        titulo = "Minecraft",
        posterResId = R.drawable.poster_juego4,
        bannerResId = R.drawable.banner_juego4,
        descripcion = "Minecraft fue lanzado inicialmente el 17 de mayo de 2009, y su versión" +
                "completa se publicó el 18 de noviembre de 2011. Desde su lanzamiento, ha sido" +
                "actualizado constantemente y ha vendido más de 300 millones de copias," +
                "convirtiéndose en el videojuego más vendido de la historia. En 2014," +
                "Microsoft adquirió Mojang Studios y Minecraft por 2.500 millones de dólares."
    ),
    Juego(
        id = 5,
        titulo = "Gears of War",
        posterResId = R.drawable.poster_juego5,
        bannerResId = R.drawable.banner_juego5,
        descripcion = "Gears of War es un videojuego de disparos en tercera persona de acción," +
                "aventura y horror táctico, en el que los jugadores combaten para salvar a la" +
                "humanidad de la invasión de la horda Locust en el planeta ficticio Sera."
    ),
    Juego(
        id = 6,
        titulo = "Call of Duty Modern Warfare 3",
        posterResId = R.drawable.poster_juego6,
        bannerResId = R.drawable.banner_juego6,
        descripcion = "Call of Duty: Modern Warfare 3 es un shooter en primera persona que" +
                "continúa la saga Modern Warfare con una campaña intensa, modos multijugador" +
                "amplios y un innovador modo zombies, ofreciendo una experiencia bélica" +
                "altamente inmersiva y estratégica."
    ),
    Juego(
        id = 7,
        titulo = "Need for Speed: Rivals",
        posterResId = R.drawable.poster_juego7,
        bannerResId = R.drawable.banner_juego7,
        descripcion = "Need for Speed: Rivals es un videojuego de carreras de mundo abierto" +
                "donde los jugadores pueden asumir el rol de pilotos ilegales o policías," +
                "compitiendo en persecuciones y carreras llenas de adrenalina en el condado" +
                "ficticio de Redview."
    ),
    Juego(
        id = 8,
        titulo = "Ark: Survival Evolved",
        posterResId = R.drawable.poster_juego8,
        bannerResId = R.drawable.banner_juego8,
        descripcion = "Para aquellos que buscan perfeccionar sus habilidades en ARK: Survival" +
        "Evolved, hay guías completas disponibles en español que ofrecen trucos, consejos y" +
                "estrategias para sobrevivir y prosperar en el juego. Desde los primeros pasos" +
                "hasta la dominación de las criaturas más temibles, estas guías cubren todas las" +
                "espaldas necesarias para una aventura épica."
    )
)

sealed class Pantalla(val ruta: String) {
    object Inicio : Pantalla("inicio")
    object Cuenta : Pantalla("cuenta")
    object Configuracion : Pantalla("configuracion")
    object DetalleJuego : Pantalla("detalle/{juegoId}") {
        fun crearRuta(juegoId: Int) = "detalle/$juegoId"
    }
}

data class ItemMenu(
    val pantalla: Pantalla,
    val titulo: String,
    val icono: ImageVector
)

val pantallasDelMenu = listOf(
    ItemMenu(Pantalla.Inicio, "Inicio", Icons.Filled.SportsEsports),
    ItemMenu(Pantalla.Cuenta, "Mi Cuenta", Icons.Filled.AccountCircle),
    ItemMenu(Pantalla.Configuracion, "Configuración", Icons.Filled.Settings)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavegacionConMenuLateral(
    themeSetting: ThemeSetting,
    onThemeChange: (ThemeSetting) -> Unit,
    loggedInUser: String?,
    onLogin: (String) -> Unit,
    onLogout: () -> Unit,
    videojuegoViewModel: VideojuegoViewModel,
    usuarioViewModel: UsuarioViewModel
) {

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    val itemMenuActual = pantallasDelMenu.find { it.pantalla.ruta == rutaActual }
    val tituloActual = when {
        itemMenuActual != null -> itemMenuActual.titulo
        rutaActual?.startsWith("detalle/") == true -> "Detalles del Juego"
        else -> "Inicio"
    }
    val mostrarIconoMenu = itemMenuActual != null

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = mostrarIconoMenu,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                pantallasDelMenu.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.titulo) },
                        icon = { Icon(imageVector = item.icono, contentDescription = item.titulo) },
                        selected = rutaActual == item.pantalla.ruta,
                        onClick = {
                            navController.navigate(item.pantalla.ruta) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(tituloActual) },
                    navigationIcon = {
                        if (mostrarIconoMenu) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Abrir menú")
                            }
                        } else {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Pantalla.Inicio.ruta,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Pantalla.Inicio.ruta) {
                    PantallaJuegos(
                        viewModel = videojuegoViewModel,
                        onJuegoClick = { juegoId ->
                            navController.navigate(Pantalla.DetalleJuego.crearRuta(juegoId))
                        }
                    )
                }
                composable(Pantalla.Cuenta.ruta) {
                    if (loggedInUser == null) {
                        PantallaLoginRegistro(
                            usuarioViewModel = usuarioViewModel,
                            onLogin = onLogin
                        )
                    } else {
                        PantallaPerfilUsuario(
                            username = loggedInUser,
                            onLogout = onLogout
                        )
                    }
                }
                // --- FIN DEL CAMBIO ---

                composable(Pantalla.Configuracion.ruta) {
                    PantallaConfiguracion(
                        currentSetting = themeSetting,
                        onSettingChange = onThemeChange
                    )
                }
                composable(
                    route = Pantalla.DetalleJuego.ruta,
                    arguments = listOf(navArgument("juegoId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val juegoId = backStackEntry.arguments?.getInt("juegoId")
                    if (juegoId != null) {
                        PantallaDetalleJuego(
                            juegoId = juegoId,
                            viewModel = videojuegoViewModel
                        )
                    } else {
                        PantallaContenido(
                            texto = "Error: Juego no encontrado",
                            colorFondo = Color.Red
                        )
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaJuegos(
    viewModel: VideojuegoViewModel,
    onJuegoClick: (Int) -> Unit
) {
    val videojuegos by viewModel.videojuegos.collectAsState()
    var mostrarDialogoAgregar by remember { mutableStateOf(false) }
    var videojuegoAEditar by remember { mutableStateOf<VideojuegoEntity?>(null) }
    var mostrarDialogoEliminar by remember { mutableStateOf<VideojuegoEntity?>(null) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(videojuegos) { videojuego ->
                GameCard(
                    videojuego = videojuego,
                    onClick = { onJuegoClick(videojuego.id) },
                    onEdit = { videojuegoAEditar = videojuego },
                    onDelete = { mostrarDialogoEliminar = videojuego }
                )
            }
        }
        
        FloatingActionButton(
            onClick = { mostrarDialogoAgregar = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Agregar videojuego")
        }
    }
    
    // Diálogo para agregar/editar videojuego
    if (mostrarDialogoAgregar || videojuegoAEditar != null) {
        DialogoAgregarEditarVideojuego(
            videojuego = videojuegoAEditar,
            onDismiss = {
                mostrarDialogoAgregar = false
                videojuegoAEditar = null
            },
            onConfirm = { titulo, descripcion, posterResId, bannerResId ->
                if (videojuegoAEditar != null) {
                    viewModel.actualizarVideojuego(
                        videojuegoAEditar!!.copy(
                            titulo = titulo,
                            descripcion = descripcion,
                            posterResId = posterResId,
                            bannerResId = bannerResId
                        )
                    )
                    videojuegoAEditar = null
                } else {
                    viewModel.agregarVideojuego(titulo, descripcion, posterResId, bannerResId)
                    mostrarDialogoAgregar = false
                }
            }
        )
    }
    
    // Diálogo para confirmar eliminación
    mostrarDialogoEliminar?.let { videojuego ->
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = null },
            title = { Text("Eliminar videojuego") },
            text = { Text("¿Estás seguro de que quieres eliminar \"${videojuego.titulo}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarVideojuego(videojuego.id)
                        mostrarDialogoEliminar = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun GameCard(
    videojuego: VideojuegoEntity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = if (videojuego.posterResId != 0) {
                        painterResource(id = videojuego.posterResId)
                    } else {
                        painterResource(id = R.drawable.avatar) // Imagen por defecto
                    },
                    contentDescription = "Póster de ${videojuego.titulo}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.7f)
                        .clickable(onClick = onClick),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Eliminar",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Text(
                text = videojuego.titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(12.dp)
                    .clickable(onClick = onClick)
            )
        }
    }
}

@Composable
fun PantallaDetalleJuego(
    juegoId: Int,
    viewModel: VideojuegoViewModel
) {
    val videojuegos by viewModel.videojuegos.collectAsState()
    val videojuego = videojuegos.firstOrNull { it.id == juegoId }
    
    if (videojuego != null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Image(
                    painter = if (videojuego.bannerResId != 0) {
                        painterResource(id = videojuego.bannerResId)
                    } else {
                        painterResource(id = R.drawable.avatar)
                    },
                    contentDescription = "Banner de ${videojuego.titulo}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                    contentScale = ContentScale.Crop
                )
            }
            item {
                Text(
                    text = videojuego.titulo,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
            item {
                Text(
                    text = videojuego.descripcion,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    } else {
        PantallaContenido(
            texto = "Error: Juego no encontrado",
            colorFondo = Color.Red
        )
    }
}

@Composable
fun PantallaConfiguracion(
    currentSetting: ThemeSetting,
    onSettingChange: (ThemeSetting) -> Unit
) {
    val options = mapOf(
        ThemeSetting.Light to "Tema Blanco",
        ThemeSetting.Dark to "Tema Negro",
        ThemeSetting.System to "Usar predeterminado del sistema"
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Text(
            text = "Seleccionar Tema",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        options.forEach { (setting, label) ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (currentSetting == setting),
                        onClick = { onSettingChange(setting) }
                    )
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (currentSetting == setting),
                    onClick = { onSettingChange(setting) }
                )
                Text(
                    text = label,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
fun PantallaContenido(texto: String, colorFondo: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 22.sp
        )
    }
}

@Composable
fun PantallaLoginRegistro(
    usuarioViewModel: UsuarioViewModel,
    onLogin: (username: String) -> Unit
) {
    var isRegistering by remember { mutableStateOf(false) }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val loginResult by usuarioViewModel.loginResult.collectAsState()
    val registroResult by usuarioViewModel.registroResult.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Image(
                painter = painterResource(id = R.drawable.avatar),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp).padding(bottom = 24.dp)
            )
            Text(
                text = if (isRegistering) "Crear Cuenta" else "Iniciar Sesión",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de Usuario") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = {
                    IconButton(onClick = { passwordHidden = !passwordHidden }) {
                        val icon = if (passwordHidden) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        Icon(icon, contentDescription = "Mostrar contraseña")
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (isRegistering) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            Button(
                onClick = {
                    if (isRegistering) {
                        if (password.isNotBlank() && password == confirmPassword) {
                            usuarioViewModel.registrar(username, password)
                        } else {
                            Toast.makeText(context, "Las contraseñas no coinciden o están vacías", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (username.isNotBlank() && password.isNotBlank()) {
                            usuarioViewModel.login(username, password)
                        } else {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isRegistering) "Registrarse" else "Iniciar Sesión")
            }
            
            // Manejar resultados de login/registro
            LaunchedEffect(loginResult) {
                loginResult?.let { usuario ->
                    onLogin(usuario.username)
                    Toast.makeText(context, "¡Bienvenido, ${usuario.username}!", Toast.LENGTH_SHORT).show()
                }
            }
            
            LaunchedEffect(registroResult) {
                registroResult?.let { exito ->
                    if (exito) {
                        Toast.makeText(context, "¡Cuenta creada exitosamente!", Toast.LENGTH_SHORT).show()
                        // Auto-login después del registro
                        usuarioViewModel.login(username, password)
                    } else {
                        Toast.makeText(context, "El usuario ya existe", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { isRegistering = !isRegistering }) {
                Text(
                    if (isRegistering)
                        "¿Ya tienes cuenta? Inicia Sesión"
                    else
                        "¿No tienes cuenta? Regístrate"
                )
            }
        }
    }
}
@Composable
fun DialogoAgregarEditarVideojuego(
    videojuego: VideojuegoEntity?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, Int) -> Unit
) {
    var titulo by remember { mutableStateOf(videojuego?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(videojuego?.descripcion ?: "") }
    var posterResId by remember { mutableStateOf(videojuego?.posterResId ?: R.drawable.avatar) }
    var bannerResId by remember { mutableStateOf(videojuego?.bannerResId ?: R.drawable.avatar) }
    var mostrarSelectorPoster by remember { mutableStateOf(false) }
    var mostrarSelectorBanner by remember { mutableStateOf(false) }
    
    if (mostrarSelectorPoster) {
        DialogoSelectorImagen(
            titulo = "Seleccionar Portada",
            recursos = recursosPoster,
            imagenSeleccionada = posterResId,
            onDismiss = { mostrarSelectorPoster = false },
            onSeleccionar = { 
                posterResId = it
                mostrarSelectorPoster = false
            }
        )
    }
    
    if (mostrarSelectorBanner) {
        DialogoSelectorImagen(
            titulo = "Seleccionar Banner",
            recursos = recursosBanner,
            imagenSeleccionada = bannerResId,
            onDismiss = { mostrarSelectorBanner = false },
            onSeleccionar = { 
                bannerResId = it
                mostrarSelectorBanner = false
            }
        )
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (videojuego != null) "Editar videojuego" else "Agregar videojuego") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    minLines = 3,
                    maxLines = 5
                )
                
                Text(
                    text = "Portada (Poster)",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = posterResId),
                        contentDescription = "Portada seleccionada",
                        modifier = Modifier
                            .size(80.dp)
                            .clickable { mostrarSelectorPoster = true },
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Button(onClick = { mostrarSelectorPoster = true }) {
                        Text("Cambiar Portada")
                    }
                }
                
                Text(
                    text = "Banner",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = bannerResId),
                        contentDescription = "Banner seleccionado",
                        modifier = Modifier
                            .size(80.dp)
                            .clickable { mostrarSelectorBanner = true },
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Button(onClick = { mostrarSelectorBanner = true }) {
                        Text("Cambiar Banner")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isNotBlank() && descripcion.isNotBlank()) {
                        onConfirm(titulo, descripcion, posterResId, bannerResId)
                    }
                },
                enabled = titulo.isNotBlank() && descripcion.isNotBlank()
            ) {
                Text(if (videojuego != null) "Guardar" else "Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DialogoSelectorImagen(
    titulo: String,
    recursos: List<Int>,
    imagenSeleccionada: Int,
    onDismiss: () -> Unit,
    onSeleccionar: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titulo) },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recursos.size) { index ->
                    val recurso = recursos[index]
                    Card(
                        modifier = Modifier
                            .clickable { onSeleccionar(recurso) }
                            .fillMaxWidth()
                            .then(
                                if (recurso == imagenSeleccionada) {
                                    Modifier.border(2.dp, Color.Blue, RoundedCornerShape(8.dp))
                                } else {
                                    Modifier
                                }
                            ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = recurso),
                            contentDescription = "Imagen $index",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.7f),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun PantallaPerfilUsuario(username: String, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.avatar),
            contentDescription = "Avatar",
            modifier = Modifier.size(150.dp).padding(bottom = 24.dp)
        )
        Text(
            text = "¡Bienvenido, $username!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar Sesión")
        }
    }
}