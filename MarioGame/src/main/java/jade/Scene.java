package jade;

public abstract class Scene {

    protected Camera camera;

    public Scene() {

    }

    //  Initialize scene
    public void init() {

    }

    public abstract void update(float dt);
}
