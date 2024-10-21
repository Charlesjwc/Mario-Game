package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    //  Which shader program this object is dealing with
    private int shaderProgramID;
    private boolean beingUsed = false;

    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    //  Constructor
    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            //  Fragment or index comes after this index
            int index = source.indexOf("#type") + 6;
            //  Index of end of line
            int eolIndex = source.indexOf("\r\n", index);
            //  Check for vertex of pattern
            String firstPattern  = source.substring(index, eolIndex).trim();

            //  Find the first pattern after #type 'pattern'
            index = source.indexOf("#type", eolIndex) + 6;
            eolIndex = source.indexOf("\r\n", index);
            //  Second pattern, vertex or fragment
            String secondPattern  = source.substring(index, eolIndex).trim();

            //  Save first pattern to source
            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            }
            else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            }
            else {
                throw new IOException("Unexpected Token '" + firstPattern + "'");
            }

            //  Save second pattern to source
            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            }
            else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            }
            else {
                throw new IOException("Unexpected Token '" + secondPattern + "'");
            }


        }
        catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: '" + filepath + "'";
        }
    }

    /** Compiles and links shaders  */
    public void compile() {
        //  =============================================================================
        //  Compile and link shaders
        //  =============================================================================
        int vertexID, fragmentID;

        //  Load and compile vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        //  Pass shader source to the GPU
        glShaderSource(vertexID, vertexSource);
        //  Compile shader
        glCompileShader(vertexID);

        // Check for errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        //  Load and compile vertex shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        //  Pass shader source to the GPU
        glShaderSource(fragmentID, fragmentSource);
        //  Compile shader
        glCompileShader(fragmentID);

        // Check for errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        //  Link shaders and check for errors
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        //  Check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tLinking of shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }
    }

    public void use() {
        //  Only use if it's not being used
        if (!beingUsed) {
            //  Bind shader program
            glUseProgram(shaderProgramID);
            //  Shader is now being used
            beingUsed = true;
        }
    }

    public void detach() {
        glUseProgram(0);
        //  Shader no longer being used
        beingUsed = false;
    }

    //  Upload a matrix variable to shader
    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();  //  Make sure shader is being used
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        //  Put matrix into buffer
        mat4.get(matBuffer);    //  Buffer of 16 variables (4x4 matrix)
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }
    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();  //  Make sure shader is being used
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);   //  Buffer of 9 variables (3x3 matrix)
        //  Put matrix into buffer
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    //  Upload vectors to shader
    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }
    public void uploadVec3f(String varName, Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }
    public void uploadVec2f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    //  Upload float to shader
    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }

    //  Upload int to shader
    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }

    //  Upload texture to shader
    public void uploadTexture(String varName, int slot) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);
    }
}
