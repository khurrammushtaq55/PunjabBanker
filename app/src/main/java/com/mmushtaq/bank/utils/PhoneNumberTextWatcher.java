package com.mmushtaq.bank.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class PhoneNumberTextWatcher implements TextWatcher {

    private static final String TAG = "PhoneNumberTextWatcher";
    private final EditText editText;

    public PhoneNumberTextWatcher(EditText edTxtPhone) {
        this.editText = edTxtPhone;
    }

    public void onTextChanged(CharSequence s, int cursorPosition, int before,
                              int count) {

        if (before == 0 && count == 1) {  //Entering values

            String val = s.toString();
            String a = "";
            String b = "";
            String c = "";
            if (val != null && val.length() > 0) {
                val = val.replace("-", "");
                if (val.length() >= 4) {
                    a = val.substring(0, 4);
                } else if (val.length() < 4) {
                    a = val;
                }
                if (val.length() >= 8) {
                    b = val.substring(4, 8);
                    c = val.substring(8);
                } else if (val.length() > 4 && val.length() < 8) {
                    b = val.substring(4);
                }
                StringBuilder stringBuffer = new StringBuilder();
                if (a != null && a.length() > 0) {
                    stringBuffer.append(a);

                }
                if (b != null && b.length() > 0) {
                    stringBuffer.append("-");
                    stringBuffer.append(b);

                }
                if (c != null && c.length() > 0) {
                    stringBuffer.append("-");
                    stringBuffer.append(c);
                }
                editText.removeTextChangedListener(this);
                editText.setText(stringBuffer.toString());
                if (cursorPosition == 4 || cursorPosition == 9) {
                    cursorPosition = cursorPosition + 2;
                } else {
                    cursorPosition = cursorPosition + 1;
                }
                if (cursorPosition <= editText.getText().toString().length()) {
                    editText.setSelection(cursorPosition);
                } else {
                    editText.setSelection(editText.getText().toString().length());
                }
                editText.addTextChangedListener(this);
            } else {
                editText.removeTextChangedListener(this);
                editText.setText("");
                editText.addTextChangedListener(this);
            }

        }

        if (before == 1 && count == 0) {  //Deleting values

            String val = s.toString();
            String a = "";
            String b = "";
            String c = "";

            if (val != null && val.length() > 0) {
                val = val.replace("-", "");
                if (cursorPosition == 4) {
                    val = removeCharAt(val, cursorPosition - 1, s.toString().length() - 1);
                } else if (cursorPosition == 9) {
                    val = removeCharAt(val, cursorPosition - 2, s.toString().length() - 2);
                }
                if (val.length() >= 4) {
                    a = val.substring(0, 4);
                } else if (val.length() < 4) {
                    a = val;
                }
                if (val.length() >= 8) {
                    b = val.substring(4, 8);
                    c = val.substring(8);
                } else if (val.length() > 4 && val.length() < 8) {
                    b = val.substring(4);
                }
                StringBuilder stringBuffer = new StringBuilder();
                if (a != null && a.length() > 0) {
                    stringBuffer.append(a);

                }
                if (b != null && b.length() > 0) {
                    stringBuffer.append("-");
                    stringBuffer.append(b);

                }
                if (c != null && c.length() > 0) {
                    stringBuffer.append("-");
                    stringBuffer.append(c);
                }
                editText.removeTextChangedListener(this);
                editText.setText(stringBuffer.toString());
                if (cursorPosition == 4 || cursorPosition == 9) {
                    cursorPosition = cursorPosition - 1;
                }
                if (cursorPosition <= editText.getText().toString().length()) {
                    editText.setSelection(cursorPosition);
                } else {
                    editText.setSelection(editText.getText().toString().length());
                }
                editText.addTextChangedListener(this);
            } else {
                editText.removeTextChangedListener(this);
                editText.setText("");
                editText.addTextChangedListener(this);
            }

        }


    }

    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    public void afterTextChanged(Editable s) {


    }

    public static String removeCharAt(String s, int pos, int length) {

        String value = "";
        if (length > pos) {
            value = s.substring(pos + 1);
        }
        return s.substring(0, pos) + value;
    }
}