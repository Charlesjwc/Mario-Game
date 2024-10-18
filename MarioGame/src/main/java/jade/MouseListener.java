package jade;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    //  Single instance
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastX, lastY;
    private boolean[] mouseButtonPressed = new boolean[3];
    private boolean isDragging;

    //  Constructors
    private MouseListener() {
        //  Initialize values to 0
        this.scrollX = 0;
        this.scrollY = 0;
        this.xPos = 0;
        this.yPos = 0;
        this.lastX = 0;
        this.lastY = 0;
    }

    /**  Accessor for single instance, creates new instance if null
     * @return  MouseListener single instance
     */
    public static MouseListener get() {
        if (MouseListener.instance == null) {
            instance = new MouseListener();
        }
        return instance;
    }

    /**  Callback mouse to center, updates current and last pos
     *
     * @param   Window this is happening on
     * @param   xPos new X position
     * @param   yPos new Y position
     */
    public static void mousePosCallback(long Window, double xPos, double yPos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xPos;
        get().yPos = yPos;
        //  Update isDragging
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];
    }

    /** Callback mouse button click,
     *
     * @param   Window this is happening on
     * @param   button pressed
     * @param   action pressed or released
     * @param   mods like ctrl
     */
    public static void mouseButtonCallback(long Window, int button, int action, int mods) {
        //  Update if mouse clicked
        if (action == GLFW_PRESS) {
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
            }
        }
        //  Update if mouse released
        else if (action == GLFW_RELEASE) {
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    /** Callback mouse scrolling
     *
     * @param   Window this is happening on
     * @param   xOffset of scroll wheel
     * @param   yOffset of scroll wheel
     */
    public static void mouseScrollCallback(long Window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    /** Reset inputs */
    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
        //  Set deltas to 0
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    /*  Getter methods  */
    public static float getX() {
        return (float)get().xPos;
    }
    public static float getY() {
        return (float)get().yPos;
    }

    public static float getDx() {
        return (float)(get().lastX - get().xPos);
    }
    public static float getDy() {
        return (float)(get().lastY - get().yPos);
    }

    public static float getScrollX() {
        return (float)get().scrollX;
    }
    public static float getScrollY() {
        return (float)get().scrollY;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        if (button < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button];
        }
        else
            return false;
    }
}
