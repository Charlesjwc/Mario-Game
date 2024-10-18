package jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    //  Field vars
    private int width, height;
    private String title;
    private long glfwWindow;

    public float r, g, b, a;

    //  Only one window
    private static Window window = null;

    //  Current scene
    private static Scene currentScene;

    //  Constructors
    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Mario";
        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }

    /** Change scenes to newScene
     *
     * @param newScene to change to
     */
    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                break;
            default:
                assert false: "Unknown scene '" + newScene + "'";
                break;
        }
    }

    //  Returns window, creates one if none
    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }

    /** Sets up and runs the window  */
    public void run() {
        //  Print info into console
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        //  Setup and run window
        init();
        loop();

        //  Free memory once loop is exited
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //  Terminate GLFW and free the error callback
        glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    /** Initializes the window  */
    public void init() {
        //  Setup error callback
        GLFWErrorCallback.createPrint(System.err).set();

        //  Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        //  Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        //  Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Unable to create the GLFW window");
        }

        //  Callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        //  Make OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        //  Enable v-sync
        glfwSwapInterval(1);

        //  Make window visible
        glfwShowWindow(glfwWindow);

        //  Makes sure we can use binding
        GL.createCapabilities();

        //  Set scene
        Window.changeScene(0);
    }

    /** Keep updating window    */
    public void loop() {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1;

        while (!glfwWindowShouldClose(glfwWindow)) {
            //  Poll events
            glfwPollEvents();

            //  Flush clear color to screen
            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            //  Update scene
            if (dt >= 0) {
                currentScene.update(dt);
            }

            //  Update frame
            glfwSwapBuffers(glfwWindow);

            //  Keep track of time
            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }
}
