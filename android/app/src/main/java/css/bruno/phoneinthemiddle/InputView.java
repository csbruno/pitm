package css.bruno.phoneinthemiddle;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class InputView extends View implements InputManagerCompat.InputDeviceListener {
    //DPADs
    private static final String DPAD_UP = "19";
    private static final String DPAD_DOWN = "20";
    private static final String DPAD_LEFT = "21";
    private static final String DPAD_RIGHT = "22";
    //XYAB Buttons
    private static final String BUTTON_X = "98";
    private static final String BUTTON_Y = "99";
    private static final String BUTTON_A = "96";
    private static final String BUTTON_B = "97";
    //Menu buttons
    private static final String BUTTON_VIEW = "102";
    private static final String BUTTON_MENU = "103";
    private static final String XBOX = "82";
    //LB and RB
    private static final String TRIGGER_LEFT = "100";
    private static final String TRIGGER_RIGHT = "101";
    //Sticks buttons
    private static final String STICK_LEFT = "104";
    private static final String STICK_RIGHT = "105";
    //Sticks axis
    private static final String RIGHT_STICK_X = "990";
    private static final String RIGHT_STICK_Y = "991";
    private static final String LEFT_STICK_X = "992";
    private static final String LEFT_STICK_Y = "993";
    //Real triggers
    private static final String LEFT_TRIGGER_AXIS = "998";
    private static final String RIGHT_TRIGGER_AXIS = "999";
    //Log
    private static final String TAG = "ControllerInput";
    //
    private InputManagerCompat m_inputManager;
    private InputDevice m_inputDevice;
    private Map<String, String> keyData;

    private boolean isStickBusy = false; //For some reason the Left stick calls Dpad Buttons. This bool fix that, only allowing DPAD input when the leftstick is in the center
    private boolean newInput = false;

    private LogInput loginput;

    public InputView(Context context) {
        super(context);
        keyData = new HashMap<>();

        keyData.put(DPAD_UP, "0");
        keyData.put(DPAD_DOWN, "0");
        keyData.put(DPAD_LEFT, "0");
        keyData.put(DPAD_RIGHT, "0");

        keyData.put(BUTTON_X, "0");
        keyData.put(BUTTON_Y, "0");
        keyData.put(BUTTON_A, "0");
        keyData.put(BUTTON_B, "0");

        keyData.put(BUTTON_VIEW, "0");
        keyData.put(BUTTON_MENU, "0");
        keyData.put(XBOX, "0");

        keyData.put(TRIGGER_LEFT, "0");
        keyData.put(TRIGGER_RIGHT, "0");

        keyData.put(STICK_LEFT, "0");
        keyData.put(STICK_RIGHT, "0");

        keyData.put(RIGHT_STICK_X, "0");
        keyData.put(RIGHT_STICK_Y, "0");

        keyData.put(LEFT_STICK_X, "0");
        keyData.put(LEFT_STICK_Y, "0");
    }

    public InputView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean get_newInput() {
        return newInput;
    }

    public void set_newInput(boolean _Change) {
        this.newInput = _Change;
    }

    public LogInput getLoginput() {
        return loginput;
    }

    public void setLoginput(LogInput loginput) {
        this.loginput = loginput;
    }

    public void initData() {
        if (keyData != null) return;
        keyData = new HashMap<>();
        keyData.put(DPAD_UP, "0");
        keyData.put(DPAD_DOWN, "0");
        keyData.put(DPAD_LEFT, "0");
        keyData.put(DPAD_RIGHT, "0");

        keyData.put(BUTTON_X, "0");
        keyData.put(BUTTON_Y, "0");
        keyData.put(BUTTON_A, "0");
        keyData.put(BUTTON_B, "0");

        keyData.put(BUTTON_VIEW, "0");
        keyData.put(BUTTON_MENU, "0");
        keyData.put(XBOX, "0");

        keyData.put(TRIGGER_LEFT, "0");
        keyData.put(TRIGGER_RIGHT, "0");

        keyData.put(STICK_LEFT, "0");
        keyData.put(STICK_RIGHT, "0");

        keyData.put(RIGHT_STICK_X, "0");
        keyData.put(RIGHT_STICK_Y, "0");

        keyData.put(LEFT_STICK_X, "0");
        keyData.put(LEFT_STICK_Y, "0");

    }

    public Map<String, String> getKeyData() {
        return keyData;
    }

    private void findControllers() {
        initData();
        if (m_inputManager == null || m_inputDevice == null) {
            m_inputManager = InputManagerCompat.Factory.getInputManager(getContext());
            m_inputManager.registerInputDeviceListener(this, null);

        }
        int[] deviceIds = m_inputManager.getInputDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice dev = m_inputManager.getInputDevice(deviceId);
            int sources = dev.getSources();
            // if the device is a gamepad/joystick, create a ship to represent it
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) ||
                    ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
                // if the device has a gamepad or joystick
                Log.e(TAG, "Bluetooth Device " + deviceId + " added");
            }
        }
    }

    public void reset() {
        findControllers();
    }

    public boolean onGenericMotionEvent(MotionEvent event) {

        if (event == null || m_inputManager == null) return false;

        m_inputManager.onGenericMotionEvent(event);

        // Check that the event came from a joystick or gamepad since a generic
        // motion event could be almost anything. API level 18 adds the useful
        // event.isFromSource() helper function.
        int eventSource = event.getSource();
        if ((((eventSource & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) ||
                ((eventSource & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK))
                && event.getAction() == MotionEvent.ACTION_MOVE) {
            int id = event.getDeviceId();
            if (-1 != id) {
                handleGenericMotionEvent(event);
            }
        }
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return onKeyDown(event.getKeyCode(), event);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            return onKeyUp(event.getKeyCode(), event);
        }
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        if (keyData == null) return false;

        int deviceId = event.getDeviceId();
        if (deviceId != -1) {
            if (event.getRepeatCount() == 0) {
                //UDPClient.SendButton(keyCode,false);
                newInput = true;
                switch (String.valueOf(keyCode)) {
                    case DPAD_LEFT:

                        if (loginput != null) loginput.onButton("KEYCODE_DPAD_LEFT DOWN");
                        if (!isStickBusy) keyData.put(DPAD_LEFT, "1");
                        handled = true;
                        break;
                    case DPAD_RIGHT:
                        if (loginput != null) loginput.onButton("KEYCODE_DPAD_RIGHT DOWN");
                        if (!isStickBusy) keyData.put(DPAD_RIGHT, "1");
                        handled = true;
                        break;
                    case DPAD_UP:
                        if (loginput != null) loginput.onButton("KEYCODE_DPAD_UP DOWN");
                        if (!isStickBusy) keyData.put(DPAD_UP, "1");
                        handled = true;
                        break;
                    case DPAD_DOWN:
                        if (loginput != null) loginput.onButton("KEYCODE_DPAD_DOWN DOWN");
                        if (!isStickBusy) keyData.put(DPAD_DOWN, "1");
                        handled = true;
                        break;
                    case BUTTON_X:
                        if (loginput != null) loginput.onButton("KEYCODE_X  DOWN");
                        keyData.put(BUTTON_X, "1");
                        handled = true;
                        break;
                    case BUTTON_A:
                        if (loginput != null) loginput.onButton("KEYCODE_A DOWN");
                        handled = true;
                        keyData.put(BUTTON_A, "1");
                        break;
                    case BUTTON_Y:
                        if (loginput != null) loginput.onButton("KEYCODE_Y DOWN");
                        handled = true;
                        keyData.put(BUTTON_Y, "1");
                        break;
                    case BUTTON_B:
                        if (loginput != null) loginput.onButton("KEYCODE_B DOWN");
                        handled = true;
                        keyData.put(BUTTON_B, "1");
                        break;
                    case BUTTON_MENU:
                        if (loginput != null) loginput.onButton("KEYCODE_MENU DOWN");
                        handled = true;
                        keyData.put(BUTTON_MENU, "1");
                        break;
                    case XBOX:
                        if (loginput != null) loginput.onButton("XBOX DOWN");
                        handled = true;
                        keyData.put(XBOX, "1");
                        break;
                    case BUTTON_VIEW:
                        if (loginput != null) loginput.onButton("VIEW DOWN");
                        handled = true;
                        keyData.put(BUTTON_VIEW, "1");
                        break;
                    case STICK_RIGHT:
                        if (loginput != null) loginput.onButton("STICK_RIGHT DOWN");
                        handled = true;
                        keyData.put(STICK_RIGHT, "1");
                        break;
                    case STICK_LEFT:
                        if (loginput != null) loginput.onButton("STICK_LEFT DOWN");
                        handled = true;
                        keyData.put(STICK_LEFT, "1");
                        break;
                    case TRIGGER_LEFT:
                        if (loginput != null) loginput.onButton("TRIGGER_LEFT DOWN");
                        handled = true;
                        keyData.put(TRIGGER_LEFT, "1");
                        break;
                    case TRIGGER_RIGHT:
                        if (loginput != null) loginput.onButton("TRIGGER_RIGHT DOWN");
                        handled = true;
                        keyData.put(TRIGGER_RIGHT, "1");
                        break;
                    default:
                        keyData.put(String.valueOf(keyCode), "1");
                        break;
                }
            }
        }
        return handled;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean handled = false;
        int deviceId = event.getDeviceId();
        if (keyData == null) return false;

        if (deviceId != -1) {

            newInput = true;
            if (event.getRepeatCount() == 0) {
                switch (String.valueOf(keyCode)) {
                    case DPAD_LEFT:

                        if (loginput != null) loginput.onButton("KEYCODE_DPAD_LEFT UP");
                        if (!isStickBusy) keyData.put(DPAD_LEFT, "0");
                        handled = true;
                        break;
                    case DPAD_RIGHT:
                        if (loginput != null) loginput.onButton("KEYCODE_DPAD_RIGHT UP");
                        if (!isStickBusy) keyData.put(DPAD_RIGHT, "0");
                        handled = true;
                        break;
                    case DPAD_UP:
                        if (loginput != null) loginput.onButton("KEYCODE_DPAD_UP UP");
                        if (!isStickBusy) keyData.put(DPAD_UP, "0");
                        handled = true;
                        break;
                    case DPAD_DOWN:
                        if (loginput != null) loginput.onButton("KEYCODE_DPAD_DOWN UP");
                        if (!isStickBusy) keyData.put(DPAD_DOWN, "0");
                        handled = true;
                        break;
                    case BUTTON_X:
                        if (loginput != null) loginput.onButton("KEYCODE_X  UP");
                        keyData.put(BUTTON_X, "0");
                        handled = true;
                        break;
                    case BUTTON_A:
                        if (loginput != null) loginput.onButton("KEYCODE_A UP");
                        handled = true;
                        keyData.put(BUTTON_A, "0");
                        break;
                    case BUTTON_Y:
                        if (loginput != null) loginput.onButton("KEYCODE_Y UP");
                        handled = true;
                        keyData.put(BUTTON_Y, "0");
                        break;
                    case BUTTON_B:
                        if (loginput != null) loginput.onButton("KEYCODE_B UP");
                        handled = true;
                        keyData.put(BUTTON_B, "0");
                        break;
                    case BUTTON_MENU:
                        if (loginput != null) loginput.onButton("KEYCODE_MENU UP");
                        handled = true;
                        keyData.put(BUTTON_MENU, "0");
                        break;
                    case XBOX:
                        if (loginput != null) loginput.onButton("XBOX UP");
                        handled = true;
                        keyData.put(XBOX, "0");
                        break;
                    case BUTTON_VIEW:
                        if (loginput != null) loginput.onButton("VIEW UP");
                        handled = true;
                        keyData.put(BUTTON_VIEW, "0");
                        break;
                    case STICK_RIGHT:
                        if (loginput != null) loginput.onButton("STICK_RIGHT UP");
                        handled = true;
                        keyData.put(STICK_RIGHT, "0");
                        break;
                    case STICK_LEFT:
                        if (loginput != null) loginput.onButton("STICK_LEFT UP");
                        handled = true;
                        keyData.put(STICK_LEFT, "0");
                        break;
                    case TRIGGER_LEFT:
                        if (loginput != null) loginput.onButton("TRIGGER_LEFT UP");
                        handled = true;
                        keyData.put(TRIGGER_LEFT, "0");
                        break;
                    case TRIGGER_RIGHT:
                        if (loginput != null) loginput.onButton("TRIGGER_RIGHT UP");
                        handled = true;
                        keyData.put(TRIGGER_RIGHT, "0");
                        break;
                    default:
                        keyData.put(String.valueOf(keyCode), "0");
                        break;
                }
            }
        }
        return handled;
    }


    private boolean handleGenericMotionEvent(MotionEvent event) {
        // Process all historical movement samples in the batch.
        final int historySize = event.getHistorySize();
        for (int i = 0; i < historySize; i++) {
            processJoystickInput(event, i);
        }

        // Process the current movement sample in the batch.
        processJoystickInput(event, -1);
        return true;
    }

    private void processJoystickInput(MotionEvent event, int historyPos) {

        if (null == m_inputDevice) {
            m_inputDevice = event.getDevice();
        }
        if (keyData == null) return;

        float lX = getCenteredAxis(event, m_inputDevice, MotionEvent.AXIS_X, historyPos);
        float lY = getCenteredAxis(event, m_inputDevice, MotionEvent.AXIS_Y, historyPos);
        float rX = getCenteredAxis(event, m_inputDevice, MotionEvent.AXIS_RX, historyPos);
        float rY = getCenteredAxis(event, m_inputDevice, MotionEvent.AXIS_RY, historyPos);
        float leftTrigger = getCenteredAxis(event, m_inputDevice, MotionEvent.AXIS_Z, historyPos);
        float rightTrigger = getCenteredAxis(event, m_inputDevice, MotionEvent.AXIS_RZ, historyPos);

        keyData.put(RIGHT_STICK_X, String.valueOf(Math.round((rX + 1) * 32768) / 2));
        keyData.put(RIGHT_STICK_Y, String.valueOf(Math.round((rY + 1) * 32768) / 2));
        keyData.put(LEFT_STICK_X, String.valueOf(Math.round((lX + 1) * 32768) / 2));
        keyData.put(LEFT_STICK_Y, String.valueOf(Math.round((lY + 1) * 32768) / 2));
        keyData.put(RIGHT_TRIGGER_AXIS, String.valueOf(Math.round((rightTrigger + 1) * 32768) / 2));
        keyData.put(LEFT_TRIGGER_AXIS, String.valueOf(Math.round((leftTrigger + 1) * 32768) / 2));

        isStickBusy = (lX != 0 | lY != 0) ? true : false;
        newInput = true;

    }

    private float getCenteredAxis(MotionEvent event, InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range = device.getMotionRange(axis, event.getSource());
        if (range != null) {
            final float flat = range.getFlat();
            final float value = historyPos < 0 ? event.getAxisValue(axis)
                    : event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            // A joystick at rest does not always report an absolute position of
            // (0,0).
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    @Override
    public void onInputDeviceAdded(int deviceId) {
        Log.e(TAG, "Bluetooth Device " + deviceId + " Added");
    }

    @Override
    public void onInputDeviceChanged(int deviceId) {
        Log.e(TAG, "onInputDeviceChanged" + String.valueOf(InputDevice.getDevice(deviceId)));
    }

    @Override
    public void onInputDeviceRemoved(int deviceId) {
        Log.e(TAG, "Bluetooth Device Removed");
    }

    interface LogInput {
        void onButton(String msg);

    }
}
