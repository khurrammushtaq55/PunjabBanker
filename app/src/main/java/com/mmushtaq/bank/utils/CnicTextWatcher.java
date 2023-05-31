package com.mmushtaq.bank.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CnicTextWatcher implements TextWatcher {

    private static final String TAG = "CnicTextWatcher";
    private final EditText editText;

    public CnicTextWatcher(EditText edTxtPhone) {
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
                if (val.length() >= 5) {
                    a = val.substring(0, 5);
                } else if (val.length() < 5) {
                    a = val;
                }
                if (val.length() >= 12) {
                    b = val.substring(5, 12);
                    c = val.substring(12);
                } else if (val.length() > 5 && val.length() < 12) {
                    b = val.substring(5);
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
                if (cursorPosition == 5 || cursorPosition == 13) {
                    cursorPosition = cursorPosition + 2;
                } else {
                    cursorPosition = cursorPosition + 1;
                }
                editText.setSelection(Math.min(cursorPosition, editText.getText().toString().length()));
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
                if (cursorPosition == 5) {
                    val = removeCharAt(val, cursorPosition - 1, s.toString().length() - 1);
                } else if (cursorPosition == 13) {
                    val = removeCharAt(val, cursorPosition - 2, s.toString().length() - 2);
                }
                if (val.length() >= 5) {
                    a = val.substring(0, 5);
                } else if (val.length() < 5) {
                    a = val;
                }
                if (val.length() >= 12) {
                    b = val.substring(5, 12);
                    c = val.substring(12);
                } else if (val.length() > 5 && val.length() < 12) {
                    b = val.substring(5);
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
                if (cursorPosition == 5 || cursorPosition == 13) {
                    cursorPosition = cursorPosition - 1;
                }
                editText.setSelection(Math.min(cursorPosition, editText.getText().toString().length()));
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