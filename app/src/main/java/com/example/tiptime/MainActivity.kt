package com.example.tiptime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiptime.ui.theme.TipTimeTheme
import java.text.NumberFormat


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipTimeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TipTImeLayout()
                }
            }
        }
    }
}

@Composable
fun TipTImeLayout(){
    var amountInput by remember { //this is state hoisting of the amountInput
        mutableStateOf("")
    }

    var tipInput by remember { mutableStateOf("") }

    var roundup by remember { mutableStateOf(false) }

    val amount = amountInput.toDoubleOrNull() ?: 0.0 //it will be converted to a double if the string is valid and if the string is not valid it returns null otherwise you get a default 0.0
    val tipPercent = tipInput.toDoubleOrNull() ?: 0.0

    val tip = calculateTip(amount, tipPercent, roundup)

    Column(
        modifier = Modifier
            .padding(40.dp)
            .verticalScroll(rememberScrollState())//this allows the orientation of the app and the ability for it to run in any orientation but doesn't save data when orientated, it refreshes on orientation
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = stringResource(R.string.calculate_tip) ,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(alignment = Alignment.Start)
        )

        EditNumberField(
            label = R.string.bill_amount,
            leadingIcon = R.drawable.money_fill0_wght400_grad0_opsz48,
            value = amountInput,
            onValueChange = { amountInput = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )

        EditNumberField(
            label = R.string.how_was_the_service,
            leadingIcon = R.drawable.percent_fill0_wght400_grad0_opsz48,
            value = tipInput ,
            onValueChange = { tipInput = it } ,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ) ,
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )

        RoundTipRow(
            roundup = roundup,
            onRoundUpChanged = {roundup = it}
        )

        Text(
            text = stringResource(R.string.tip_amount, " $tip"), //this shows the amount with tip to pay
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.height(150.dp))
    }
}

@VisibleForTesting//allows the function to be tested using a local test
internal fun calculateTip(
    amount: Double,
    tipPercent: Double = 15.0,
    roundup: Boolean
): String {//thi function was private but it's internal so we can test
    var tip = tipPercent / 100 * amount
    if(roundup) {
        tip = kotlin.math.ceil(tip)
    }
    return NumberFormat.getCurrencyInstance().format(tip) //this format returns the tip with 2decimals to represent cents
}

@Composable
fun RoundTipRow(roundup: Boolean , onRoundUpChanged: (Boolean) -> Unit ,modifier: Modifier = Modifier){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .size(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(id = R.string.round_up_tip))
        Switch(
            checked = roundup ,
            onCheckedChange = onRoundUpChanged,
            modifier = modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNumberField(
    @StringRes label: Int,
    @DrawableRes leadingIcon: Int,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier){

    TextField(
        value = value,
        leadingIcon = { androidx.compose.material3.Icon(painter = painterResource(id = leadingIcon), contentDescription = null) },
        onValueChange = onValueChange ,
        singleLine = true,//this makes the input fit into one line
        label = { Text(stringResource(label)) },
        keyboardOptions = keyboardOptions,
        modifier = modifier,
    )


}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipTimeTheme {
        TipTImeLayout()
    }
}