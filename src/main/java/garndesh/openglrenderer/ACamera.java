package garndesh.openglrenderer;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public abstract class ACamera {


	protected Vector3f   position;
	protected Quaternion orientation;
	
	// The projection and view matrices
	protected Matrix4f projection;
	protected Matrix4f view;

	// Projection and View matrix buffers
	protected FloatBuffer projBuffer;
	protected FloatBuffer viewBuffer;
	
	public enum Direction {
		FORWARD,
		BACKWARD,
		LEFT,
		RIGHT,
		UP,
		DOWN
	}
	
	public ACamera(){

	    // Default position is the origin
	    position    = new Vector3f();
	    orientation = new Quaternion();
	    
	 // Create projection and view matrices
	    projection = new Matrix4f();
	    view       = new Matrix4f();

	    // Create the projection and view matrix buffers
	    projBuffer = BufferUtils.createFloatBuffer(16);
	    viewBuffer = BufferUtils.createFloatBuffer(16);

	}
	
	public abstract void rotateY(float angle);
	
	public abstract void rotateZ(float angle);
	
	public abstract void rotateX(float angle);
	
	public abstract void move(Vector3f dir, float amount);
	
	public abstract void move(Direction dir, float amount);
	
	public abstract void update();

	public void setPosition(Vector3f position) {
		// TODO Auto-generated method stub
		this.position = position;
	}

	public FloatBuffer getViewBuffer() {
		return this.viewBuffer;
	}

	public FloatBuffer getProjectionBuffer() {
		// TODO Auto-generated method stub
		return this.projBuffer;
	}
	
	public Vector3f getPosition(){
		return this.position;
	}
}
