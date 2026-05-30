package es.upv.etsit.calculadora;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class MainActivity extends AppCompatActivity {

    enum OPERACION { SUMAR, RESTAR, MULTIPLICAR, DIVIDIR, IGUAL}
    OPERACION OP_ANT;

    enum ESTADOS {INIC, NUMERO, OPERACION}
    ESTADOS estado;

    double acumulador;
    String operando2_str;
    double operando2;

    TextView display;
    DecimalFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        display = (TextView)findViewById(R.id.display);

        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator('.');
        df = new DecimalFormat("#.#############", decimalFormatSymbols);

        aINIC();
    }

    private void aINIC() {
        operando2_str   = "";
        display.setText("0");
        operando2 = 0.;
        acumulador = 0.;
        OP_ANT = OPERACION.IGUAL;
        estado = ESTADOS.INIC;
    }

    public void alPulsarTecla(View tecla){

        int id = tecla.getId();

        if ( id == R.id.b_punto ) {
            // Solo añadir punto si no hay ya uno
            if (!operando2_str.contains(".")) {
                if (operando2_str.equals("")) {
                    operando2_str = "0.";
                } else {
                    operando2_str += ".";
                }
                operando2 = miStringToDouble(operando2_str);
                display.setText(operando2_str);
                estado = ESTADOS.NUMERO;
            }
        }

        else if (
                id==R.id.b_0 || id==R.id.b_1 || id==R.id.b_2 ||
                        id==R.id.b_3 || id==R.id.b_4 || id==R.id.b_5 ||
                        id==R.id.b_6 || id==R.id.b_7 || id==R.id.b_8 ||
                        id==R.id.b_9
        ) {
            operando2_str += ((TextView) tecla).getText().toString();
            operando2 = miStringToDouble(operando2_str);
            display.setText(operando2_str);
            estado = ESTADOS.NUMERO;
        }

        else if (
                id == R.id.b_mas || id == R.id.b_menos ||
                        id == R.id.b_por || id == R.id.b_div ||
                        id == R.id.b_igual
        ) {
            // Si ya estamos en OPERACION, solo cambiamos la operación pendiente
            if (estado == ESTADOS.OPERACION) {
                if (id == R.id.b_mas) OP_ANT = OPERACION.SUMAR;
                else if (id == R.id.b_menos) OP_ANT = OPERACION.RESTAR;
                else if (id == R.id.b_por) OP_ANT = OPERACION.MULTIPLICAR;
                else if (id == R.id.b_div) OP_ANT = OPERACION.DIVIDIR;
                else if (id == R.id.b_igual) OP_ANT = OPERACION.IGUAL;
                return;
            }

            acumulador = operar(acumulador, OP_ANT, operando2);
            display.setText(miDoubleToString(acumulador));

            if (id == R.id.b_mas) OP_ANT = OPERACION.SUMAR;
            else if (id == R.id.b_menos) OP_ANT = OPERACION.RESTAR;
            else if (id == R.id.b_por) OP_ANT = OPERACION.MULTIPLICAR;
            else if (id == R.id.b_div) OP_ANT = OPERACION.DIVIDIR;
            else if (id == R.id.b_igual) OP_ANT = OPERACION.IGUAL;

            operando2_str = "";
            operando2 = 0.;
            estado = ESTADOS.OPERACION;
        }

        else if ( id == R.id.b_signo ) {
            if (estado == ESTADOS.NUMERO) {
                operando2 = -operando2;
                if (operando2_str.startsWith("-")) {
                    operando2_str = operando2_str.substring(1);
                } else {
                    operando2_str = "-" + operando2_str;
                }
                display.setText(operando2_str);
            } else {
                acumulador = -acumulador;
                display.setText(miDoubleToString(acumulador));
            }
        }

        else if ( id == R.id.clear ) {
            if (estado == ESTADOS.NUMERO) {
                operando2_str = "";
                operando2 = 0.;
                display.setText("0");
            } else {
                aINIC();
            }
        }
    }

    private double operar (double operando1, OPERACION operacion, double operando2) {
        switch (operacion) {
            case SUMAR:       return operando1 + operando2;
            case RESTAR:      return operando1 - operando2;
            case MULTIPLICAR: return operando1 * operando2;
            case DIVIDIR:     return operando1 / operando2;
            case IGUAL:       return ((operando2_str.equals("")) ? operando1 : operando2);
        }
        return 0.;
    }

    private double miStringToDouble(String numero_string) {
        if (numero_string.equals(".") || numero_string.equals("0.")) {
            operando2_str = "0.";
            display.setText(operando2_str);
            return 0.;
        }
        try {
            return Double.parseDouble(numero_string);
        } catch (NumberFormatException e) {
            return 0.;
        }
    }

    private String miDoubleToString(double numero_double) {
        return (df.format(numero_double));
    }
}