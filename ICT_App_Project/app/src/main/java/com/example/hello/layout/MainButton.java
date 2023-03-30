package com.example.hello.layout;

import android.widget.CompoundButton;

public class MainButton {

    private static MainButton mainButton;
    private static CompoundButton toggleButton;

    private MainButton(CompoundButton toggleButton) {
        this.toggleButton = toggleButton;
    }

    public static void changeButtonState(ButtonContent buttonContent) {
        if (buttonContent==ButtonContent.OFF) {
            toggleButton.setChecked(false); // 켜졌으면 끄는버튼으로
            toggleButton.setText("UV 작동 중...");
        } else{
            toggleButton.setChecked(true); // 꺼졌으면 켜는버튼으로
            toggleButton.setText("UV 작동 안함...");
        }

    }

    public static MainButton getInstance() {
        if (mainButton == null) {
            throw new IllegalArgumentException();
        }
        return mainButton;
    }

    public static void makeInstance(CompoundButton toggleButton) {
        mainButton = new MainButton(toggleButton);
    }
}
