package garndesh.openglrenderer;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.HashMap;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

public class OvrGame extends Game {

	
	private ShaderProgram shader;
	private Transform transform;
	private ModelBaseTest modelRenderer;

	/**
	 * Initialize the game
	 */
	@Override
	public void init() {
		transform = new Transform();

		Mouse.setGrabbed(true);

		// Create a new ShaderProgram
		shader = new ShaderProgram();
		shader.attachVertexShader("garndesh/openglrenderer/vertex01.vert");
		shader.attachFragmentShader("garndesh/openglrenderer/fragment01.frag");
		shader.link();
		

		// Set the texture sampler
		shader.setUniform("tex",new float[]{2});

		 HashMap<String, ModelBaseTest> models = ModelBaseTest.generateModelsFromFile("src/main/resources/garndesh/openglrenderer/models/aapje.obj", "garndesh/openglrenderer/textures/aapje.png");
		 modelRenderer = models.get("JasperFinal");
		// modelRenderer2 = models.get("CubeTop");
		// Unbind the VAO
		glBindVertexArray(0);

		glEnable(GL_DEPTH_TEST);
	}
}
