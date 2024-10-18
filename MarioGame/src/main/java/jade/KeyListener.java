package jade;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
    //  Field vars
    private static KeyListener instance;
    private boolean keyPressed[] = new boolean[350];

    //  Constructors
    private KeyListener() {}

    public static KeyListener get() {
        if (instance == null) {
            instance = new KeyListener();
        }
        return instance;
    }

    /** Callback keypresses
     *
     * @param window this is on
     * @param key pressed
     * @param scancode of keys
     * @param action pressed or released
     * @param mods like ctrl or shift
     */
    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().keyPressed[key] = true;
        }
        else if (action == GLFW_RELEASE) {
            get().keyPressed[key] = false;
        }
    }

    /** Check if key is pressed
     *
     * @param keyCode to check if pressed
     * @return if key is pressed
     */
    public static boolean isKeyPressed(int keyCode) {
        return get().keyPressed[keyCode];
    }
}
