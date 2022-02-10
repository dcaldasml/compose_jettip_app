package com.caldas.jettipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.caldas.jettipapp.components.InputField
import com.caldas.jettipapp.ui.theme.JetTipAppTheme
import com.caldas.jettipapp.util.calculateTotalPerPerson
import com.caldas.jettipapp.util.calculateTotalTip
import com.caldas.jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipAppTheme {
        Surface(color = MaterialTheme.colors.background) {
            content()
        }
    }
}

@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            //.clip(shape = CircleShape.copy(all = CornerSize(12.dp)))
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$ $total",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun MainContent() {
    val totalPerPerson = remember { mutableStateOf(0.0) }
    val splitBy = remember { mutableStateOf(1) }
    val tipAmount = remember { mutableStateOf(0.0) }
    val range = IntRange(start = 1, endInclusive = 15)

    BillForm(
        range = range,
        totalPerPerson = totalPerPerson,
        tipAmount = tipAmount,
        splitBy = splitBy
    ) {
    }
}

@ExperimentalComposeUiApi
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitBy: MutableState<Int>,
    tipAmount: MutableState<Double>,
    totalPerPerson: MutableState<Double>,
    onValChange: (String) -> Unit = {}
) {
    val totalBillState = remember { mutableStateOf("") }
    val validState = remember(totalBillState.value ) { totalBillState.value.trim().isNotEmpty() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val sliderPosition = remember { mutableStateOf(0f) }
    val tipPercengage = (sliderPosition.value * 100).toInt()

    Column(modifier = Modifier.padding(all = 12.dp)) {
        TopHeader(totalPerPerson.value)
        
        Spacer(modifier = Modifier.height(15.dp))

        Surface(
            modifier = modifier
                .padding(2.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color.LightGray)
        ) {
            Column(
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                InputField(
                    valueState = totalBillState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions

                        onValChange(totalBillState.value.trim())
                        keyboardController?.hide()
                        tipAmount.value = calculateTotalTip(totalBillState.value.toDouble(), tipPercengage)
                        totalPerPerson.value = calculateTotalPerPerson(totalBillState.value.toDouble(), splitBy.value, tipPercengage)
                    }
                )

                if (validState) {
                    Row(
                        modifier = Modifier.padding(3.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Split",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(120.dp))
                        Row(
                            modifier = Modifier.padding(horizontal = 3.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            RoundIconButton(
                                imageVector = Icons.Default.Remove,
                                onClick = {
                                    splitBy.value = if (splitBy.value > 1) splitBy.value - 1 else 1
                                    tipAmount.value = calculateTotalTip(totalBillState.value.toDouble(), tipPercengage)
                                    totalPerPerson.value = calculateTotalPerPerson(totalBillState.value.toDouble(), splitBy.value, tipPercengage)
                                }
                            )
                            Text(
                                text = splitBy.value.toString(),
                                modifier = modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 9.dp, end = 9.dp)
                            )
                            RoundIconButton(
                                imageVector = Icons.Default.Add,
                                onClick = {
                                    if (splitBy.value < range.last) {
                                        splitBy.value += 1
                                    }
                                    tipAmount.value = calculateTotalTip(totalBillState.value.toDouble(), tipPercengage)
                                    totalPerPerson.value = calculateTotalPerPerson(totalBillState.value.toDouble(), splitBy.value, tipPercengage)
                                }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp),

                        ) {
                        Text(
                            text = "Tip",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(200.dp))
                        Text(
                            text = "$ ${tipAmount.value}",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "$tipPercengage%")
                        Spacer(modifier = Modifier.height(14.dp))
                        Slider(
                            value = sliderPosition.value,
                            onValueChange = { newVal ->
                                sliderPosition.value = newVal
                                tipAmount.value = calculateTotalTip(totalBillState.value.toDouble(), tipPercengage)
                                totalPerPerson.value = calculateTotalPerPerson(totalBillState.value.toDouble(), splitBy.value, tipPercengage)
                            },
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            steps = 5
                        )
                    }
                } else {
                    Box {}
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetTipAppTheme {
        MyApp {
            MainContent()
        }
    }
}