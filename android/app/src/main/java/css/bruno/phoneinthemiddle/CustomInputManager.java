package css.bruno.phoneinthemiddle;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Handler;
import android.view.InputDevice;
import android.view.MotionEvent;

import java.util.HashMap;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class CustomInputManager implements InputManagerCompat {

    private final InputManager mInputManager;
    private final Map<InputManagerCompat.InputDeviceListener, CustomInputDeviceListener> mListeners;

    public CustomInputManager(Context context) {
        mInputManager = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
        mListeners = new HashMap<InputManagerCompat.InputDeviceListener, CustomInputDeviceListener>();
    }

    @Override
    public InputDevice getInputDevice(int id) {
        return mInputManager.getInputDevice(id);
    }

    @Override
    public int[] getInputDeviceIds() {
        return mInputManager.getInputDeviceIds();
    }

    @Override
    public void registerInputDeviceListener(InputDeviceListener listener, Handler handler) {
        CustomInputDeviceListener customListener = new CustomInputDeviceListener(listener);
        mInputManager.registerInputDeviceListener(customListener, handler);
        mListeners.put(listener, customListener);
    }

    @Override
    public void unregisterInputDeviceListener(InputDeviceListener listener) {
        CustomInputDeviceListener curListener = mListeners.remove(listener);
        if (null != curListener) {
            mInputManager.unregisterInputDeviceListener(curListener);
        }

    }

    @Override
    public void onGenericMotionEvent(MotionEvent event) {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    static class CustomInputDeviceListener implements InputManager.InputDeviceListener {
        final InputManagerCompat.InputDeviceListener inputDeviceListener;

        public CustomInputDeviceListener(InputDeviceListener idl) {
            inputDeviceListener = idl;
        }

        @Override
        public void onInputDeviceAdded(int deviceId) {
            inputDeviceListener.onInputDeviceAdded(deviceId);
        }

        @Override
        public void onInputDeviceChanged(int deviceId) {
            inputDeviceListener.onInputDeviceChanged(deviceId);
        }

        @Override
        public void onInputDeviceRemoved(int deviceId) {
            inputDeviceListener.onInputDeviceRemoved(deviceId);
        }

    }
}