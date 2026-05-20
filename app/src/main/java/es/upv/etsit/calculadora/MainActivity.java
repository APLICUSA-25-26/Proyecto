package es.upv.etsit.calculadora;

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
    /** Operacion anterior pendiente de realizar */
    OPERACION OP_ANT;

    /** Clase enumerada de estados de la calculadora */
    enum ESTADOS {INIC, NUMERO, OPERACION}
    /** Estado en el que se está */
    ESTADOS estado;

    /** Acumulador de operaciones: operando1 */
    double acumulador;

    /** operando2 de las operaciones en format String para escribir en pantalla y double para hacer operaciones */
    String operando2_str;
    double operando2;


    /** TextView para escribir resultados y operandos en pantalla */
    TextView display;

    /** Para fijar formato en salida de pantalla */
    DecimalFormat df;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Enlazamos el TextView en variable display para pantalla de calculadora
        display = (TextView)findViewById(R.id.display);

        // Formato de salida de los resultados
        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator('.');
        df = new DecimalFormat("#.#############", decimalFormatSymbols);

        // Inicialización de calculadora
        aINIC();
    }

    /**
     * Inicialización
     */
    private void aINIC() {
        operando2_str   = "";     // String de formación de operando2 vacío
        display.setText("0");    // Visualización en TextView display
        operando2 = 0.;           // operando2 double
        acumulador = 0.;          // valor inicial del acumulador
        OP_ANT = OPERACION.IGUAL; // operacion_anterior pendiente: igual
        estado = ESTADOS.INIC;    // estamos en el estado INIC.
    }

    /**
     * Método de call-back ejecutado cuando se clica alguno de los TextView en los que se
     * ha programado el atributo android:onClick con alPulsarTecla.
     *
     * A partir del TextView tecla se extrae su id y se compara con todos los posibles id mediante la
     * clase R. Las comparaciones se hacen en una estructura switch, agrupándose los distintos cases
     * según funcionalidad común
     *
     * @param tecla TextView que ha sido clicado
     */
    public void alPulsarTecla(View tecla){

        int id = tecla.getId(); // id de la tecla pulsada


        // Comparación del id con punto decimal

        if ( id == R.id.b_punto ) {
            //// Control de si se ha pulsado previamente el .

                operando2_str += ((TextView) tecla).getText().toString();  // acumulación de digito o punto
                operando2 = miStringToDouble(operando2_str); // conversión de String a double
                display.setText(operando2_str); // Visualización
                estado = ESTADOS.NUMERO; // estamos en estado NUMERO

        }

        // Comparación del id de la tecla pulsada con los id de dígitos,
        else if (
                id==R.id.b_0 ||
                        id==R.id.b_1 ||
                        id==R.id.b_2 ||
                        id==R.id.b_3 ||
                        id==R.id.b_4 ||
                        id==R.id.b_5 ||
                        id==R.id.b_6 ||
                        id==R.id.b_7 ||
                        id==R.id.b_8 ||
                        id==R.id.b_9
        ) {
            operando2_str += ((TextView) tecla).getText().toString();  // acumulación de digito o punto
            operando2 = miStringToDouble(operando2_str); // conversión de String a double
            display.setText(operando2_str); // Visualización
            estado = ESTADOS.NUMERO; // estamos en estado NUMERO
        }

        // Comparación del id de la tecla pulsada con los id de las operaciones
        else if (
                id == R.id.b_mas ||
                        id == R.id.b_menos ||
                        id == R.id.b_por ||
                        id == R.id.b_div ||
                        id == R.id.b_igual
        ) {

            // Distinción del estado en el que se encuentra: NUMERO u OPERACION


                acumulador = operar(acumulador, OP_ANT, operando2); // opera acumulador y operando2 según la operacion anterior (OP_ANT) pendiente
                display.setText(miDoubleToString(acumulador)); // visualiza el resultado (acumulador)

                // guarda operacion actual en la operacion anterior (OP_ANT)
                if (id == R.id.b_mas) {
                    OP_ANT = OPERACION.SUMAR;
                } else if (id == R.id.b_menos) {
                    OP_ANT = OPERACION.RESTAR;
                } else if (id == R.id.b_por) {
                    OP_ANT = OPERACION.MULTIPLICAR;
                } else if (id == R.id.b_div) {
                    OP_ANT = OPERACION.DIVIDIR;
                } else if (id == R.id.b_igual) {
                    OP_ANT = OPERACION.IGUAL;
                }

                // tras acabar operación, se reinicializa operando2
                operando2_str = "";
                operando2 = 0.;

                estado = ESTADOS.OPERACION; // estamos en estado OPERACION

            // ¿Operación anterior???
            // ¿Nuevo estado?
        }

        // Comparación con el id de cambio de signo +/-
        else if ( id == R.id.b_signo ) {
            ///
        }

        // Comparación con el id de la tecla C clear
        else if ( id == R.id.clear ) {
            ///

        }

    }

    /**
     * Devuelve el resultado de la operación entre dos operandos
     *
     * @param operando1 el primer operando double
     * @param operacion operación como valor enumerado: OPERACION.SUMAR, OPERACION.RESTAR, ...
     * @param operando2 el segundo operando double
     * @return operando1 OPERACION operando2
     *
     */
    private double operar (double operando1, OPERACION operacion, double operando2) {
        switch (operacion) {
            case SUMAR:       return operando1 + operando2;
            case RESTAR:      return operando1 - operando2;
            case MULTIPLICAR: return operando1 * operando2;
            case DIVIDIR:     return operando1 / operando2;
            case IGUAL:       return ( (operando2_str.equals(""))? operando1 : operando2 ); // Si operando2_str=="",
            // no existe todavía operando2
        }
        return 0.; // indiferente
    }

    /**
     * Convierte a double un String
     *
     * @param numero_string String que representa a un número
     * @return un double desde un String
     */
    private double miStringToDouble(String numero_string) {
        if (numero_string.equals(".")) {
            operando2_str="0.";
            display.setText(operando2_str);
            return 0.;
        }
        return Double.parseDouble(numero_string);
    }

    /**
     * Devuelve un String con formato desde un double
     *
     * @param numero_double double a convertir a String
     * @return String convertido del parámetro
     */
    private String miDoubleToString(double numero_double) {
        return (df.format(numero_double));
    }

}