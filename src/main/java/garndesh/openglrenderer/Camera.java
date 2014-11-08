package garndesh.openglrenderer;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	public enum Direction {
		FORWARD,
		BACKWARD,
		LEFT,
		RIGHT,
		UP,
		DOWN
	}
	
	public static final Vector3f AXIS_X = new Vector3f(1, 0, 0);
	public static final Vector3f AXIS_Y = new Vector3f(0, 1, 0);
	public static final Vector3f AXIS_Z = new Vector3f(0, 0, 1);
	
	private Vector3f   position;
	private Quaternion orientation;
	
	// The projection and view matrices
	private Matrix4f projection;
	private Matrix4f view;

	// Projection and View matrix buffers
	private FloatBuffer projBuffer;
	private FloatBuffer viewBuffer;
	
	// Local axes (relative to the Camera)
	private Vector3f up;
	private Vector3f forward;
	private Vector3f right;
	
	
	public Camera(float fov, float aspect, float zNear, float zFar)
	{
	    // Default position is the origin
	    position    = new Vector3f();
	    orientation = new Quaternion();

	    // Create the default local axes
	    up      = new Vector3f(AXIS_Y);
	    forward = new Vector3f(AXIS_Z);
	    right   = new Vector3f(AXIS_X);

	    // Create projection and view matrices
	    projection = MatrixUtil.createPerspective(fov, aspect, zNear, zFar);
	    view       = new Matrix4f();

	    // Create the projection and view matrix buffers
	    projBuffer = BufferUtils.createFloatBuffer(16);
	    viewBuffer = BufferUtils.createFloatBuffer(16);

	    // Store the projection matrix in buffer
	    projection.store(projBuffer);
	    projBuffer.rewind();
	}
	
	public void rotateY(float angle){
	    Quaternion yRot = QuaternionUtil.createFromAxisAngle(AXIS_Y, angle, null);
	    Quaternion.mul(yRot, orientation, orientation);

	    QuaternionUtil.rotate(right, yRot, right);
	    QuaternionUtil.rotate(forward, yRot, forward);

	    right.normalise();
	    forward.normalise();
	}
	
	public void rotateZ(float angle){
	    Quaternion zRot = QuaternionUtil.createFromAxisAngle(AXIS_Z, angle, null);
	    Quaternion.mul(zRot, orientation, orientation);

	    QuaternionUtil.rotate(up, zRot, up);
	    QuaternionUtil.rotate(right, zRot, right);

	    up.normalise();
	    right.normalise();
	}
	
	public void rotateX(float angle){
	    Quaternion xRot = QuaternionUtil.createFromAxisAngle(AXIS_X, angle, null);
	    Quaternion.mul(xRot, orientation, orientation);

	    QuaternionUtil.rotate(up, xRot, up);
	    QuaternionUtil.rotate(forward, xRot, forward);

	    up.normalise();
	    forward.normalise();
	}
	
	public void move(Vector3f dir, float amount){
	    // Create a copy of direction
	    Vector3f deltaMove = new Vector3f(dir);

	    // Normalise the direction and scale it by amount
	    deltaMove.normalise();
	    deltaMove.scale(amount);

	    // Add the delta to the camera's position
	    Vector3f.add(position, deltaMove, position);
	}
	
	public void move(Direction dir, float amount)
	{
	    switch (dir)
	    {
	        case FORWARD:  move(forward, +amount); break;
	        case BACKWARD: move(forward, -amount); break;
	        case LEFT:     move(right,   -amount); break;
	        case RIGHT:    move(right,   +amount); break;
	        case UP:       move(up,      +amount); break;
	        case DOWN:     move(up,      -amount); break;
	    }
	}
	
	public void update(){
	    // Rotate the scene and translate the world back
	    QuaternionUtil.toRotationMatrix(orientation, view);
	    Matrix4f.translate(position.negate(null), view, view);

	    // Store the view matrix in the buffer
	    view.store(viewBuffer);
	    viewBuffer.rewind();
	}

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
}
