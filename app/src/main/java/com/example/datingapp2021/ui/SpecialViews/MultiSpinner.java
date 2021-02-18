package com.example.datingapp2021.ui.SpecialViews;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiSpinner extends androidx.appcompat.widget.AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnCancelListener {

    private List<String> items;
    private boolean[] selected;
    private MultiSpinnerListener listener;
    private int arrayResId;

    public MultiSpinner(Context context) {
        super(context);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        selected[which] = isChecked;
        listener.itemSelected(selected);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // refresh text on spinner
        StringBuilder spinnerBuffer = new StringBuilder();
        boolean someSelected = false;
        for (int i = 0; i < items.size(); i++) {
            if (selected[i]) {
                spinnerBuffer.append(items.get(i));
                spinnerBuffer.append(", ");
                someSelected = true;
            }
        }
        String spinnerText;
        if (someSelected) {
            spinnerText = spinnerBuffer.toString();
            if (spinnerText.length() > 2)
                spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
        } else {
            spinnerText = Arrays.toString(getResources().getStringArray(arrayResId));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[] { spinnerText });
        setAdapter(adapter);
        listener.onItemsSelected(selected);
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(
                items.toArray(new CharSequence[items.size()]), selected, this);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.setOnCancelListener(this);
        builder.show();
        return true;
    }

    public void setItems(int textArrayResId, int textViewResId, MultiSpinnerListener listener) {
        List<String> items = Arrays.asList(getResources().getStringArray(textArrayResId).clone());
        this.arrayResId = textArrayResId;
        this.items = items;
        this.listener = listener;
        // all text on the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), textArrayResId, textViewResId);
        setAdapter(adapter);
    }

    public void setItemsSelected(ArrayList<Integer> ints){
        if (ints.size() == 0){
            selected = new boolean[items.size()];
            return;
        }
        selected = new boolean[items.size()];
        boolean needsToLoop = false;
        for (int i = 0; i < selected.length; i++){
            if (i == ints.get(i)){
                selected[i] = true;
                ints.remove(i);
                if (ints.size() > 0)
                    needsToLoop = true;
                break;
            }
        }
        while (needsToLoop){
            setItemsSelected(ints);
        }
    }

    public interface MultiSpinnerListener {
        void onItemsSelected(boolean[] selected);
        void itemSelected(boolean[] selected);
    }
}
