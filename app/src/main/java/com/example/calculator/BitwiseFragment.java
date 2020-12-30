package com.example.calculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.NumberFormat;
import static android.content.Context.VIBRATOR_SERVICE;

public class BitwiseFragment extends Fragment {
    private TextView dec;
    private TextView bin;
    private TextView hex;
    private TextInputEditText input1;
    private RadioGroup radioType1;
    private int type1;
    private TextInputEditText input2;
    private RadioGroup radioType2;
    private int type2;
    private int operation;
    private View view;
    private RadioGroup radioRow1;
    private RadioGroup.OnCheckedChangeListener row1 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            //remove other checked box if checked
            radioRow2.setOnCheckedChangeListener(null);
            radioRow2.clearCheck();
            radioRow2.setOnCheckedChangeListener(row2);
            vibrate(5, 10);

            switch (i) {
                case R.id.con:
                    operation = 0;
                    break;
                case R.id.add:
                    operation = 1;
                    break;
                case R.id.sub:
                    operation = 2;
                    break;
                case R.id.mul:
                    operation = 3;
                    break;
                case R.id.div:
                    operation = 4;
                    break;
            }
            RadioButton bt = (RadioButton) view.findViewById(radioRow1.getCheckedRadioButtonId());
            bt.startAnimation(buttonPress);
            calculate();
        }
    };
    private RadioGroup radioRow2;
    private RadioGroup.OnCheckedChangeListener row2 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            //remove other checked box if checked
            radioRow1.setOnCheckedChangeListener(null);
            radioRow1.clearCheck();
            radioRow1.setOnCheckedChangeListener(row1);
            vibrate(5, 10);

            switch (i) {
                case R.id.and:
                    operation = 5;
                    break;
                case R.id.or:
                    operation = 6;
                    break;
                case R.id.xor:
                    operation = 7;
                    break;
                case R.id.left_shift:
                    operation = 8;
                    break;
                case R.id.right_shift:
                    operation = 9;
                    break;
            }
            RadioButton bt = (RadioButton) view.findViewById(radioRow2.getCheckedRadioButtonId());
            bt.startAnimation(buttonPress);
            calculate();
        }
    };
    private RadioGroup.OnCheckedChangeListener type2List = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            RadioButton bt;
            vibrate(5, 10);
            switch (i) {
                case R.id.dec_type2:
                    type2 = 0;
                    if (twoCom.isChecked())
                        input2.setInputType(InputType.TYPE_CLASS_NUMBER |
                                InputType.TYPE_NUMBER_FLAG_SIGNED);
                    else
                        input2.setInputType(InputType.TYPE_CLASS_DATETIME);
                    bt = (RadioButton) view
                            .findViewById(radioType2.getCheckedRadioButtonId());
                    bt.startAnimation(buttonPress);
                    calculate();
                    break;
                case R.id.bin_type2:
                    type2 = 1;
                    input2.setInputType(InputType.TYPE_CLASS_DATETIME);
                    bt = (RadioButton) view
                            .findViewById(radioType2.getCheckedRadioButtonId());
                    bt.startAnimation(buttonPress);
                    calculate();
                    break;
                case R.id.hex_type2:
                    type2 = 2;
                    input2.setInputType(InputType.TYPE_CLASS_TEXT);
                    bt = (RadioButton) view
                            .findViewById(radioType2.getCheckedRadioButtonId());
                    bt.startAnimation(buttonPress);
                    calculate();
                    break;
            }
        }
    };
    private ToggleButton twoCom;
    private Animation buttonPress;

    public BitwiseFragment() {}

    public static BitwiseFragment newInstance() { return new BitwiseFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bitwise, container, false);

        //initialize result panels and long click listeners
        dec = (TextView) view.findViewById(R.id.dec_result);
        dec.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                vibrate(40, 50);
                ClipboardManager cm =
                        (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("Dec", dec.getText().toString()
                        .trim().replaceAll(",", "")));
                Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        bin = (TextView) view.findViewById(R.id.bin_result);
        bin.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                vibrate(40, 50);
                ClipboardManager cm =
                        (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("Bin", bin.getText().toString()
                        .trim().replaceAll(" ", "")));
                Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        hex = (TextView) view.findViewById(R.id.hex_result);
        hex.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                vibrate(40, 50);
                ClipboardManager cm =
                        (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("Hex", hex.getText().toString()
                        .trim().replaceAll(" ", "")));
                Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //initialize inputs
        //start as dec
        input1 = (TextInputEditText) view.findViewById(R.id.input1_edit);
        input1.setInputType(InputType.TYPE_CLASS_DATETIME);
        type1 = 0;

        //listen for change
        input1.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (radioRow1.getCheckedRadioButtonId() == -1 &&
                        radioRow2.getCheckedRadioButtonId() == -1)
                    radioRow1.check(R.id.con);
                calculate();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        //handle Done button
        input1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                    input1.clearFocus();
                return false;
            }
        });

        //handle type from radio group
        radioType1 = (RadioGroup) view.findViewById(R.id.input1_type);
        radioType1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton bt;
                vibrate(5, 10);
                switch (i) {
                    case R.id.dec_type1:
                        type1 = 0;
                        if (twoCom.isChecked())
                            input1.setInputType(InputType.TYPE_CLASS_NUMBER |
                                                InputType.TYPE_NUMBER_FLAG_SIGNED);
                        else
                            input1.setInputType(InputType.TYPE_CLASS_DATETIME);
                        bt = (RadioButton) view
                                .findViewById(radioType1.getCheckedRadioButtonId());
                        bt.startAnimation(buttonPress);
                        calculate();
                        break;
                    case R.id.bin_type1:
                        type1 = 1;
                        input1.setInputType(InputType.TYPE_CLASS_DATETIME);
                        bt = (RadioButton) view
                                .findViewById(radioType1.getCheckedRadioButtonId());
                        bt.startAnimation(buttonPress);
                        calculate();
                        break;
                    case R.id.hex_type1:
                        type1 = 2;
                        input1.setInputType(InputType.TYPE_CLASS_TEXT);
                        bt = (RadioButton) view
                                .findViewById(radioType1.getCheckedRadioButtonId());
                        bt.startAnimation(buttonPress);
                        calculate();
                        break;
                }
            }
        });

        //start as dec
        input2 = (TextInputEditText) view.findViewById(R.id.input2_edit);
        input2.setInputType(InputType.TYPE_CLASS_DATETIME);
        type2 = 0;

        //listen for change
        input2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculate();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        //handle Done button
        input2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                    input2.clearFocus();
                return false;
            }
        });

        //handle type from radio group
        radioType2 = (RadioGroup) view.findViewById(R.id.input2_type);
        radioType2.setOnCheckedChangeListener(type2List);

        //initialize radio groups for operation
        operation = 0;
        radioRow1 = (RadioGroup) view.findViewById(R.id.radio_row1);
        radioRow2 = (RadioGroup) view.findViewById(R.id.radio_row2);

        //initialize animations
        buttonPress = AnimationUtils.loadAnimation(getContext(), R.anim.button_press);

        //initialize 2's compliment
        twoCom = (ToggleButton) view.findViewById(R.id.com);
        twoCom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                twoCom.startAnimation(buttonPress);
                vibrate(5, 10);
                if (twoCom.isChecked()) {
                    input1.setInputType(InputType.TYPE_CLASS_NUMBER |
                                        InputType.TYPE_NUMBER_FLAG_SIGNED);
                    input2.setInputType(InputType.TYPE_CLASS_NUMBER |
                                        InputType.TYPE_NUMBER_FLAG_SIGNED);
                }
                else {
                    input1.setInputType(InputType.TYPE_CLASS_DATETIME);
                    input2.setInputType(InputType.TYPE_CLASS_DATETIME);
                }
                calculate();
            }
        });

        //set listeners and start convert operation
        disableInput2();
        radioRow1.setOnCheckedChangeListener(row1);
        radioRow2.setOnCheckedChangeListener(row2);

        return view;
    }

    /**
     * Enable input 2 panel.
     */
    private void enableInput2() {
        input2.setEnabled(true);
        input2.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
        input2.setClickable(true);
        TextView title = (TextView) view.findViewById(R.id.input2_title);
        title.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
        for (int i = 0; i < radioType2.getChildCount(); i++)
            radioType2.getChildAt(i).setEnabled(true);
        radioType2.setOnCheckedChangeListener(type2List);
        if (radioType2.getCheckedRadioButtonId() == -1) radioType2.check(R.id.dec_type2);
    }

    /**
     * Disable input 2 panel.
     */
    private void disableInput2() {
        input2.setTextColor(ContextCompat.getColor(getContext(), R.color.toggle_off));
        input2.setEnabled(false);
        input2.setClickable(false);
        TextView title = (TextView) view.findViewById(R.id.input2_title);
        title.setTextColor(ContextCompat.getColor(getContext(), R.color.toggle_off));
        radioType2.setOnCheckedChangeListener(null);
        radioType2.clearCheck();
        for (int i = 0; i < radioType2.getChildCount(); i++)
            radioType2.getChildAt(i).setEnabled(false);
    }

    /**
     * Calculate the result
     */
    private void calculate() {
        try {
            dec.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
            bin.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
            hex.setTextColor(ContextCompat.getColor(getContext(), R.color.text));

            switch (operation) {
                case 0:
                    disableInput2();
                    convert();
                    break;
                case 1:
                    enableInput2();
                    arithmetic("add");
                    break;
                case 2:
                    enableInput2();
                    arithmetic("sub");
                    break;
                case 3:
                    enableInput2();
                    arithmetic("mul");
                    break;
                case 4:
                    enableInput2();
                    arithmetic("div");
                    break;
                case 5:
                    enableInput2();
                    arithmetic("and");
                    break;
                case 6:
                    enableInput2();
                    arithmetic("or");
                    break;
                case 7:
                    enableInput2();
                    arithmetic("xor");
                    break;
                case 8:
                    enableInput2();
                    arithmetic("lShift");
                    break;
                case 9:
                    enableInput2();
                    arithmetic("rShift");
                    break;
            }
        } catch (Exception e) {
            if (!input1.getText().toString().trim().equals("") &&
                !input2.getText().toString().trim().equals("") &&
                !input1.getText().toString().trim().equals("-")&&
                !input2.getText().toString().trim().equals("-")) {
                dec.setTextSize(25);
                dec.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                dec.setText(R.string.error);
                bin.setTextSize(25);
                bin.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                bin.setText(R.string.error);
                hex.setTextSize(25);
                hex.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                hex.setText(R.string.error);
            }
            else {
                dec.setText("");
                bin.setText("");
                hex.setText("");
            }
            e.printStackTrace();
        }
    }

    /**
     * Converts value in input1.
     */
    private void convert() {
        String n = input1.getText().toString().toLowerCase().replace(" ", "");
        String decimal = "", binary = "", hexadecimal = "";

        switch (type1) {
            case 0:
                if (twoCom.isChecked()) {
                    long num = Long.parseLong(n);

                    if (num >= Short.MIN_VALUE && num < 0) {
                        decimal = NumberFormat.getInstance().format(Integer.parseInt(n));
                        binary = Integer.toBinaryString(0xFFFF & Integer.parseInt(n));
                        hexadecimal = Integer.toHexString(0xFFFF & Integer.parseInt(n))
                                .toUpperCase();
                    }
                    else if (num >= Integer.MIN_VALUE && num < 0) {
                        decimal = NumberFormat.getInstance().format(Integer.parseInt(n));
                        binary = Integer.toBinaryString(Integer.parseInt(n));
                        hexadecimal = Integer.toHexString(Integer.parseInt(n)).toUpperCase();
                    }
                    else {
                        decimal = NumberFormat.getInstance().format(Long.parseLong(n));
                        binary = Long.toBinaryString(Long.parseLong(n));
                        hexadecimal = Long.toHexString(Long.parseLong(n)).toUpperCase();
                    }
                }
                else {
                    decimal = NumberFormat.getInstance().format(Long.parseLong(n));
                    binary = Long.toBinaryString(Long.parseLong(n));
                    hexadecimal = Long.toHexString(Long.parseLong(n)).toUpperCase();
                }
                break;
            case 1:
                if (twoCom.isChecked()) {
                    decimal = (n.charAt(0) == '1')?
                        "-" + (NumberFormat.getInstance()
                                .format(Long.parseLong(twosCompliment(n), 2))):
                        NumberFormat.getInstance().format(Long.parseLong(n, 2));
                }
                else {
                    decimal = NumberFormat.getInstance().format(Long.parseLong(n, 2));
                }
                binary = n;
                hexadecimal = Long.toHexString(Long.parseLong(n, 2)).toUpperCase();
                break;
            case 2:
                binary = Long.toBinaryString(Long.parseLong(n, 16));
                hexadecimal = n.toUpperCase();
                if (twoCom.isChecked())
                    decimal = (binary.charAt(0) == '1')?
                            "-" + (NumberFormat.getInstance()
                                    .format(Long.parseLong(twosCompliment(binary), 2))):
                            NumberFormat.getInstance().format(Long.parseLong(binary, 2));
                else
                    decimal = NumberFormat.getInstance().format(Long.parseLong(n, 16));
                break;
        }

        //adjust text sizes to fit
        bin.setTextSize((binary.length() > 16)? 18 : 25);
        dec.setTextSize((decimal.length() > 20)? 20 : 25);

        dec.setText(decimal);
        bin.setText(groupBinary(binary));
        hex.setText(groupHex(hexadecimal));
    }

    /**
     * Performs arithmetic operations.
     *
     * @param operation operation to perform
     */
    private void arithmetic(String operation) {
        String i0 = input1.getText().toString().toLowerCase().replace(" ", "");
        String i1 = input2.getText().toString().toLowerCase().replace(" ", "");
        long v0 = 0, v1 = 0,  res = 0;
        String decimal, binary, hexadecimal;

        switch (type1) {
            case 0:
                v0 = Long.parseLong(i0);
                break;
            case 1:
                if (twoCom.isChecked()) {
                    v0 = (i0.charAt(0) == '1')?
                            (-1 * Long.parseLong(twosCompliment(i0), 2)):
                            Long.parseLong(i0, 2);
                }
                else v0 = Long.parseLong(i0, 2);
                break;
            case 2:
                if (twoCom.isChecked()) {
                    String tempBin = Long.toBinaryString(Long.parseLong(i0, 16));
                    v0 = (tempBin.charAt(0) == '1')?
                            (-1 * Long.parseLong(twosCompliment(tempBin), 2)):
                            Long.parseLong(tempBin, 2);
                }
                else v0 = Long.parseLong(i0, 16);
                break;
        }

        switch (type2) {
            case 0:
                v1 = Long.parseLong(i1);
                break;
            case 1:
                if (twoCom.isChecked()) {
                    v1 = (i1.charAt(0) == '1')?
                            (-1 * Long.parseLong(twosCompliment(i1), 2)):
                            Long.parseLong(i1, 2);
                }
                else v1 = Long.parseLong(i1, 2);
                break;
            case 2:
                if (twoCom.isChecked()) {
                    String tempBin = Long.toBinaryString(Long.parseLong(i1, 16));
                    v1 = (tempBin.charAt(0) == '1')?
                            (-1 * Long.parseLong(twosCompliment(tempBin), 2)):
                            Long.parseLong(tempBin, 2);
                }
                else v1 = Long.parseLong(i1, 16);
                break;
        }

        switch (operation) {
            case "add":
                res = v0 + v1;
                break;
            case "sub":
                res = v0 - v1;
                break;
            case "mul":
                res = v0 * v1;
                break;
            case "div":
                res = v0 / v1;
                break;
            case "and":
                res = v0 & v1;
                break;
            case "or":
                res = v0 | v1;
                break;
            case "xor":
                res = v0 ^ v1;
                break;
            case "lShift":
                res = v0 << v1;
                break;
            case "rShift":
                res = (twoCom.isChecked())? (v0 >> v1) : (v0 >>> v1);
                break;
        }

        if (res >= Short.MIN_VALUE && res < 0) {
            decimal = NumberFormat.getInstance().format(res);
            binary = Integer.toBinaryString(0xFFFF & Math.toIntExact(res));
            hexadecimal = Integer.toHexString(0xFFFF & Math.toIntExact(res)).toUpperCase();
        }
        else if (res >= Integer.MIN_VALUE && res < 0) {
            decimal = NumberFormat.getInstance().format(Math.toIntExact(res));
            binary = Integer.toBinaryString(Math.toIntExact(res));
            hexadecimal = Integer.toHexString(Math.toIntExact(res)).toUpperCase();
        }
        else {
            decimal = NumberFormat.getInstance().format(res);
            binary = Long.toBinaryString(res);
            hexadecimal = Long.toHexString(res).toUpperCase();
        }

        //adjust text sizes to fit
        bin.setTextSize((binary.length() > 16)? 18 : 25);
        dec.setTextSize((decimal.length() > 20)? 20 : 25);

        dec.setText(decimal);
        bin.setText(groupBinary(binary));
        hex.setText(groupHex(hexadecimal));
    }

    /**
     * Spaces out binary strings by groups of 4 or 8 depending on length.
     *
     * @param binary original binary string
     * @return spaced out binary string
     */
    private String groupBinary(String binary) {
        StringBuilder sb = new StringBuilder();
        int n = (binary.length() > 16)? 8 : 4;

        if (binary.length() <= 4)
            binary = String.format("%4s", binary).replaceAll(" ", "0");
        else if (binary.length() <= 8)
            binary = String.format("%8s", binary).replaceAll(" ", "0");
        else if (binary.length() <= 12)
            binary = String.format("%12s", binary).replaceAll(" ", "0");
        else if (binary.length() <= 16)
            binary = String.format("%16s", binary).replaceAll(" ", "0");
        else if (binary.length() <= 24)
            binary = String.format("%24s", binary).replaceAll(" ", "0");
        else if (binary.length() <= 32)
            binary = String.format("%32s", binary).replaceAll(" ", "0");
        else if (binary.length() <= 40)
            binary = String.format("%40s", binary).replaceAll(" ", "0");
        else if (binary.length() <= 48)
            binary = String.format("%48s", binary).replaceAll(" ", "0");
        else if (binary.length() <= 56)
            binary = String.format("%56s", binary).replaceAll(" ", "0");
        else
            binary = String.format("%64s", binary).replaceAll(" ", "0");

        for (int i = binary.length() - 1; i >= 0; i--) {
            if (i != binary.length() - 1 && i != 0 && i % n == 0)
                sb.insert(0, binary.charAt(i)).insert(0, " ");
            else
                sb.insert(0, binary.charAt(i));
        }

        return sb.toString();
    }

    /**
     * Spaces out hex string in groups of 1, 2, or 4 depending on length.
     *
     * @param hex original hex string
     * @return spaced out hex string
     */
    private String groupHex(String hex) {
        StringBuilder sb = new StringBuilder();

        if (hex.length() <= 1)
            hex = String.format("%1s", hex).replaceAll(" ", "0");
        else if (hex.length() <= 2)
            hex = String.format("%2s", hex).replaceAll(" ", "0");
        else if (hex.length() <= 4)
            hex = String.format("%4s", hex).replaceAll(" ", "0");
        else if (hex.length() <= 8)
            hex = String.format("%8s", hex).replaceAll(" ", "0");
        else if (hex.length() <= 12)
            hex = String.format("%12s", hex).replaceAll(" ", "0");
        else
            hex = String.format("%16s", hex).replaceAll(" ", "0");

        for (int i = hex.length() - 1; i >= 0; i--) {
            if (i != 0 && i % 4 == 0)
                sb.insert(0, hex.charAt(i)).insert(0, " ");
            else
                sb.insert(0, hex.charAt(i));
        }
        return sb.toString();
    }

    /**
     * Finds the 2's compliment of a binary string.
     *
     * @param bin original binary string
     * @return 2's compliment of binary string
     */
    private String twosCompliment(String bin) {
        String twos = "", ones = "";

        if (bin.charAt(0) == '1' && Integer.parseInt(bin.substring(1)) == 0)
            return bin;

        for (int i = 0; i < bin.length(); i++) {
            ones += onesCompliment(bin.charAt(i));
        }

        StringBuilder builder = new StringBuilder(ones);
        boolean b = false;
        for (int i = ones.length() - 1; i > 0; i--) {
            if (ones.charAt(i) == '1') {
                builder.setCharAt(i, '0');
            } else {
                builder.setCharAt(i, '1');
                b = true;
                break;
            }
        }
        if (!b)
            builder.append("1", 0, 7);

        twos = builder.toString();

        return twos;
    }

    /**
     * Finds the ones compliment of a bit.
     *
     * @param c bit to grow_from_bottom
     * @return 1's compliment of bit
     */
    private char onesCompliment(char c) {
        return (c == '0') ? '1' : '0';
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
}
