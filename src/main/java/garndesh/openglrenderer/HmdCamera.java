package garndesh.openglrenderer;

import static com.oculusvr.capi.OvrLibrary.ovrHmdType.ovrHmd_DK1;
import static com.oculusvr.capi.OvrLibrary.ovrRenderAPIType.ovrRenderAPI_OpenGL;
import static com.oculusvr.capi.OvrLibrary.ovrTrackingCaps.ovrTrackingCap_Orientation;
import static com.oculusvr.capi.OvrLibrary.ovrTrackingCaps.ovrTrackingCap_Position;

import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.oculusvr.capi.FovPort;
import com.oculusvr.capi.Hmd;
import com.oculusvr.capi.OvrVector2i;
import com.oculusvr.capi.OvrVector3f;
import com.oculusvr.capi.Posef;
import com.oculusvr.capi.Texture;
import com.oculusvr.capi.TextureHeader;

public class HmdCamera extends ACamera {

	private Hmd hmd;
	private float hFov;
	private float aspect;
	private final FovPort fovPorts[] = new FovPort[2];
	private final Texture eyeTextures[] = new Texture[2];
	private final Matrix4f projections[] = new Matrix4f[2];
	private final Posef poses[] = new Posef[2];
	private int frameCount;
	private OvrVector3f eyeOffsets[] = new OvrVector3f[2];

	public HmdCamera(float zNear, float zFar) {

		Hmd.initialize();

		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}

		hmd = openFirstHmd();
		if (null == hmd) {
			throw new IllegalStateException("Unable to initialize HMD");
		}

		if (0 == hmd.configureTracking(ovrTrackingCap_Orientation
				| ovrTrackingCap_Position, 0)) {
			throw new IllegalStateException("Unable to start the sensor");
		}

		for (int eye = 0; eye < 2; ++eye) {
			fovPorts[eye] = hmd.DefaultEyeFov[eye];
			projections[eye] = MatrixUtil
					.toMatrix4f(Hmd.getPerspectiveProjection(fovPorts[eye],
							zNear, zFar, true));

			Texture texture = eyeTextures[eye];
			TextureHeader header = texture.Header;
			header.API = ovrRenderAPI_OpenGL;
			header.TextureSize = hmd
					.getFovTextureSize(eye, fovPorts[eye], 1.0f);
			header.RenderViewport.Size = header.TextureSize;
			header.RenderViewport.Pos = new OvrVector2i(0, 0);
		}
	}

	private static Hmd openFirstHmd() {
		Hmd hmd = Hmd.create(0);
		if (null == hmd) {
			hmd = Hmd.createDebug(ovrHmd_DK1);
		}
		return hmd;
	}
	//should not be in de camera class, move to the renderer
	/*public void render(RenderScene scene){

		 ++frameCount;
		    hmd.beginFrame(frameCount);
		    Posef eyePoses[] = hmd.getEyePoses(frameCount, eyeOffsets);
		    for (int i = 0; i < 2; ++i) {
		      int eye = hmd.EyeRenderOrder[i];
		      Posef pose = eyePoses[eye];
		      //MatrixStack.PROJECTION.set(projections[eye]);
		      projection = projections[eye];
		      // This doesn't work as it breaks the contiguous nature of the array
		      // FIXME there has to be a better way to do this
		      poses[eye].Orientation = pose.Orientation;
		      poses[eye].Position = pose.Position;

		      
		    }
		    hmd.endFrame(poses, eyeTextures);
	}*/

	@Override
	public void rotateY(float angle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rotateZ(float angle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void rotateX(float angle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(Vector3f dir, float amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(Direction dir, float amount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPosition(Vector3f position) {
		// TODO Auto-generated method stub

	}

	@Override
	public FloatBuffer getViewBuffer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FloatBuffer getProjectionBuffer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector3f getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

}
