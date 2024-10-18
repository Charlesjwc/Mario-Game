package jade;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    //  Shaders
    private String vertexShaderSrc = "#version 330 core\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main() {\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
        //  Position            //  Color
         0.5f, -0.5f,  0.0f,    1.0f, 0.0f, 0.0f, 1.0f,  //  Bottom right 0
        -0.5f,  0.5f,  0.0f,    0.0f, 1.0f, 0.0f, 1.0f,  //  Top left     1
         0.5f,  0.5f,  0.0f,    0.0f, 0.0f, 1.0f, 1.0f,  //  Top right    2
        -0.5f, -0.5f,  0.0f,    1.0f, 1.0f, 0.0f, 1.0f,  //  Bottom left  3
    };

    //  IMPORTANT: Must be in ccw order
    private int[] elementArray = {
        2, 1, 0, //  Top right triangle
        0, 1, 3 //  Bottom left
    };

    private int vaoID, vboID, eboID;


    public LevelEditorScene() {

    }

    @Override
    public void init() {
        //  Compile and link shaders
        //  Load and compile vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        //  Pass shader source to the GPU
        glShaderSource(vertexID, vertexShaderSrc);
        //  Compile shader
        glCompileShader(vertexID);

        // Check for errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        //  Compile and link shaders
        //  Load and compile vertex shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        //  Pass shader source to the GPU
        glShaderSource(fragmentID, fragmentShaderSrc);
        //  Compile shader
        glCompileShader(fragmentID);

        // Check for errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        //  Link shaders and check for errors
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        //  Check for linking errors
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tLinking of shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false : "";
        }

        //  Generate VAO, BAO, and EBO buffer objects, and sent to GPU
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //  Vertex float buffer
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //  Create VBO upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        //  Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        //  Create EBO
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        //  Add vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        //  Bind shader program
        glUseProgram(shaderProgram);
        //  Bind VAO being used
        glBindVertexArray(vaoID);

        //  Enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        //  Draw elements
        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        //  Unbind
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        glUseProgram(0);
    }
}
