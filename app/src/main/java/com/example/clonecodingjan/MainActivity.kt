package com.example.clonecodingjan

import android.annotation.SuppressLint
import android.inputmethodservice.Keyboard
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.timer
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = viewModel<MainViewModel>()
            HomeScreen(viewModel = viewModel)


//2
//            val viewModel = viewModel<MainViewModel>()
//            val sec = viewModel.sec.value
//            val milli = viewModel.milli.value
//            val isRunning = viewModel.isRunning.value
//            val lapTimes = viewModel.lapTimes.value
//
//            MainScreen(
//                sec = sec,
//                milli = milli,
//                isRunning = isRunning,
//                lapTimes = lapTimes,
//                onReset = { viewModel.reset() },
//                onToggle = { running ->
//                    if (running)
//                    {
//                        viewModel.pause()
//                    }else{
//                        viewModel.start()
//                    }
//                           },
//                onLapTime = {
//                    viewModel.recordLapTime()
//                }
//            )
//1
//            val viewModel = viewModel<BmiViewModel>()
//            val navController = rememberNavController()
//
//            val bmi = viewModel.bmi.value
//
//            NavHost(navController = navController, startDestination = "home"){// 홈 화면을 보여주게 된다.
//                composable(route = "home"){
//                    HomeScreen(){ height, weight ->
//                        viewModel.bmiCalculate(height, weight)
//                        navController.navigate("result")
//                    }
//                }
//                composable(route = "result"){
//                    ResultScreen(
//                        navController = navController,
//                        bmi = bmi
//                    )
//                }
//            }

        }
    }
}
//3
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel){
    val focusManager = LocalFocusManager.current

    val (inputUrl, setUrl) = rememberSaveable {
        mutableStateOf("https://www.google.com")
    }// 구조 분해
    val snackbarHostState = remember { SnackbarHostState() }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "나만의 웹 브라우저")},
                actions = {
                    IconButton(onClick = {
                        viewModel.undo()
                     }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = {
                        viewModel.redo()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "forward",
                            tint = Color.Black
                        )

                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = inputUrl,
                onValueChange = setUrl,
                label = { Text("https://")},
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.url.value = inputUrl
                    focusManager.clearFocus()//키보드가 내려가게 설정하는 것
                })
            )
            Spacer(modifier = Modifier.height(16.dp))
            MyWebView(
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
            )
        }
    }

}

