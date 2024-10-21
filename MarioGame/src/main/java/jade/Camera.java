package jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {

    //  Matrices to convert world coordinates to camera coordinates
    private Matrix4f projectionMatrix, viewMatrix;
    //  Position of this camera
    public Vector2f position;


    //  Constructor
    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection() {
        //  Set projection matrix to identity matrix, any matrix multiplied by it is 1
        projectionMatrix.identity();
        //  Each tile is 32  pixels, 40 tiles horizontally, 21 vertically, able to view objects from 0 to 100 units away
        projectionMatrix.ortho(0.0f, 32.0f * 40.0f, 0.0f, 32.0f * 21.0f, 0, 100);
    }

    public Matrix4f getViewMatrix() {
        //  The front of the camera is at z = -1
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();
        //  Camera is at posion, x, y, and arbitrary z, front is moved with camera
        this.viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f),
                                            cameraFront.add(position.x, position.y, 0),
                                            cameraUp);
        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }
}
