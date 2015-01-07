package garndesh.openglrenderer;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;


/**
 * @author christiaan
 *
 */
public class Camera extends ACamera{
	
	public static final Vector3f AXIS_X = new Vector3f(0, 0, -1);
	public static final Vector3f AXIS_Y = new Vector3f(0, 1, 0);
	public static final Vector3f AXIS_Z = new Vector3f(1, 0, 0);
	
	// Local axes (relative to the Camera)
	private Vector3f up; 
	private Vector3f forward; 
	private Vector3f right; 
	
	
	public Camera(float fov, float aspect, float zNear, float zFar)
	{
		super();
	    // Create the default local axes
	    up      = new Vector3f(AXIS_Y);
	    forward = new Vector3f(AXIS_X);
	    right   = new Vector3f(AXIS_Z);

	    // Create projection matrice
	    projection = MatrixUtil.createPerspective(fov, aspect, zNear, zFar);

	    // Store the projection matrix in buffer
	    projection.store(projBuffer);
	    projBuffer.rewind();
	}
	
	public void rotateY(float angle){
	    Quaternion yRot = QuaternionUtil.createFromAxisAngle(AXIS_Y, angle, null);
	    Quaternion.mul(yRot, orientation, orientation);
	    //orientation.setY(orientation.getY()+yRot.y);

	    QuaternionUtil.rotate(right, yRot, right);
	    QuaternionUtil.rotate(forward, yRot, forward);

	    right.normalise();
	    forward.normalise();
	}
	
	public void rotateZ(float angle){
	    Quaternion zRot = QuaternionUtil.createFromAxisAngle(right, angle, null);
	    Quaternion.mul(zRot, orientation, orientation);
	    //orientation.setZ(orientation.getZ()+zRot.z);

	    QuaternionUtil.rotate(up, zRot, up);
	    QuaternionUtil.rotate(forward, zRot, forward);

	    up.normalise();
	    forward.normalise();
	}
	
	public void rotateX(float angle){
	    
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
	
	// raw move function will change given vector
	private void moveRaw(Vector3f dir, float amount){
		dir.normalise();
		dir.scale(amount);
		Vector3f.add(position, dir, position);
	}
	
	public void move(Direction dir, float amount)
	{
	    switch (dir)
	    {
	        case FORWARD:  moveRaw(new Vector3f(forward.x, 0, forward.z), +amount); break;
	        case BACKWARD: moveRaw(new Vector3f(forward.x, 0, forward.z), -amount); break;
	        case LEFT:     moveRaw(new Vector3f(right.x, 0, right.z),   -amount); break;
	        case RIGHT:    moveRaw(new Vector3f(right.x, 0, right.z),   +amount); break;
	        case UP:       move(AXIS_Y,      +amount); break;
	        case DOWN:     move(AXIS_Y,      -amount); break;
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

}
