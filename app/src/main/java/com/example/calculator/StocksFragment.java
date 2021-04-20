package com.example.calculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

import static android.content.Context.VIBRATOR_SERVICE;

public class StocksFragment extends Fragment {
    private TextView change, percentChange;
    private EditText quantity, original, newVal;
    private String quantityAmount, originalAmount, newValAmount;

    public StocksFragment() {}

    public static StocksFragment newInstance() { return new StocksFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_stocks, container, false);

        //initialize change amount
        change = (TextView) view.findViewById(R.id.change);
        String changeAmount = "$0.00";
        change.setText(changeAmount);
        change.setTextColor(ContextCompat.getColor(getContext(), R.color.text));

        //handle change amount long press
        change.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                vibrate(40, 50);

                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("Change", change.getText().toString().trim()));

                Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        //initialize percent change amount
        percentChange = (TextView) view.findViewById(R.id.percent_change);
        String percentChangeAmount = "0%";
        percentChange.setText(percentChangeAmount);
        percentChange.setTextColor(ContextCompat.getColor(getContext(), R.color.text));

        //handle percent change long press
        percentChange.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                vibrate(40, 50);

                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("Percent Change", percentChange.getText().toString().trim()));

                Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        //initialize quantity
        quantity = (EditText) view.findViewById(R.id.quantity);
        quantityAmount = "1";
        quantity.setText(quantityAmount);

        //handle Done button on quantity keyboard
        quantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                    quantity.clearFocus();
                return false;
            }
        });

        //add numbers to quantity
        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateQuantity(charSequence, this);
                calculate();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        //initialize original
        original = (EditText) view.findViewById(R.id.original);
        originalAmount = "$0.00";
        original.setText(originalAmount);

        //handle Done button on original keyboard
        original.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                    original.clearFocus();
                return false;
            }
        });

        //add numbers to original
        original.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateOriginal(charSequence, this);
                calculate();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        //initialize newVal
        newVal = (EditText) view.findViewById(R.id.new_val);
        newValAmount = "$0.00";
        newVal.setText(newValAmount);

        //handle Done button on new keyboard
        newVal.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                    newVal.clearFocus();
                return false;
            }
        });

        //add numbers to new
        newVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateNew(charSequence, this);
                calculate();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        //initialize reset button
        final Button resetButton = (Button) view.findViewById(R.id.reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset(resetButton);
                vibrate(5, 25);
            }
        });

        return view;
    }

    /**
     * Calculates the percent difference and updates results.
     */
    private void calculate() {
        double num = Double.parseDouble(quantityAmount);
        double v0 = Double.parseDouble(originalAmount.replaceAll("[$,]", ""));
        double v1 = Double.parseDouble(newValAmount.replaceAll("[$,]", ""));
        StringBuilder sb = new StringBuilder();
        double result = num * (v1 - v0);
        double percent = 100 * ((v1 - v0) / v0);

        if (Double.isNaN(result)) result = 0;
        if (Double.isNaN(percent)) percent = 0;

        if (num == 0) {
            result = 0;
            percent = 0;
        }

        //change color based on change
        if (result == 0) {
            change.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
            percentChange.setTextColor(ContextCompat.getColor(getContext(), R.color.text));
        }
        else if (result > 0) {
            change.setTextColor(ContextCompat.getColor(getContext(), R.color.green_text));
            percentChange.setTextColor(ContextCompat.getColor(getContext(), R.color.green_text));

        }
        else {
            change.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            percentChange.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }

        result = Math.abs(result);
        percent = Math.abs(percent);

        change.setText(NumberFormat.getCurrencyInstance().format(result));
        DecimalFormat df = new DecimalFormat("0.##");
        percentChange.setText(sb.append(df.format(percent)).append("%"));
    }

    /**
     * Add number to quantity.
     *
     * @param charSequence EditText content
     * @param textWatcher current TextWatcher
     */
    private void updateQuantity(CharSequence charSequence, TextWatcher textWatcher) {
        quantity.removeTextChangedListener(textWatcher);

        String str = charSequence.toString();
        int x = (str.equals(""))? 0 : Integer.parseInt(str) % 100000000;
        quantityAmount = Integer.toString(x);

        quantity.setText(quantityAmount);
        quantity.setSelection(quantityAmount.length());

        quantity.addTextChangedListener(textWatcher);
    }

    /**
     * Add number to original.
     *
     * @param charSequence EditText content
     * @param textWatcher current TextWatcher
     */
    private void updateOriginal(CharSequence charSequence, TextWatcher textWatcher) {
        original.removeTextChangedListener(textWatcher);

        String cleanString = charSequence.toString().replaceAll("[$,.]", "");
        double parsed = Double.parseDouble(cleanString);
        originalAmount = NumberFormat.getCurrencyInstance().format((parsed / 100) % 100000);

        original.setText(originalAmount);
        original.setSelection(originalAmount.length());

        original.addTextChangedListener(textWatcher);
    }

    /**
     * Add number to new.
     *
     * @param charSequence EditText content
     * @param textWatcher current TextWatcher
     */
    private void updateNew(CharSequence charSequence, TextWatcher textWatcher) {
        newVal.removeTextChangedListener(textWatcher);

        String cleanString = charSequence.toString().replaceAll("[$,.]", "");
        double parsed = Double.parseDouble(cleanString);
        newValAmount = NumberFormat.getCurrencyInstance().format((parsed / 100) % 100000);

        newVal.setText(newValAmount);
        newVal.setSelection(newValAmount.length());

        newVal.addTextChangedListener(textWatcher);
    }

    private void reset(Button resetButton) {
        Animation press = AnimationUtils.loadAnimation(getContext(), R.anim.button_press);
        resetButton.startAnimation(press);
        quantityAmount = "1";
        quantity.setText(quantityAmount);
        originalAmount = "$0.00";
        original.setText(originalAmount);
        newValAmount = "$0.00";
        newVal.setText(newValAmount);
    }

    /**
     * Vibrates for certain length and amplitude.
     *
     * @param length length in ms
     * @param amplitude amplitude of vibration
     */
    private void vibrate(int length, int amplitude) {
        ((Vibrator) Objects.requireNonNull(Objects.requireNonNull(getActivity())
                .getSystemService(VIBRATOR_SERVICE)))
                .vibrate(VibrationEffect.createOneShot(length,amplitude));
    }
}