package com.example.calculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import static android.content.Context.VIBRATOR_SERVICE;

public class BasicFragment extends Fragment implements View.OnClickListener {
    private final int SCALE = 18;
    private final int ROUNDING_MODE = BigDecimal.ROUND_HALF_EVEN;
    private final MathContext RANGE = MathContext.DECIMAL128;
    private EditText result;
    private TextView pendingResult;
    private boolean mode;
    private boolean solved;
    private String[] history;
    private Animation buttonPress;
    private Button b00, b01, b02, b03,
                   b10, b11, b12, b13,
                   b20, b21, b22, b23,
                   b30, b31, b32, b33,
                   b40, b41, b42, b43,
                   b50, b51, b52, b53;

    public BasicFragment() {}

    public static BasicFragment newInstance() { return new BasicFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic, container, false);

        //back button ends activity
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                getActivity().finish();
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });

        //equation and results edit text
        result = (EditText) view.findViewById(R.id.result);
        result.setShowSoftInputOnFocus(false);
        solved = false;

        //ensures that the error message is not computed
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result.getText().toString().equals("ERROR")) result.setText("");
                result.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
                solved = false;
            }
        });

        //copies current results text to clipboard
        result.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager cm =
                        (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("Result", result.getText().toString()
                        .trim()));
                Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                vibrate(40, 75);
                return true;
            }
        });

        //hinted result
        pendingResult = (TextView) view.findViewById(R.id.pending_result);

        //initialize history array
        history = new String[20];
        Arrays.fill(history, "");
        readFromFile();

        //buttons
        mode = true;
        b00 = (Button) view.findViewById(R.id.b00);
        b00.setOnClickListener(this);
        b00.setText(R.string.clear);
        b00.setTextSize(27);
        b00.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        b01 = (Button) view.findViewById(R.id.b01);
        b01.setOnClickListener(this);
        b01.setText(R.string.delete);
        b01.setTextSize(25);
        b02 = (Button) view.findViewById(R.id.b02);
        b02.setOnClickListener(this);
        b02.setText(R.string.answer);
        b02.setTextSize(25);
        b03 = (Button) view.findViewById(R.id.b03);
        b03.setOnClickListener(this);
        b03.setText(R.string.second);
        b03.setTextSize(25);
        b10 = (Button) view.findViewById(R.id.b10);
        b10.setOnClickListener(this);
        b10.setText(R.string.exponent);
        b10.setTextSize(25);
        b11 = (Button) view.findViewById(R.id.b11);
        b11.setOnClickListener(this);
        b11.setText(R.string.ee);
        b11.setTextSize(25);
        b12 = (Button) view.findViewById(R.id.b12);
        b12.setOnClickListener(this);
        b12.setText(R.string.parentheses);
        b12.setTextSize(25);
        b13 = (Button) view.findViewById(R.id.b13);
        b13.setOnClickListener(this);
        b13.setText(R.string.div_symbol);
        b13.setTextSize(35);
        b20 = (Button) view.findViewById(R.id.b20);
        b20.setOnClickListener(this);
        b20.setText(R.string.seven);
        b20.setTextSize(35);
        b21 = (Button) view.findViewById(R.id.b21);
        b21.setOnClickListener(this);
        b21.setText(R.string.eight);
        b21.setTextSize(35);
        b22 = (Button) view.findViewById(R.id.b22);
        b22.setOnClickListener(this);
        b22.setText(R.string.nine);
        b22.setTextSize(35);
        b23 = (Button) view.findViewById(R.id.b23);
        b23.setOnClickListener(this);
        b23.setText(R.string.mul_symbol);
        b23.setTextSize(35);
        b30 = (Button) view.findViewById(R.id.b30);
        b30.setOnClickListener(this);
        b30.setText(R.string.four);
        b30.setTextSize(35);
        b31 = (Button) view.findViewById(R.id.b31);
        b31.setOnClickListener(this);
        b31.setText(R.string.five);
        b31.setTextSize(35);
        b32 = (Button) view.findViewById(R.id.b32);
        b32.setOnClickListener(this);
        b32.setText(R.string.six);
        b32.setTextSize(35);
        b33 = (Button) view.findViewById(R.id.b33);
        b33.setOnClickListener(this);
        b33.setText(R.string.sub_symbol);
        b33.setTextSize(45);
        b40 = (Button) view.findViewById(R.id.b40);
        b40.setOnClickListener(this);
        b40.setText(R.string.one);
        b40.setTextSize(35);
        b41 = (Button) view.findViewById(R.id.b41);
        b41.setOnClickListener(this);
        b41.setText(R.string.two);
        b41.setTextSize(35);
        b42 = (Button) view.findViewById(R.id.b42);
        b42.setOnClickListener(this);
        b42.setText(R.string.three);
        b42.setTextSize(35);
        b43 = (Button) view.findViewById(R.id.b43);
        b43.setOnClickListener(this);
        b43.setText(R.string.add_symbol);
        b43.setTextSize(35);
        b50 = (Button) view.findViewById(R.id.b50);
        b50.setOnClickListener(this);
        b50.setText(R.string.pos_neg);
        b50.setTextSize(30);
        b51 = (Button) view.findViewById(R.id.b51);
        b51.setOnClickListener(this);
        b51.setText(R.string.zero);
        b51.setTextSize(35);
        b52 = (Button) view.findViewById(R.id.b52);
        b52.setOnClickListener(this);
        b52.setText(R.string.decimal);
        b52.setTextSize(40);
        b53 = (Button) view.findViewById(R.id.b53);
        b53.setOnClickListener(this);
        b53.setText(R.string.equal_symbol);
        b53.setTextSize(40);
        b53.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        b53.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.green));

        //clear history on long press
        b02.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mode) {
                    Arrays.fill(history, "");
                    writeToFile();
                    vibrate(40, 75);
                }
                return true;
            }
        });

        //initialize animation
        buttonPress = AnimationUtils.loadAnimation(getContext(), R.anim.button_press);

        return view;
    }

    /**
     * Listener for all of the calculator buttons.
     *
     * @param view current view
     */
    @Override
    public void onClick(View view) {
        vibrate(5, 50);
        view.findViewById(view.getId()).startAnimation(buttonPress);
        switch (view.getId()) {
            case R.id.b00:
                if (mode) {
                    result.setText("");
                    pendingResult.setText("");
                }
                else {
                    addStr("\u221a(", 2);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b01:
                if (mode)
                    removeChar();
                else {
                    addStr("\u00B3\u221a(", 2);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b02:
                if (mode) {
                    selectPrevAns();
                    break;
                }
                else {
                    addStr("2^(", 2);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b03:
                mode = !mode;
                switchMode();
                break;
            case R.id.b10:
                if (mode)
                    addStr("^(", 1);
                else {
                    addStr("sin(", 2);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b11:
                if (mode)
                    addStr("E", 1);
                else {
                    addStr("cos(", 2);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b12:
                if (mode)
                    addStr("(", 2);
                else {
                    addStr("tan(", 2);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b13:
                if (mode)
                    addStr("\u00f7", 1);
                else {
                    addStr("\u03C0", 2);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b20:
                if (mode)
                    addStr("7", 0);
                else {
                    addStr("asin(", 2);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b21:
                if (mode)
                    addStr("8", 0);
                else {
                    addStr("acos(", 2);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b22:
                if (mode) addStr("9", 0);
                else {
                    addStr("atan(", 2);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b23:
                if (mode)
                    addStr("\u00d7", 1);
                else {
                    addStr("%", 1);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b30:
                if (mode)
                    addStr("4", 0);
                else {
                    addStr("log(", 2);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b31:
                if (mode)
                    addStr("5", 0);
                else {
                    addStr("10^(", 2);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b32:
                if (mode)
                    addStr("6", 0);
                else {
                    addStr("ln(", 2);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b33:
                if (mode)
                    addStr("-", 1);
                else {
                    addStr("e^(", 2);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b40:
                if (mode)
                    addStr("1", 0);
                else {
                    addStr("k", 1);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b41:
                if (mode)
                    addStr("2", 0);
                else {
                    addStr("M", 1);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b42:
                if (mode)
                    addStr("3", 0);
                else {
                    addStr("G", 1);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b43:
                if (mode)
                    addStr("+", 1);
                else {
                    addStr("T", 1);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b50:
                if (mode)
                    addStr("(-", 2);
                else {
                    addStr("m", 1);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b51:
                if (mode)
                    addStr("0", 0);
                else {
                    addStr("\u00b5", 1);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b52:
                if (mode)
                    addStr(".", 0);
                else {
                    addStr("n", 1);
                    mode = !mode;
                    switchMode();
                }
                break;
            case R.id.b53:
                if (mode)
                    calculate();
                else {
                    addStr("p", 1);
                    mode = !mode;
                    switchMode();
                }
                break;
        }
    }

    /**
     * Switch button modes.
     */
    private void switchMode() {
        if (mode) {
            b00.setText(R.string.clear);
            b00.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            b00.setTextSize(27);
            b01.setText(R.string.delete);
            b01.setTextSize(25);
            b02.setText(R.string.answer);
            b02.setTextSize(25);
            b03.setText(R.string.second);
            b03.setTextSize(25);
            b10.setText(R.string.exponent);
            b10.setTextSize(25);
            b11.setText(R.string.ee);
            b11.setTextSize(25);
            b12.setText(R.string.parentheses);
            b12.setTextSize(25);
            b13.setText(R.string.div_symbol);
            b13.setTextSize(35);
            b20.setText(R.string.seven);
            b20.setTextSize(35);
            b21.setText(R.string.eight);
            b21.setTextSize(35);
            b22.setText(R.string.nine);
            b22.setTextSize(35);
            b23.setText(R.string.mul_symbol);
            b23.setTextSize(35);
            b30.setText(R.string.four);
            b30.setTextSize(35);
            b31.setText(R.string.five);
            b31.setTextSize(35);
            b32.setText(R.string.six);
            b32.setTextSize(35);
            b33.setText(R.string.sub_symbol);
            b33.setTextSize(45);
            b40.setText(R.string.one);
            b40.setTextSize(35);
            b41.setText(R.string.two);
            b41.setTextSize(35);
            b42.setText(R.string.three);
            b42.setTextSize(35);
            b43.setText(R.string.add_symbol);
            b43.setTextSize(35);
            b50.setText(R.string.pos_neg);
            b50.setTextSize(30);
            b51.setText(R.string.zero);
            b51.setTextSize(35);
            b52.setText(R.string.decimal);
            b52.setTextSize(40);
            b53.setText(R.string.equal_symbol);
            b53.setTextSize(40);
            b53.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            b53.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.green));
        }
        else {
            b00.setText(R.string.sqrt);
            b00.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
            b00.setTextSize(25);
            b01.setText(R.string.cbrt);
            b01.setTextSize(25);
            b02.setText(R.string.pow_two);
            b02.setTextSize(25);
            b03.setText(R.string.first);
            b03.setTextSize(25);
            b10.setText(R.string.sin);
            b10.setTextSize(25);
            b11.setText(R.string.cos);
            b11.setTextSize(25);
            b12.setText(R.string.tan);
            b12.setTextSize(25);
            b13.setText(R.string.pi);
            b13.setTextSize(25);
            b20.setText(R.string.asin);
            b20.setTextSize(25);
            b21.setText(R.string.acos);
            b21.setTextSize(25);
            b22.setText(R.string.atan);
            b22.setTextSize(25);
            b23.setText(R.string.mod);
            b23.setTextSize(25);
            b30.setText(R.string.log);
            b30.setTextSize(25);
            b31.setText(R.string.pow_ten);
            b31.setTextSize(25);
            b32.setText(R.string.ln);
            b32.setTextSize(25);
            b33.setText(R.string.e);
            b33.setTextSize(25);
            b40.setText(R.string.kilo);
            b40.setTextSize(25);
            b41.setText(R.string.mega);
            b41.setTextSize(25);
            b42.setText(R.string.giga);
            b42.setTextSize(25);
            b43.setText(R.string.terra);
            b43.setTextSize(25);
            b50.setText(R.string.milli);
            b50.setTextSize(25);
            b51.setText(R.string.micro);
            b51.setTextSize(25);
            b52.setText(R.string.nano);
            b52.setTextSize(25);
            b53.setText(R.string.pico);
            b53.setTextSize(25);
            b53.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
            b53.setBackgroundTintList(ContextCompat.getColorStateList(
                    getContext(), R.color.button_background));
        }
    }

    /**
     * Get a previous answer from history (max 20)
     */
    private void selectPrevAns() {
        final BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(R.layout.bottom_sheet_view);
        dialog.getWindow().setDimAmount((float) 0.25);

        BottomSheetListView lv = (BottomSheetListView) dialog.findViewById(R.id.listViewBtmSheet);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!history[i].equals("")) {
                    vibrate(5, 50);
                    String ans = history[i].substring(history[i].indexOf("\n") + 1);
                    addStr(ans, 0);
                    dialog.dismiss();
                }
            }
        });
        lv.setAdapter(new ArrayAdapter<String>(getContext(),
                R.layout.listview_wrapper, R.id.listview_item, history));
        dialog.show();
    }

    /**
     * Vibrates for certain length and amplitude.
     *
     * @param length length in ms
     * @param amplitude amplitude of vibration
     */
    private void vibrate(int length, int amplitude) {
        ((Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE))
                .vibrate(VibrationEffect.createOneShot(length,amplitude));
    }

    /**
     * Adds character at cursor.
     *
     * @param string The character to add.
     * @param type 0 for digit, 1 for basic operation, 2 for others
     */
    private void addStr(String string, int type) {
        //if previous answer in edit text
        if (solved) {
            result.setText("");
            pendingResult.setText("");
            solved = false;
        }

        //get cursor position and content of edit text
        int index = result.getSelectionStart();
        String str = result.getText().toString();
        StringBuilder sb = new StringBuilder();

        if (index > 0) {
            //check for parenthesis
            char prevChar = str.charAt(index - 1);
            if (string.equals("(") && getParenCount(str) > 0 &&
               ((prevChar >= '0' && prevChar <= '9') || prevChar == ')' || prevChar == '\u03c0' ||
                 prevChar == 'k' || prevChar == 'M' || prevChar == 'G' || prevChar == 'T' ||
                 prevChar == 'm' || prevChar == '\u00b5' || prevChar == 'n' || prevChar == 'p'))
                string = ")";
            //check if multiplication sign is needed before
            else if ((prevChar != '+' && prevChar != '-' && prevChar != '%' &&
                    prevChar != '\u00d7' && prevChar != '\u00f7' && prevChar != 'E') &&
                    (string.equals("(") && getParenCount(str) == 0) ||
                    (type == 2 && prevChar >= '0' && prevChar <= '9') ||
                    (type == 0 && prevChar == '\u03c0'))
                string = sb.append("\u00d7").append(string).toString();
            //add leading 0 to '.'
            else if (string.equals(".") &&
                    !(prevChar >= '0' && prevChar <= '9'))
                string = "0.";
        }
        else if (string.equals(".")) //add leading 0
            string = "0.";
        else if (type == 1) { //use last ans
            String lastAns = history[0].substring(history[0].indexOf("\n") + 1);
            if (!lastAns.equals(""))
                string = sb.append(lastAns).append(string).toString();
        }

        //string to output
        sb = new StringBuilder();
        str = sb.append(str.substring(0, index))
                .append(string)
                .append(str.substring(index)).toString();

        //adjust font size depending on length of string
        if (str.length() <= 16) {
            result.setText(str);
            result.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
            result.setSelection(index + string.length());
            result.setTextSize(40);
        }
        else {
            result.setText(str);
            result.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
            result.setSelection(index + string.length());
            result.setTextSize(32);
        }

        processPending();
    }

    /**
     * Updates the pending results text.
     */
    private void processPending() {
        try {
            //string to evaluate
            String raw = result.getText().toString();
            String evalStr = formatExpression(raw);
            if (!evalStr.trim().equals("")) {
                //try BigDecimal calculation
                double num = evalLow(evalStr).setScale(15, ROUNDING_MODE).doubleValue();
                //if value is large
                if (num >= Integer.MAX_VALUE) num = round(evalHigh(evalStr), 15);
                //remove decimals if none needed
                String ans = (num % 1 == 0 && num < Math.pow(10, 9))?
                        Integer.toString((int)(num)) : Double.toString(num);

                //update pending result
                pendingResult.setText(ans);
            }
            else
                pendingResult.setText("");
        } catch (Exception e) {
            pendingResult.setText("");
        }
    }

    /**
     * Counts the amount of '(' in string.
     *
     * @param str String to check.
     * @return Number of '(' in string.
     */
    private int getParenCount(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++)
            if (str.charAt(i) == '(')
                count++;
            else if (str.charAt(i) ==')')
                count--;
        return count;
    }

    /**
     * Deletes character at cursor.
     */
    private void removeChar() {
        int index = result.getSelectionStart();
        if (index > 0) {
            String str = result.getText().toString();
            str = str.substring(0, index - 1) + str.substring(index);
            result.setTextSize((str.length() <= 16)? 40 : 32);
            result.setText(str);
            result.setSelection(index - 1);
        }

        processPending();
    }

    /**
     * Calculates current expression and updates edit text.
     */
    private void calculate() {
        try {
            //string to evaluate
            String raw = result.getText().toString();
            String evalStr = formatExpression(raw);
            if (!evalStr.trim().equals("")) {
                //try BigDecimal calculation
                double num = evalLow(evalStr).setScale(15, ROUNDING_MODE).doubleValue();
                //if value is large
                if (num >= Integer.MAX_VALUE) num = round(evalHigh(evalStr), 15);
                //remove decimals if none needed
                String ans = (num % 1 == 0 && num < Math.pow(10, 9))?
                        Integer.toString((int)(num)) : Double.toString(num);

                //add to edit text and history array
                result.setTextSize((ans.length() <= 16)? 40 : 32);
                result.setText(ans);
                pendingResult.setText("");
                result.setTextColor(ContextCompat.getColor(getContext(), R.color.green_text));
                result.setSelection(ans.length());
                solved = true;
                StringBuilder sb = new StringBuilder();
                addHistory(sb.append(raw).append("=\n").append(ans).toString());
                writeToFile();
            }
        } catch (Exception e) {
            result.setText(R.string.error);
            result.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            solved = true;
        }
    }

    /**
     * Formats unicode characters to readable string for eval.
     *
     * @param raw Raw text from edit text.
     * @return Formatted string ready to be evaluated.
     */
    private String formatExpression(String raw) {
        String formatted = "";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < raw.length(); i++) {
            switch (raw.charAt(i)) {
                case '\u03c0': //pi
                    formatted = sb.append(Math.PI).toString();
                    break;
                case 'e': //e^(
                    formatted = sb.append("exp").toString();
                    i++;
                    break;
                case '\u221a': //sqrt symbol
                    formatted = sb.append("sqrt").toString();
                    break;
                case '\u00B3': //^3 for cbrt symbol
                    formatted = sb.append("cbrt").toString();
                    i++;
                    break;
                default:
                    formatted = sb.append(raw.charAt(i)).toString();
                    break;
            }
        }

        return formatted;
    }

    /**
     * Rounds double value to desired precision.
     *
     * @param value value to round
     * @param places places to round to
     * @return rounded decimal
     */
    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Shifts all array elements to the right and adds new element at position 0.
     *
     * @param str new element to add
     */
    private void addHistory(String str) {
        for (int i = history.length - 2; i >= 0; i--)
            history[i+1] = history[i];
        history[0] = str;
    }

    /**
     * Computes result for numbers less than (2^31)-1 for accuracy.
     *
     * @param str Expression to calculate.
     * @return The answer as a BigDecimal.
     */
    private BigDecimal evalLow(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length())? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            BigDecimal parse() {
                nextChar();
                BigDecimal x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            BigDecimal parseExpression() {
                BigDecimal x = parseTerm();
                while (true) {
                    if (eat('+')) x = x.add(parseTerm(), RANGE); //add
                    else if (eat('-')) x = x.subtract(parseTerm(), RANGE); //sub
                    else if (eat('%')) x = x.remainder(parseTerm()); //mod
                    else return x;
                }
            }

            BigDecimal parseTerm() {
                BigDecimal x = parseFactor();
                while (true) {
                    if (eat('\u00d7')) x = x.multiply(parseFactor(), RANGE); //mul
                    else if (eat('\u00f7')) x = x.divide(parseFactor(), RANGE); //div
                    else return x;
                }
            }

            BigDecimal parseFactor() {
                if (eat('+')) return parseFactor(); //unary plus
                if (eat('-')) return parseFactor().negate(); //unary minus

                BigDecimal x;
                int startPos = this.pos;
                if (eat('(')) { //parentheses
                    x = parseExpression();
                    eat(')');
                }
                else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = BigDecimal.valueOf(Double.parseDouble(str.substring(startPos, this.pos)));
                }
                else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    switch (func) {
                        case "sqrt": x = sqrt(x); break;
                        case "cbrt": x = cbrt(x); break;
                        case "log": x = log10(x); break;
                        case "ln": x = ln(x, SCALE); break;
                        case "exp": x = exp(x, SCALE); break;
                        case "sin": x = sin(x); break;
                        case "cos": x = cos(x); break;
                        case "tan": x = tan(x); break;
                        case "asin": x = BigDecimal.valueOf(Math.asin(x.doubleValue())); break;
                        case "acos": x = BigDecimal.valueOf(Math.acos(x.doubleValue())); break;
                        case "atan": x = BigDecimal.valueOf(Math.atan(x.doubleValue())); break;
                        default: throw new RuntimeException("Unknown function: " + func);
                    }
                }
                else throw new RuntimeException("Unexpected: " + (char)ch);

                if (eat('^')) x = pow(x, parseFactor()); //exponents
                else if (eat('E')) x = x.multiply(
                        pow(BigDecimal.valueOf(10), parseFactor())); //times 10 to the
                else if (eat('k')) x = x.multiply(
                        BigDecimal.valueOf(Math.pow(10, 3))); //kila
                else if (eat('M')) x = x.multiply(
                        BigDecimal.valueOf(Math.pow(10, 6))); //Mega
                else if (eat('G')) x = x.multiply(
                        BigDecimal.valueOf(Math.pow(10, 9))); //Giga
                else if (eat('T')) x = x.multiply(
                        BigDecimal.valueOf(Math.pow(10, 12))); //Terra
                else if (eat('m')) x = x.divide(
                        BigDecimal.valueOf(Math.pow(10, 3))); //milli
                else if (eat('\u00b5')) x = x.divide(
                        BigDecimal.valueOf(Math.pow(10, 6))); //micro
                else if (eat('n')) x = x.divide(
                        BigDecimal.valueOf(Math.pow(10, 9))); //nano
                else if (eat('p')) x = x.divide(
                        BigDecimal.valueOf(Math.pow(10, 12))); //pico

                return x;
            }
        }.parse();
    }

    /**
     * Conputes result for numbers greater than or equal to (2^31) to raise cap.
     *
     * @param str Expression to calculate.
     * @return The result as a double.
     */
    private double evalHigh(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() { ch = (++pos < str.length()) ? str.charAt(pos) : -1; }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else if (eat('%')) x %= parseTerm(); //mod
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (eat('\u00d7')) x *= parseFactor(); // multiplication
                    else if (eat('\u00f7')) x /= parseFactor(); // division
                    else if (eat('k')) x *= Math.pow(10, 3); //kila
                    else if (eat('M')) x *= Math.pow(10, 6); //Mega
                    else if (eat('G')) x *= Math.pow(10, 9); //Giga
                    else if (eat('T')) x *= Math.pow(10, 12); //Terra
                    else if (eat('m')) x /= Math.pow(10, 3); //milli
                    else if (eat('\u00b5')) x /= Math.pow(10, 6); //micro
                    else if (eat('n')) x /= Math.pow(10, 9); //nano
                    else if (eat('p')) x /= Math.pow(10, 12); //pico
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                }
                else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                }
                else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    switch (func) {
                        case "sqrt": x = Math.sqrt(x); break;
                        case "cbrt": x = Math.cbrt(x); break;
                        case "log": x = Math.log10(x); break;
                        case "ln": x = Math.log(x); break;
                        case "exp": x = Math.exp(x); break;
                        case "sin": x = Math.sin(x); break;
                        case "cos": x = Math.cos(x); break;
                        case "tan": x = Math.tan(x); break;
                        case "asin": x = Math.asin(x); break;
                        case "acos": x = Math.acos(x); break;
                        case "atan": x = Math.atan(x); break;
                        default: throw new RuntimeException("Unknown function: " + func);
                    }
                }
                else throw new RuntimeException("Unexpected: " + (char)ch);

                if (eat('^')) x = Math.pow(x, parseFactor());
                if (eat('E')) x *= Math.pow(10, parseFactor());

                return x;
            }
        }.parse();
    }

    /**
     * Helper method to calculate the square root of a BigDecimal.
     *
     * @param x number to calculate
     * @return square root of the input
     */
    private BigDecimal sqrt(BigDecimal x) {
        if (x.signum() < 0) throw new ArithmeticException("x < 0");

        BigInteger n = x.movePointRight(SCALE << 1).toBigInteger();
        int bits = (n.bitLength() + 1) >> 1;
        BigInteger ix = n.shiftRight(bits);
        BigInteger ixPrev;

        do {
            ixPrev = ix;
            ix = ix.add(n.divide(ix)).shiftRight(1);
            Thread.yield();
        } while (ix.compareTo(ixPrev) != 0);

        return new BigDecimal(ix, SCALE);
    }

    /**
     * Helper method to calculate cube root of a BigDecimal.
     *
     * @param b number to calculate
     * @return cube root of the input
     */
    public static BigDecimal cbrt(BigDecimal b) {
        MathContext mc = new MathContext(40);
        BigDecimal x = new BigDecimal("1", mc);

        for (int i = 0; i < 1000; i++)
            x = x.subtract(x.pow(3, mc).subtract(b, mc)
                    .divide(new BigDecimal("3", mc).multiply(x.pow(2, mc), mc), mc), mc);

        return x;
    }

    /**
     * Helper method to calculate the natural log of a BigDecimal.
     *
     * @param x number to calculate
     * @param scale scale of ln
     * @return natural log of the input
     */
    private BigDecimal ln(BigDecimal x, int scale) {
        if (x.signum() <= 0) throw new IllegalArgumentException("x <= 0");

        int magnitude = x.toString().length() - x.scale() - 1;

        if (magnitude < 3) return lnNewton(x, scale);
        else {
            BigDecimal root = intRoot(x, magnitude, scale);
            BigDecimal lnRoot = lnNewton(root, scale);
            return BigDecimal.valueOf(magnitude).multiply(lnRoot)
                    .setScale(scale, BigDecimal.ROUND_HALF_EVEN);
        }
    }

    /**
     * Helper method to calculate log base 10 of a BigDecimal.
     *
     * @param b number to calculate
     * @return log10 of the input
     */
    private BigDecimal log10(BigDecimal b) {
        final int NUM_OF_DIGITS = SCALE + 2;
        MathContext mc = new MathContext(NUM_OF_DIGITS, RoundingMode.HALF_EVEN);

        if (b.signum() <= 0) throw new ArithmeticException("log of a negative number! (or zero)");
        else if(b.compareTo(BigDecimal.ONE) == 0) return BigDecimal.ZERO;
        else if(b.compareTo(BigDecimal.ONE) < 0)
            return (log10((BigDecimal.ONE).divide(b,mc))).negate();

        StringBuilder sb = new StringBuilder();
        int leftDigits = b.precision() - b.scale();
        sb.append(leftDigits - 1).append(".");

        int n = 0;
        while (n < NUM_OF_DIGITS) {
            b = (b.movePointLeft(leftDigits - 1)).pow(10, mc);
            leftDigits = b.precision() - b.scale();
            sb.append(leftDigits - 1);
            n++;
        }

        BigDecimal ans = new BigDecimal(sb.toString());
        ans = ans.round(new MathContext(
                ans.precision() - ans.scale() + SCALE, RoundingMode.HALF_EVEN));
        return ans;
    }

    /**
     * Helper method that use's Newton's algorithm to calculate ln.
     *
     * @param x input as a BigDecimal
     * @param scale scale of ln
     * @return result of the algorithm
     */
    private BigDecimal lnNewton(BigDecimal x, int scale) {
        int sp1 = scale + 1;
        BigDecimal n = x;
        BigDecimal term;
        BigDecimal tolerance = BigDecimal.valueOf(5).movePointLeft(sp1);

        do {
            BigDecimal eToX = exp(x, sp1);
            term = eToX.subtract(n).divide(eToX, sp1, BigDecimal.ROUND_DOWN);
            x = x.subtract(term);
            Thread.yield();
        } while (term.compareTo(tolerance) > 0);

        return x.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
    }

    /**
     * Helper method to calculate integral root using Newton's algorithm.
     *
     * @param x input as a BigDecimal
     * @param index index of integral root value
     * @param scale scale of result
     * @return integral root of the input
     */
    private BigDecimal intRoot(BigDecimal x, long index, int scale) {
        if (x.signum() < 0) throw new IllegalArgumentException("x < 0");

        int sp1 = scale + 1;
        BigDecimal n = x;
        BigDecimal i = BigDecimal.valueOf(index);
        BigDecimal im1 = BigDecimal.valueOf(index-1);
        BigDecimal tolerance = BigDecimal.valueOf(5).movePointLeft(sp1);
        BigDecimal xPrev;
        x = x.divide(i, scale, BigDecimal.ROUND_HALF_EVEN);

        do {
            BigDecimal xToIm1 = intPower(x, index-1, sp1);
            BigDecimal xToI = x.multiply(xToIm1).setScale(sp1, BigDecimal.ROUND_HALF_EVEN);
            BigDecimal numerator =
                    n.add(im1.multiply(xToI)).setScale(sp1, BigDecimal.ROUND_HALF_EVEN);
            BigDecimal denominator = i.multiply(xToIm1).setScale(sp1, BigDecimal.ROUND_HALF_EVEN);
            xPrev = x;
            x = numerator.divide(denominator, sp1, BigDecimal.ROUND_DOWN);
            Thread.yield();
        } while (x.subtract(xPrev).abs().compareTo(tolerance) > 0);

        return x;
    }

    /**
     * Helper method to calculate x^exponent.
     *
     * @param x root of exponent
     * @param exponent power to raise to
     * @param scale scale of the result
     * @return result of the inputs
     */
    private BigDecimal intPower(BigDecimal x, long exponent, int scale) {
        if (exponent < 0) {
            return BigDecimal.valueOf(1)
                    .divide(intPower(x, -exponent, scale), scale,
                            BigDecimal.ROUND_HALF_EVEN);
        }
        BigDecimal power = BigDecimal.valueOf(1);

        while (exponent > 0) {
            if ((exponent & 1) == 1)
                power = power.multiply(x).setScale(scale, BigDecimal.ROUND_HALF_EVEN);

            x = x.multiply(x).setScale(scale, BigDecimal.ROUND_HALF_EVEN);
            exponent >>= 1;
            Thread.yield();
        }

        return power;
    }

    /**
     * Helper method to calculate the power of a BigDecimal.
     *
     * @param savedValue base
     * @param value exponent
     * @return result of the inputs
     */
    private BigDecimal pow(BigDecimal savedValue, BigDecimal value) {
        BigDecimal result = null;
        result = exp(ln(savedValue, 32).multiply(value), 32);
        return result;
    }

    /**
     * Helper method to calculate e^x of a BigDecimal using Taylor's formula.
     *
     * @param x power to raise to
     * @param scale scale of the result
     * @return result of the input
     */
    private BigDecimal exp(BigDecimal x, int scale) {
        if (x.signum() == 0) return BigDecimal.valueOf(1);

        else if (x.signum() == -1) {
            return BigDecimal.valueOf(1).divide(exp(x.negate(), scale), scale,
                    BigDecimal.ROUND_HALF_EVEN);
        }

        BigDecimal xWhole = x.setScale(0, BigDecimal.ROUND_DOWN);

        if (xWhole.signum() == 0) return expTaylor(x, scale);

        BigDecimal xFraction = x.subtract(xWhole);
        BigDecimal z = BigDecimal.valueOf(1)
                .add(xFraction.divide(xWhole, scale, BigDecimal.ROUND_HALF_EVEN));
        BigDecimal t = expTaylor(z, scale);
        BigDecimal maxLong = BigDecimal.valueOf(Long.MAX_VALUE);
        BigDecimal result  = BigDecimal.valueOf(1);

        while (xWhole.compareTo(maxLong) >= 0) {
            result = result.multiply(
                    intPower(t, Long.MAX_VALUE, scale))
                    .setScale(scale, BigDecimal.ROUND_HALF_EVEN);
            xWhole = xWhole.subtract(maxLong);
            Thread.yield();
        }
        return result.multiply(intPower(t, xWhole.longValue(), scale))
                .setScale(scale, BigDecimal.ROUND_HALF_EVEN);
    }

    /**
     * Helper method using Taylor series.
     *
     * @param x value as a BigDecimal
     * @param scale scale of result
     * @return result of series
     */
    private BigDecimal expTaylor(BigDecimal x, int scale) {
        BigDecimal factorial = BigDecimal.valueOf(1);
        BigDecimal xPower    = x;
        BigDecimal sumPrev;
        BigDecimal sum  = x.add(BigDecimal.valueOf(1));
        int i = 2;

        do {
            xPower = xPower.multiply(x).setScale(scale, BigDecimal.ROUND_HALF_EVEN);
            factorial = factorial.multiply(BigDecimal.valueOf(i));
            BigDecimal term = xPower.divide(factorial, scale, BigDecimal.ROUND_HALF_EVEN);
            sumPrev = sum;
            sum = sum.add(term);
            ++i;
            Thread.yield();
        } while (sum.compareTo(sumPrev) != 0);

        return sum;
    }

    /**
     * Helper method to calculate the sin of a BigDecimal.
     *
     * @param x number to calculate
     * @return sine of the input
     */
    public BigDecimal sin(BigDecimal x) {
        BigDecimal lastVal = x.add(BigDecimal.ONE);
        BigDecimal currentValue = x;
        BigDecimal xSquared = x.multiply(x);
        BigDecimal numerator = x;
        BigDecimal denominator = BigDecimal.ONE;
        int i = 0;

        while (lastVal.compareTo(currentValue) != 0) {
            lastVal = currentValue;
            int z = 2 * i + 3;
            denominator = denominator.multiply(BigDecimal.valueOf(z));
            denominator = denominator.multiply(BigDecimal.valueOf(z - 1));
            numerator   = numerator.multiply(xSquared);
            BigDecimal term = numerator.divide(denominator, SCALE + 5, ROUNDING_MODE);
            if (i % 2 == 0) currentValue = currentValue.subtract(term);
            else currentValue = currentValue.add(term);
            i++;
        }
        return currentValue;
    }

    /**
     * Helper method to calculate the cos of a BigDecimal
     *
     * @param x number to calculate
     * @return cosine of the input
     */
    private BigDecimal cos(BigDecimal x) {
        BigDecimal currentValue = BigDecimal.ONE;
        BigDecimal lastVal = currentValue.add(BigDecimal.ONE);
        BigDecimal xSquared = x.multiply(x);
        BigDecimal numerator = BigDecimal.ONE;
        BigDecimal denominator = BigDecimal.ONE;
        int i = 0;

        while (lastVal.compareTo(currentValue) != 0) {
            lastVal = currentValue;
            int z = 2 * i + 2;
            denominator = denominator.multiply(BigDecimal.valueOf(z));
            denominator = denominator.multiply(BigDecimal.valueOf(z - 1));
            numerator   = numerator.multiply(xSquared);
            BigDecimal term = numerator.divide(denominator, SCALE + 5, ROUNDING_MODE);
            if (i % 2 == 0) currentValue = currentValue.subtract(term);
            else currentValue = currentValue.add(term);
            i++;
        }

        return currentValue;
    }

    /**
     * Helper method to calculate the tan of a BigDecimal.
     *
     * @param x number to calculate
     * @return tangent of the input
     */
    private BigDecimal tan(BigDecimal x) {
        BigDecimal sin = sin(x);
        BigDecimal cos = cos(x);
        return sin.divide(cos, SCALE, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Saves the current history array to file
     */
    private void writeToFile() {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    getContext().openFileOutput("history.txt", Context.MODE_PRIVATE));

            for (String data : history) {
                if (data.equals("")) break;
                outputStreamWriter.write(data.replaceAll("\n", "") + "\n");
            }

            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /**
     * Uses file contents to fill history array.
     */
    private void readFromFile() {
        try {
            InputStream inputStream = getContext().openFileInput("history.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String data;
                StringBuilder sb = new StringBuilder();

                int index = 0;
                while ((data = bufferedReader.readLine()) != null) {
                    String formattedData = sb.append(data).toString();
                    formattedData = formattedData.substring(0, formattedData.indexOf("=") + 1) +
                            "\n" + formattedData.substring(formattedData.indexOf("=") + 1);
                    history[index] = formattedData;
                    index++;
                    sb = new StringBuilder();
                }

                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e("Exception", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Exception", "Can not read file: " + e.toString());
        }
    }
}
