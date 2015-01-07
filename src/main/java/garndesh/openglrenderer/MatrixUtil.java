package garndesh.openglrenderer;

import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.oculusvr.capi.OvrMatrix4f;
import com.oculusvr.capi.OvrVector3f;

public class MatrixUtil {

	
	/**
	 * Creates a Perspective Projection matrix.
	 *
	 * @param fov    The Field Of View Angle.
	 * @param aspect The Aspect ratio of the Display.
	 * @param zNear  The near depth plane.
	 * @param zFar   The far depth plane.
	 *
	 * @return A Perspective Projection Matrix.
	 */
	public static Matrix4f createPerspective(float fov, float aspect, float zNear, float zFar)
	{
	    // Create a new empty matrix
	    Matrix4f mat = new Matrix4f();
	        
	    // Calculate the frustum properties from aspect ratio
	    float yScale = 1f / (float) Math.tan(Math.toRadians(fov / 2f));
	    float xScale = yScale / aspect;
	    float frustumLength = zFar - zNear;
	        
	    // Apply them to the matrix
	    mat.m00 = xScale;
	    mat.m11 = yScale;
	    mat.m22 = -((zFar + zNear) / frustumLength);
	    mat.m23 = -1;
	    mat.m32 = -((2 * zFar * zNear) / frustumLength);
	    mat.m33 = 0;
	        
	    return mat;
	}
	
	
	/**
	 * Creates an Orthographic Projection Matrix.
	 *
	 * @param left   Coordinates of the left vertical clipping plane.
	 * @param right  Coordinates of the right vertical clipping plane.
	 * @param bottom Coordinates of the bottom horizontal clipping plane.
	 * @param top    Coordinates of the top horizontal clipping plane.
	 * @param zNear  The distance to the near clipping plane.
	 * @param zFar   The distance to the far clipping plane.
	 *
	 * @return An Orthographic projection matrix.
	 */
	public static Matrix4f createOrthographic(float left, float right,
	    float bottom, float top,
	    float zNear, float zFar)
	{
	    // Create a new empty matrix
	    Matrix4f mat = new Matrix4f();
	        
	    // Apply the projection to it
	    mat.m00 = 2 / (right - left);
	    mat.m11 = 2 / (top - bottom);
	    mat.m22 = -2 / (zFar - zNear);
	    mat.m30 = -(right + left) / (right - left);
	    mat.m31 = -(top + bottom) / (top - bottom);
	    mat.m32 = -(zFar + zNear) / (zFar - zNear);
	    mat.m33 = 1;
	        
	    return mat;
	}

	public static Vector3f toVector3f(OvrVector3f positionProjection){
		return new Vector3f(positionProjection.x, positionProjection.y, positionProjection.z);
	}

	public static Matrix4f toMatrix4f(OvrMatrix4f perspectiveProjection) {
		Matrix4f m = new Matrix4f();
		FloatBuffer fb = FloatBuffer.allocate(16);
		fb.put(perspectiveProjection.M);
		fb.rewind();
		Log.d("tmp", "fb.position: "+fb.position()+" fb.capacity: "+fb.capacity());
		m.load(fb);
		return (Matrix4f) m.transpose();
	}
}
