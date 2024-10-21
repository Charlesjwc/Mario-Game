package jade;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import renderer.Texture;
import util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private float[] vertexArray = {
        //  Position            //  Color                   //  UV coords
        100f,   0.0f,  0.0f,    1.0f, 0.0f, 0.0f, 1.0f,     1, 1, //  Bottom right 0
        0.0f,   100f,  0.0f,    0.0f, 1.0f, 0.0f, 1.0f,     0, 0, //  Top left     1
        100f,   100f,  0.0f,    0.0f, 0.0f, 1.0f, 1.0f,     1, 0, //  Top right    2
        0.0f,   0.0f,  0.0f,    1.0f, 1.0f, 0.0f, 1.0f,     0, 1, //  Bottom left  3
    };

    //  IMPORTANT: Must be in ccw order
    private int[] elementArray = {
        2, 1, 0, //  Top right triangle
        0, 1, 3 //  Bottom left
    };

    private int vaoID, vboID, eboID;

    private Shader defaultShader;
    private Texture testTexture;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        //  Camera at 0, 0, 0
        this.camera = new Camera(new Vector2f());

        //  Compile shaders
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();

        //  Get textures
        this.testTexture = new Texture("assets/images/test.png");

        //  =============================================================================
        //  Generate VAO, BAO, and EBO buffer objects, and sent to GPU
        //  =============================================================================

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
        int uvSize = 2;
        int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {
        //  Use the shader
        defaultShader.use();

        //  Upload texture to shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        //  Upload variables to camera field vars
        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

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

        defaultShader.detach();
    }
}