@Composable
fun MyWebView(
    viewModel: MainViewModel,
    snackbarHostState: SnackbarHostState,

) {
    val scope = rememberCoroutineScope()// 없으면 계속해서 실행되는 현상이 있다.
    //SharedFlow는 코루틴 스코프에서 작성해야 한다.

    val webView = rememberWebView()

    LaunchedEffect(Unit) {
        viewModel.undoSharedFlow.collectLatest {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                snackbarHostState.showSnackbar("더 이상 뒤로 갈 수 없음")
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.redoSharedFlow.collectLatest {
            if (webView.canGoForward()) {
                webView.goForward()
            } else {
                snackbarHostState.showSnackbar("더 이상 앞으로 갈 수 없음")
            }
        }
    }


    //기존 안드로이드 앱에서 제공하는 일반 뷰를 Composable로 변환 할려면
    //안드로이드 뷰를 사용해야 한다.
    AndroidView(
        modifier = Modifier
            .fillMaxSize(),
        factory = {//코루틴 스코프가 아니다.
                  webView
//              WebView(it).apply{
//                  settings.javaScriptEnabled = true
//                  webViewClient = WebViewClient()
//                  loadUrl("https://google.com")
//              }
        },
        update ={webView -> //코루틴 스코프가 아니다.
            webView.loadUrl(viewModel.url.value)

            scope.launch {
                viewModel.undoSharedFlow.collectLatest {
                    if(webView.canGoBack()){
                        webView.goBack()
                    }else {
                        snackbarHostState.showSnackbar("더 이상 뒤로 갈 수 없음")
                    }

                }
            }
            scope.launch {
                viewModel.redoSharedFlow.collectLatest {
                    if(webView.canGoForward()){
                        webView.goForward()
                    }else {
                        snackbarHostState.showSnackbar("더 이상 앞으로 갈 수 없음")
                    }

                }
            }
        }
    )
}
@Composable
fun rememberWebView(): WebView {
    val context = LocalContext.current
    val webView = remember {// 기억하기 위해 사용
        WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            loadUrl("https://google.com")
        }
    }
    return webView
}
////1
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreen(
//    onResultClicked: (Double, Double) -> Unit,
//               ){
//    val (height, setHeight) = rememberSaveable {
//        mutableStateOf("")
//    }
//    val (weight, setWeight) = rememberSaveable {
//        mutableStateOf("")
//    }
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("비만도 계산기")}
//            )
//        }
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(it)
//        ){
//            OutlinedTextField(
//                value = height,
//                onValueChange = setHeight,
//                label = { Text("키")},
//                modifier = Modifier.fillMaxWidth(),
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            )
//            OutlinedTextField(
//                value = weight,
//                onValueChange = setWeight,
//                label = { Text("몸무게")},
//                modifier = Modifier.fillMaxWidth(),
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Button(
//                onClick = {
//                    if (height.isNotEmpty() && weight.isNotEmpty()){
//                        onResultClicked(height.toDouble(), weight.toDouble())
//                    }
//                },
//                modifier = Modifier.align(Alignment.End)) {
//                Text("결과")
//            }
//        }
//    }
//}
////1
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ResultScreen(
//    navController: NavController,
//    bmi: Double,
//    ){
//
//    val text = when{
//        bmi >= 35 -> "고도 비만"
//        bmi >= 30 -> "2단계 비만"
//        bmi >= 25 -> "1단계 비만"
//        bmi >= 23 -> "과체중"
//        bmi >= 18.5 -> "정상"
//        else -> "저체중"
//    }
//    val imageRes = when {
//        bmi >= 23 -> R.drawable.icon_unknown_pace
//        bmi >= 18.5 -> R.drawable.icon_unknown_pace_little_happy
//        else -> R.drawable.icon_unknown_pace_happy
//    }
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("비만도 계산기")},
//                navigationIcon = {
//                    Icon(
//                        imageVector = Icons.Default.ArrowBack,
//                        contentDescription = "home",
//                        modifier = Modifier.clickable{
//                            navController.popBackStack()
//                        }
//                    )
//                }
//            )
//        }
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Text(text, fontSize = 30.sp)// 글자는 sp로 사용해서 작성해야 한다.
//            Spacer(modifier = Modifier.height(50.dp))
//            Image(painter = painterResource(id = imageRes),
//                contentDescription = null,
//                modifier = Modifier.size(100.dp),
//                colorFilter = ColorFilter.tint(
//                    color = Color.Black,
//                )
//                )
//        }
//    }
//}
//
////1
//class BmiViewModel : ViewModel(){
//
//    private val _bmi = mutableStateOf(0.0)
//    val bmi : State<Double> = _bmi
//
//    fun bmiCalculate(
//        height : Double, weight : Double,
//    ){
//        _bmi.value = weight / (height / 100.0).pow(2.0)
//    }
//
//}


////2
//class MainViewModel: ViewModel(){
//
//    private var time = 0
//    private var timerTask: Timer? = null
//
//    private val _isRunning = mutableStateOf(false)
//    val isRunning : State<Boolean> = _isRunning
//
//    private val _sec = mutableStateOf(0)
//    val sec : State<Int> = _sec
//
//    private val _milli = mutableStateOf(0)
//    val milli : State<Int> = _milli
//
//    private val _lapTimes = mutableStateOf(mutableListOf<String>())
//    val lapTimes : State<List<String>> = _lapTimes
//
//    private var lap = 1
//
//    fun start(){
//        _isRunning.value = true
//
//        timerTask = timer(period = 10){
//            time++
//            _sec.value = time / 100
//            _milli.value = time % 100
//        }
//    }
//    fun pause(){
//        _isRunning.value = false
//
//        timerTask?.cancel()
//    }
//    fun reset(){
//        _isRunning.value = false
//        timerTask?.cancel()
//        time = 0
//        _sec.value = 0
//        _milli.value = 0
//
//        _lapTimes.value.clear()
//        lap = 1
//    }
//    fun recordLapTime(){
//        //list 표시를 해야한다
//        _lapTimes.value.add(0,"$lap LAP : ${sec.value}.${milli.value}")
//        lap++
//    }
//}
//2
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MainScreen(
//    sec : Int,
//    milli: Int,
//    isRunning : Boolean,
//    lapTimes : List<String>,
//    onReset: () -> Unit,//눌른 것을 처리하기 위해서
//    onToggle: (Boolean) -> Unit,// 재생, 정지
//    onLapTime: () -> Unit,
//){
//    Scaffold(
//        topBar = {
//            TopAppBar(title = {
//                Text("StopWatch")
//            })
//        }
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally
//
//        ) {
//            Spacer(modifier = Modifier.height(40.dp))
//
//            Row(
//                verticalAlignment = Alignment.Bottom
//            ) {
//                Text("$sec", fontSize = 100.sp)
//                Text("$milli")
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .verticalScroll(rememberScrollState()),
//            ) {
//                lapTimes.forEach{lapTime ->
//                    Text(lapTime)
//                }
//            }
//            Row(
//                modifier = Modifier
//                    .padding(8.dp)
//                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                FloatingActionButton(
//                    onClick = { onReset() },
//                    contentColor = Color.Red,
//                    ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.icon_refresh),
//                        contentDescription = "reset")
//                }
//                FloatingActionButton(
//                    onClick = { onToggle(isRunning) },
//                    contentColor = Color.Green,
//                ) {
//                    Image(
//                        painter = painterResource(
//                            id = if(isRunning) R.drawable.icon_pause
//                            else R.drawable.icon_play
//                        ),
//                        contentDescription = "start/pause")
//                }
//                Button(onClick = { onLapTime() }) {
//                    Text("랩 타임")
//                }
//            }
//        }
//    }
//}