package garndesh.openglrenderer;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

import java.util.HashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector3f;

public class Game {
	private static Game instance;
	private ShaderProgram shader;
	private int vaoID, vboID, vboTexID, eboID;
	private Texture texture;
	private Transform transform;
	private Camera camera;
	private CubeRenderer cubeRenderer;
	private ModelBaseTest modelRenderer;
	private ModelBaseTest modelRenderer2;

	/**
	 * Create a new Game
	 */
	public Game() {
		try {
			// Set the static reference
			instance = this;

			// Create the default PixelFormat
			PixelFormat pfmt = new PixelFormat();

			// We need a core context with atleast OpenGL 3.2
			ContextAttribs cattr = new ContextAttribs(3, 2)
					.withForwardCompatible(true).withProfileCore(true);

			// Create the Display
			Display.create(pfmt, cattr);
			Display.setResizable(true);
			setDisplayMode(800, 600, false);

			// Start the game
			gameLoop();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1); // There is no point in running without hardware
								// acceleration right?
		}
	}

	/**
	 * Initialize the game
	 */
	public void init() {
		transform = new Transform();

		Mouse.setGrabbed(true);
		camera = new Camera(67, ((float)Display.getWidth())/((float)Display.getHeight()), 0.1f, 100);
		camera.setPosition(new Vector3f(0, 0, 0.8f));

		// Create a new ShaderProgram
		shader = new ShaderProgram();
		shader.attachVertexShader("garndesh/openglrenderer/vertex01.vert");
		shader.attachFragmentShader("garndesh/openglrenderer/fragment01.frag");
		shader.link();
		

		// Set the texture sampler
		shader.setUniform("tex",new float[]{2});

		cubeRenderer = new CubeRenderer();
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		 HashMap<String, ModelBaseTest> models = ModelBaseTest.generateModelsFromFile("src/main/resources/garndesh/openglrenderer/models/aapje.obj", "garndesh/openglrenderer/textures/aapje.png");
		 modelRenderer = models.get("JasperFinal");
		// modelRenderer2 = models.get("CubeTop");
		// Unbind the VAO
		glBindVertexArray(0);

		glEnable(GL_DEPTH_TEST);
		/*
		 * texture = Texture
		 * .loadTexture("garndesh/openglrenderer/textures/tut_texture.png");
		 * texture.setActiveTextureUnit(0); texture.bind();
		 */
	}

	public static long getCurrentTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

	private void gameLoop() {
		long lastFrame = getCurrentTime();
		long thisFrame = getCurrentTime();

		init();

		while (!Display.isCloseRequested()) {
			thisFrame = getCurrentTime();
			update(thisFrame - lastFrame);
			render();

			lastFrame = thisFrame;
			Display.update();

			// Check the resize property
			if (Display.wasResized())
				resized();

			Display.sync(60); // You can write this value to a variable to
								// change it.
		}

		end();
	}

	/**
	 * Update the game
	 */
	public void update(long elapsedTime) {
		//transform.rotate(1, 1, 1);
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			Game.end();
		
		// Look up
//		if (Keyboard.isKeyDown(Keyboard.KEY_UP))
//			camera.rotateZ(1);
			camera.rotateZ((float)0.5*Mouse.getDY());
		// Look down
//		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
//			camera.rotateZ(-1);
		// Turn left
//		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
//			camera.rotateY(1);
			camera.rotateY((float)-0.5*Mouse.getDX());
		// Turn right
		//if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
		//	camera.rotateY(-1);
		// Move front
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			camera.move(Camera.Direction.FORWARD, 0.05f);
		// Move back
		if (Keyboard.isKeyDown(Keyboard.KEY_S))
			camera.move(Camera.Direction.BACKWARD, 0.05f);
		// Strafe left
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			camera.move(Camera.Direction.LEFT, 0.05f);
		// Strafe right
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			camera.move(Camera.Direction.RIGHT, 0.05f);
		// Move up
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			camera.move(Camera.Direction.UP, 0.05f);
		// Move down
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			camera.move(Camera.Direction.DOWN, 0.05f);
		// Update the Camera
		camera.update();
	}

	/**
	 * Render the game
	 */
	public void render() {
		// Clear the screen and depth buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		// Move the entire scene left by one
		//transform.translate(-1, 0, 0);
		// Draw an array of cubes
		for (int x = 0; x < 2; x++) {
			// Move the column of cubes
			//transform.translate(x, 0, 0);
			for (int z = 0; z < 5; z++) {
				// Add some depth for each row
				transform.translate(x*2-1, 0, -2*z);
				
				cubeRenderer.RenderCube(transform, shader, camera);
				
				transform.translate(0, 4, 0);
				transform.scale(0.04F, 0.04F, 0.04F);
				modelRenderer.renderModel(transform, shader, camera);
				
				//transform.rotate(0, 45, 0);
				//modelRenderer2.renderModel(transform, shader, camera);
				transform.reset();
			}
		}
	}

	/**
	 * Handle Display resizing
	 */
	public void resized() {
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	/**
	 * Sets a DisplayMode after selecting for a better one.
	 * 
	 * @param width
	 *            The width of the display.
	 * @param height
	 *            The height of the display.
	 * @param fullscreen
	 *            The fullscreen mode.
	 *
	 * @return True if switching is successful. Else false.
	 */
	public static boolean setDisplayMode(int width, int height,
			boolean fullscreen) {
		// return if requested DisplayMode is already set
		if ((Display.getDisplayMode().getWidth() == width)
				&& (Display.getDisplayMode().getHeight() == height)
				&& (Display.isFullscreen() == fullscreen))
			return true;

		try {
			// The target DisplayMode
			DisplayMode targetDisplayMode = null;

			if (fullscreen) {
				// Gather all the DisplayModes available at fullscreen
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;

				// Iterate through all of them
				for (DisplayMode current : modes) {
					// Make sure that the width and height matches
					if ((current.getWidth() == width)
							&& (current.getHeight() == height)) {
						// Select the one with greater frequency
						if ((targetDisplayMode == null)
								|| (current.getFrequency() >= freq)) {
							// Select the one with greater bits per pixel
							if ((targetDisplayMode == null)
									|| (current.getBitsPerPixel() > targetDisplayMode
											.getBitsPerPixel())) {
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						// if we've found a match for bpp and frequency against
						// the
						// original display mode then it's probably best to go
						// for this one
						// since it's most likely compatible with the monitor
						if ((current.getBitsPerPixel() == Display
								.getDesktopDisplayMode().getBitsPerPixel())
								&& (current.getFrequency() == Display
										.getDesktopDisplayMode().getFrequency())) {
							targetDisplayMode = current;
							break;
						}
					}
				}
			} else {
				// No need to query for windowed mode
				targetDisplayMode = new DisplayMode(width, height);
			}

			if (targetDisplayMode == null) {
				System.out.println("Failed to find value mode: " + width + "x"
						+ height + " fs=" + fullscreen);
				return false;
			}

			// Set the DisplayMode we've found
			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);

			System.out.println("Selected DisplayMode: "
					+ targetDisplayMode.toString());

			// Generate a resized event
			instance.resized();

			return true;
		} catch (LWJGLException e) {
			System.out.println("Unable to setup mode " + width + "x" + height
					+ " fullscreen=" + fullscreen + e);
		}

		return false;
	}

	/**
	 * Dispose the game
	 */
	public void dispose() {
		// Dispose the shaders
		shader.dispose();

		cubeRenderer.dispose();
		modelRenderer.dispose();
	}

	public static void end() {
		Mouse.setGrabbed(false);
		instance.dispose();
		Display.destroy();
		System.exit(0);
	}
}
