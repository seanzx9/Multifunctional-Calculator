package com.example.calculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.text.NumberFormat;
import static android.content.Context.VIBRATOR_SERVICE;

public class TipsFragment extends Fragment implements View.OnClickListener {
    private TextView total;
    private TextView tip;
    private EditText bill;
    private String billAmount;
    private EditText tipPercent;
    private String tipPercentAmount;
    private ImageButton tipLeft;
    private ImageButton tipRight;
    private EditText split;
    private String splitAmount;
    private ImageButton splitLeft;
    private ImageButton splitRight;
    private Animation buttonPress;
    private ConstraintLayout inputContainer;
    private Animation grow;

    public TipsFragment() {}

    public static TipsFragment newInstance() { return new TipsFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tips, container, false);

        //initialize total
        total = (TextView) view.findViewById(R.id.total);
        String totalAmount = "$0.00";
        total.setText(totalAmount);

        //handle total long press
        total.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                vibrate(40, 50);
                ClipboardManager cm =
                        (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("Total", total.getText().toString()
                        .trim()));
                Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //initialize tip
        tip = (TextView) view.findViewById(R.id.tip);
        String tipAmount = "$0.00";
        tip.setText(tipAmount);

        //handle tip long press
        tip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                vibrate(40, 50);
                ClipboardManager cm =
                        (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("Tip", tip.getText().toString()
                        .trim()));
                Toast.makeText(getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //initialize bill
        bill = (EditText) view.findViewById(R.id.bill);
        billAmount = "$0.00";
        bill.setText(billAmount);

        //handle Done button on bill keyboard
        bill.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                    bill.clearFocus();
                return false;
            }
        });

        //add numbers to bill
        bill.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateBill(charSequence, this);
                calculate();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        //initialize tip percent
        tipPercent = (EditText) view.findViewById(R.id.tip_percent);
        tipPercentAmount = "15%";
        tipPercent.setText(tipPercentAmount);

        //handle Done button on tip percent keyboard
        tipPercent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                    tipPercent.clearFocus();
                return false;
            }
        });

        //add numbers to percent
        tipPercent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateTipPercent(charSequence, this);
                calculate();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        //initialize tip buttons
        tipLeft = (ImageButton) view.findViewById(R.id.tip_left);
        tipLeft.setOnClickListener(this);
        tipRight = (ImageButton) view.findViewById(R.id.tip_right);
        tipRight.setOnClickListener(this);

        //initialize split
        split = (EditText) view.findViewById(R.id.split);
        splitAmount = "1";
        split.setText(splitAmount);

        //initialize split buttons
        splitLeft = (ImageButton) view.findViewById(R.id.split_left);
        splitLeft.setOnClickListener(this);
        splitRight = (ImageButton) view.findViewById(R.id.split_right);
        splitRight.setOnClickListener(this);

        //handle Done button on split keyboard
        split.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE)
                    split.clearFocus();
                return false;
            }
        });

        //add numbers to split
        split.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateSplit(charSequence, this);
                calculate();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        //initialize reset button
        Button resetButton = (Button) view.findViewById(R.id.reset);
        resetButton.setOnClickListener(this);

        //initialize animations
        buttonPress = AnimationUtils.loadAnimation(getContext(), R.anim.button_press);
        grow = AnimationUtils.loadAnimation(getContext(), R.anim.grow_from_bottom);
        grow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                billAmount = "$0.00";
                bill.setText(billAmount);
                tipPercentAmount = "15%";
                tipPercent.setText(tipPercentAmount);
                splitAmount = "1";
                split.setText(splitAmount);
                vibrate(5, 25);
            }

            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        //initialize container
        inputContainer = (ConstraintLayout) view.findViewById(R.id.input_container);

        return view;
    }

    /**
     * Calculate the total price and tip with current values.
     */
    private void calculate() {
        double cost = Double.parseDouble(billAmount.replaceAll("[$,]", ""));
        double percent = Double.parseDouble(tipPercentAmount.replaceAll("%", ""));
        double people = Double.parseDouble(splitAmount);

        double totalTip = cost * percent / 100;
        double totalPrice = cost + totalTip;

        total.setText(NumberFormat.getCurrencyInstance().format(totalPrice / people));
        tip.setText(NumberFormat.getCurrencyInstance().format(totalTip / people));
    }

    /**
     * Add number to the bill.
     *
     * @param charSequence EditText content
     * @param textWatcher current TextWatcher
     */
    private void updateBill(CharSequence charSequence, TextWatcher textWatcher) {
        bill.removeTextChangedListener(textWatcher);

        String cleanString =
                charSequence.toString().replaceAll("[$,.]", "");
        double parsed = Double.parseDouble(cleanString);
        billAmount = NumberFormat.getCurrencyInstance().format((parsed / 100) % 100000);

        bill.setText(billAmount);
        bill.setSelection(billAmount.length());

        bill.addTextChangedListener(textWatcher);
    }

    /**
     * Add number to tip.
     *
     * @param charSequence EditText content
     * @param textWatcher current TextWatcher
     */
    private void updateTipPercent(CharSequence charSequence, TextWatcher textWatcher) {
        tipPercent.removeTextChangedListener(textWatcher);

        String str =
                charSequence.toString().replaceAll("%", "");
        int x = (str.equals(""))? 0 : Integer.parseInt(str) % 100;
        StringBuilder sb = new StringBuilder();
        tipPercentAmount = sb.append(x).append("%").toString();

        tipPercent.setText(tipPercentAmount);
        tipPercent.setSelection(tipPercentAmount.length() - 1);

        tipPercent.addTextChangedListener(textWatcher);
    }

    /**
     * Add number to split.
     *
     * @param charSequence EditText content
     * @param textWatcher current TextWatcher
     */
    private void updateSplit(CharSequence charSequence, TextWatcher textWatcher) {
        split.removeTextChangedListener(textWatcher);

        String str = charSequence.toString();
        int x = (str.equals(""))? 1 : Integer.parseInt(str) % 100;
        splitAmount = Integer.toString(x);

        split.setText(splitAmount);
        split.setSelection(splitAmount.length());

        split.addTextChangedListener(textWatcher);
    }

    /**
     * Listener for all buttons.
     *
     * @param view current view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tip_left:
                vibrate(5, 50);
                decTip();
                calculate();
                break;
            case R.id.tip_right:
                vibrate(5, 50);
                incTip();
                calculate();
                break;
            case R.id.split_left:
                vibrate(5, 50);
                decSplit();
                calculate();
                break;
            case R.id.split_right:
                vibrate(5, 50);
                incSplit();
                calculate();
                break;
            case R.id.reset:
                inputContainer.startAnimation(grow);
                break;
        }
    }

    /**
     * Decrements the tip.
     */
    private void decTip() {
        int x = Integer.parseInt(tipPercentAmount.replaceAll("%", ""));
        if (x > 0) x -= 1;
        StringBuilder sb = new StringBuilder();
        tipPercentAmount = sb.append(x).append("%").toString();
        tipPercent.setText(tipPercentAmount);
        tipLeft.startAnimation(buttonPress);
    }

    /**
     * Increments the tip.
     */
    private void incTip() {
        int x = Integer.parseInt(tipPercentAmount.replaceAll("%", ""));
        if (x < 99) x += 1;
        StringBuilder sb = new StringBuilder();
        tipPercentAmount = sb.append(x).append("%").toString();
        tipPercent.setText(tipPercentAmount);
        tipRight.startAnimation(buttonPress);
    }

    /**
     * Decrements the split amount.
     */
    private void decSplit() {
        int x = Integer.parseInt(splitAmount);
        if (x > 1) x -= 1;
        splitAmount = Integer.toString(x);
        split.setText(splitAmount);
        splitLeft.startAnimation(buttonPress);
    }

    /**
     * Increments the split amount.
     */
    private void incSplit() {
        int x = Integer.parseInt(splitAmount);
        if (x < 99) x += 1;
        splitAmount = Integer.toString(x);
        split.setText(splitAmount);
        splitRight.startAnimation(buttonPress);
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
}
