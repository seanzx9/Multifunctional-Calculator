package com.example.calculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import static android.content.Context.VIBRATOR_SERVICE;

public class ConvertFragment extends Fragment {
    private final int ROUNDING_MODE = BigDecimal.ROUND_HALF_EVEN;
    private final MathContext RANGE = MathContext.DECIMAL128;
    private int operation;
    private View view;
    private RadioGroup radioRow1;
    private RadioGroup.OnCheckedChangeListener row1 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            //remove other checked box if checked
            radioRow2.setOnCheckedChangeListener(null);
            radioRow3.setOnCheckedChangeListener(null);
            radioRow2.clearCheck();
            radioRow3.clearCheck();
            radioRow2.setOnCheckedChangeListener(row2);
            radioRow3.setOnCheckedChangeListener(row3);
            vibrate(5, 10);

            switch (i) {
                case R.id.temperature:
                    operation = 0;

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.temperature, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input1Type.setAdapter(adapter);

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.temperature, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input2Type.setAdapter(adapter);
                    input2Type.setSelection(1);

                    handleEmpty("70");
                    calculate(R.id.input1);

                    break;
                case R.id.angle:
                    operation = 1;

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.angle, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input1Type.setAdapter(adapter);

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.angle, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input2Type.setAdapter(adapter);
                    input2Type.setSelection(1);

                    handleEmpty("1");
                    calculate(R.id.input1);

                    break;
                case R.id.mass:
                    operation = 2;

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.mass, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input1Type.setAdapter(adapter);
                    input1Type.setSelection(1);

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.mass, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input2Type.setAdapter(adapter);
                    input2Type.setSelection(6);

                    handleEmpty("1");
                    calculate(R.id.input1);

                    break;
            }
            RadioButton bt = (RadioButton) view.findViewById(radioRow1.getCheckedRadioButtonId());
            bt.startAnimation(buttonPress);
        }
    };
    private RadioGroup radioRow2;
    private RadioGroup.OnCheckedChangeListener row2 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            //remove other checked box if checked
            radioRow1.setOnCheckedChangeListener(null);
            radioRow3.setOnCheckedChangeListener(null);
            radioRow1.clearCheck();
            radioRow3.clearCheck();
            radioRow1.setOnCheckedChangeListener(row1);
            radioRow3.setOnCheckedChangeListener(row3);
            vibrate(5, 10);

            switch (i) {
                case R.id.length:
                    operation = 3;

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.length, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input1Type.setAdapter(adapter);
                    input1Type.setSelection(1);

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.length, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input2Type.setAdapter(adapter);
                    input2Type.setSelection(8);

                    handleEmpty("1");
                    calculate(R.id.input1);

                    break;
                case R.id.area:
                    operation = 4;

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.area, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input1Type.setAdapter(adapter);
                    input1Type.setSelection(1);

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.area, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input2Type.setAdapter(adapter);
                    input2Type.setSelection(3);

                    handleEmpty("1");
                    calculate(R.id.input1);

                    break;
                case R.id.volume:
                    operation = 5;

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.volume, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input1Type.setAdapter(adapter);

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.volume, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input2Type.setAdapter(adapter);
                    input2Type.setSelection(6);

                    handleEmpty("1");
                    calculate(R.id.input1);

                    break;
            }
            RadioButton bt = (RadioButton) view.findViewById(radioRow2.getCheckedRadioButtonId());
            bt.startAnimation(buttonPress);
        }
    };
    private RadioGroup radioRow3;
    private RadioGroup.OnCheckedChangeListener row3 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            //remove other checked box if checked
            radioRow1.setOnCheckedChangeListener(null);
            radioRow2.setOnCheckedChangeListener(null);
            radioRow1.clearCheck();
            radioRow2.clearCheck();
            radioRow1.setOnCheckedChangeListener(row1);
            radioRow2.setOnCheckedChangeListener(row2);
            vibrate(5, 10);

            switch (i) {
                case R.id.speed:
                    operation = 6;

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.speed, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input1Type.setAdapter(adapter);

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.speed, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input2Type.setAdapter(adapter);
                    input2Type.setSelection(2);

                    handleEmpty("60");
                    calculate(R.id.input1);

                    break;
                case R.id.time:
                    operation = 7;

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.time, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input1Type.setAdapter(adapter);
                    input1Type.setSelection(3);

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.time, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input2Type.setAdapter(adapter);
                    input2Type.setSelection(4);

                    handleEmpty("60");
                    calculate(R.id.input1);

                    break;
                case R.id.currency:
                    operation = 8;

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.currency, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input1Type.setAdapter(adapter);

                    adapter = ArrayAdapter.createFromResource(getContext(),
                            R.array.currency, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    input2Type.setAdapter(adapter);
                    input2Type.setSelection(1);

                    handleEmpty("1");
                    calculate(R.id.input1);

                    break;
            }
            RadioButton bt = (RadioButton) view.findViewById(radioRow3.getCheckedRadioButtonId());
            bt.startAnimation(buttonPress);
        }
    };
    private Spinner input1Type;
    private Spinner input2Type;
    private ArrayAdapter<CharSequence> adapter;
    private TextInputEditText input1;
    private TextWatcher input1List = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            updateInput1(charSequence, this);
            calculate(R.id.input1);
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {}
    };
    private TextInputEditText input2;
    private TextWatcher input2List = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            updateInput2(charSequence, this);
            calculate(R.id.input2);
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {}
    };
    private static HashMap<String, BigDecimal> curList;
    private Animation buttonPress;

    public ConvertFragment() {}

    public static ConvertFragment newInstance() { return new ConvertFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_convert, container, false);

        //update with today's currency rates
        loadRatesFromXml();

        //initialize radio groups
        radioRow1 = (RadioGroup) view.findViewById(R.id.radio_row1);
        radioRow2 = (RadioGroup) view.findViewById(R.id.radio_row2);
        radioRow3 = (RadioGroup) view.findViewById(R.id.radio_row3);

        //initialize input 1 spinner
        input1Type = (Spinner) view.findViewById(R.id.input1_type);
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.temperature, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        input1Type.setAdapter(adapter);

        //initialize input 2 spinner
        input2Type = (Spinner) view.findViewById(R.id.input2_type);
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.temperature, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        input2Type.setAdapter(adapter);
        input2Type.setSelection(1);

        //initialize animations
        buttonPress = AnimationUtils.loadAnimation(getContext(), R.anim.button_press);

        //set radio group listeners
        radioRow1.setOnCheckedChangeListener(row1);
        radioRow2.setOnCheckedChangeListener(row2);
        radioRow3.setOnCheckedChangeListener(row3);

        //initialize input1
        input1 = (TextInputEditText) view.findViewById(R.id.input1_edit);

        //set Done button on input1 keyboard
        input1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                    input1.clearFocus();
                return false;
            }
        });

        //handle input1 long press
        input1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                vibrate(40, 50);
                ClipboardManager cm =
                        (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("Value", input1.getText().toString()
                        .trim()));
                Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //initialize input2
        input2 = (TextInputEditText) view.findViewById(R.id.input2_edit);

        //set Done button on input2 keyboard
        input2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                    input2.clearFocus();
                return false;
            }
        });

        //handle input2 long press
        input2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                vibrate(40, 50);
                ClipboardManager cm =
                        (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("Value", input2.getText().toString()
                        .trim()));
                Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //listen for changing text on inputs
        input1.addTextChangedListener(input1List);
        input2.addTextChangedListener(input2List);

        //listen for changes in input1 type
        input1Type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView,
                                       int position,
                                       long id) {
                calculate(R.id.input1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}

        });

        //listen for changes in input2 type
        input2Type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView,
                                       int position,
                                       long id) {
                calculate(R.id.input1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        //start with temp conversion
        operation = 0;
        handleEmpty("70");
        calculate(R.id.input1);

        return view;
    }

    /**
     * Adds text to input1.
     *
     * @param charSequence EditText content
     * @param textWatcher current TextWatcher
     */
    private void updateInput1(CharSequence charSequence, TextWatcher textWatcher) {
        input1.removeTextChangedListener(textWatcher);

        String str = charSequence.toString().trim();

        if (str.equals("")) {
            input1.setText("0");
            input1.setSelection(1);
        }
        else if (str.charAt(0) == '0' && (str.length() > 1 && str.charAt(1) != '.')) {
            input1.setText(charSequence.toString().substring(1));
            input1.setSelection(input1.getText().toString().length());
        }

        input1.addTextChangedListener(textWatcher);
    }

    /**
     * Adds text to input2.
     *
     * @param charSequence EditText content
     * @param textWatcher current TextWatcher
     */
    private void updateInput2(CharSequence charSequence, TextWatcher textWatcher) {
        input2.removeTextChangedListener(textWatcher);

        String str = charSequence.toString().trim();

        if (str.equals("")) {
            input2.setText("0");
            input2.setSelection(1);
        }
        else if (str.charAt(0) == '0' && (str.length() > 1 && str.charAt(1) != '.')) {
            input2.setText(charSequence.toString().substring(1));
            input2.setSelection(input2.getText().toString().length());
        }

        input2.addTextChangedListener(textWatcher);
    }

    /**
     * Calculates the result based on current operation.
     *
     * @param editTextId id of EditText to take input from
     */
    private void calculate(int editTextId) {
        try {
            switch (operation) {
                case 0:
                    convertTemp(editTextId);
                    break;
                case 1:
                    convertAngle(editTextId);
                    break;
                case 2:
                    convertMass(editTextId);
                    break;
                case 3:
                    convertLength(editTextId);
                    break;
                case 4:
                    convertArea(editTextId);
                    break;
                case 5:
                    convertVolume(editTextId);
                    break;
                case 6:
                    convertSpeed(editTextId);
                    break;
                case 7:
                    convertTime(editTextId);
                    break;
                case 8:
                    convertCurrency(editTextId);
                    break;
            }
        } catch (Exception e) {
            input1.setText("");
            input2.setText("");
        }
    }

    /**
     * Converts temperature values.
     *
     * @param editTextId id of changing EditText
     */
    private void convertTemp(int editTextId) {
        BigDecimal orig, mid = new BigDecimal(0), con = new BigDecimal(0);

        //if input1 is being changed
        if (editTextId == R.id.input1) {
            //value inside input
            orig = new BigDecimal(input1.getText().toString());

            //convert all inputs to C
            switch (input1Type.getSelectedItem().toString()) {
                case "Fahrenheit":
                    mid = (orig.subtract(BigDecimal.valueOf(32.0), RANGE))
                            .multiply(BigDecimal.valueOf(5.0 / 9.0), RANGE);
                    break;
                case "Celsius":
                    mid = orig;
                    break;
                case "Kelvin":
                    mid = orig.subtract(BigDecimal.valueOf(273.15), RANGE);
                    break;
            }

            //convert to desired
            switch (input2Type.getSelectedItem().toString()) {
                case "Fahrenheit":
                    con = (mid.multiply(BigDecimal.valueOf(9.0 / 5.0), RANGE))
                            .add(BigDecimal.valueOf(32.0), RANGE);
                    break;
                case "Celsius":
                    con = mid;
                    break;
                case "Kelvin":
                    con = mid.add(BigDecimal.valueOf(273.15), RANGE);
                    break;
            }

            //display in opposite EditText
            input2.removeTextChangedListener(input2List);

            BigDecimal ans = new BigDecimal(con.toString()).setScale(2, ROUNDING_MODE);

            input2.setTextSize((ans.toString().length() > 15)? 35 : 40);

            input2.setText(String.format("%s", ans.stripTrailingZeros().toPlainString()));

            input2.addTextChangedListener(input2List);
        }
        else {
            //value inside input
            orig = new BigDecimal(input2.getText().toString());

            //convert all inputs to C
            switch (input2Type.getSelectedItem().toString()) {
                case "Fahrenheit":
                    mid = (orig.subtract(BigDecimal.valueOf(32.0), RANGE))
                            .multiply(BigDecimal.valueOf(5.0 / 9.0), RANGE);
                    break;
                case "Celsius":
                    mid = orig;
                    break;
                case "Kelvin":
                    mid = orig.subtract(BigDecimal.valueOf(273.15), RANGE);
                    break;
            }

            //convert to desired
            switch (input1Type.getSelectedItem().toString()) {
                case "Fahrenheit":
                    con = (mid.multiply(BigDecimal.valueOf(9.0 / 5.0), RANGE))
                            .add(BigDecimal.valueOf(32.0), RANGE);
                    break;
                case "Celsius":
                    con = mid;
                    break;
                case "Kelvin":
                    con = mid.add(BigDecimal.valueOf(273.15), RANGE);
                    break;
            }

            //display in opposite EditText
            input1.removeTextChangedListener(input1List);

            BigDecimal ans = new BigDecimal(con.toString()).setScale(2, ROUNDING_MODE);

            input1.setTextSize((ans.toString().length() > 15)? 35 : 40);

            input1.setText(String.format("%s", ans.stripTrailingZeros().toPlainString()));

            input1.addTextChangedListener(input1List);
        }
    }

    /**
     * Converts angle values.
     *
     * @param editTextId id of changing EditText
     */
    private void convertAngle(int editTextId) {
        double orig, con = 0;
        DecimalFormat df = new DecimalFormat("0.########");

        //if input1 is being changed
        if (editTextId == R.id.input1) {
            //value inside input
            orig = Double.parseDouble(input1.getText().toString());

            if (input1Type.getSelectedItemPosition() != input2Type.getSelectedItemPosition()) {
                switch (input1Type.getSelectedItem().toString()) {
                    case "Radians":
                        con = Math.toDegrees(orig);
                        df = new DecimalFormat("0.#####");
                        break;
                    case "Degrees":
                        con = Math.toRadians(orig);
                        break;
                }
            }
            else con = orig;

            //display in opposite edit text
            input2.removeTextChangedListener(input2List);

            input2.setTextSize((df.format(con).length() > 15)? 35 : 40);

            input2.setText(df.format(con));

            input2.addTextChangedListener(input2List);
        }
        else {
            //value inside input
            orig = Double.parseDouble(input2.getText().toString());

            if (input1Type.getSelectedItemPosition() != input2Type.getSelectedItemPosition()) {
                switch (input2Type.getSelectedItem().toString()) {
                    case "Radians":
                        con = Math.toDegrees(orig);
                        df = new DecimalFormat("0.#####");
                        break;
                    case "Degrees":
                        con = Math.toRadians(orig);
                        break;
                }
            }
            else con = orig;

            //display in opposite edit text
            input1.removeTextChangedListener(input1List);

            input1.setTextSize((df.format(con).length() > 15)? 35 : 40);

            input1.setText(df.format(con));

            input1.addTextChangedListener(input1List);
        }
    }

    /**
     * Converts mass values.
     *
     * @param editTextId id of changing edit text.
     */
    private void convertMass(int editTextId) {
        BigDecimal orig, mid = new BigDecimal(0), con = new BigDecimal(0);

        //if input1 is being changed
        if (editTextId == R.id.input1) {
            //value inside input
            orig = new BigDecimal(input1.getText().toString());

            //convert all inputs to kg
            switch (input1Type.getSelectedItem().toString()) {
                case "Metric Ton":
                    mid = orig.multiply(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Kilogram":
                    mid = orig;
                    break;
                case "Gram":
                    mid = orig.divide(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Milligram":
                    mid = orig.divide(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Microgram":
                    mid = orig.divide(BigDecimal.valueOf(1000000000.0), RANGE);
                    break;
                case "US Ton":
                    mid = orig.multiply(BigDecimal.valueOf(907.18474), RANGE);
                    break;
                case "Pound":
                    mid = orig.divide(BigDecimal.valueOf(2.20462262), RANGE);
                    break;
                case "Ounce":
                    mid = orig.divide(BigDecimal.valueOf(35.27396195), RANGE);
                    break;
            }

            //convert to desired
            switch (input2Type.getSelectedItem().toString()) {
                case "Metric Ton":
                    con = mid.divide(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Kilogram":
                    con = mid;
                    break;
                case "Gram":
                    con = mid.multiply(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Milligram":
                    con = mid.multiply(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Microgram":
                    con = mid.multiply(BigDecimal.valueOf(1000000000.0), RANGE);
                    break;
                case "US Ton":
                    con = mid.divide(BigDecimal.valueOf(907.18474), RANGE);
                    break;
                case "Pound":
                    con = mid.multiply(BigDecimal.valueOf(2.20462262), RANGE);
                    break;
                case "Ounce":
                    con = mid.multiply(BigDecimal.valueOf(35.27396195), RANGE);
                    break;
            }

            //display in opposite edit text
            input2.removeTextChangedListener(input2List);

            BigDecimal ans;
            if (con.compareTo(BigDecimal.valueOf(100)) > 0)
                ans = new BigDecimal(con.toString()).setScale(2, ROUNDING_MODE);
            else if (con.compareTo(BigDecimal.valueOf(1)) > 0)
                ans = new BigDecimal(con.toString()).setScale(4, ROUNDING_MODE);
            else
                ans = new BigDecimal(con.toString()).setScale(10, ROUNDING_MODE);

            input2.setTextSize((ans.toString().length() > 15)? 35 : 40);

            input2.setText((ans.compareTo(BigDecimal.valueOf(999999)) > 0 ||
                    ans.compareTo(BigDecimal.valueOf(0.001)) < 0)?
                    String.format("%s", ans.stripTrailingZeros().toString().toLowerCase()):
                    String.format("%s", ans.stripTrailingZeros().toPlainString()));

            input2.addTextChangedListener(input2List);
        }
        else {
            //value inside input
            orig = new BigDecimal(input2.getText().toString());

            //convert all inputs to kg
            switch (input2Type.getSelectedItem().toString()) {
                case "Metric Ton":
                    mid = orig.multiply(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Kilogram":
                    mid = orig;
                    break;
                case "Gram":
                    mid = orig.divide(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Milligram":
                    mid = orig.divide(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Microgram":
                    mid = orig.divide(BigDecimal.valueOf(1000000000.0), RANGE);
                    break;
                case "US Ton":
                    mid = orig.multiply(BigDecimal.valueOf(907.18474), RANGE);
                    break;
                case "Pound":
                    mid = orig.divide(BigDecimal.valueOf(2.20462262), RANGE);
                    break;
                case "Ounce":
                    mid = orig.divide(BigDecimal.valueOf(35.27396195), RANGE);
                    break;
            }

            //convert to desired
            switch (input1Type.getSelectedItem().toString()) {
                case "Metric Ton":
                    con = mid.divide(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Kilogram":
                    con = mid;
                    break;
                case "Gram":
                    con = mid.multiply(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Milligram":
                    con = mid.multiply(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Microgram":
                    con = mid.multiply(BigDecimal.valueOf(1000000000.0), RANGE);
                    break;
                case "US Ton":
                    con = mid.divide(BigDecimal.valueOf(907.18474), RANGE);
                    break;
                case "Pound":
                    con = mid.multiply(BigDecimal.valueOf(2.20462262), RANGE);
                    break;
                case "Ounce":
                    con = mid.multiply(BigDecimal.valueOf(35.27396195), RANGE);
                    break;
            }

            //display in opposite edit text
            input1.removeTextChangedListener(input1List);

            BigDecimal ans;
            if (con.compareTo(BigDecimal.valueOf(100)) > 0)
                ans = new BigDecimal(con.toString()).setScale(2, ROUNDING_MODE);
            else if (con.compareTo(BigDecimal.valueOf(1)) > 0)
                ans = new BigDecimal(con.toString()).setScale(4, ROUNDING_MODE);
            else
                ans = new BigDecimal(con.toString()).setScale(10, ROUNDING_MODE);

            input1.setTextSize((ans.toString().length() > 15)? 35 : 40);

            input1.setText((ans.compareTo(BigDecimal.valueOf(999999)) > 0 ||
                    ans.compareTo(BigDecimal.valueOf(0.001)) < 0)?
                    String.format("%s", ans.stripTrailingZeros().toString().toLowerCase()):
                    String.format("%s", ans.stripTrailingZeros().toPlainString()));

            input1.addTextChangedListener(input1List);
        }
    }

    /**
     * Converts length values.
     *
     * @param editTextId id of changing edit text
     */
    private void convertLength(int editTextId) {
        BigDecimal orig, mid = new BigDecimal(0), con = new BigDecimal(0);

        //if input1 is being changed
        if (editTextId == R.id.input1) {
            //value inside input
            orig = new BigDecimal(input1.getText().toString());

            //convert all inputs to meter
            switch (input1Type.getSelectedItem().toString()) {
                case "Kilometer":
                    mid = orig.multiply(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Meter":
                    mid = orig;
                    break;
                case "Centimeter":
                    mid = orig.divide(BigDecimal.valueOf(100.0), RANGE);
                    break;
                case "Millimeter":
                    mid = orig.divide(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Micrometer":
                    mid = orig.divide(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Nanometer":
                    mid = orig.divide(BigDecimal.valueOf(1000000000.0), RANGE);
                    break;
                case "Mile":
                    mid = orig.multiply(BigDecimal.valueOf(1609.344), RANGE);
                    break;
                case "Yard":
                    mid = orig.divide(BigDecimal.valueOf(1.0936133), RANGE);
                    break;
                case "Foot":
                    mid = orig.divide(BigDecimal.valueOf(3.2808399), RANGE);
                    break;
                case "Inch":
                    mid = orig.divide(BigDecimal.valueOf(39.3700787), RANGE);
                    break;
                case "Mil":
                    mid = orig.divide(BigDecimal.valueOf(39370.078740157), RANGE);
                    break;
            }

            //convert to desired
            switch (input2Type.getSelectedItem().toString()) {
                case "Kilometer":
                    con = mid.divide(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Meter":
                    con = mid;
                    break;
                case "Centimeter":
                    con = mid.multiply(BigDecimal.valueOf(100.0), RANGE);
                    break;
                case "Millimeter":
                    con = mid.multiply(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Micrometer":
                    con = mid.multiply(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Nanometer":
                    con = mid.multiply(BigDecimal.valueOf(1000000000.0), RANGE);
                    break;
                case "Mile":
                    con = mid.divide(BigDecimal.valueOf(1609.344), RANGE);
                    break;
                case "Yard":
                    con = mid.multiply(BigDecimal.valueOf(1.0936133), RANGE);
                    break;
                case "Foot":
                    con = mid.multiply(BigDecimal.valueOf(3.2808399), RANGE);
                    break;
                case "Inch":
                    con = mid.multiply(BigDecimal.valueOf(39.3700787), RANGE);
                    break;
                case "Mil":
                    con = mid.multiply(BigDecimal.valueOf(39370.078740157), RANGE);
                    break;
            }

            //display in opposite edit text
            input2.removeTextChangedListener(input2List);

            BigDecimal ans;
            if (con.compareTo(BigDecimal.valueOf(100)) > 0)
                ans = new BigDecimal(con.toString()).setScale(2, ROUNDING_MODE);
            else if (con.compareTo(BigDecimal.valueOf(1)) > 0)
                ans = new BigDecimal(con.toString()).setScale(4, ROUNDING_MODE);
            else
                ans = new BigDecimal(con.toString()).setScale(10, ROUNDING_MODE);

            input2.setTextSize((ans.toString().length() > 15)? 35 : 40);

            input2.setText((ans.compareTo(BigDecimal.valueOf(999999)) > 0 ||
                    ans.compareTo(BigDecimal.valueOf(0.001)) < 0)?
                    String.format("%s", ans.stripTrailingZeros().toString().toLowerCase()):
                    String.format("%s", ans.stripTrailingZeros().toPlainString()));

            input2.addTextChangedListener(input2List);
        }
        else {
            //value inside input
            orig = new BigDecimal(input2.getText().toString());

            //convert all inputs to meter
            switch (input2Type.getSelectedItem().toString()) {
                case "Kilometer":
                    mid = orig.multiply(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Meter":
                    mid = orig;
                    break;
                case "Centimeter":
                    mid = orig.divide(BigDecimal.valueOf(100.0), RANGE);
                    break;
                case "Millimeter":
                    mid = orig.divide(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Micrometer":
                    mid = orig.divide(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Nanometer":
                    mid = orig.divide(BigDecimal.valueOf(1000000000.0), RANGE);
                    break;
                case "Mile":
                    mid = orig.multiply(BigDecimal.valueOf(1609.344), RANGE);
                    break;
                case "Yard":
                    mid = orig.divide(BigDecimal.valueOf(1.0936133), RANGE);
                    break;
                case "Foot":
                    mid = orig.divide(BigDecimal.valueOf(3.2808399), RANGE);
                    break;
                case "Inch":
                    mid = orig.divide(BigDecimal.valueOf(39.3700787), RANGE);
                    break;
                case "Mil":
                    mid = orig.divide(BigDecimal.valueOf(39370.078740157), RANGE);
                    break;
            }

            //convert to desired
            switch (input1Type.getSelectedItem().toString()) {
                case "Kilometer":
                    con = mid.divide(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Meter":
                    con = mid;
                    break;
                case "Centimeter":
                    con = mid.multiply(BigDecimal.valueOf(100.0), RANGE);
                    break;
                case "Millimeter":
                    con = mid.multiply(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Micrometer":
                    con = mid.multiply(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Nanometer":
                    con = mid.multiply(BigDecimal.valueOf(1000000000.0), RANGE);
                    break;
                case "Mile":
                    con = mid.divide(BigDecimal.valueOf(1609.344), RANGE);
                    break;
                case "Yard":
                    con = mid.multiply(BigDecimal.valueOf(1.0936133), RANGE);
                    break;
                case "Foot":
                    con = mid.multiply(BigDecimal.valueOf(3.2808399), RANGE);
                    break;
                case "Inch":
                    con = mid.multiply(BigDecimal.valueOf(39.3700787), RANGE);
                    break;
                case "Mil":
                    con = mid.multiply(BigDecimal.valueOf(39370.078740157), RANGE);
                    break;
            }

            //display in opposite edit text
            input1.removeTextChangedListener(input1List);

            BigDecimal ans;
            if (con.compareTo(BigDecimal.valueOf(100)) > 0)
                ans = new BigDecimal(con.toString()).setScale(2, ROUNDING_MODE);
            else if (con.compareTo(BigDecimal.valueOf(1)) > 0)
                ans = new BigDecimal(con.toString()).setScale(4, ROUNDING_MODE);
            else
                ans = new BigDecimal(con.toString()).setScale(10, ROUNDING_MODE);

            input1.setTextSize((ans.toString().length() > 15)? 35 : 40);

            input1.setText((ans.compareTo(BigDecimal.valueOf(999999)) > 0 ||
                    ans.compareTo(BigDecimal.valueOf(0.001)) < 0)?
                    String.format("%s", ans.stripTrailingZeros().toString().toLowerCase()):
                    String.format("%s", ans.stripTrailingZeros().toPlainString()));

            input1.addTextChangedListener(input1List);
        }
    }

    /**
     * Converts area values.
     *
     * @param editTextId id of changing edit text
     */
    private void convertArea(int editTextId) {
        BigDecimal orig, mid = new BigDecimal(0), con = new BigDecimal(0);

        //if input1 is being changed
        if (editTextId == R.id.input1) {
            //value inside input
            orig = new BigDecimal(input1.getText().toString());

            //convert all inputs to square meter
            switch (input1Type.getSelectedItem().toString()) {
                case "Square Kilometer":
                    mid = orig.multiply(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Square Meter":
                    mid = orig;
                    break;
                case "Square Mile":
                    mid = orig.multiply(BigDecimal.valueOf(2589988.1103), RANGE);
                    break;
                case "Square Yard":
                    mid = orig.divide(BigDecimal.valueOf(1.19599005), RANGE);
                    break;
                case "Square Foot":
                    mid = orig.divide(BigDecimal.valueOf(10.763910417), RANGE);
                    break;
                case "Square Inch":
                    mid = orig.divide(BigDecimal.valueOf(1550.0031), RANGE);
                    break;
                case "Hectare":
                    mid = orig.multiply(BigDecimal.valueOf(10000.0), RANGE);
                    break;
                case "Acre":
                    mid = orig.multiply(BigDecimal.valueOf(4046.8564224), RANGE);
                    break;
            }

            //convert to desired
            switch (input2Type.getSelectedItem().toString()) {
                case "Square Kilometer":
                    con = mid.divide(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Square Meter":
                    con = mid;
                    break;
                case "Square Mile":
                    con = mid.divide(BigDecimal.valueOf(2589988.1103), RANGE);
                    break;
                case "Square Yard":
                    con = mid.multiply(BigDecimal.valueOf(1.19599005), RANGE);
                    break;
                case "Square Foot":
                    con = mid.multiply(BigDecimal.valueOf(10.763910417), RANGE);
                    break;
                case "Square Inch":
                    con = mid.multiply(BigDecimal.valueOf(1550.0031), RANGE);
                    break;
                case "Hectare":
                    con = mid.divide(BigDecimal.valueOf(10000.0), RANGE);
                    break;
                case "Acre":
                    con = mid.divide(BigDecimal.valueOf(4046.8564224), RANGE);
                    break;
            }

            //display in opposite edit text
            input2.removeTextChangedListener(input2List);

            BigDecimal ans;
            if (con.compareTo(BigDecimal.valueOf(100)) > 0)
                ans = new BigDecimal(con.toString()).setScale(2, ROUNDING_MODE);
            else if (con.compareTo(BigDecimal.valueOf(1)) > 0)
                ans = new BigDecimal(con.toString()).setScale(4, ROUNDING_MODE);
            else
                ans = new BigDecimal(con.toString()).setScale(10, ROUNDING_MODE);

            input2.setTextSize((ans.toString().length() > 15)? 35 : 40);

            input2.setText((ans.compareTo(BigDecimal.valueOf(999999)) > 0 ||
                    ans.compareTo(BigDecimal.valueOf(0.001)) < 0)?
                    String.format("%s", ans.stripTrailingZeros().toString().toLowerCase()):
                    String.format("%s", ans.stripTrailingZeros().toPlainString()));

            input2.addTextChangedListener(input2List);
        }
        else {
            //value inside input
            orig = new BigDecimal(input2.getText().toString());

            //convert all inputs to square meter
            switch (input2Type.getSelectedItem().toString()) {
                case "Square Kilometer":
                    mid = orig.multiply(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Square Meter":
                    mid = orig;
                    break;
                case "Square Mile":
                    mid = orig.multiply(BigDecimal.valueOf(2589988.1103), RANGE);
                    break;
                case "Square Yard":
                    mid = orig.divide(BigDecimal.valueOf(1.19599005), RANGE);
                    break;
                case "Square Foot":
                    mid = orig.divide(BigDecimal.valueOf(10.763910417), RANGE);
                    break;
                case "Square Inch":
                    mid = orig.divide(BigDecimal.valueOf(1550.0031), RANGE);
                    break;
                case "Hectare":
                    mid = orig.multiply(BigDecimal.valueOf(10000.0), RANGE);
                    break;
                case "Acre":
                    mid = orig.multiply(BigDecimal.valueOf(4046.8564224), RANGE);
                    break;
            }

            //convert to desired
            switch (input1Type.getSelectedItem().toString()) {
                case "Square Kilometer":
                    con = mid.divide(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Square Meter":
                    con = mid;
                    break;
                case "Square Mile":
                    con = mid.divide(BigDecimal.valueOf(2589988.1103), RANGE);
                    break;
                case "Square Yard":
                    con = mid.multiply(BigDecimal.valueOf(1.19599005), RANGE);
                    break;
                case "Square Foot":
                    con = mid.multiply(BigDecimal.valueOf(10.763910417), RANGE);
                    break;
                case "Square Inch":
                    con = mid.multiply(BigDecimal.valueOf(1550.0031), RANGE);
                    break;
                case "Hectare":
                    con = mid.divide(BigDecimal.valueOf(10000.0), RANGE);
                    break;
                case "Acre":
                    con = mid.divide(BigDecimal.valueOf(4046.8564224), RANGE);
                    break;
            }

            //display in opposite edit text
            input1.removeTextChangedListener(input1List);

            BigDecimal ans;
            if (con.compareTo(BigDecimal.valueOf(100)) > 0)
                ans = new BigDecimal(con.toString()).setScale(2, ROUNDING_MODE);
            else if (con.compareTo(BigDecimal.valueOf(1)) > 0)
                ans = new BigDecimal(con.toString()).setScale(4, ROUNDING_MODE);
            else
                ans = new BigDecimal(con.toString()).setScale(10, ROUNDING_MODE);

            input1.setTextSize((ans.toString().length() > 15)? 35 : 40);

            input1.setText((ans.compareTo(BigDecimal.valueOf(999999)) > 0 ||
                    ans.compareTo(BigDecimal.valueOf(0.001)) < 0)?
                    String.format("%s", ans.stripTrailingZeros().toString().toLowerCase()):
                    String.format("%s", ans.stripTrailingZeros().toPlainString()));

            input1.addTextChangedListener(input1List);
        }
    }

    /**
     * Converts volume values.
     *
     * @param editTextId id of changing edit text
     */
    private void convertVolume(int editTextId) {
        BigDecimal orig, mid = new BigDecimal(0), con = new BigDecimal(0);

        //if input1 is being changed
        if (editTextId == R.id.input1) {
            //value inside input
            orig = new BigDecimal(input1.getText().toString());

            //convert all inputs to cubic meter
            switch (input1Type.getSelectedItem().toString()) {
                case "Liter":
                    mid = orig.divide(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Milliliter":
                    mid = orig.divide(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Cubic Meter":
                    mid = orig;
                    break;
                case "Gallon":
                    mid = orig.divide(BigDecimal.valueOf(264.172052), RANGE);
                    break;
                case "Quart":
                    mid = orig.divide(BigDecimal.valueOf(1056.68821), RANGE);
                    break;
                case "Pint":
                    mid = orig.divide(BigDecimal.valueOf(2113.37642), RANGE);
                    break;
                case "Cup":
                    mid = orig.divide(BigDecimal.valueOf(4226.75284), RANGE);
                    break;
                case "Fluid Ounce":
                    mid = orig.divide(BigDecimal.valueOf(33814.0227), RANGE);
                    break;
                case "Tablespoon":
                    mid = orig.multiply(BigDecimal.valueOf(0.00001478676), RANGE);
                    break;
                case "Teaspoon":
                    mid = orig.divide(BigDecimal.valueOf(202884.1362), RANGE);
                    break;
                case "Cubic Yard":
                    mid = orig.divide(BigDecimal.valueOf(1.30795062), RANGE);
                    break;
                case "Cubic Foot":
                    mid = orig.divide(BigDecimal.valueOf(35.3146667), RANGE);
                    break;
                case "Cubic Inch":
                    mid = orig.divide(BigDecimal.valueOf(61023.7441), RANGE);
                    break;
            }

            //convert to desired
            switch (input2Type.getSelectedItem().toString()) {
                case "Liter":
                    con = mid.multiply(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Milliliter":
                    con = mid.multiply(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Cubic Meter":
                    con = mid;
                    break;
                case "Gallon":
                    con = mid.multiply(BigDecimal.valueOf(264.172052), RANGE);
                    break;
                case "Quart":
                    con = mid.multiply(BigDecimal.valueOf(1056.68821), RANGE);
                    break;
                case "Pint":
                    con = mid.multiply(BigDecimal.valueOf(2113.37642), RANGE);
                    break;
                case "Cup":
                    con = mid.multiply(BigDecimal.valueOf(4226.75284), RANGE);
                    break;
                case "Fluid Ounce":
                    con = mid.multiply(BigDecimal.valueOf(33814.0227), RANGE);
                    break;
                case "Tablespoon":
                    con = mid.multiply(BigDecimal.valueOf(67628.0454), RANGE);
                    break;
                case "Teaspoon":
                    con = mid.multiply(BigDecimal.valueOf(202884.1362), RANGE);
                    break;
                case "Cubic Yard":
                    con = mid.multiply(BigDecimal.valueOf(1.30795062), RANGE);
                    break;
                case "Cubic Foot":
                    con = mid.multiply(BigDecimal.valueOf(35.3146667), RANGE);
                    break;
                case "Cubic Inch":
                    con = mid.multiply(BigDecimal.valueOf(61023.7441), RANGE);
                    break;
            }

            //display in opposite edit text
            input2.removeTextChangedListener(input2List);

            BigDecimal ans;
            if (con.compareTo(BigDecimal.valueOf(100)) > 0)
                ans = new BigDecimal(con.toString()).setScale(2, ROUNDING_MODE);
            else if (con.compareTo(BigDecimal.valueOf(1)) > 0)
                ans = new BigDecimal(con.toString()).setScale(4, ROUNDING_MODE);
            else
                ans = new BigDecimal(con.toString()).setScale(10, ROUNDING_MODE);

            input2.setTextSize((ans.toString().length() > 15)? 35 : 40);

            input2.setText((ans.compareTo(BigDecimal.valueOf(999999)) > 0 ||
                    ans.compareTo(BigDecimal.valueOf(0.001)) < 0)?
                    String.format("%s", ans.stripTrailingZeros().toString().toLowerCase()):
                    String.format("%s", ans.stripTrailingZeros().toPlainString()));

            input2.addTextChangedListener(input2List);
        }
        else {
            //value inside input
            orig = new BigDecimal(input2.getText().toString());

            //convert all inputs to cubic meter
            switch (input2Type.getSelectedItem().toString()) {
                case "Liter":
                    mid = orig.divide(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Milliliter":
                    mid = orig.divide(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Cubic Meter":
                    mid = orig;
                    break;
                case "Gallon":
                    mid = orig.divide(BigDecimal.valueOf(264.172052), RANGE);
                    break;
                case "Quart":
                    mid = orig.divide(BigDecimal.valueOf(1056.68821), RANGE);
                    break;
                case "Pint":
                    mid = orig.divide(BigDecimal.valueOf(2113.37642), RANGE);
                    break;
                case "Cup":
                    mid = orig.divide(BigDecimal.valueOf(4226.75284), RANGE);
                    break;
                case "Fluid Ounce":
                    mid = orig.divide(BigDecimal.valueOf(33814.0227), RANGE);
                    break;
                case "Tablespoon":
                    mid = orig.multiply(BigDecimal.valueOf(0.00001478676), RANGE);
                    break;
                case "Teaspoon":
                    mid = orig.divide(BigDecimal.valueOf(202884.1362), RANGE);
                    break;
                case "Cubic Yard":
                    mid = orig.divide(BigDecimal.valueOf(1.30795062), RANGE);
                    break;
                case "Cubic Foot":
                    mid = orig.divide(BigDecimal.valueOf(35.3146667), RANGE);
                    break;
                case "Cubic Inch":
                    mid = orig.divide(BigDecimal.valueOf(61023.7441), RANGE);
                    break;
            }

            //convert to desired
            switch (input1Type.getSelectedItem().toString()) {
                case "Liter":
                    con = mid.multiply(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Milliliter":
                    con = mid.multiply(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Cubic Meter":
                    con = mid;
                    break;
                case "Gallon":
                    con = mid.multiply(BigDecimal.valueOf(264.172052), RANGE);
                    break;
                case "Quart":
                    con = mid.multiply(BigDecimal.valueOf(1056.68821), RANGE);
                    break;
                case "Pint":
                    con = mid.multiply(BigDecimal.valueOf(2113.37642), RANGE);
                    break;
                case "Cup":
                    con = mid.multiply(BigDecimal.valueOf(4226.75284), RANGE);
                    break;
                case "Fluid Ounce":
                    con = mid.multiply(BigDecimal.valueOf(33814.0227), RANGE);
                    break;
                case "Tablespoon":
                    con = mid.multiply(BigDecimal.valueOf(67628.0454), RANGE);
                    break;
                case "Teaspoon":
                    con = mid.multiply(BigDecimal.valueOf(202884.1362), RANGE);
                    break;
                case "Cubic Yard":
                    con = mid.multiply(BigDecimal.valueOf(1.30795062), RANGE);
                    break;
                case "Cubic Foot":
                    con = mid.multiply(BigDecimal.valueOf(35.3146667), RANGE);
                    break;
                case "Cubic Inch":
                    con = mid.multiply(BigDecimal.valueOf(61023.7441), RANGE);
                    break;
            }

            //display in opposite edit text
            input1.removeTextChangedListener(input1List);

            BigDecimal ans;
            if (con.compareTo(BigDecimal.valueOf(100)) > 0)
                ans = new BigDecimal(con.toString()).setScale(2, ROUNDING_MODE);
            else if (con.compareTo(BigDecimal.valueOf(1)) > 0)
                ans = new BigDecimal(con.toString()).setScale(4, ROUNDING_MODE);
            else
                ans = new BigDecimal(con.toString()).setScale(10, ROUNDING_MODE);

            input1.setTextSize((ans.toString().length() > 15)? 35 : 40);

            input1.setText((ans.compareTo(BigDecimal.valueOf(999999)) > 0 ||
                    ans.compareTo(BigDecimal.valueOf(0.001)) < 0)?
                    String.format("%s", ans.stripTrailingZeros().toString().toLowerCase()):
                    String.format("%s", ans.stripTrailingZeros().toPlainString()));

            input1.addTextChangedListener(input1List);
        }
    }

    /**
     * Converts speed values.
     *
     * @param editTextId id of changing edit text
     */
    private void convertSpeed(int editTextId) {
        BigDecimal orig, mid = new BigDecimal(0), con = new BigDecimal(0);

        //if input1 is being changed
        if (editTextId == R.id.input1) {
            //value inside input
            orig = new BigDecimal(input1.getText().toString());

            //convert all inputs to kph
            switch (input1Type.getSelectedItem().toString()) {
                case "Miles per Hour":
                    mid = orig.multiply(BigDecimal.valueOf(1.609344), RANGE);
                    break;
                case "Foot per Second":
                    mid = orig.multiply(BigDecimal.valueOf(1.09728), RANGE);
                    break;
                case "Kilometer per Hour":
                    mid = orig;
                    break;
                case "Meter per Second":
                    mid = orig.multiply(BigDecimal.valueOf(3.6), RANGE);
                    break;
                case "Knot":
                    mid = orig.multiply(BigDecimal.valueOf(1.852), RANGE);
                    break;
            }

            //convert to desired
            switch (input2Type.getSelectedItem().toString()) {
                case "Miles per Hour":
                    con = mid.divide(BigDecimal.valueOf(1.609344), RANGE);
                    break;
                case "Foot per Second":
                    con = mid.divide(BigDecimal.valueOf(1.09728), RANGE);
                    break;
                case "Kilometer per Hour":
                    con = mid;
                    break;
                case "Meter per Second":
                    con = mid.divide(BigDecimal.valueOf(3.6), RANGE);
                    break;
                case "Knot":
                    con = mid.divide(BigDecimal.valueOf(1.852), RANGE);
                    break;
            }

            //display in opposite edit text
            input2.removeTextChangedListener(input2List);

            BigDecimal ans;
            if (con.compareTo(BigDecimal.valueOf(100)) > 0)
                ans = new BigDecimal(con.toString()).setScale(2, ROUNDING_MODE);
            else if (con.compareTo(BigDecimal.valueOf(1)) > 0)
                ans = new BigDecimal(con.toString()).setScale(4, ROUNDING_MODE);
            else
                ans = new BigDecimal(con.toString()).setScale(10, ROUNDING_MODE);

            input2.setTextSize((ans.toString().length() > 15)? 35 : 40);

            input2.setText((ans.compareTo(BigDecimal.valueOf(999999)) > 0 ||
                    ans.compareTo(BigDecimal.valueOf(0.001)) < 0)?
                    String.format("%s", ans.stripTrailingZeros().toString().toLowerCase()):
                    String.format("%s", ans.stripTrailingZeros().toPlainString()));

            input2.addTextChangedListener(input2List);
        }
        else {
            //value inside input
            orig = new BigDecimal(input2.getText().toString());

            //convert all inputs to kph
            switch (input2Type.getSelectedItem().toString()) {
                case "Miles per Hour":
                    mid = orig.multiply(BigDecimal.valueOf(1.609344), RANGE);
                    break;
                case "Foot per Second":
                    mid = orig.multiply(BigDecimal.valueOf(1.09728), RANGE);
                    break;
                case "Kilometer per Hour":
                    mid = orig;
                    break;
                case "Meter per Second":
                    mid = orig.multiply(BigDecimal.valueOf(3.6), RANGE);
                    break;
                case "Knot":
                    mid = orig.multiply(BigDecimal.valueOf(1.852), RANGE);
                    break;
            }

            //convert to desired
            switch (input1Type.getSelectedItem().toString()) {
                case "Miles per Hour":
                    con = mid.divide(BigDecimal.valueOf(1.609344), RANGE);
                    break;
                case "Foot per Second":
                    con = mid.divide(BigDecimal.valueOf(1.09728), RANGE);
                    break;
                case "Kilometer per Hour":
                    con = mid;
                    break;
                case "Meter per Second":
                    con = mid.divide(BigDecimal.valueOf(3.6), RANGE);
                    break;
                case "Knot":
                    con = mid.divide(BigDecimal.valueOf(1.852), RANGE);
                    break;
            }

            //display in opposite edit text
            input1.removeTextChangedListener(input1List);

            BigDecimal ans;
            if (con.compareTo(BigDecimal.valueOf(100)) > 0)
                ans = new BigDecimal(con.toString()).setScale(2, ROUNDING_MODE);
            else if (con.compareTo(BigDecimal.valueOf(1)) > 0)
                ans = new BigDecimal(con.toString()).setScale(4, ROUNDING_MODE);
            else
                ans = new BigDecimal(con.toString()).setScale(10, ROUNDING_MODE);

            input1.setTextSize((ans.toString().length() > 15)? 35 : 40);

            input1.setText((ans.compareTo(BigDecimal.valueOf(999999)) > 0 ||
                    ans.compareTo(BigDecimal.valueOf(0.001)) < 0)?
                    String.format("%s", ans.stripTrailingZeros().toString().toLowerCase()):
                    String.format("%s", ans.stripTrailingZeros().toPlainString()));

            input1.addTextChangedListener(input1List);
        }
    }

    /**
     * Converts time values.
     *
     * @param editTextId id of changing edit text
     */
    private void convertTime(int editTextId) {
        BigDecimal orig, mid = new BigDecimal(0), con = new BigDecimal(0);

        //if input1 is being changed
        if (editTextId == R.id.input1) {
            //value inside input
            orig = new BigDecimal(input1.getText().toString());

            //convert all inputs to seconds
            switch (input1Type.getSelectedItem().toString()) {
                case "Nanosecond":
                    mid = orig.divide(BigDecimal.valueOf(1000000000.0), RANGE);
                    break;
                case "Microsecond":
                    mid = orig.divide(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Millisecond":
                    mid = orig.divide(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Second":
                    mid = orig;
                    break;
                case "Minute":
                    mid = orig.multiply(BigDecimal.valueOf(60.0), RANGE);
                    break;
                case "Hour":
                    mid = orig.multiply(BigDecimal.valueOf(3600.0), RANGE);
                    break;
                case "Day":
                    mid = orig.multiply(BigDecimal.valueOf(86400.0), RANGE);
                    break;
                case "Week":
                    mid = orig.multiply(BigDecimal.valueOf(604800.0), RANGE);
                    break;
                case "Month":
                    mid = orig.multiply(BigDecimal.valueOf(2592000.0), RANGE);
                    break;
                case "Year":
                    mid = orig.multiply(BigDecimal.valueOf(31536000.0), RANGE);
                    break;
                case "Decade":
                    mid = orig.multiply(BigDecimal.valueOf(315360000.0), RANGE);
                    break;
                case "Century":
                    mid = orig.multiply(BigDecimal.valueOf(3153600000.0), RANGE);
                    break;
            }

            //convert to desired
            switch (input2Type.getSelectedItem().toString()) {
                case "Nanosecond":
                    con = mid.multiply(BigDecimal.valueOf(1000000000.0), RANGE);
                    break;
                case "Microsecond":
                    con = mid.multiply(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Millisecond":
                    con = mid.multiply(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Second":
                    con = mid;
                    break;
                case "Minute":
                    con = mid.divide(BigDecimal.valueOf(60.0), RANGE);
                    break;
                case "Hour":
                    con = mid.divide(BigDecimal.valueOf(3600.0), RANGE);
                    break;
                case "Day":
                    con = mid.divide(BigDecimal.valueOf(86400.0), RANGE);
                    break;
                case "Week":
                    con = mid.divide(BigDecimal.valueOf(604800.0), RANGE);
                    break;
                case "Month":
                    con = mid.divide(BigDecimal.valueOf(2592000.0), RANGE);
                    break;
                case "Year":
                    con = mid.divide(BigDecimal.valueOf(31536000.0), RANGE);
                    break;
                case "Decade":
                    con = mid.divide(BigDecimal.valueOf(315360000.0), RANGE);
                    break;
                case "Century":
                    con = mid.divide(BigDecimal.valueOf(3153600000.0), RANGE);
                    break;
            }

            //display in opposite edit text
            input2.removeTextChangedListener(input2List);

            BigDecimal ans;
            if (con.compareTo(BigDecimal.valueOf(100)) > 0)
                ans = new BigDecimal(con.toString()).setScale(1, ROUNDING_MODE);
            else if (con.compareTo(BigDecimal.valueOf(1)) > 0)
                ans = new BigDecimal(con.toString()).setScale(2, ROUNDING_MODE);
            else
                ans = new BigDecimal(con.toString()).setScale(15, ROUNDING_MODE);

            input2.setTextSize((ans.toString().length() > 15)? 35 : 40);

            input2.setText((ans.compareTo(BigDecimal.valueOf(999999)) > 0 ||
                    ans.compareTo(BigDecimal.valueOf(0.001)) < 0)?
                    String.format("%s", ans.stripTrailingZeros().toString().toLowerCase()):
                    String.format("%s", ans.stripTrailingZeros().toPlainString()));

            input2.addTextChangedListener(input2List);
        }
        else {
            //value inside input
            orig = new BigDecimal(input2.getText().toString());

            //convert all inputs to seconds
            switch (input2Type.getSelectedItem().toString()) {
                case "Nanosecond":
                    mid = orig.divide(BigDecimal.valueOf(1000000000.0), RANGE);
                    break;
                case "Microsecond":
                    mid = orig.divide(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Millisecond":
                    mid = orig.divide(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Second":
                    mid = orig;
                    break;
                case "Minute":
                    mid = orig.multiply(BigDecimal.valueOf(60.0), RANGE);
                    break;
                case "Hour":
                    mid = orig.multiply(BigDecimal.valueOf(3600.0), RANGE);
                    break;
                case "Day":
                    mid = orig.multiply(BigDecimal.valueOf(86400.0), RANGE);
                    break;
                case "Week":
                    mid = orig.multiply(BigDecimal.valueOf(604800.0), RANGE);
                    break;
                case "Month":
                    mid = orig.multiply(BigDecimal.valueOf(2592000.0), RANGE);
                    break;
                case "Year":
                    mid = orig.multiply(BigDecimal.valueOf(31536000.0), RANGE);
                    break;
                case "Decade":
                    mid = orig.multiply(BigDecimal.valueOf(315360000.0), RANGE);
                    break;
                case "Century":
                    mid = orig.multiply(BigDecimal.valueOf(3153600000.0), RANGE);
                    break;
            }

            //convert to desired
            switch (input1Type.getSelectedItem().toString()) {
                case "Nanosecond":
                    con = mid.multiply(BigDecimal.valueOf(1000000000.0), RANGE);
                    break;
                case "Microsecond":
                    con = mid.multiply(BigDecimal.valueOf(1000000.0), RANGE);
                    break;
                case "Millisecond":
                    con = mid.multiply(BigDecimal.valueOf(1000.0), RANGE);
                    break;
                case "Second":
                    con = mid;
                    break;
                case "Minute":
                    con = mid.divide(BigDecimal.valueOf(60.0), RANGE);
                    break;
                case "Hour":
                    con = mid.divide(BigDecimal.valueOf(3600.0), RANGE);
                    break;
                case "Day":
                    con = mid.divide(BigDecimal.valueOf(86400.0), RANGE);
                    break;
                case "Week":
                    con = mid.divide(BigDecimal.valueOf(604800.0), RANGE);
                    break;
                case "Month":
                    con = mid.divide(BigDecimal.valueOf(2592000.0), RANGE);
                    break;
                case "Year":
                    con = mid.divide(BigDecimal.valueOf(31536000.0), RANGE);
                    break;
                case "Decade":
                    con = mid.divide(BigDecimal.valueOf(315360000.0), RANGE);
                    break;
                case "Century":
                    con = mid.divide(BigDecimal.valueOf(3153600000.0), RANGE);
                    break;
            }

            //display in opposite edit text
            input1.removeTextChangedListener(input1List);

            BigDecimal ans;
            if (con.compareTo(BigDecimal.valueOf(100)) > 0)
                ans = new BigDecimal(con.toString()).setScale(1, ROUNDING_MODE);
            else if (con.compareTo(BigDecimal.valueOf(1)) > 0)
                ans = new BigDecimal(con.toString()).setScale(2, ROUNDING_MODE);
            else
                ans = new BigDecimal(con.toString()).setScale(15, ROUNDING_MODE);

            input1.setTextSize((ans.toString().length() > 15)? 35 : 40);

            input1.setText((ans.compareTo(BigDecimal.valueOf(999999)) > 0 ||
                    ans.compareTo(BigDecimal.valueOf(0.001)) < 0)?
                    String.format("%s", ans.stripTrailingZeros().toString().toLowerCase()):
                    String.format("%s", ans.stripTrailingZeros().toPlainString()));

            input1.addTextChangedListener(input1List);
        }
    }

    /**
     * Converts currency values (updated daily from European Central Bank data).
     *
     * @param editTextId id of changing edit text
     */
    private void convertCurrency(int editTextId) {
        BigDecimal orig, mid = new BigDecimal(0), con = new BigDecimal(0);

        //if input1 is being changed
        if (editTextId == R.id.input1) {
            //value inside input
            orig = new BigDecimal(input1.getText().toString());

            //convert all inputs to EUR
            switch (input1Type.getSelectedItem().toString()) {
                case "US Dollar (USD)":
                    mid = (curList.get("USD") != null)? orig.divide(curList.get("USD"), RANGE) :
                                                        BigDecimal.valueOf(-1);
                    break;
                case "Euro (EUR)":
                    mid = orig;
                    break;
                case "British Pound (GBP)":
                    mid = (curList.get("GBP") != null)? orig.divide(curList.get("GBP"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Canadian Dollar (CAD)":
                    mid = (curList.get("CAD") != null)? orig.divide(curList.get("CAD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Mexican Peso (MXN)":
                    mid = (curList.get("MXN") != null)? orig.divide(curList.get("MXN"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Australian Dollar (AUD)":
                    mid = (curList.get("AUD") != null)? orig.divide(curList.get("AUD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Japanese Yen (JPY)":
                    mid = (curList.get("JPY") != null)? orig.divide(curList.get("JPY"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Chinese Yuan (CNY)":
                    mid = (curList.get("CNY") != null)? orig.divide(curList.get("CNY"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Korean Won (KRW)":
                    mid = (curList.get("KRW") != null)? orig.divide(curList.get("KRW"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Hong Kong Dollar (HKD)":
                    mid = (curList.get("HKD") != null)? orig.divide(curList.get("HKD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Singapore Dollar (SGD)":
                    mid = (curList.get("SGD") != null)? orig.divide(curList.get("SGD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Indian Rupee (INR)":
                    mid = (curList.get("INR") != null)? orig.divide(curList.get("INR"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Russian Ruble (RUB)":
                    mid = (curList.get("RUB") != null)? orig.divide(curList.get("RUB"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Swiss Franc (CHF)":
                    mid = (curList.get("CHF") != null)? orig.divide(curList.get("CHF"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Vietnamese Dong (VND)":
                    mid = (curList.get("VND") != null)? orig.divide(curList.get("VND"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "New Zealand Dollar (NZD)":
                    mid = (curList.get("NZD") != null)? orig.divide(curList.get("NZD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "South African Rand (ZAR)":
                    mid = (curList.get("ZAR") != null)? orig.divide(curList.get("ZAR"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
            }

            //convert to desired
            switch (input2Type.getSelectedItem().toString()) {
                case "US Dollar (USD)":
                    con = (curList.get("USD") != null)? mid.multiply(curList.get("USD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Euro (EUR)":
                    con = mid;
                    break;
                case "British Pound (GBP)":
                    con = (curList.get("GBP") != null)? mid.multiply(curList.get("GBP"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Canadian Dollar (CAD)":
                    con = (curList.get("CAD") != null)? mid.multiply(curList.get("CAD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Mexican Peso (MXN)":
                    con = (curList.get("MXN") != null)? mid.multiply(curList.get("MXN"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Australian Dollar (AUD)":
                    con = (curList.get("AUD") != null)? mid.multiply(curList.get("AUD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Japanese Yen (JPY)":
                    con = (curList.get("JPY") != null)? mid.multiply(curList.get("JPY"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Chinese Yuan (CNY)":
                    con = (curList.get("CNY") != null)? mid.multiply(curList.get("CNY"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Korean Won (KRW)":
                    con = (curList.get("KRW") != null)? mid.multiply(curList.get("KRW"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Hong Kong Dollar (HKD)":
                    con = (curList.get("HKD") != null)? mid.multiply(curList.get("HKD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Singapore Dollar (SGD)":
                    con = (curList.get("SGD") != null)? mid.multiply(curList.get("SGD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Indian Rupee (INR)":
                    con = (curList.get("INR") != null)? mid.multiply(curList.get("INR"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Russian Ruble (RUB)":
                    con = (curList.get("RUB") != null)? mid.multiply(curList.get("RUB"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Swiss Franc (CHF)":
                    con = (curList.get("CHF") != null)? mid.multiply(curList.get("CHF"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Vietnamese Dong (VND)":
                    con = (curList.get("VND") != null)? mid.multiply(curList.get("VND"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "New Zealand Dollar (NZD)":
                    con = (curList.get("NZD") != null)? mid.multiply(curList.get("NZD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "South African Rand (ZAR)":
                    con = (curList.get("ZAR") != null)? mid.multiply(curList.get("ZAR"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
            }

            //display in opposite edit text
            input2.removeTextChangedListener(input2List);

            BigDecimal ans = new BigDecimal(con.toString()).setScale(2, ROUNDING_MODE);

            input2.setTextSize((ans.toString().length() > 15)? 35 : 40);

            input2.setText(String.format("%s", ans.toPlainString()));

            input2.addTextChangedListener(input2List);
        }
        else {
            //value inside input
            orig = new BigDecimal(input2.getText().toString());

            //convert all inputs to usd
            switch (input2Type.getSelectedItem().toString()) {
                case "US Dollar (USD)":
                    mid = (curList.get("USD") != null)? orig.divide(curList.get("USD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Euro (EUR)":
                    mid = orig;
                    break;
                case "British Pound (GBP)":
                    mid = (curList.get("GBP") != null)? orig.divide(curList.get("GBP"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Canadian Dollar (CAD)":
                    mid = (curList.get("CAD") != null)? orig.divide(curList.get("CAD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Mexican Peso (MXN)":
                    mid = (curList.get("MXN") != null)? orig.divide(curList.get("MXN"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Australian Dollar (AUD)":
                    mid = (curList.get("AUD") != null)? orig.divide(curList.get("AUD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Japanese Yen (JPY)":
                    mid = (curList.get("JPY") != null)? orig.divide(curList.get("JPY"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Chinese Yuan (CNY)":
                    mid = (curList.get("CNY") != null)? orig.divide(curList.get("CNY"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Korean Won (KRW)":
                    mid = (curList.get("KRW") != null)? orig.divide(curList.get("KRW"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Hong Kong Dollar (HKD)":
                    mid = (curList.get("HKD") != null)? orig.divide(curList.get("HKD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Singapore Dollar (SGD)":
                    mid = (curList.get("SGD") != null)? orig.divide(curList.get("SGD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Indian Rupee (INR)":
                    mid = (curList.get("INR") != null)? orig.divide(curList.get("INR"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Russian Ruble (RUB)":
                    mid = (curList.get("RUB") != null)? orig.divide(curList.get("RUB"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Swiss Franc (CHF)":
                    mid = (curList.get("CHF") != null)? orig.divide(curList.get("CHF"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Vietnamese Dong (VND)":
                    mid = (curList.get("VND") != null)? orig.divide(curList.get("VND"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "New Zealand Dollar (NZD)":
                    mid = (curList.get("NZD") != null)? orig.divide(curList.get("NZD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "South African Rand (ZAR)":
                    mid = (curList.get("ZAR") != null)? orig.divide(curList.get("ZAR"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
            }

            //convert to desired
            switch (input1Type.getSelectedItem().toString()) {
                case "US Dollar (USD)":
                    con = (curList.get("USD") != null)? mid.multiply(curList.get("USD"), RANGE) :
                            BigDecimal.valueOf(-1);
                    break;
                case "Euro (EUR)":
                    con = mid;
                    break;
                case "British Pound (GBP)":
                    con = (curList.get("GBP") != null)? mid.multiply(curList.get("GBP"), RANGE) :
                    BigDecimal.valueOf(-1);
                    break;
                case "Canadian Dollar (CAD)":
                    con = (curList.get("CAD") != null)? mid.multiply(curList.get("CAD"), RANGE) :
                    BigDecimal.valueOf(-1);
                    break;
                case "Mexican Peso (MXN)":
                    con = (curList.get("MXN") != null)? mid.multiply(curList.get("MXN"), RANGE) :
                    BigDecimal.valueOf(-1);
                    break;
                case "Australian Dollar (AUD)":
                    con = (curList.get("AUD") != null)? mid.multiply(curList.get("AUD"), RANGE) :
                    BigDecimal.valueOf(-1);
                    break;
                case "Japanese Yen (JPY)":
                    con = (curList.get("JPY") != null)? mid.multiply(curList.get("JPY"), RANGE) :
                    BigDecimal.valueOf(-1);
                    break;
                case "Chinese Yuan (CNY)":
                    con = (curList.get("CNY") != null)? mid.multiply(curList.get("CNY"), RANGE) :
                    BigDecimal.valueOf(-1);
                    break;
                case "Korean Won (KRW)":
                    con = (curList.get("KRW") != null)? mid.multiply(curList.get("KRW"), RANGE) :
                    BigDecimal.valueOf(-1);
                    break;
                case "Hong Kong Dollar (HKD)":
                    con = (curList.get("HKD") != null)? mid.multiply(curList.get("HKD"), RANGE) :
                    BigDecimal.valueOf(-1);
                    break;
                case "Singapore Dollar (SGD)":
                    con = (curList.get("SGD") != null)? mid.multiply(curList.get("SGD"), RANGE) :
                    BigDecimal.valueOf(-1);
                    break;
                case "Indian Rupee (INR)":
                    con = (curList.get("INR") != null)? mid.multiply(curList.get("INR"), RANGE) :
                    BigDecimal.valueOf(-1);
                    break;
                case "Russian Ruble (RUB)":
                    con = (curList.get("RUB") != null)? mid.multiply(curList.get("RUB"), RANGE) :
                    BigDecimal.valueOf(-1);
                    break;
                case "Swiss Franc (CHF)":
                    con = (curList.get("CHF") != null)? mid.multiply(curList.get("CHF"), RANGE) :
                    BigDecimal.valueOf(-1);
                    break;
                case "Vietnamese Dong (VND)":
                    con = (curList.get("VND") != null)? mid.multiply(curList.get("VND"), RANGE) :
                    BigDecimal.valueOf(-1);
                    break;
                case "New Zealand Dollar (NZD)":
                    con = (curList.get("NZD") != null)? mid.multiply(curList.get("NZD"), RANGE) :
                    BigDecimal.valueOf(-1);
                    break;
                case "South African Rand (ZAR)":
                    con = (curList.get("ZAR") != null)? mid.multiply(curList.get("ZAR"), RANGE) :
                    BigDecimal.valueOf(-1);
                    break;
            }

            //display in opposite edit text
            input1.removeTextChangedListener(input1List);

            BigDecimal ans = new BigDecimal(con.toString()).setScale(2, ROUNDING_MODE);

            input1.setTextSize((ans.toString().length() > 15)? 35 : 40);

            input1.setText(String.format("%s", ans.toPlainString()));

            input1.addTextChangedListener(input1List);
        }
    }

    /**
     * Places initial value in edit text.
     *
     */
    private void handleEmpty(String val) {
        input1.removeTextChangedListener(input1List);
        input1.setText(val);
        input1.addTextChangedListener(input1List);
    }

    /**
     * Vibrates for certain length and amplitude.
     *
     * @param length length in ms
     * @param amplitude amplitude of vibration
     */
    private void vibrate(int length, int amplitude) {
        ((Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE)).
                vibrate(VibrationEffect.createOneShot(length,amplitude));
    }

    /**
     * Loads xml file data from European Central Bank with currency rates (updates daily).
     */
    private void loadRatesFromXml() {
        curList = new HashMap<>();

        //load live rate from internet or read existing file
        if (isInternetAvailable()) {
            CurrencyRequest request = new CurrencyRequest();
            request.execute();
        }
        else {
            readFromFile();
        }
    }

    /**
     * Makes the request to load the xml file using okhttp.
     */
    private class CurrencyRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String[] params) {
            try {
                String url = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String xml) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new InputSource(new StringReader(xml)));
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("Cube");

                //add elements to map of currency and rates
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        if (!eElement.getAttribute("currency").equals(""))
                            curList.put(eElement.getAttribute("currency"),
                                    new BigDecimal(eElement.getAttribute("rate")));

                    }
                }
                writeToFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the current curList to file
     */
    private void writeToFile() {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    getContext().openFileOutput("rates.txt", Context.MODE_PRIVATE));

            for (Map.Entry<String, BigDecimal> entry : curList.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().toString();
                outputStreamWriter.write(key + ":" + value + "\n");
            }

            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /**
     * Uses file contents to fill curList.
     */
    private void readFromFile() {
        try {
            InputStream inputStream = getContext().openFileInput("rates.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String data;

                while ((data = bufferedReader.readLine()) != null) {
                    String[] split = data.split(":");
                    String key = split[0].trim();
                    BigDecimal value = new BigDecimal(split[1].trim());
                    curList.put(key, value);
                }

                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e("Exception", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Exception", "Can not read file: " + e.toString());
        }
    }

    /**
     * Checks if internet is available.
     *
     * @return true if internet is available and false otherwise
     */
    public boolean isInternetAvailable() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (Exception e) { return false; }
    }
}
